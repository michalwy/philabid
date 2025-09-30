-- Create the catalog_values table
CREATE TABLE catalog_values
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_item_id INTEGER        NOT NULL,
    condition_id    INTEGER        NOT NULL,
    catalog_id      INTEGER        NOT NULL,
    value           DECIMAL(19, 4) NOT NULL,
    currency_code   VARCHAR(3)     NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_item_id) REFERENCES auction_items (id),
    FOREIGN KEY (condition_id) REFERENCES conditions (id),
    FOREIGN KEY (catalog_id) REFERENCES catalogs (id)
);
