package nl.itz_kiwisap_.devroom.banplugin.database.query.queries;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.database.query.SQLUpdateQuery;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
public final class UpdateRemovedBanQuery extends SQLUpdateQuery {

    private final Ban ban;

    @Override
    public String getQuery() {
        return "UPDATE `bans` SET `removed`=?, `removed_at`=?, `removed_by_uuid`=? WHERE `id`=?;";
    }

    @Override
    public List<Object> getParams() {
        return List.of(
                this.ban.isRemoved(),
                new Timestamp(this.ban.getRemovedAt().toEpochMilli()),
                this.ban.getRemovedByUuid().toString(),
                this.ban.getId()
        );
    }
}