-- Initial schema creation for Philabid application
-- This creates the basic tables for stamp auction bidding assistance

-- Auction houses table
CREATE TABLE auction_houses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    website TEXT,
    contact_email TEXT,
    contact_phone TEXT,
    address TEXT,
    country TEXT,
    currency TEXT DEFAULT 'USD',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Auctions table
CREATE TABLE auctions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_house_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    auction_date DATETIME,
    end_date DATETIME,
    status TEXT DEFAULT 'upcoming' CHECK (status IN ('upcoming', 'active', 'ended', 'cancelled')),
    catalog_url TEXT,
    results_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_house_id) REFERENCES auction_houses(id)
);

-- Stamp catalog table - for stamp identification and valuation
CREATE TABLE stamp_catalog (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    country TEXT NOT NULL,
    year INTEGER,
    scott_number TEXT,
    michel_number TEXT,
    description TEXT NOT NULL,
    denomination TEXT,
    color TEXT,
    perforation TEXT,
    watermark TEXT,
    catalog_value_cents INTEGER, -- stored in cents/smallest currency unit
    currency TEXT DEFAULT 'USD',
    image_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Auction lots table
CREATE TABLE auction_lots (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_id INTEGER NOT NULL,
    lot_number TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    estimate_low_cents INTEGER,
    estimate_high_cents INTEGER,
    starting_bid_cents INTEGER,
    currency TEXT DEFAULT 'USD',
    stamp_catalog_id INTEGER,
    image_url TEXT,
    condition_description TEXT,
    lot_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auctions(id),
    FOREIGN KEY (stamp_catalog_id) REFERENCES stamp_catalog(id),
    UNIQUE(auction_id, lot_number)
);

-- User bids table
CREATE TABLE user_bids (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_lot_id INTEGER NOT NULL,
    bid_amount_cents INTEGER NOT NULL,
    currency TEXT DEFAULT 'USD',
    max_bid_cents INTEGER, -- for automatic bidding
    bid_status TEXT DEFAULT 'pending' CHECK (bid_status IN ('pending', 'placed', 'winning', 'outbid', 'won', 'lost')),
    notes TEXT,
    bid_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_lot_id) REFERENCES auction_lots(id)
);

-- Watchlist table - for items user wants to monitor
CREATE TABLE watchlist (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_lot_id INTEGER NOT NULL,
    notes TEXT,
    alert_price_cents INTEGER, -- alert when estimate or current bid reaches this
    currency TEXT DEFAULT 'USD',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_lot_id) REFERENCES auction_lots(id),
    UNIQUE(auction_lot_id)
);

-- Application settings table
CREATE TABLE app_settings (
    key TEXT PRIMARY KEY,
    value TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insert some default settings
INSERT INTO app_settings (key, value) VALUES ('default_currency', 'USD');
INSERT INTO app_settings (key, value) VALUES ('locale', 'en');
INSERT INTO app_settings (key, value) VALUES ('theme', 'default');
INSERT INTO app_settings (key, value) VALUES ('auto_refresh_interval', '30');

-- Create indexes for better performance
CREATE INDEX idx_auctions_date ON auctions(auction_date);
CREATE INDEX idx_auctions_status ON auctions(status);
CREATE INDEX idx_auction_lots_auction_id ON auction_lots(auction_id);
CREATE INDEX idx_stamp_catalog_country ON stamp_catalog(country);
CREATE INDEX idx_stamp_catalog_year ON stamp_catalog(year);
CREATE INDEX idx_user_bids_auction_lot_id ON user_bids(auction_lot_id);
CREATE INDEX idx_user_bids_status ON user_bids(bid_status);
CREATE INDEX idx_watchlist_auction_lot_id ON watchlist(auction_lot_id);