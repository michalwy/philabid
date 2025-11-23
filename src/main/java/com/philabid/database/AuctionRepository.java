package com.philabid.database;

import com.philabid.database.util.EqualFilterCondition;
import com.philabid.database.util.FilterCondition;
import com.philabid.database.util.query.*;
import com.philabid.model.Auction;
import org.javatuples.Pair;

import java.util.*;

/**
 * Repository for managing Auction entities in the database.
 */
public class AuctionRepository extends CrudRepository<Auction> {
    private static final Collection<QueryField<Auction, ?>> FIELDS = List.of(
            new LongQueryField<>("a", "auction_house_id", Auction::setAuctionHouseId).withEntityValue(
                    Auction::getAuctionHouseId),
            new LongQueryField<>("a", "seller_id", Auction::setSellerId).withEntityValue(
                    Auction::getSellerId),
            new LongQueryField<>("a", "trading_item_id", Auction::setTradingItemId).withEntityValue(
                    Auction::getTradingItemId),
            new LongQueryField<>("a", "condition_id", Auction::setConditionId).withEntityValue(Auction::getConditionId),
            new StringQueryField<>("a", "lot_id", Auction::setLotId).withEntityValue(Auction::getLotId),
            new StringQueryField<>("a", "url", Auction::setUrl).withEntityValue(Auction::getUrl),
            new CurrencyQueryField<Auction>("a", "currency_code", null).withMultiCurrencyEntityValue(
                    Auction::getCurrentPrice),
            new MonetaryAmountQueryField<>("a", "current_price", "currency_code",
                    Auction::setRawCurrentPrice).withMultiCurrencyEntityValue(Auction::getCurrentPrice),
            new MonetaryAmountQueryField<>("a", "max_bid", "currency_code",
                    Auction::setRawMaxBid).withMultiCurrencyEntityValue(Auction::getMaxBid),
            new TimestampQueryField<>("a", "end_date", Auction::setEndDate).withEntityValue(Auction::getEndDate),
            new BooleanQueryField<>("a", "archived", Auction::setArchived).withEntityValue(Auction::isArchived),
            new CurrencyQueryField<Auction>("a", "archived_catalog_currency_code", null).withMultiCurrencyEntityValue(
                    Auction::getArchivedCatalogValue),
            new MonetaryAmountQueryField<>("a", "archived_catalog_value", "archived_catalog_currency_code",
                    Auction::setRawArchivedCatalogValue).withMultiCurrencyEntityValue(Auction::getArchivedCatalogValue),
            new DoubleQueryField<>("a", "archived_catalog_value_percentage",
                    Auction::setArchivedCatalogValuePercentage).withEntityValue(
                    Auction::getArchivedCatalogValuePercentage),
            new StringQueryField<>("ah", "name", "auction_house_name", Auction::setAuctionHouseName),
            new CurrencyQueryField<>("ah", "currency", "auction_house_currency_code", Auction::setAuctionHouseCurrency),
            new StringQueryField<>("s", "name", "seller_name", Auction::setSellerName),
            new StringQueryField<>("s", "full_name", "seller_full_name", Auction::setSellerFullName),
            new InternalQueryField<>("cv", "currency_code", "catalog_currency_code"),
            new MonetaryAmountQueryField<>("cv", "value", "catalog_value", "catalog_currency_code",
                    Auction::setRawCatalogValue),
            new StringQueryField<>("ti", "catalog_number", "trading_item_catalog_number",
                    Auction::setTradingItemCatalogNumber),
            new LongQueryField<>("ti", "order_number", "trading_item_order_number", Auction::setTradingItemOrderNumber),
            new StringQueryField<>("catg", "name", "trading_item_category_name", Auction::setTradingItemCategoryName),
            new StringQueryField<>("catg", "code", "trading_item_category_code", Auction::setTradingItemCategoryCode),
            new LongQueryField<>("catg", "id", "trading_item_category_id", Auction::setTradingItemCategoryId),
            new LongQueryField<>("cond", "id", "condition_id", Auction::setConditionId),
            new StringQueryField<>("cond", "name", "condition_name", Auction::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", Auction::setConditionCode),
            new BooleanQueryField<>("cat", "is_active", "catalog_is_active", Auction::setCatalogActive),
            new StringQueryField<>("cat", "name", "catalog_name", Auction::setCatalogName),
            new IntQueryField<>("cat", "issue_year", Auction::setCatalogIssueYear)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("auction_houses", "ah", "a.auction_house_id = ah.id"),
            new QueryLeftOuterJoin("sellers", "s", "a.seller_id = s.id"),
            new QueryLeftOuterJoin("trading_items", "ti", "a.trading_item_id = ti.id"),
            new QueryLeftOuterJoin("conditions", "cond", "a.condition_id = cond.id"),
            new QueryLeftOuterJoin("categories", "catg", "ti.category_id = catg.id"),
            new QueryLeftOuterJoin("catalog_values", "cv",
                    "cv.trading_item_id = a.trading_item_id AND cv.condition_id = a.condition_id"),
            new QueryLeftOuterJoin("catalogs", "cat", "cv.catalog_id = cat.id")
    );

    public AuctionRepository(DatabaseManager databaseManager) {
        super(databaseManager, Auction.class, "auctions", "a");
        addFields(FIELDS);
        addJoins(JOINS);
    }

    public Collection<Auction> findAllActive(Collection<FilterCondition> filterConditions) {
        return findByArchivedStatus(false, filterConditions);
    }

    public Collection<Auction> findAllArchived(Collection<FilterCondition> filterConditions) {
        return findByArchivedStatus(true, filterConditions);
    }

    private Collection<Auction> findByArchivedStatus(boolean isArchived, Collection<FilterCondition> filterConditions) {
        Collection<FilterCondition> allConditions = new ArrayList<>(filterConditions);
        allConditions.add(new EqualFilterCondition<>("archived", isArchived));
        return findAll(allConditions);
    }

    public Map<Long, List<Auction>> findArchivedForActiveAuctions() {
        Map<Long, List<Auction>> archiveMap = new HashMap<>();

        findMany(
                List.of(new EqualFilterCondition<>("act_a.archived", false)),
                List.of(),
                List.of(new InternalQueryField<>("act_a", "id", "active_id")),
                List.of(new QueryInnerJoin("auctions", "act_a",
                        "act_a.trading_item_id = a.trading_item_id " +
                                "AND act_a.condition_id = a.condition_id " +
                                "AND a.archived = 1")),
                (rs, e) -> {
                    long activeId = rs.getLong("active_id");
                    archiveMap.computeIfAbsent(activeId, k -> new ArrayList<>()).add(e);
                    return true;
                }
        );
        return archiveMap;
    }

    public Map<Pair<Long, Long>, List<Auction>> findArchivedForActiveCategories() {
        Map<Pair<Long, Long>, List<Auction>> archiveMap = new HashMap<>();

        findMany(
                List.of(new EqualFilterCondition<>("a.archived", true)),
                List.of(),
                List.of(),
                List.of(
                        new QueryInnerJoin("trading_items", "act_ti", "act_ti.category_id = ti.category_id"),
                        new QueryInnerJoin("auctions", "act_a",
                                "act_a.trading_item_id = act_ti.id " +
                                        "AND act_a.condition_id = a.condition_id " +
                                        "AND act_a.archived = 0")),
                (rs, e) -> {
                    archiveMap.computeIfAbsent(Pair.with(e.getTradingItemCategoryId(), e.getConditionId()),
                            k -> new ArrayList<>()).add(e);
                    return true;
                }
        );
        return archiveMap;
    }

    public Collection<Auction> findArchivedByItemAndCondition(Long tradingItemId, Long conditionId) {
        return findByItemAndCondition(tradingItemId, conditionId, true);
    }

    public Collection<Auction> findActiveByItemAndCondition(Long tradingItemId, Long conditionId) {
        return findByItemAndCondition(tradingItemId, conditionId, false);
    }

    private Collection<Auction> findByItemAndCondition(Long tradingItemId, Long conditionId, boolean isArchived) {
        return findMany(
                List.of(new EqualFilterCondition<>("a.archived", isArchived),
                        new EqualFilterCondition<>("a.trading_item_id", tradingItemId),
                        new EqualFilterCondition<>("a.condition_id", conditionId)),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
