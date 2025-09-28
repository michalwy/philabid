-- Create the catalogs table
CREATE TABLE catalogs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    issue_year INTEGER,
    currency_code TEXT,
    is_active INTEGER NOT NULL DEFAULT 1, -- 1 for true, 0 for false
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
