package com.philabid.database;

import com.philabid.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Currency entities in the database.
 */
public class CurrencyRepository {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRepository.class);
    private final DatabaseManager databaseManager;

    public CurrencyRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Retrieves all currencies from the database.
     *
     * @return A list of all currencies.
     */
    public List<Currency> findAll() throws SQLException {
        String sql = "SELECT * FROM currencies ORDER BY name";
        List<Currency> currencies = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                currencies.add(mapRowToCurrency(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all currencies", e);
            throw e;
        }
        return currencies;
    }

    private Currency mapRowToCurrency(ResultSet rs) throws SQLException {
        return new Currency(
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("symbol")
        );
    }
}
