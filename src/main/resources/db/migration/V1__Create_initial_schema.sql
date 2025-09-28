-- Initial schema creation for Philabid application
-- This creates only the basic settings table as requested

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