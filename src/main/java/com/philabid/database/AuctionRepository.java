package com.philabid.database;

import com.philabid.model.Auction;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Auction entities in the database.
 */
public class AuctionRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRepository.class);
    private static final String FIND_QUERY = """
                      SELECT
                          a.id, a.auction_house_id, a.auction_item_id, a.condition_id, a.lot_id, a.url,
                          a.current_price, a.max_bid, a.currency_code, a.end_date, a.archived, a.created_at, a.updated_at,
                          cv.value AS catalog_value, cv.currency_code AS catalog_currency_code,
                          ah.name AS auction_house_name,
                          ai.catalog_number AS auction_item_catalog_number,
                          ai.order_number AS auction_item_order_number,
                          catg.name AS auction_item_category_name,
                          catg.code AS auction_item_category_code,
                          cond.name AS condition_name,
                          cond.code AS condition_code
                      FROM auctions a
                      LEFT JOIN auction_houses ah ON a.auction_house_id = ah.id
                      LEFT JOIN auction_items ai ON a.auction_item_id = ai.id
                      LEFT JOIN conditions cond ON a.condition_id = cond.id
                      LEFT JOIN categories catg ON ai.category_id = catg.id
                      LEFT JOIN catalog_values cv ON cv.auction_item_id = a.auction_item_id AND cv.condition_id = a.condition_id
            """;
    private final DatabaseManager databaseManager;

    public AuctionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Auction save(Auction auction) throws SQLException {
        if (auction.getId() == null) {
            return insert(auction);
        } else {
            return update(auction);
        }
    }

    public Optional<Auction> findById(long id) throws SQLException {
        String sql = FIND_QUERY + " WHERE a.id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAuction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding auction with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    /**
     * @deprecated Use findAllActive() or findAllArchived() instead.
     */
    @Deprecated
    public List<Auction> findAll() throws SQLException {
        return findAllActive();
    }

    public List<Auction> findAllActive() throws SQLException {
        return findByArchivedStatus(false);
    }

    public List<Auction> findAllArchived() throws SQLException {
        return findByArchivedStatus(true);
    }

    private List<Auction> findByArchivedStatus(boolean isArchived) throws SQLException {
        String sql = FIND_QUERY + " WHERE a.archived = ? ORDER BY a.end_date DESC";
        List<Auction> auctions = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isArchived);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                auctions.add(mapRowToAuction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all auctions", e);
            throw e;
        }
        return auctions;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM auctions WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting auction with ID: {}", id, e);
            throw e;
        }
    }

    private Auction insert(Auction auction) throws SQLException {
        String sql =
                "INSERT INTO auctions(auction_house_id, auction_item_id, condition_id, lot_id, url, " +
                        "current_price, currency_code, end_date, archived, created_at, updated_at, max_bid) VALUES(?," +
                        "?,?,?,?," +
                        "?," +
                        "?,?,?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            auction.setCreatedAt(LocalDateTime.now());
            auction.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, auction.getAuctionHouseId());
            pstmt.setLong(2, auction.getAuctionItemId());
            pstmt.setLong(3, auction.getConditionId());
            pstmt.setString(4, auction.getLotId());
            pstmt.setString(5, auction.getUrl());
            pstmt.setBigDecimal(6, auction.getCurrentPrice().getNumber().numberValue(BigDecimal.class));
            pstmt.setString(7, auction.getCurrentPrice().getCurrency().getCurrencyCode());
            pstmt.setTimestamp(8, auction.getEndDate() != null ? Timestamp.valueOf(auction.getEndDate()) : null);
            pstmt.setBoolean(9, auction.isArchived());
            pstmt.setTimestamp(10, Timestamp.valueOf(auction.getCreatedAt()));
            pstmt.setTimestamp(11, Timestamp.valueOf(auction.getUpdatedAt()));
            pstmt.setBigDecimal(12,
                    auction.getMaxBid() != null ? auction.getMaxBid().getNumber().numberValue(BigDecimal.class) : null);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating auction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    auction.setId(generatedKeys.getLong(1));
                    return auction;
                } else {
                    throw new SQLException("Creating auction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting auction", e);
            throw e;
        }
    }

    private Auction update(Auction auction) throws SQLException {
        String sql =
                "UPDATE auctions SET auction_house_id = ?, auction_item_id = ?, condition_id = ?, " +
                        "lot_id = ?, url = ?, current_price = ?, currency_code = ?, end_date = ?, " +
                        "archived = ?, updated_at = ?, max_bid = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            auction.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, auction.getAuctionHouseId());
            pstmt.setLong(2, auction.getAuctionItemId());
            pstmt.setLong(3, auction.getConditionId());
            pstmt.setString(4, auction.getLotId());
            pstmt.setString(5, auction.getUrl());
            pstmt.setBigDecimal(6, auction.getCurrentPrice().getNumber().numberValue(BigDecimal.class));
            pstmt.setString(7, auction.getCurrentPrice().getCurrency().getCurrencyCode());
            pstmt.setTimestamp(8, auction.getEndDate() != null ? Timestamp.valueOf(auction.getEndDate()) : null);
            pstmt.setBoolean(9, auction.isArchived());
            pstmt.setTimestamp(10, Timestamp.valueOf(auction.getUpdatedAt()));
            pstmt.setBigDecimal(11,
                    auction.getMaxBid() != null ? auction.getMaxBid().getNumber().numberValue(BigDecimal.class) : null);
            pstmt.setLong(12, auction.getId());

            pstmt.executeUpdate();
            return auction;
        } catch (SQLException e) {
            logger.error("Error updating auction with ID: {}", auction.getId(), e);
            throw e;
        }
    }

    private Auction mapRowToAuction(ResultSet rs) throws SQLException {
        Auction auction = new Auction();
        auction.setId(rs.getLong("id"));
        auction.setAuctionHouseId(rs.getLong("auction_house_id"));
        auction.setAuctionItemId(rs.getLong("auction_item_id"));
        auction.setConditionId(rs.getLong("condition_id"));
        auction.setLotId(rs.getString("lot_id"));
        auction.setUrl(rs.getString("url"));
        auction.setCurrentPrice(Money.of(rs.getBigDecimal("current_price"), rs.getString("currency_code")));
        BigDecimal maxBid = rs.getBigDecimal("max_bid");
        if (maxBid != null) {
            auction.setMaxBid(Money.of(rs.getBigDecimal("max_bid"), rs.getString("currency_code")));
        }
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) {
            auction.setEndDate(endDate.toLocalDateTime());
        }
        auction.setArchived(rs.getBoolean("archived"));
        auction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        auction.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        BigDecimal catalogValueAmount = rs.getBigDecimal("catalog_value");
        String catalogValueCurrency = rs.getString("catalog_currency_code");
        if (catalogValueAmount != null && catalogValueCurrency != null) {
            auction.setCatalogValue(Money.of(catalogValueAmount, Monetary.getCurrency(catalogValueCurrency)));
        }

        // Joined fields
        auction.setAuctionHouseName(rs.getString("auction_house_name"));
        auction.setAuctionItemCatalogNumber(rs.getString("auction_item_catalog_number"));
        auction.setAuctionItemOrder(rs.getObject("auction_item_order_number", Long.class));
        auction.setAuctionItemCategoryName(rs.getString("auction_item_category_name"));
        auction.setAuctionItemCategoryCode(rs.getString("auction_item_category_code"));
        auction.setConditionName(rs.getString("condition_name"));
        auction.setConditionCode(rs.getString("condition_code"));
        return auction;
    }
}
