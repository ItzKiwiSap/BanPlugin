package nl.itz_kiwisap_.devroom.banplugin.database.query;

import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;

import java.sql.SQLException;
import java.util.List;

public abstract class SQLInsertQuery {

    public abstract String getQuery();

    public abstract List<Object> getParams();

    public void execute(SQLDatabase database) {
        try {
            List<Object> params = this.getParams();
            database.execute(this.getQuery(), (params == null) ? new Object[]{} : params.toArray());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}