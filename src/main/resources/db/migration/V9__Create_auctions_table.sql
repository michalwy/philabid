-- Create the auctions table
CREATE TABLE auctions
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_house_id INTEGER NOT NULL,
    auction_item_id  INTEGER NOT NULL,
    condition_id     INTEGER NOT NULL,
    lot_id           TEXT,
    url              TEXT,
    current_price    DECIMAL(16, 4),
    currency_code    VARCHAR(3),
    end_date         DATETIME,
    status           TEXT    NOT NULL DEFAULT 'ACTIVE', -- Can be 'ACTIVE', 'ENDED', etc.
    created_at       DATETIME         DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME         DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_house_id) REFERENCES auction_houses (id),
    FOREIGN KEY (auction_item_id) REFERENCES auction_items (id),
    FOREIGN KEY (condition_id) REFERENCES conditions (id)
);
