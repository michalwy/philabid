CREATE TABLE exchange_rates
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    rate_date       DATE           NOT NULL,
    source_currency VARCHAR(3)     NOT NULL,
    target_currency VARCHAR(3)     NOT NULL,
    rate            DECIMAL(16, 4) NOT NULL,
    UNIQUE (rate_date, source_currency, target_currency)
);