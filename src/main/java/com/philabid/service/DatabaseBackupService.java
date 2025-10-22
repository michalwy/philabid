package com.philabid.service;

import com.philabid.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages the creation and rotation of database backups.
 */
public class DatabaseBackupService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupService.class);
    private static final int MAX_BACKUPS_TO_KEEP = 50;
    private static final DateTimeFormatter BACKUP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final DatabaseManager databaseManager;

    public DatabaseBackupService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Performs the database backup process: creates a new backup and cleans up old ones.
     */
    public void performBackup() {
        try {
            Path dbPath = databaseManager.getDatabasePath();
            if (dbPath == null || !Files.exists(dbPath)) {
                logger.warn("Database file does not exist. Skipping backup.");
                return;
            }
            
            Path backupDir = dbPath.getParent().resolve("backups");
            createBackupDirectory(backupDir);

            createNewBackup(dbPath, backupDir);
            cleanupOldBackups(backupDir);
        } catch (IOException e) {
            logger.error("An error occurred during the database backup process.", e);
        }
    }

    private void createBackupDirectory(Path backupDir) throws IOException {
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
            logger.info("Created backup directory at: {}", backupDir);
        }
    }

    private void createNewBackup(Path sourceFile, Path backupDir) throws IOException {
        String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMATTER);
        String backupFileName = "philabid_" + timestamp + ".db";
        Path destinationFile = backupDir.resolve(backupFileName);

        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Successfully created database backup: {}", destinationFile);
    }

    private void cleanupOldBackups(Path backupDir) throws IOException {
        List<Path> backups;
        try (Stream<Path> files = Files.list(backupDir)) {
            backups = files
                    .filter(p -> p.toString().endsWith(".db"))
                    .sorted(Comparator.comparing((Path p) -> p.getFileName().toString())
                            .reversed()) // Sort descending by name (newest first)
                    .toList();
        }

        if (backups.size() > MAX_BACKUPS_TO_KEEP) {
            logger.info("Found {} backups. Cleaning up the oldest ones...", backups.size());
            List<Path> backupsToDelete = backups.subList(MAX_BACKUPS_TO_KEEP, backups.size());
            for (Path backup : backupsToDelete) {
                try {
                    Files.delete(backup);
                    logger.debug("Deleted old backup: {}", backup);
                } catch (IOException e) {
                    logger.error("Failed to delete old backup file: {}", backup, e);
                }
            }
        }
    }
}