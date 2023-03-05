package nl.itz_kiwisap_.devroom.banplugin.database.query;

import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public abstract class SQLUpdateQuery {

    public abstract String getQuery();

    public abstract List<Object> getParams();

    public void execute(@NotNull SQLDatabase database) {
        try {
            List<Object> params = this.getParams();
            database.execute(this.getQuery(), (params == null) ? new Object[]{} : params.toArray());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}