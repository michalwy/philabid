package com.philabid.database;

import com.philabid.model.Auction;
import com.philabid.ui.control.FilterCondition;
import org.javamoney.moneta.Money;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Repository for managing Auction entities in the database.
 */
public class AuctionRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuctionRepository.class);
    private static final String FIND_QUERY = """
                      SELECT {additional_fields}
                          a.id, a.auction_house_id, a.auction_item_id, a.condition_id, a.lot_id, a.url,
                          a.current_price, a.max_bid, a.currency_code, a.end_date, a.archived, a.created_at, a.updated_at,
                          a.archived_catalog_value, a.archived_catalog_currency_code, a.archived_catalog_value_percentage,
                          cv.value AS catalog_value, cv.currency_code AS catalog_currency_code,
                          cat.is_active as catalog_is_active,
                          ah.name AS auction_house_name,
                          ah.currency AS auction_house_currency_code,
                          ai.catalog_number AS auction_item_catalog_number,
                          ai.order_number AS auction_item_order_number,
                          catg.name AS auction_item_category_name,
                          catg.code AS auction_item_category_code,
                          catg.id AS auction_item_category_id,
                          cond.name AS condition_name,
                          cond.code AS condition_code,
                          cond.id AS condition_id
                      FROM auctions a
                      LEFT JOIN auction_houses ah ON a.auction_house_id = ah.id
                      LEFT JOIN auction_items ai ON a.auction_item_id = ai.id
                      LEFT JOIN conditions cond ON a.condition_id = cond.id
                      LEFT JOIN categories catg ON ai.category_id = catg.id
                      LEFT JOIN catalog_values cv ON cv.auction_item_id = a.auction_item_id AND cv.condition_id = a.condition_id
                      LEFT JOIN catalogs cat ON cv.catalog_id = cat.id
                      {join_clause}
                      WHERE {where_clause}
                      ORDER BY {order_clause}
            """;
    private final DatabaseManager databaseManager;

    public AuctionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    private static String getFindQuery(List<String> additionalFields, String whereClause, String joinClause,
                                       String orderClause) {
        String additionalFieldsString = String.join(",", additionalFields);
        if (!additionalFieldsString.isEmpty()) {
            additionalFieldsString = additionalFieldsString + ",";
        }
        return FIND_QUERY
                .replace("{additional_fields}", additionalFieldsString)
                .replace("{where_clause}", whereClause)
                .replace("{join_clause}", joinClause)
                .replace("{order_clause}", orderClause);
    }

    public Auction save(Auction auction) throws SQLException {
        if (auction.getId() == null) {
            return insert(auction);
        } else {
            return update(auction);
        }
    }

    public Optional<Auction> findById(long id) throws SQLException {
        final Auction[] result = new Auction[1];
        new FindQueryBuilder()
                .withWhereClause("a.id = ?", List.of(id))
                .execute((rs, auction) -> result[0] = auction);
        return result[0] != null ? Optional.of(result[0]) : Optional.empty();
    }

    /**
     * @deprecated Use findAllActive() or findAllArchived() instead.
     */
    @Deprecated
    public List<Auction> findAll(Collection<FilterCondition> filterConditions) throws SQLException {
        return findAllActive(filterConditions);
    }

    public List<Auction> findAllActive(Collection<FilterCondition> filterConditions) throws SQLException {
        return findByArchivedStatus(false, filterConditions);
    }

    public List<Auction> findAllArchived(Collection<FilterCondition> filterConditions) throws SQLException {
        return findByArchivedStatus(true, filterConditions);
    }

    private List<Auction> findByArchivedStatus(boolean isArchived, Collection<FilterCondition> filterConditions)
            throws SQLException {
        List<Auction> auctions = new ArrayList<>();
        new FindQueryBuilder()
                .withWhereClause("a.archived = ?", List.of(isArchived))
                .withFilterConditions(filterConditions)
                .withOrderClause("a.end_date DESC")
                .execute((rs, auction) -> auctions.add(auction));
        return auctions;
    }

    public Map<Long, List<Auction>> findArchivedForActiveAuctions() throws SQLException {
        Map<Long, List<Auction>> archiveMap = new HashMap<>();
        new FindQueryBuilder()
                .withAdditionalField("act_a.id AS active_id")
                .withWhereClause("act_a.archived = 0")
                .withJoinClause(
                        "INNER JOIN auctions act_a ON " +
                                "act_a.auction_item_id = a.auction_item_id AND " +
                                "act_a.condition_id = a.condition_id AND " +
                                "a.archived = 1 ")
                .execute((rs, auction) -> {
                    long activeId = 0;
                    try {
                        activeId = rs.getLong("active_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    archiveMap.computeIfAbsent(activeId, k -> new ArrayList<>()).add(auction);
                });
        return archiveMap;
    }

    public Map<Pair<Long, Long>, List<Auction>> findArchivedForActiveCategories() throws SQLException {
        Map<Pair<Long, Long>, List<Auction>> archiveMap = new HashMap<>();
        new FindQueryBuilder()
                .withJoinClause(
                        "INNER JOIN auction_items act_ai ON act_ai.category_id = ai.category_id " +
                                "INNER JOIN auctions act_a " +
                                "ON act_a.auction_item_id = act_ai.id AND " +
                                "act_a.condition_id = a.condition_id AND " +
                                "act_a.archived = 0")
                .withWhereClause("a.archived = 1")
                .execute((rs, auction) -> {
                    long categoryId = 0;
                    long conditionId = 0;
                    try {
                        categoryId = rs.getLong("auction_item_category_id");
                        conditionId = rs.getLong("condition_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    archiveMap.computeIfAbsent(Pair.with(categoryId, conditionId), k -> new ArrayList<>()).add(auction);
                });
        return archiveMap;
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
                "INSERT INTO auctions(auction_house_id, auction_item_id, condition_id, lot_id, url, current_price, " +
                        "currency_code, end_date, archived, created_at, updated_at, max_bid, " +
                        "archived_catalog_value, archived_catalog_currency_code, archived_catalog_value_percentage) " +
                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            auction.setCreatedAt(LocalDateTime.now());
            auction.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, auction.getAuctionHouseId());
            pstmt.setLong(2, auction.getAuctionItemId());
            pstmt.setLong(3, auction.getConditionId());
            pstmt.setString(4, auction.getLotId());
            pstmt.setString(5, auction.getUrl());
            if (auction.getCurrentPrice() != null) {
                pstmt.setBigDecimal(6,
                        auction.getCurrentPrice().originalAmount().getNumber().numberValue(BigDecimal.class));
                pstmt.setString(7, auction.getCurrentPrice().getOriginalCurrency().getCurrencyCode());
            } else {
                pstmt.setNull(6, Types.DECIMAL);
                pstmt.setNull(7, Types.VARCHAR);
            }
            pstmt.setTimestamp(8, auction.getEndDate() != null ? Timestamp.valueOf(auction.getEndDate()) : null);
            pstmt.setBoolean(9, auction.isArchived());
            pstmt.setTimestamp(10, Timestamp.valueOf(auction.getCreatedAt()));
            pstmt.setTimestamp(11, Timestamp.valueOf(auction.getUpdatedAt()));
            pstmt.setBigDecimal(12,
                    auction.getMaxBid() != null ?
                            auction.getMaxBid().originalAmount().getNumber().numberValue(BigDecimal.class) : null);
            if (auction.getArchivedCatalogValue() != null) {
                pstmt.setBigDecimal(13,
                        auction.getArchivedCatalogValue().originalAmount().getNumber().numberValue(BigDecimal.class));
                pstmt.setString(14, auction.getArchivedCatalogValue().getOriginalCurrency().getCurrencyCode());
            } else {
                pstmt.setNull(13, Types.DECIMAL);
                pstmt.setNull(14, Types.VARCHAR);
            }
            if (auction.getArchivedCatalogValuePercentage() != null) {
                pstmt.setDouble(15, auction.getArchivedCatalogValuePercentage());
            } else {
                pstmt.setNull(15, Types.DOUBLE);
            }

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
                        "lot_id = ?, url = ?, current_price = ?, currency_code = ?, end_date = ?, archived = ?, " +
                        "updated_at = ?, max_bid = ?, archived_catalog_value = ?, archived_catalog_currency_code = ?," +
                        " " +
                        "archived_catalog_value_percentage = ? " +
                        "WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            auction.setUpdatedAt(LocalDateTime.now());

            pstmt.setLong(1, auction.getAuctionHouseId());
            pstmt.setLong(2, auction.getAuctionItemId());
            pstmt.setLong(3, auction.getConditionId());
            pstmt.setString(4, auction.getLotId());
            pstmt.setString(5, auction.getUrl());
            if (auction.getCurrentPrice() != null) {
                pstmt.setBigDecimal(6,
                        auction.getCurrentPrice().originalAmount().getNumber().numberValue(BigDecimal.class));
                pstmt.setString(7, auction.getCurrentPrice().getOriginalCurrency().getCurrencyCode());
            } else {
                pstmt.setNull(6, Types.DECIMAL);
                pstmt.setString(7, auction.getAuctionHouseCurrency().getCurrencyCode());
            }
            pstmt.setTimestamp(8, auction.getEndDate() != null ? Timestamp.valueOf(auction.getEndDate()) : null);
            pstmt.setBoolean(9, auction.isArchived());
            pstmt.setTimestamp(10, Timestamp.valueOf(auction.getUpdatedAt()));
            if (auction.getMaxBid() != null) {
                pstmt.setBigDecimal(11,
                        auction.getMaxBid() != null ?
                                auction.getMaxBid().originalAmount().getNumber().numberValue(BigDecimal.class) : null);
            } else {
                pstmt.setNull(11, Types.DECIMAL);
            }
            if (auction.getArchivedCatalogValue() != null) {
                pstmt.setBigDecimal(12,
                        auction.getArchivedCatalogValue().originalAmount().getNumber().numberValue(BigDecimal.class));
                pstmt.setString(13, auction.getArchivedCatalogValue().getOriginalCurrency().getCurrencyCode());
            } else {
                pstmt.setNull(12, Types.DECIMAL);
                pstmt.setNull(13, Types.VARCHAR);
            }
            if (auction.getArchivedCatalogValuePercentage() != null) {
                pstmt.setDouble(14, auction.getArchivedCatalogValuePercentage());
            } else {
                pstmt.setNull(14, Types.DOUBLE);
            }
            pstmt.setLong(15, auction.getId());

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
        BigDecimal currentPrice = rs.getBigDecimal("current_price");
        if (currentPrice != null) {
            auction.setRawCurrentPrice(Money.of(currentPrice, rs.getString("currency_code")));
        }
        BigDecimal maxBid = rs.getBigDecimal("max_bid");
        if (maxBid != null) {
            auction.setRawMaxBid(Money.of(rs.getBigDecimal("max_bid"), rs.getString("currency_code")));
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
            auction.setRawCatalogValue(Money.of(catalogValueAmount, Monetary.getCurrency(catalogValueCurrency)));
            auction.setCatalogActive(rs.getBoolean("catalog_is_active"));
        }
        BigDecimal archivedCatalogValueAmount = rs.getBigDecimal("archived_catalog_value");
        String archivedCatalogValueCurrency = rs.getString("archived_catalog_currency_code");
        if (archivedCatalogValueAmount != null && archivedCatalogValueCurrency != null) {
            auction.setRawArchivedCatalogValue(
                    Money.of(archivedCatalogValueAmount, Monetary.getCurrency(archivedCatalogValueCurrency)));
        }
        auction.setArchivedCatalogValuePercentage(rs.getDouble("archived_catalog_value_percentage"));

        // Joined fields
        auction.setAuctionHouseName(rs.getString("auction_house_name"));
        auction.setAuctionHouseCurrency(Monetary.getCurrency(rs.getString("auction_house_currency_code")));
        auction.setAuctionItemCatalogNumber(rs.getString("auction_item_catalog_number"));
        auction.setAuctionItemOrder(rs.getObject("auction_item_order_number", Long.class));
        auction.setAuctionItemCategoryName(rs.getString("auction_item_category_name"));
        auction.setAuctionItemCategoryCode(rs.getString("auction_item_category_code"));
        auction.setAuctionItemCategoryId(rs.getLong("auction_item_category_id"));
        auction.setConditionName(rs.getString("condition_name"));
        auction.setConditionCode(rs.getString("condition_code"));

        return auction;
    }

    class FindQueryBuilder {
        private final List<String> additionalFields = new ArrayList<>();
        private final List<String> whereClauses = new ArrayList<>();
        private final List<String> joinClauses = new ArrayList<>();
        private final List<String> orderClauses = new ArrayList<>();
        private final List<Object> params = new ArrayList<>();

        public FindQueryBuilder withAdditionalField(String field) {
            additionalFields.add(field);
            return this;
        }

        public FindQueryBuilder withWhereClause(String clause) {
            return withWhereClause(clause, List.of());
        }

        public FindQueryBuilder withWhereClause(String clause, Collection<Object> clauseParams) {
            addWhereClause(clause, clauseParams);
            return this;
        }

        public FindQueryBuilder withFilterConditions(Collection<FilterCondition> filterConditions) {
            filterConditions.forEach(fc -> {
                addWhereClause(fc.getSqlText(), fc.getSqlParams());
            });
            return this;
        }

        protected void addWhereClause(String clause, Collection<Object> clauseParams) {
            whereClauses.add(clause);
            params.addAll(clauseParams);
        }

        public FindQueryBuilder withOrderClause(String clause) {
            orderClauses.add(clause);
            return this;
        }

        public FindQueryBuilder withJoinClause(String clause) {
            joinClauses.add(clause);
            return this;
        }

        public void execute(BiConsumer<ResultSet, Auction> consumer) throws SQLException {
            String whereClause = String.join(" AND ", whereClauses);
            if (whereClause.isEmpty()) {
                whereClause = "1=1"; // Always true if no specific conditions
            }
            String joinClause = String.join(" ", joinClauses);
            if (joinClause.isEmpty()) {
                joinClause = "";
            }
            String orderClause = String.join(", ", orderClauses);
            if (orderClause.isEmpty()) {
                orderClause = "a.end_date DESC";
            }
            String sql = getFindQuery(additionalFields, whereClause, joinClause, orderClause);
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    consumer.accept(rs, mapRowToAuction(rs));
                }
            } catch (SQLException e) {
                logger.error("Error executing find query", e);
                throw e;
            }
        }
    }
}
