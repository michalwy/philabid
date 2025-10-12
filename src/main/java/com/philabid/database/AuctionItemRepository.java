package com.philabid.database;

import com.philabid.model.AuctionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing AuctionItem entities in the database.
 */
public class AuctionItemRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemRepository.class);
    private final DatabaseManager databaseManager;

    public AuctionItemRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public AuctionItem save(AuctionItem auctionItem) throws SQLException {
        if (auctionItem.getId() == null) {
            return insert(auctionItem);
        } else {
            return update(auctionItem);
        }
    }

    public Optional<AuctionItem> findById(long id) throws SQLException {
        String sql = "SELECT " +
                "    ai.id, ai.category_id, ai.catalog_number, ai.order_number, ai.notes, ai.created_at, " +
                "ai.updated_at, " +
                "    catg.name AS category_name, catg.code AS category_code, " +
                "    cat.name AS catalog_name, cat.issue_year AS catalog_issue_year " +
                "FROM " +
                "    auction_items ai " +
                "LEFT JOIN " +
                "    categories catg ON ai.category_id = catg.id " +
                "LEFT JOIN " +
                "    catalogs cat ON catg.catalog_id = cat.id " +
                "WHERE ai.id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAuctionItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding auction item with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public List<AuctionItem> findAll() throws SQLException {
        String sql = "SELECT " +
                "    ai.id, ai.category_id, ai.catalog_number, ai.order_number, ai.notes, ai.created_at, " +
                "ai.updated_at, " +
                "    c.name AS category_name, c.code AS category_code, " +
                "    cat.name AS catalog_name, cat.issue_year AS catalog_issue_year " +
                "FROM " +
                "    auction_items ai " +
                "LEFT JOIN " +
                "    categories c ON ai.category_id = c.id " +
                "LEFT JOIN " +
                "    catalogs cat ON c.catalog_id = cat.id " +
                "ORDER BY " +
                "    ai.catalog_number";
        List<AuctionItem> auctionItems = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                auctionItems.add(mapRowToAuctionItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all auction items", e);
            throw e;
        }
        return auctionItems;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM auction_items WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting auction item with ID: {}", id, e);
            throw e;
        }
    }

    private AuctionItem insert(AuctionItem auctionItem) throws SQLException {
        String sql = "INSERT INTO auction_items(category_id, catalog_number, order_number, notes, created_at, " +
                "updated_at) VALUES(?,?,?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            auctionItem.setCreatedAt(LocalDateTime.now());
            auctionItem.setUpdatedAt(LocalDateTime.now());

            pstmt.setObject(1, auctionItem.getCategoryId());
            pstmt.setString(2, auctionItem.getCatalogNumber());
            pstmt.setLong(3, auctionItem.getOrderNumber());
            pstmt.setString(4, auctionItem.getNotes());
            pstmt.setTimestamp(5, Timestamp.valueOf(auctionItem.getCreatedAt()));
            pstmt.setTimestamp(6, Timestamp.valueOf(auctionItem.getUpdatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating auction item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    auctionItem.setId(generatedKeys.getLong(1));
                    return auctionItem;
                } else {
                    throw new SQLException("Creating auction item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting auction item: {}", auctionItem.getCatalogNumber(), e);
            throw e;
        }
    }

    private AuctionItem update(AuctionItem auctionItem) throws SQLException {
        String sql = "UPDATE auction_items SET category_id = ?, catalog_number = ?, order_number = ?, notes = ?, " +
                "updated_at = ? WHERE " +
                "id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            auctionItem.setUpdatedAt(LocalDateTime.now());

            pstmt.setObject(1, auctionItem.getCategoryId());
            pstmt.setString(2, auctionItem.getCatalogNumber());
            pstmt.setLong(3, auctionItem.getOrderNumber());
            pstmt.setString(4, auctionItem.getNotes());
            pstmt.setTimestamp(5, Timestamp.valueOf(auctionItem.getUpdatedAt()));
            pstmt.setLong(6, auctionItem.getId());

            pstmt.executeUpdate();
            return auctionItem;
        } catch (SQLException e) {
            logger.error("Error updating auction item with ID: {}", auctionItem.getId(), e);
            throw e;
        }
    }

    private AuctionItem mapRowToAuctionItem(ResultSet rs) throws SQLException {
        AuctionItem auctionItem = new AuctionItem();
        auctionItem.setId(rs.getLong("id"));
        auctionItem.setCategoryId(rs.getObject("category_id", Long.class));
        auctionItem.setCatalogNumber(rs.getString("catalog_number"));
        auctionItem.setOrderNumber(rs.getLong("order_number"));
        auctionItem.setNotes(rs.getString("notes"));
        auctionItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        auctionItem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        // Joined fields
        auctionItem.setCategoryName(rs.getString("category_name"));
        auctionItem.setCategoryCode(rs.getString("category_code"));
        auctionItem.setCatalogName(rs.getString("catalog_name"));
        auctionItem.setCatalogIssueYear(rs.getObject("catalog_issue_year", Integer.class));

        return auctionItem;
    }
}
