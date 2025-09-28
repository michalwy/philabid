package com.philabid.database;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages SQLite database connections and migrations for the Philabid application.
 * Handles database initialization, schema migrations using Flyway, and connection management.
 */
public class DatabaseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DATABASE_FILE = "philabid.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_FILE;
    
    private Connection connection;
    
    /**
     * Initializes the database, creating the file if it doesn't exist and running migrations.
     */
    public void initialize() throws SQLException {
        logger.info("Initializing database at: {}", DATABASE_FILE);
        
        try {
            // Ensure database directory exists
            Path dbPath = Paths.get(DATABASE_FILE).getParent();
            if (dbPath != null && !Files.exists(dbPath)) {
                Files.createDirectories(dbPath);
            }
            
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            connection = DriverManager.getConnection(DATABASE_URL);
            connection.setAutoCommit(true);
            
            // Run Flyway migrations
            runMigrations();
            
            logger.info("Database initialization completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new SQLException("Database initialization failed", e);
        }
    }
    
    /**
     * Runs database migrations using Flyway.
     */
    private void runMigrations() {
        logger.info("Running database migrations...");
        
        Flyway flyway = Flyway.configure()
            .dataSource(DATABASE_URL, null, null)
            .locations("classpath:db/migration")
            .load();
        
        flyway.migrate();
        
        logger.info("Database migrations completed successfully");
    }
    
    /**
     * Gets a database connection.
     * 
     * @return active database connection
     * @throws SQLException if connection is not available
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);
        }
        return connection;
    }
    
    /**
     * Shuts down the database connection.
     */
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.warn("Error closing database connection", e);
            }
        }
    }
}