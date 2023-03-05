package nl.itz_kiwisap_.devroom.banplugin.database.query.queries;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.database.query.SQLSelectQuery;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

@AllArgsConstructor
public final class SelectAllBansQuery extends SQLSelectQuery<Map<UUID, Collection<Ban>>> {

    @Override
    public String getQuery() {
        return "SELECT * FROM `bans`;";
    }

    @Override
    public List<Object> getParams() {
        return List.of();
    }

    @Override
    public Map<UUID, Collection<Ban>> getObject(SQLDatabase database) {
        try (CachedRowSet result = this.execute(database)) {
            if (result == null) return new HashMap<>();

            Map<UUID, Collection<Ban>> bans = new HashMap<>();
            while (result.next()) {
                Ban ban = new Ban(result);
                bans.compute(ban.getBannedUuid(), (key, value) -> {
                    if (value == null) value = new ArrayList<>();
                    value.add(ban);
                    return value;
                });
            }

            return bans;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return new HashMap<>();
    }
}