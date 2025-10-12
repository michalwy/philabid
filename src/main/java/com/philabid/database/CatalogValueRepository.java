package com.philabid.database;

import com.philabid.model.CatalogValue;
import com.philabid.ui.control.FilterCondition;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CatalogValue entities in the database.
 */
public class CatalogValueRepository {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueRepository.class);
    private static final String FIND_QUERY = """
            SELECT
                cv.id, cv.auction_item_id, cv.condition_id, cv.catalog_id, cv.value, cv.currency_code, cv.created_at, cv.updated_at,
                ai.catalog_number AS auction_item_catalog_number,
                ai.order_number AS auction_item_order_number,
                catg.code AS auction_item_category_code,
                catg.name AS auction_item_category_name,
                cond.name AS condition_name,
                cond.code AS condition_code,
                cat.name AS catalog_name,
                cat.issue_year AS catalog_issue_year
            FROM
                catalog_values cv
            LEFT JOIN auction_items ai ON cv.auction_item_id = ai.id
            LEFT JOIN conditions cond ON cv.condition_id = cond.id
            LEFT JOIN catalogs cat ON cv.catalog_id = cat.id
            LEFT JOIN categories catg ON ai.category_id = catg.id
            """;
    private final DatabaseManager databaseManager;

    public CatalogValueRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public CatalogValue save(CatalogValue catalogValue) throws SQLException {
        if (catalogValue.getId() == null) {
            return insert(catalogValue);
        } else {
            return update(catalogValue);
        }
    }

    public Optional<CatalogValue> findById(long id) throws SQLException {
        String sql = FIND_QUERY + " WHERE cv.id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCatalogValue(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding catalog value with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    public Optional<CatalogValue> findByAuctionItemAndCondition(long auctionItemId, long conditionId)
            throws SQLException {
        // This might return one of many if multiple catalogs have a value.
        // It's assumed for now that the combination is unique enough for this context.
        String sql = FIND_QUERY + " WHERE cv.auction_item_id = ? AND cv.condition_id = ? LIMIT 1";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, auctionItemId);
            pstmt.setLong(2, conditionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCatalogValue(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<CatalogValue> findAll() throws SQLException {
        return findAll(List.of());
    }

    public List<CatalogValue> findAll(Collection<FilterCondition> filterConditions) throws SQLException {
        String sql = FIND_QUERY + " ORDER BY ai.catalog_number, cond.name";
        List<CatalogValue> catalogValues = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                catalogValues.add(mapRowToCatalogValue(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all catalog values", e);
            throw e;
        }
        return catalogValues;
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM catalog_values WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting catalog value with ID: {}", id, e);
            throw e;
        }
    }

    private CatalogValue insert(CatalogValue catalogValue) throws SQLException {
        String sql = "INSERT INTO catalog_values(auction_item_id, condition_id, catalog_id, value, currency_code, " +
                "created_at, updated_at) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            catalogValue.setCreatedAt(LocalDateTime.now());
            catalogValue.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, catalogValue.getAuctionItemId());
            pstmt.setLong(2, catalogValue.getConditionId());
            pstmt.setLong(3, catalogValue.getCatalogId());
            pstmt.setBigDecimal(4, catalogValue.getValue().originalAmount().getNumber().numberValue(BigDecimal.class));
            pstmt.setString(5, catalogValue.getValue().getOriginalCurrency().getCurrencyCode());
            pstmt.setTimestamp(6, Timestamp.valueOf(catalogValue.getCreatedAt()));
            pstmt.setTimestamp(7, Timestamp.valueOf(catalogValue.getUpdatedAt()));

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Creating catalog value failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    catalogValue.setId(generatedKeys.getLong(1));
                    return catalogValue;
                } else {
                    throw new SQLException("Creating catalog value failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting catalog value", e);
            throw e;
        }
    }

    private CatalogValue update(CatalogValue catalogValue) throws SQLException {
        String sql = "UPDATE catalog_values SET auction_item_id = ?, condition_id = ?, catalog_id = ?, value = ?, " +
                "currency_code = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            catalogValue.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, catalogValue.getAuctionItemId());
            pstmt.setLong(2, catalogValue.getConditionId());
            pstmt.setLong(3, catalogValue.getCatalogId());
            pstmt.setBigDecimal(4, catalogValue.getValue().originalAmount().getNumber().numberValue(BigDecimal.class));
            pstmt.setString(5, catalogValue.getValue().getOriginalCurrency().getCurrencyCode());
            pstmt.setTimestamp(6, Timestamp.valueOf(catalogValue.getUpdatedAt()));
            pstmt.setLong(7, catalogValue.getId());

            pstmt.executeUpdate();
            return catalogValue;
        } catch (SQLException e) {
            logger.error("Error updating catalog value with ID: {}", catalogValue.getId(), e);
            throw e;
        }
    }

    private CatalogValue mapRowToCatalogValue(ResultSet rs) throws SQLException {
        CatalogValue cv = new CatalogValue();
        cv.setId(rs.getLong("id"));
        cv.setAuctionItemId(rs.getLong("auction_item_id"));
        cv.setConditionId(rs.getLong("condition_id"));
        cv.setCatalogId(rs.getLong("catalog_id"));
        cv.setValue(Money.of(rs.getBigDecimal("value"), Monetary.getCurrency(rs.getString("currency_code"))));
        cv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        cv.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        // Joined fields
        cv.setAuctionItemCatalogNumber(rs.getString("auction_item_catalog_number"));
        cv.setAuctionItemOrderNumber(rs.getLong("auction_item_order_number"));
        cv.setAuctionItemCategoryCode(rs.getString("auction_item_category_code"));
        cv.setAuctionItemCategoryName(rs.getString("auction_item_category_name"));
        cv.setConditionName(rs.getString("condition_name"));
        cv.setConditionCode(rs.getString("condition_code"));
        cv.setCatalogName(rs.getString("catalog_name"));
        cv.setCatalogIssueYear(rs.getInt("catalog_issue_year"));

        return cv;
    }
}
