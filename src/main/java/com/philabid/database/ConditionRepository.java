package com.philabid.database;

import com.philabid.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Condition entities in the database.
 */
public class ConditionRepository {

    private static final Logger logger = LoggerFactory.getLogger(ConditionRepository.class);
    private final DatabaseManager databaseManager;

    public ConditionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Condition save(Condition condition) throws SQLException {
        if (condition.getId() == null) {
            return insert(condition);
        } else {
            return update(condition);
        }
    }

    public Optional<Condition> findById(long id) throws SQLException {
        String sql = "SELECT * FROM conditions WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCondition(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding condition with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<Condition> findAll() throws SQLException {
        String sql = "SELECT * FROM conditions ORDER BY name";
        List<Condition> conditions = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                conditions.add(mapRowToCondition(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all conditions", e);
            throw e;
        }
        return conditions;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM conditions WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting condition with ID: {}", id, e);
            throw e;
        }
    }

    private Condition insert(Condition condition) throws SQLException {
        String sql = "INSERT INTO conditions(name, code, created_at, updated_at) VALUES(?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            condition.setCreatedAt(LocalDateTime.now());
            condition.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, condition.getName());
            pstmt.setString(2, condition.getCode());
            pstmt.setTimestamp(3, Timestamp.valueOf(condition.getCreatedAt()));
            pstmt.setTimestamp(4, Timestamp.valueOf(condition.getUpdatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating condition failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    condition.setId(generatedKeys.getLong(1));
                    return condition;
                } else {
                    throw new SQLException("Creating condition failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting condition: {}", condition.getName(), e);
            throw e;
        }
    }

    private Condition update(Condition condition) throws SQLException {
        String sql = "UPDATE conditions SET name = ?, code = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            condition.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, condition.getName());
            pstmt.setString(2, condition.getCode());
            pstmt.setTimestamp(3, Timestamp.valueOf(condition.getUpdatedAt()));
            pstmt.setLong(4, condition.getId());

            pstmt.executeUpdate();
            return condition;
        } catch (SQLException e) {
            logger.error("Error updating condition with ID: {}", condition.getId(), e);
            throw e;
        }
    }

    private Condition mapRowToCondition(ResultSet rs) throws SQLException {
        Condition condition = new Condition();
        condition.setId(rs.getLong("id"));
        condition.setName(rs.getString("name"));
        condition.setCode(rs.getString("code"));
        condition.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        condition.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return condition;
    }
}
