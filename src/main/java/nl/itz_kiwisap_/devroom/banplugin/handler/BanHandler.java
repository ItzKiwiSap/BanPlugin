package nl.itz_kiwisap_.devroom.banplugin.handler;

import lombok.AccessLevel;
import lombok.Getter;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.database.query.queries.InsertBanQuery;
import nl.itz_kiwisap_.devroom.banplugin.database.query.queries.SelectAllBansQuery;
import nl.itz_kiwisap_.devroom.banplugin.database.query.queries.UpdateRemovedBanQuery;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public final class BanHandler {

    @Getter(AccessLevel.NONE)
    private final BanPlugin plugin;

    private final Map<UUID, Collection<Ban>> playerBans;

    public BanHandler(BanPlugin plugin) {
        this.plugin = plugin;

        this.playerBans = new SelectAllBansQuery().getObject(plugin.getDatabase());
    }

    public Ban getBan(UUID uuid) {
        Collection<Ban> bans = this.playerBans.get(uuid);
        if (bans == null) return null;

        return bans.stream()
                .filter(Ban::isActive)
                .findFirst()
                .orElse(null);
    }

    public void addBan(UUID bannedUuid, UUID bannedByUuid, Duration duration, String reason, Runnable completed) {
        CompletableFuture.runAsync(() -> {
            Ban ban = new InsertBanQuery(
                    bannedUuid,
                    bannedByUuid,
                    Instant.now(),
                    duration,
                    reason
            ).getObject(this.plugin.getDatabase());

            if (ban == null) {
                throw new RuntimeException("Failed to insert ban into database!");
            }

            this.playerBans.compute(ban.getBannedUuid(), (key, value) -> {
                if (value == null) value = new ArrayList<>();
                value.add(ban);
                return value;
            });

            completed.run();
        });
    }

    public void unban(Ban ban, UUID removedByUuid, Runnable completed) {
        CompletableFuture.runAsync(() -> {
            ban.setRemoved(true);
            ban.setRemovedAt(Instant.now());
            ban.setRemovedByUuid(removedByUuid);

            new UpdateRemovedBanQuery(ban).execute(this.plugin.getDatabase());

            completed.run();
        });
    }

    public Collection<Ban> getPlayerHistory(UUID uuid) {
        return this.playerBans.getOrDefault(uuid, new ArrayList<>());
    }
}