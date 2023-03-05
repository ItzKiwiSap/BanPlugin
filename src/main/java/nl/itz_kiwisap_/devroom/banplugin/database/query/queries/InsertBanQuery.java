package nl.itz_kiwisap_.devroom.banplugin.database.query.queries;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.database.query.SQLSelectGeneratedKeyQuery;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public final class InsertBanQuery extends SQLSelectGeneratedKeyQuery<Integer, Ban> {

    private final UUID bannedUuid;
    private final UUID bannedByUuid;
    private final Instant bannedAt;
    private final Duration duration;
    private final String reason;

    @Override
    public String getQuery() {
        return "INSERT INTO `bans` (`banned_uuid`, `banned_by_uuid`, `banned_at`, `duration`, `reason`) VALUES (?, ?, ?, ?, ?);";
    }

    @Override
    public List<Object> getParams() {
        List<Object> list = new ArrayList<>();
        list.add(this.bannedUuid.toString());
        list.add(this.bannedByUuid.toString());
        list.add(new Timestamp(this.bannedAt.toEpochMilli()));
        list.add((this.duration == null) ? null : this.duration.toMillis());
        list.add(this.reason);
        return list;
    }

    @Override
    protected Ban getObject(ResultSet result, Integer generatedKey) {
        return new Ban(
                generatedKey,
                this.bannedUuid,
                this.bannedByUuid,
                this.bannedAt,
                this.duration,
                this.reason,
                false,
                null,
                null
        );
    }
}