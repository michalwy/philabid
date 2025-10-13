package com.philabid.database;

import com.philabid.database.util.*;
import com.philabid.model.Auction;
import org.javatuples.Pair;

import java.sql.SQLException;
import java.util.*;

/**
 * Repository for managing Auction entities in the database.
 */
public class AuctionRepository extends CrudRepository<Auction> {
    private static final Collection<QueryField<Auction, ?>> FIELDS = List.of(
            new LongQueryField<>("a", "auction_house_id", Auction::setAuctionHouseId).withEntityValue(
                    Auction::getAuctionHouseId),
            new LongQueryField<>("a", "auction_item_id", Auction::setAuctionItemId).withEntityValue(
                    Auction::getAuctionItemId),
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
            new InternalQueryField<>("cv", "currency_code", "catalog_currency_code"),
            new MonetaryAmountQueryField<>("cv", "value", "catalog_value", "catalog_currency_code",
                    Auction::setRawCatalogValue),
            new StringQueryField<>("ai", "catalog_number", "auction_item_catalog_number",
                    Auction::setAuctionItemCatalogNumber),
            new LongQueryField<>("ai", "order_number", "auction_item_order_number", Auction::setAuctionItemOrder),
            new StringQueryField<>("catg", "name", "auction_item_category_name", Auction::setAuctionItemCategoryName),
            new StringQueryField<>("catg", "code", "auction_item_category_code", Auction::setAuctionItemCategoryCode),
            new LongQueryField<>("catg", "id", "auction_item_category_id", Auction::setAuctionItemCategoryId),
            new LongQueryField<>("cond", "id", "condition_id", Auction::setConditionId),
            new StringQueryField<>("cond", "name", "condition_name", Auction::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", Auction::setConditionCode),
            new BooleanQueryField<>("cat", "is_active", "catalog_is_active", Auction::setCatalogActive)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("auction_houses", "ah", "a.auction_house_id = ah.id"),
            new QueryLeftOuterJoin("auction_items", "ai", "a.auction_item_id = ai.id"),
            new QueryLeftOuterJoin("conditions", "cond", "a.condition_id = cond.id"),
            new QueryLeftOuterJoin("categories", "catg", "ai.category_id = catg.id"),
            new QueryLeftOuterJoin("catalog_values", "cv",
                    "cv.auction_item_id = a.auction_item_id AND cv.condition_id = a.condition_id"),
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

    public Map<Long, List<Auction>> findArchivedForActiveAuctions() throws SQLException {
        Map<Long, List<Auction>> archiveMap = new HashMap<>();

        findMany(
                List.of(new EqualFilterCondition<>("act_a.archived", false)),
                List.of(new InternalQueryField<>("act_a", "id", "active_id")),
                List.of(new QueryInnerJoin("auctions", "act_a",
                        "act_a.auction_item_id = a.auction_item_id " +
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

    public Map<Pair<Long, Long>, List<Auction>> findArchivedForActiveCategories() throws SQLException {
        Map<Pair<Long, Long>, List<Auction>> archiveMap = new HashMap<>();

        findMany(
                List.of(new EqualFilterCondition<>("a.archived", true)),
                List.of(),
                List.of(
                        new QueryInnerJoin("auction_items", "act_ai", "act_ai.category_id = ai.category_id"),
                        new QueryInnerJoin("auctions", "act_a",
                                "act_a.auction_item_id = act_ai.id " +
                                        "AND act_a.condition_id = a.condition_id " +
                                        "AND act_a.archived = 0")),
                (rs, e) -> {
                    archiveMap.computeIfAbsent(Pair.with(e.getAuctionItemCategoryId(), e.getConditionId()),
                            k -> new ArrayList<>()).add(e);
                    return true;
                }
        );
        return archiveMap;
    }
}
