package org.gaga.oversoul.dto.config;

public class DatabaseDTO {
    public Db game;

    public static class Db {
        public String name;
        public String host;
        public String user;
        public String pass;
        public int port;

        public int maxPoolSize;
        public long connectionTimeout;
        public long leakDetectionThreshold;
        public int minimumIdle;
        public long idleTimeout;
        public long maxLifetime;

        public boolean cachePrepStmts;
        public int prepStmtCacheSize;
        public int prepStmtCacheSqlLimit;

        public boolean ssl;
        public boolean allowPublicKeyRetrieval;
    }
}
