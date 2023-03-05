package nl.itz_kiwisap_.devroom.banplugin.handler;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public final class Ban {

    public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final int id;
    private final UUID bannedUuid;
    private final UUID bannedByUuid;
    private final Instant bannedAt;
    private final Duration duration;
    private final String reason;
    private final Instant expiresAt;

    private boolean removed;
    private Instant removedAt;
    private UUID removedByUuid;

    public Ban(int id, UUID bannedUuid, UUID bannedByUuid, Instant bannedAt, Duration duration, String reason, boolean removed, Instant removedAt, UUID removedByUuid) {
        this.id = id;
        this.bannedUuid = bannedUuid;
        this.bannedByUuid = bannedByUuid;
        this.bannedAt = bannedAt;
        this.duration = duration;
        this.reason = reason;
        this.removed = removed;
        this.removedAt = removedAt;
        this.removedByUuid = removedByUuid;

        this.expiresAt = (this.duration == null) ? null : this.bannedAt.plus(this.duration);
    }

    public Ban(int id, UUID bannedUuid, UUID bannedByUuid, Instant bannedAt, Duration duration, String reason) {
        this(id, bannedUuid, bannedByUuid, bannedAt, duration, reason, false, null, null);
    }

    public Ban(ResultSet result) throws SQLException {
        this(fromResultSet(result));
    }

    private Ban(Ban ban) {
        this(
                ban.getId(),
                ban.getBannedUuid(),
                ban.getBannedByUuid(),
                ban.getBannedAt(),
                ban.getDuration(),
                ban.getReason(),
                ban.isRemoved(),
                ban.getRemovedAt(),
                ban.getRemovedByUuid()
        );
    }

    public boolean isActive() {
        return !this.isExpired() && !this.isRemoved();
    }

    public boolean isPermanent() {
        return this.duration == null || this.duration.isZero();
    }

    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(Instant.now());
    }

    public boolean isBannedByConsole() {
        return this.bannedByUuid.equals(CONSOLE_UUID);
    }

    private static Ban fromResultSet(ResultSet result) throws SQLException {
        int id = result.getInt("id");
        UUID bannedUuid = UUID.fromString(result.getString("banned_uuid"));
        UUID bannedByUuid = UUID.fromString(result.getString("banned_by_uuid"));
        Instant bannedAt = result.getTimestamp("banned_at").toInstant();
        long durationLong = result.getLong("duration");
        Duration duration = (result.wasNull()) ? null : Duration.ofMillis(durationLong);
        String reason = result.getString("reason");
        boolean removed = result.getBoolean("removed");
        Timestamp removedAtTimestamp = result.getTimestamp("removed_at");
        Instant removedAt = (result.wasNull()) ? null : removedAtTimestamp.toInstant();
        String removedByUuidString = result.getString("removed_by_uuid");
        UUID removedByUuid = (result.wasNull()) ? null : UUID.fromString(removedByUuidString);

        return new Ban(id, bannedUuid, bannedByUuid, bannedAt, duration, reason, removed, removedAt, removedByUuid);
    }
}