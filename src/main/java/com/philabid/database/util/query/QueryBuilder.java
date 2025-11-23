package com.philabid.database.util.query;

import com.philabid.database.util.FilterCondition;
import com.philabid.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder<T extends BaseModel<T>> {
    private static final Logger logger = LoggerFactory.getLogger(QueryBuilder.class);
    private final Collection<QueryField<T, ?>> fields = new ArrayList<>();
    private final Collection<FilterCondition> filterConditions = new ArrayList<>();
    private final Collection<QueryJoin> joins = new ArrayList<>();
    private final Collection<QueryOrder> orders = new ArrayList<>();
    private QueryType queryType;
    private String fromTable;
    private String fromAlias;

    public QueryBuilder<T> select() {
        return select(List.of());
    }

    public QueryBuilder<T> select(Collection<QueryField<T, ?>> fields) {
        queryType = QueryType.SELECT;
        this.fields.addAll(fields);
        return this;
    }

    public QueryBuilder<T> from(String tableName) {
        return from(tableName, null);
    }

    public QueryBuilder<T> from(String tableName, String tableAlias) {
        fromTable = tableName;
        fromAlias = tableAlias;
        return this;
    }

    public QueryBuilder<T> withFields(Collection<QueryField<T, ?>> fields) {
        this.fields.addAll(fields);
        return this;
    }

    public QueryBuilder<T> withField(QueryField<T, ?> field) {
        fields.add(field);
        return this;
    }

    public QueryBuilder<T> where(Collection<FilterCondition> filterConditions) {
        this.filterConditions.addAll(filterConditions);
        return this;
    }

    public QueryBuilder<T> where(FilterCondition filterCondition) {
        filterConditions.add(filterCondition);
        return this;
    }

    public QueryBuilder<T> join(QueryJoin join) {
        joins.add(join);
        return this;
    }

    public QueryBuilder<T> join(Collection<QueryJoin> joins) {
        this.joins.addAll(joins);
        return this;
    }

    public QueryBuilder<T> order(QueryOrder order) {
        orders.add(order);
        return this;
    }

    public QueryBuilder<T> order(Collection<QueryOrder> orders) {
        this.orders.addAll(orders);
        return this;
    }

    public QueryBuildResult<T> build() {
        Collection<String> fieldNames = fields.stream().map(QueryField::toSql).toList();

        String selectClause = queryType.getType() + " " + String.join(", ", fieldNames);
        String fromClause = "FROM " + fromTable + (fromAlias != null ? " AS " + fromAlias : "");
        List<Object> params = new ArrayList<>();

        String joinClause = joins.stream()
                .peek(j -> params.addAll(j.getParams()))
                .map(QueryJoin::getSqlText)
                .collect(Collectors.joining(" "));

        String whereClause = filterConditions.stream()
                .peek(fc -> params.addAll(fc.getSqlParams()))
                .map(fc -> "AND " + fc.getSqlText())
                .collect(Collectors.joining(" ", "WHERE 1=1 ", ""));

        String orderClause = orders.stream()
                .map(QueryOrder::getSqlText)
                .collect(Collectors.joining(" "));
        if (!orderClause.isBlank()) {
            orderClause = "ORDER BY " + orderClause;
        }

        String sql = String.join(" ", selectClause, fromClause, joinClause, whereClause, orderClause);

        logger.debug("SQL: {}", sql);
        logger.debug("Params: {}", params);

        return new QueryBuildResult<>(sql, params, fields);
    }

    public enum QueryType {
        SELECT("SELECT"),
        INSERT("INSERT"),
        UPDATE("UPDATE"),
        DELETE("DELETE");

        private final String type;

        QueryType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public record QueryBuildResult<T extends BaseModel<T>>(String sql, List<Object> params,
                                                           Collection<QueryField<T, ?>> fields) {
    }
}
