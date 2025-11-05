package com.philabid.database;

import com.philabid.database.util.EqualFilterCondition;
import com.philabid.database.util.QueryField;
import com.philabid.database.util.StringQueryField;
import com.philabid.model.Seller;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SellerRepository extends CrudRepository<Seller> {
    private static final Collection<QueryField<Seller, ?>> FIELDS = List.of(
            new StringQueryField<>("name", Seller::setName).withEntityValue(Seller::getName),
            new StringQueryField<>("full_name", Seller::setFullName).withEntityValue(Seller::getFullName),
            new StringQueryField<>("contact_email", Seller::setContactEmail).withEntityValue(
                    Seller::getContactEmail),
            new StringQueryField<>("contact_phone", Seller::setContactPhone).withEntityValue(
                    Seller::getContactPhone)
    );

    public SellerRepository(DatabaseManager databaseManager) {
        super(databaseManager, Seller.class, "sellers");
        addFields(FIELDS);
    }

    public Optional<Seller> findByName(String name) throws SQLException {
        return findOne(List.of(new EqualFilterCondition<>("name", name)));
    }
}
