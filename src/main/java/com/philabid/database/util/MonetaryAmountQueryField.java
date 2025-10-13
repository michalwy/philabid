package com.philabid.database.util;

import com.philabid.model.BaseModel;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.function.BiConsumer;

public class MonetaryAmountQueryField<T extends BaseModel<T>> extends QueryField<T, MonetaryAmount> {
    public MonetaryAmountQueryField(String table, String fieldName, String alias, String currencyCodeField,
                                    BiConsumer<T, MonetaryAmount> valueConsumer) {
        super(table, fieldName, alias, (r, name) -> {
            BigDecimal amount = r.getBigDecimal(name);
            String currencyCode = r.getString(currencyCodeField);
            if (amount != null && currencyCode != null) {
                return Money.of(amount, currencyCode);
            } else {
                return null;
            }
        }, valueConsumer, (stmt, index, value) -> {
            stmt.setBigDecimal(index, value.getNumber().numberValue(BigDecimal.class));
        });
    }

    public MonetaryAmountQueryField(String table, String fieldName, String currencyCodeField,
                                    BiConsumer<T, MonetaryAmount> valueConsumer) {
        this(table, fieldName, null, currencyCodeField, valueConsumer);
    }

    public QueryField<T, MonetaryAmount> withMultiCurrencyEntityValue(
            EntityValueAccessor<T, MultiCurrencyMonetaryAmount> entityValueAccessor) {
        return withEntityValue(entity -> {
            MultiCurrencyMonetaryAmount amount = entityValueAccessor.supply(entity);
            if (amount != null) {
                return amount.originalAmount();
            } else {
                return null;
            }
        });
    }
}
