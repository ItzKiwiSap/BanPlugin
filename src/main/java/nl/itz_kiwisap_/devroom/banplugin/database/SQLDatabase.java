package nl.itz_kiwisap_.devroom.banplugin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SQLDatabase {

    private final HikariDataSource dataSource;

    public SQLDatabase(String hostname, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);

        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTestQuery("SELECT 1");

        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(2));

        config.setMaximumPoolSize(10);

        // Default properties
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");

        config.addDataSourceProperty("useSSL", false);
        config.addDataSourceProperty("verifyServerCertificate", false);
        config.addDataSourceProperty("allowMultiQueries", true);

        try {
            this.dataSource = new HikariDataSource(config);
            this.dataSource.getConnection();
            System.out.println("Successfully connected to MySQL: " + config.getJdbcUrl());
        } catch (SQLException exception) {
            throw new RuntimeException("Could not establish a connection with MySQL, SQLException: " + exception.getMessage());
        }
    }

    public void disconnect() {
        this.dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public CachedRowSet query(String query, Object... parameters) throws SQLException {
        try (Connection connection = this.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            this.replaceStatement(statement, Arrays.asList(parameters));

            try (ResultSet resultSet = statement.executeQuery()) {
                CachedRowSet resultCached = RowSetProvider.newFactory().createCachedRowSet();
                resultCached.populate(resultSet);

                return resultCached;
            }
        }
    }

    public void execute(String query, Object... parameters) throws SQLException {
        try (Connection connection = this.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            if (!Arrays.asList(parameters).isEmpty()) {
                this.replaceStatement(statement, Arrays.asList(parameters));
            }

            statement.execute();
        }
    }

    public void replaceStatement(PreparedStatement statement, List<Object> params) throws SQLException {
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
        }
    }
}