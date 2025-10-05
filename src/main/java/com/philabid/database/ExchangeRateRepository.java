package com.philabid.database;

import com.philabid.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ExchangeRateRepository {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateRepository.class);
    private final DatabaseManager databaseManager;

    public ExchangeRateRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Optional<ExchangeRate> findRate(LocalDate date, CurrencyUnit sourceCurrency, CurrencyUnit targetCurrency)
            throws SQLException {
        String sql = "SELECT * FROM exchange_rates WHERE rate_date = ? AND source_currency = ? AND target_currency = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            pstmt.setString(2, sourceCurrency.getCurrencyCode());
            pstmt.setString(3, targetCurrency.getCurrencyCode());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToExchangeRate(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void saveRate(ExchangeRate exchangeRate) throws SQLException {
        String sql =
                "INSERT OR IGNORE INTO exchange_rates(rate_date, source_currency, target_currency, rate) VALUES(?,?," +
                        "?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exchangeRate.getDate().toString());
            pstmt.setString(2, exchangeRate.getSourceCurrency().getCurrencyCode());
            pstmt.setString(3, exchangeRate.getTargetCurrency().getCurrencyCode());
            pstmt.setBigDecimal(4, exchangeRate.getRate());
            pstmt.executeUpdate();
            logger.debug("Saved exchange rate for {} from {} to {}: {}", exchangeRate.getDate(),
                    exchangeRate.getSourceCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate());
        }
    }

    private ExchangeRate mapRowToExchangeRate(ResultSet rs) throws SQLException {
        ExchangeRate rate = new ExchangeRate();
        rate.setId(rs.getLong("id"));
        rate.setDate(LocalDate.parse(rs.getString("rate_date")));
        rate.setSourceCurrency(rs.getString("source_currency"));
        rate.setTargetCurrency(rs.getString("target_currency"));
        rate.setRate(rs.getBigDecimal("rate"));
        return rate;
    }
}