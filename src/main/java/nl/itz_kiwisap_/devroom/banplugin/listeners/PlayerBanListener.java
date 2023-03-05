package nl.itz_kiwisap_.devroom.banplugin.listeners;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.utils.Messages;
import nl.itz_kiwisap_.devroom.banplugin.utils.MojangFetcher;
import nl.itz_kiwisap_.devroom.banplugin.utils.TimeUtils;
import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@AllArgsConstructor
public final class PlayerBanListener implements Listener {

    private final BanPlugin plugin;

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Ban ban = this.plugin.getBanHandler().getBan(event.getUniqueId());
        if (ban == null) return;

        String formattedDuration = (ban.getDuration() != null) ? TimeUtils.formatDuration(ban.getDuration()) : Messages.PERMANENT.get();
        String formattedReason = (ban.getReason() != null) ? ban.getReason() : Messages.NO_REASON_SPECIFIED.get();

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.PLAYER_BAN_KICK_MESSAGE.get(
                PlaceholderResolver.builder()
                        .placeholder("duration", formattedDuration)
                        .placeholder("reason", formattedReason)
                        .placeholder("banned_by", MojangFetcher.getName(ban.getBannedByUuid()))
                        .build()
        ));
    }
}