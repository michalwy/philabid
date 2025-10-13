package com.philabid.database.util;

import com.philabid.model.BaseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.function.BiConsumer;

public class QueryField<T extends BaseModel<T>, V> {
    private final String table;
    private final String fieldName;
    private final String alias;
    private final ResultSetValueAccessor<V> rsValueAccessor;
    private final BiConsumer<T, V> valueConsumer;
    private final StatementParamSetter<V> statementParamSetter;
    private EntityValueAccessor<T, V> entityValueAccessor;

    public QueryField(String table, String fieldName, String alias,
                      ResultSetValueAccessor<V> rsValueAccessor,
                      BiConsumer<T, V> valueConsumer, StatementParamSetter<V> statementParamSetter) {
        this.table = table;
        this.fieldName = fieldName;
        this.alias = alias;
        this.rsValueAccessor = rsValueAccessor;
        this.valueConsumer = valueConsumer;
        if (statementParamSetter != null) {
            this.statementParamSetter = statementParamSetter;
        } else {
            this.statementParamSetter = PreparedStatement::setObject;
        }
    }

    public QueryField(String fieldName, ResultSetValueAccessor<V> rsValueAccessor,
                      BiConsumer<T, V> valueConsumer, StatementParamSetter<V> statementParamSetter) {
        this(null, fieldName, null, rsValueAccessor, valueConsumer, statementParamSetter);
    }

    public QueryField(String table, String fieldName, ResultSetValueAccessor<V> rsValueAccessor,
                      BiConsumer<T, V> valueConsumer, StatementParamSetter<V> statementParamSetter) {
        this(table, fieldName, null, rsValueAccessor, valueConsumer, statementParamSetter);
    }

    public QueryField<T, V> withEntityValue(EntityValueAccessor<T, V> entityValueAccessor) {
        this.entityValueAccessor = entityValueAccessor;
        return this;
    }

    public boolean isInsertable() {
        return entityValueAccessor != null;
    }

    public boolean isUpdatable() {
        return entityValueAccessor != null;
    }

    public V getEntityValue(T entity) {
        return entityValueAccessor.supply(entity);
    }

    public String toSql() {
        String fullFieldName = table != null ? table + "." + fieldName : fieldName;
        return alias != null ? fullFieldName + " AS " + alias : fullFieldName;
    }

    private String getResultSetFieldName() {
        return alias != null ? alias : fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public void mapToEntity(ResultSet rs, T entity) {
        try {
            if (valueConsumer != null) {
                valueConsumer.accept(entity, rsValueAccessor.apply(rs, getResultSetFieldName()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void mapToStatementParam(PreparedStatement pstmt, int index, T entity) throws SQLException {
        V value = entityValueAccessor.supply(entity);
        if (value == null) {
            pstmt.setNull(index, Types.NULL);
        } else {
            statementParamSetter.accept(pstmt, index, value);
        }
    }

    @FunctionalInterface
    public interface EntityValueAccessor<T, V> {
        V supply(T entity);
    }

    @FunctionalInterface
    public interface StatementParamSetter<V> {
        void accept(PreparedStatement statement, int index, V value) throws SQLException;
    }
}
