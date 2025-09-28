package com.philabid.database;

import com.philabid.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Category entities in the database.
 */
public class CategoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRepository.class);
    private final DatabaseManager databaseManager;

    public CategoryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Category save(Category category) throws SQLException {
        if (category.getId() == null) {
            return insert(category);
        } else {
            return update(category);
        }
    }

    public Optional<Category> findById(long id) throws SQLException {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCategory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding category with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Category> findAll() throws SQLException {
        String sql = "SELECT * FROM categories ORDER BY name";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all categories", e);
            throw e;
        }
        return categories;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting category with ID: {}", id, e);
            throw e;
        }
    }

    private Category insert(Category category) throws SQLException {
        String sql = "INSERT INTO categories(name, code, catalog_id, created_at, updated_at) VALUES(?,?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getCode());
            pstmt.setObject(3, category.getCatalogId());
            pstmt.setTimestamp(4, Timestamp.valueOf(category.getCreatedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(category.getUpdatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getLong(1));
                    return category;
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting category: {}", category.getName(), e);
            throw e;
        }
    }

    private Category update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, code = ?, catalog_id = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            category.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getCode());
            pstmt.setObject(3, category.getCatalogId());
            pstmt.setTimestamp(4, Timestamp.valueOf(category.getUpdatedAt()));
            pstmt.setLong(5, category.getId());

            pstmt.executeUpdate();
            return category;
        } catch (SQLException e) {
            logger.error("Error updating category with ID: {}", category.getId(), e);
            throw e;
        }
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setCode(rs.getString("code"));
        category.setCatalogId(rs.getObject("catalog_id", Long.class));
        category.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        category.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return category;
    }
}
