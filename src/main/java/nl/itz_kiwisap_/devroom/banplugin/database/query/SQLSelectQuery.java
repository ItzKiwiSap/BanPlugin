package nl.itz_kiwisap_.devroom.banplugin.database.query;

import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;
import org.jetbrains.annotations.NotNull;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.List;

public abstract class SQLSelectQuery<T> {

    public abstract String getQuery();

    public abstract List<Object> getParams();

    public abstract T getObject(SQLDatabase database);

    public final CachedRowSet execute(@NotNull SQLDatabase database) {
        try {
            List<Object> params = this.getParams();
            return database.query(this.getQuery(), (params == null) ? new Object[]{} : params.toArray());
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}