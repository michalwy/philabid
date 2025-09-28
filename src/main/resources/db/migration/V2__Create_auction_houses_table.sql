-- Create the auction_houses table
CREATE TABLE auction_houses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    website TEXT,
    contact_email TEXT,
    contact_phone TEXT,
    address TEXT,
    country TEXT,
    currency TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
