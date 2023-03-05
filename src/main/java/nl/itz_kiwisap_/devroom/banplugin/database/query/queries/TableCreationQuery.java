package nl.itz_kiwisap_.devroom.banplugin.database.query.queries;

import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;

import java.sql.SQLException;

public final class TableCreationQuery {

    public void execute(SQLDatabase database) throws SQLException {
        database.execute("CREATE TABLE IF NOT EXISTS `bans` ("
                + "`id` INT NOT NULL AUTO_INCREMENT,"
                + "`banned_uuid` VARCHAR(36) NOT NULL,"
                + "`banned_by_uuid` VARCHAR(36) NOT NULL,"
                + "`banned_at` TIMESTAMP NOT NULL,"
                + "`duration` BIGINT DEFAULT NULL,"
                + "`reason` VARCHAR(255) DEFAULT NULL,"
                + "`removed` BOOLEAN NOT NULL DEFAULT 0,"
                + "`removed_at` TIMESTAMP NULL DEFAULT NULL,"
                + "`removed_by_uuid` VARCHAR(36) NULL DEFAULT NULL,"
                + "PRIMARY KEY (`id`)"
                + ");");
    }
}