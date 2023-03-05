package nl.itz_kiwisap_.devroom.banplugin.database.query;

import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;

import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.Collections;
import java.util.List;

public abstract class SQLSelectGeneratedKeyQuery<T, O> {

    public abstract String getQuery();

    public List<Object> getParams() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public final O getObject(SQLDatabase database) {
        try (Connection connection = database.getConnection(); PreparedStatement statement = connection.prepareStatement(this.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
            List<Object> params = this.getParams();
            if (params != null && !params.isEmpty()) {
                database.replaceStatement(statement, params);
            }

            statement.execute();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result == null) return null;

                if (result.next()) {
                    Class<T> generatedKeyClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                    if (generatedKeyClass == null) return null;

                    T generatedKey = result.getObject("GENERATED_KEY", generatedKeyClass);
                    if (generatedKey == null) return null;

                    return this.getObject(result, generatedKey);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    protected abstract O getObject(ResultSet result, T generatedKey);
}