package com.philabid.database;

import com.philabid.model.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Catalog entities in the database.
 */
public class CatalogRepository {

    private static final Logger logger = LoggerFactory.getLogger(CatalogRepository.class);
    private final DatabaseManager databaseManager;

    public CatalogRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Catalog save(Catalog catalog) throws SQLException {
        if (catalog.getId() == null) {
            return insert(catalog);
        } else {
            return update(catalog);
        }
    }

    public Optional<Catalog> findById(long id) throws SQLException {
        String sql = "SELECT * FROM catalogs WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCatalog(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding catalog with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Catalog> findAll() throws SQLException {
        String sql = "SELECT * FROM catalogs ORDER BY name, issue_year";
        List<Catalog> catalogs = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                catalogs.add(mapRowToCatalog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all catalogs", e);
            throw e;
        }
        return catalogs;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM catalogs WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting catalog with ID: {}", id, e);
            throw e;
        }
    }

    private Catalog insert(Catalog catalog) throws SQLException {
        String sql = "INSERT INTO catalogs(name, issue_year, currency_code, is_active, created_at, updated_at) VALUES(?,?,?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            catalog.setCreatedAt(LocalDateTime.now());
            catalog.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, catalog.getName());
            pstmt.setObject(2, catalog.getIssueYear());
            pstmt.setString(3, catalog.getCurrencyCode());
            pstmt.setInt(4, catalog.isActive() ? 1 : 0);
            pstmt.setTimestamp(5, Timestamp.valueOf(catalog.getCreatedAt()));
            pstmt.setTimestamp(6, Timestamp.valueOf(catalog.getUpdatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating catalog failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    catalog.setId(generatedKeys.getLong(1));
                    return catalog;
                } else {
                    throw new SQLException("Creating catalog failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting catalog: {}", catalog.getName(), e);
            throw e;
        }
    }

    private Catalog update(Catalog catalog) throws SQLException {
        String sql = "UPDATE catalogs SET name = ?, issue_year = ?, currency_code = ?, is_active = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            catalog.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, catalog.getName());
            pstmt.setObject(2, catalog.getIssueYear());
            pstmt.setString(3, catalog.getCurrencyCode());
            pstmt.setInt(4, catalog.isActive() ? 1 : 0);
            pstmt.setTimestamp(5, Timestamp.valueOf(catalog.getUpdatedAt()));
            pstmt.setLong(6, catalog.getId());

            pstmt.executeUpdate();
            return catalog;
        } catch (SQLException e) {
            logger.error("Error updating catalog with ID: {}", catalog.getId(), e);
            throw e;
        }
    }

    private Catalog mapRowToCatalog(ResultSet rs) throws SQLException {
        Catalog catalog = new Catalog();
        catalog.setId(rs.getLong("id"));
        catalog.setName(rs.getString("name"));
        catalog.setIssueYear(rs.getObject("issue_year", Integer.class));
        catalog.setCurrencyCode(rs.getString("currency_code"));
        catalog.setActive(rs.getInt("is_active") == 1);
        catalog.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        catalog.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return catalog;
    }
}
