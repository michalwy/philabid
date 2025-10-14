package com.philabid.database;

import com.philabid.database.util.*;
import com.philabid.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CrudRepository<T extends BaseModel<T>> {
    private static final Logger logger = LoggerFactory.getLogger(CrudRepository.class);

    protected final DatabaseManager databaseManager;
    protected final Class<T> entityClass;

    private final String tableName;
    private final String tableAlias;

    private final Collection<QueryField<T, ?>> fields = new ArrayList<>();
    private final Collection<QueryJoin> joins = new ArrayList<>();

    public CrudRepository(DatabaseManager databaseManager, Class<T> entityClass, String tableName) {
        this(databaseManager, entityClass, tableName, null);
    }

    public CrudRepository(DatabaseManager databaseManager, Class<T> entityClass, String tableName, String tableAlias) {
        this(databaseManager, entityClass, tableName, tableAlias, true);
    }

    public CrudRepository(DatabaseManager databaseManager, Class<T> entityClass, String tableName, String tableAlias,
                          boolean includeCommonFields) {
        this.databaseManager = databaseManager;
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.tableAlias = tableAlias;

        String commonFieldsTable = tableAlias != null ? tableAlias : tableName;

        if (includeCommonFields) {
            fields.addAll(List.of(
                    new LongQueryField<>(commonFieldsTable, "id", "id", BaseModel<T>::setId),
                    new TimestampQueryField<>(commonFieldsTable, "created_at", "created_at", BaseModel<T>::setCreatedAt)
                            .withEntityValue(BaseModel::getCreatedAt),
                    new TimestampQueryField<>(commonFieldsTable, "updated_at", "updated_at", BaseModel<T>::setUpdatedAt)
                            .withEntityValue(BaseModel::getUpdatedAt)
            ));
        }
    }

    public void addFields(Collection<QueryField<T, ?>> fields) {
        this.fields.addAll(fields);
    }

    public void addJoins(Collection<QueryJoin> joins) {
        this.joins.addAll(joins);
    }

    public T create() {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error("Could not create entity class {}.", entityClass.getName(), e);
        }
        return null;
    }

    public final Optional<T> save(T entity) {
        try {
            if (entity.getId() == null) {
                return Optional.of(insert(entity));
            } else {
                return Optional.of(update(entity));
            }
        } catch (SQLException e) {
            logger.error("Error saving {}: {}", entityClass.getName(), entity, e);
            return Optional.empty();
        }
    }

    public final boolean delete(Long id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting {} with ID: {}", entityClass.getName(), id, e);
            return false;
        }
    }

    protected T insert(T entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        List<QueryField<T, ?>> effectiveFields = fields.stream()
                .filter(QueryField::isInsertable)
                .toList();

        String fields = effectiveFields.stream()
                .map(QueryField::getFieldName)
                .collect(Collectors.joining(", "));

        String valuesPlaceholders = String.join(", ", Collections.nCopies(effectiveFields.size(), "?"));

        String sql = "INSERT INTO " + tableName + " (" + fields + ") VALUES(" + valuesPlaceholders + ")";

        logger.debug("INSERT: {}", sql);

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < effectiveFields.size(); ++i) {
                effectiveFields.get(i).mapToStatementParam(pstmt, i + 1, entity);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating " + entityClass.getName() + " failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return entity;
                } else {
                    throw new SQLException("Creating " + entityClass.getName() + " failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting {}: {}", entityClass.getName(), entity.getDisplayName(), e);
            throw new RuntimeException(e);
        }
    }

    protected T update(T entity) throws SQLException {
        entity.setUpdatedAt(LocalDateTime.now());

        List<QueryField<T, ?>> effectiveFields = fields.stream()
                .filter(QueryField::isUpdatable)
                .toList();

        String fields = effectiveFields.stream()
                .map(qf -> qf.getFieldName() + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + tableName + " SET " + fields + " WHERE id = ?";

        logger.debug("UPDATE: {}", sql);

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < effectiveFields.size(); ++i) {
                effectiveFields.get(i).mapToStatementParam(pstmt, i + 1, entity);
            }
            pstmt.setLong(effectiveFields.size() + 1, entity.getId());

            pstmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            logger.error("Error updating {}: {}", entityClass.getName(), entity.getDisplayName(), e);
            throw new RuntimeException(e);
        }
    }

    public Collection<T> findAll(Collection<FilterCondition> filterConditions) {
        return findMany(filterConditions, List.of(), List.of());
    }

    public void findAll(Collection<FilterCondition> filterConditions, Consumer<T> consumer) {
        findMany(filterConditions, List.of(), List.of(), (rs, e) -> {
            consumer.accept(e);
            return true;
        });
    }

    private T mapResultSetToEntity(ResultSet rs, Collection<QueryField<T, ?>> fields) {
        T entity = create();

        fields.forEach(field -> field.mapToEntity(rs, entity));

        return entity;
    }

    public Optional<T> findById(Long id) {
        return findOne(List.of(new EqualFilterCondition<>((tableAlias != null ? tableAlias : tableName) + ".id", id)));
    }

    public Collection<T> findMany(Collection<FilterCondition> filterConditions,
                                  Collection<QueryField<T, ?>> additionalFields,
                                  Collection<QueryJoin> additionalJoins) {
        List<T> entities = new ArrayList<>();
        findMany(filterConditions, additionalFields, additionalJoins, (rs, e) -> {
            entities.add(e);
            return true;
        });
        return entities;
    }

    public void findMany(Collection<FilterCondition> filterConditions,
                         Collection<QueryField<T, ?>> additionalFields,
                         Collection<QueryJoin> additionalJoins, EntityConsumer<T> consumer) {
        doFindQuery(filterConditions, additionalFields, additionalJoins, consumer);
    }

    public Optional<T> findOne(Collection<FilterCondition> filterConditions) {
        List<T> entities = new ArrayList<>();
        doFindQuery(filterConditions, List.of(), List.of(), (rs, e) -> {
            entities.add(e);
            return false;
        });
        return entities.isEmpty() ? Optional.empty() : Optional.of(entities.getFirst());
    }

    private void doFindQuery(Collection<FilterCondition> filterConditions,
                             Collection<QueryField<T, ?>> additionalFields, Collection<QueryJoin> additionalJoins,
                             EntityConsumer<T> consumer) {
        QueryBuilder.QueryBuildResult<T> query = new QueryBuilder<T>()
                .select(fields)
                .withFields(additionalFields)
                .from(tableName, tableAlias)
                .join(joins)
                .join(additionalJoins)
                .where(filterConditions)
                .build();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.sql())
        ) {
            for (int i = 0; i < query.params().size(); i++) {
                stmt.setObject(i + 1, query.params().get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (true) {
                    if (!rs.next() || !consumer.accept(rs, mapResultSetToEntity(rs, query.fields()))) break;
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving entities {}", entityClass.getName(), e);
        }
    }

    @FunctionalInterface
    public interface EntityConsumer<T> {
        boolean accept(ResultSet rs, T entity) throws SQLException;
    }
}
