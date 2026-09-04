package com.systemhealthmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private Connection connection;;
    private final String dbPath;
    private DatabaseManager() {
        // Store database in user's home directory under .systemhealthmonitor
        String appDataDir = System.getProperty("user.home") + File.separator + ".systemhealthmonitor";
        this.dbPath = appDataDir + File.separator + "health_data.db";
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }


    public void initialize() {
        try {
            logger.info("Initializing database at: {}", dbPath);

            // Ensure app data directory exists
            File appDataDir = new File(dbPath).getParentFile();
            if (!appDataDir.exists()) {
                boolean created = appDataDir.mkdirs();
                if (created) {
                    logger.info("Created app data directory: {}", appDataDir.getAbsolutePath());
                }
            }

            // Connect to SQLite database (creates file if doesn't exist)
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);

            logger.info("Connected to SQLite database");


            createSchema();

            logger.info("Database initialization complete");

        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Table: scan_sessions
            // Stores metadata about each scan (timestamp, health score, result status)
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS scan_sessions (" +
                            "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    captured_at     TEXT    NOT NULL," +
                            "    health_score    INTEGER NOT NULL," +
                            "    scan_result     TEXT    NOT NULL" +
                            ")"
            );

            // Table: cpu_snapshots
            // Stores CPU information for each scan
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cpu_snapshots (" +
                            "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    session_id      INTEGER NOT NULL REFERENCES scan_sessions(id)," +
                            "    model           TEXT," +
                            "    core_count      INTEGER," +s
                            "    usage_percent   REAL" +
                            ")"
            );

            // Table: memory_snapshots
            // Stores memory (RAM) information for each scan
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS memory_snapshots (" +
                            "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    session_id      INTEGER NOT NULL REFERENCES scan_sessions(id)," +
                            "    total_bytes     INTEGER," +
                            "    used_bytes      INTEGER," +
                            "    free_bytes      INTEGER" +
                            ")"
            );

            // Table: disk_snapshots
            // Stores disk/storage information for each scan
            // One row per disk per scan
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS disk_snapshots (" +
                            "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    session_id      INTEGER NOT NULL REFERENCES scan_sessions(id)," +
                            "    mount_point     TEXT," +
                            "    total_bytes     INTEGER," +
                            "    used_bytes      INTEGER," +
                            "    free_bytes      INTEGER" +
                            ")"
            );

            // Table: health_warnings
            // Stores warnings/alerts generated during health analysis
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS health_warnings (" +
                            "    id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    session_id      INTEGER NOT NULL REFERENCES scan_sessions(id)," +
                            "    category        TEXT    NOT NULL," +
                            "    severity        TEXT    NOT NULL," +
                            "    message         TEXT    NOT NULL" +
                            ")"
            );

            logger.debug("Database schema created/verified");
        }
    }


    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not initialized. Call initialize() first.");
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}

