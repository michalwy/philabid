package com.philabid.database;

import com.philabid.model.AuctionHouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing AuctionHouse entities in the database.
 * Handles all CRUD (Create, Read, Update, Delete) operations.
 */
public class AuctionHouseRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseRepository.class);
    private final DatabaseManager databaseManager;

    public AuctionHouseRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Saves or updates an auction house.
     * If the auction house has a null ID, it's inserted.
     * Otherwise, it's updated.
     *
     * @param auctionHouse The AuctionHouse object to save.
     * @return The saved AuctionHouse, possibly with a new ID.
     */
    public AuctionHouse save(AuctionHouse auctionHouse) throws SQLException {
        if (auctionHouse.getId() == null) {
            return insert(auctionHouse);
        } else {
            return update(auctionHouse);
        }
    }

    /**
     * Finds an auction house by its ID.
     *
     * @param id The ID of the auction house to find.
     * @return An Optional containing the found AuctionHouse, or empty if not found.
     */
    public Optional<AuctionHouse> findById(long id) throws SQLException {
        String sql = "SELECT * FROM auction_houses WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAuctionHouse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding auction house with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    /**
     * Retrieves all auction houses from the database.
     *
     * @return A list of all auction houses.
     */
    public List<AuctionHouse> findAll() throws SQLException {
        String sql = "SELECT * FROM auction_houses ORDER BY name";
        List<AuctionHouse> auctionHouses = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                auctionHouses.add(mapRowToAuctionHouse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all auction houses", e);
            throw e;
        }
        return auctionHouses;
    }

    /**
     * Deletes an auction house by its ID.
     *
     * @param id The ID of the auction house to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM auction_houses WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted auction house with ID: {}", id);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error deleting auction house with ID: {}", id, e);
            throw e;
        }
    }

    private AuctionHouse insert(AuctionHouse auctionHouse) throws SQLException {
        String sql = "INSERT INTO auction_houses(name, website, contact_email, contact_phone, address, country, " +
                "currency, created_at, updated_at) VALUES(?,?,?,?,?,?,?,?,?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            auctionHouse.setCreatedAt(LocalDateTime.now());
            auctionHouse.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, auctionHouse.getName());
            pstmt.setString(2, auctionHouse.getWebsite());
            pstmt.setString(3, auctionHouse.getContactEmail());
            pstmt.setString(4, auctionHouse.getContactPhone());
            pstmt.setString(5, auctionHouse.getAddress());
            pstmt.setString(6, auctionHouse.getCountry());
            pstmt.setString(7, auctionHouse.getCurrency().getCurrencyCode());
            pstmt.setTimestamp(8, Timestamp.valueOf(auctionHouse.getCreatedAt()));
            pstmt.setTimestamp(9, Timestamp.valueOf(auctionHouse.getUpdatedAt()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating auction house failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    auctionHouse.setId(generatedKeys.getLong(1));
                    logger.info("Inserted new auction house with ID: {}", auctionHouse.getId());
                    return auctionHouse;
                } else {
                    throw new SQLException("Creating auction house failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting auction house: {}", auctionHouse.getName(), e);
            throw e;
        }
    }

    private AuctionHouse update(AuctionHouse auctionHouse) throws SQLException {
        String sql = "UPDATE auction_houses SET name = ?, website = ?, contact_email = ?, contact_phone = ?, address " +
                "= ?, country = ?, currency = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            auctionHouse.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, auctionHouse.getName());
            pstmt.setString(2, auctionHouse.getWebsite());
            pstmt.setString(3, auctionHouse.getContactEmail());
            pstmt.setString(4, auctionHouse.getContactPhone());
            pstmt.setString(5, auctionHouse.getAddress());
            pstmt.setString(6, auctionHouse.getCountry());
            pstmt.setString(7, auctionHouse.getCurrency().getCurrencyCode());
            pstmt.setTimestamp(8, Timestamp.valueOf(auctionHouse.getUpdatedAt()));
            pstmt.setLong(9, auctionHouse.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated auction house with ID: {}", auctionHouse.getId());
                return auctionHouse;
            } else {
                throw new SQLException("Updating auction house failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error updating auction house with ID: {}", auctionHouse.getId(), e);
            throw e;
        }
    }

    private AuctionHouse mapRowToAuctionHouse(ResultSet rs) throws SQLException {
        AuctionHouse ah = new AuctionHouse();
        ah.setId(rs.getLong("id"));
        ah.setName(rs.getString("name"));
        ah.setWebsite(rs.getString("website"));
        ah.setContactEmail(rs.getString("contact_email"));
        ah.setContactPhone(rs.getString("contact_phone"));
        ah.setAddress(rs.getString("address"));
        ah.setCountry(rs.getString("country"));
        ah.setCurrency(rs.getString("currency"));
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        ah.setCreatedAt(createdAtTs != null ? createdAtTs.toLocalDateTime() : null);
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        ah.setUpdatedAt(updatedAtTs != null ? updatedAtTs.toLocalDateTime() : null);
        return ah;
    }
}
