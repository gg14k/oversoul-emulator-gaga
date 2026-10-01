package org.gaga.oversoul.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.gaga.oversoul.dto.config.DatabaseDTO; // Ajuste para o pacote do seu DTO
import org.gaga.oversoul.utils.GameLogger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.EnumMap;
import java.util.Map;

public final class DatabaseConnectionHandler {

    private static final Map<DatabaseKey, HikariDataSource> DS = new EnumMap<>(DatabaseKey.class);
    private static final Map<DatabaseKey, DSLContext> DSL_CONTEXTS = new EnumMap<>(DatabaseKey.class);

    private DatabaseConnectionHandler() {}

    /**
     * Initialize all databases
     */
    public static void init(DatabaseDTO cfg) {
        GameLogger.info("Initializing database connections...");

        initOne(DatabaseKey.GAME, cfg.game);
    }

    /**
     * Get DSLContext by database key
     */
    public static DSLContext dsl(DatabaseKey key) {
        return DSL_CONTEXTS.get(key);
    }

    private static void initOne(DatabaseKey key, DatabaseDTO.Db db) {
        var jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&allowPublicKeyRetrieval=%s",
                db.host, db.port, db.name, db.ssl, db.allowPublicKeyRetrieval
        );

        HikariDataSource ds = getHikariDataSource(db, jdbcUrl);

        DS.put(key, ds);
        DSL_CONTEXTS.put(key, DSL.using(ds, SQLDialect.MYSQL));

        GameLogger.info("Database '{}' connected successfully.", key);
        GameLogger.debug("DB Details -> host={}:{} | pool={} | minIdle={}",
                db.host, db.port, db.maxPoolSize, db.minimumIdle
        );
    }

    private static HikariDataSource getHikariDataSource(DatabaseDTO.Db db, String jdbcUrl) {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(jdbcUrl);
        hc.setUsername(db.user);
        hc.setPassword(db.pass);

        hc.setDriverClassName("com.mysql.cj.jdbc.Driver");

        hc.setMaximumPoolSize(db.maxPoolSize);
        hc.setMinimumIdle(db.minimumIdle);
        hc.setIdleTimeout(db.idleTimeout);
        hc.setMaxLifetime(db.maxLifetime);
        hc.setConnectionTimeout(db.connectionTimeout);

        if (db.leakDetectionThreshold > 0) {
            hc.setLeakDetectionThreshold(db.leakDetectionThreshold);
        }

        hc.addDataSourceProperty("cachePrepStmts", db.cachePrepStmts);
        hc.addDataSourceProperty("prepStmtCacheSize", db.prepStmtCacheSize);
        hc.addDataSourceProperty("prepStmtCacheSqlLimit", db.prepStmtCacheSqlLimit);

        return new HikariDataSource(hc);
    }

    /**
     * Shutdown all pools gracefully
     */
    public static void shutdown() {
        GameLogger.info("Shutting down database connection pools...");
        for (var ds : DS.values()) {
            if (ds != null) {
                ds.close();
            }
        }
    }
}
