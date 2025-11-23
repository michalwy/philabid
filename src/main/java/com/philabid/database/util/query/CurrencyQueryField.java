package com.philabid.database.util.query;

import com.philabid.model.BaseModel;
import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.function.BiConsumer;

public class CurrencyQueryField<T extends BaseModel<T>> extends QueryField<T, CurrencyUnit> {
    public CurrencyQueryField(String table, String fieldName, String alias,
                              BiConsumer<T, CurrencyUnit> valueConsumer) {
        super(table, fieldName, alias, (r, name) -> Monetary.getCurrency(r.getString(name)), valueConsumer,
                (stmt, index, value) -> {
                    stmt.setString(index, value.getCurrencyCode());
                });
    }

    public CurrencyQueryField(String fieldName, BiConsumer<T, CurrencyUnit> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }

    public CurrencyQueryField(String table, String fieldName, BiConsumer<T, CurrencyUnit> valueConsumer) {
        this(table, fieldName, null, valueConsumer);
    }

    public QueryField<T, CurrencyUnit> withMultiCurrencyEntityValue(
            EntityValueAccessor<T, MultiCurrencyMonetaryAmount> entityValueAccessor) {
        return withEntityValue(entity -> {
            MultiCurrencyMonetaryAmount amount = entityValueAccessor.supply(entity);
            if (amount != null) {
                return amount.originalAmount().getCurrency();
            } else {
                return null;
            }
        });
    }
}
