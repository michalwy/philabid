package com.philabid.database;

import com.philabid.database.util.CurrencyQueryField;
import com.philabid.database.util.EqualFilterCondition;
import com.philabid.database.util.QueryField;
import com.philabid.database.util.StringQueryField;
import com.philabid.model.AuctionHouse;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AuctionHouseRepository extends CrudRepository<AuctionHouse> {
    private static final Collection<QueryField<AuctionHouse, ?>> FIELDS = List.of(
            new StringQueryField<>("name", AuctionHouse::setName).withEntityValue(AuctionHouse::getName),
            new StringQueryField<>("website", AuctionHouse::setWebsite).withEntityValue(AuctionHouse::getWebsite),
            new StringQueryField<>("contact_email", AuctionHouse::setContactEmail).withEntityValue(
                    AuctionHouse::getContactEmail),
            new StringQueryField<>("contact_phone", AuctionHouse::setContactPhone).withEntityValue(
                    AuctionHouse::getContactPhone),
            new StringQueryField<>("address", AuctionHouse::setAddress).withEntityValue(AuctionHouse::getAddress),
            new StringQueryField<>("country", AuctionHouse::setCountry).withEntityValue(AuctionHouse::getCountry),
            new CurrencyQueryField<AuctionHouse>("currency", AuctionHouse::setCurrency).withEntityValue(
                    AuctionHouse::getCurrency)
    );

    public AuctionHouseRepository(DatabaseManager databaseManager) {
        super(databaseManager, AuctionHouse.class, "auction_houses");
        addFields(FIELDS);
    }

    public Optional<AuctionHouse> findByName(String name) throws SQLException {
        return findOne(List.of(new EqualFilterCondition<>("name", name)));
    }
}
