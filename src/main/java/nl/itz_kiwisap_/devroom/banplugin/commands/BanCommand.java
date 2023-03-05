package nl.itz_kiwisap_.devroom.banplugin.commands;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.utils.Messages;
import nl.itz_kiwisap_.devroom.banplugin.utils.TimeUtils;
import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public final class BanCommand implements CommandExecutor, TabCompleter {

    private final BanPlugin plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Messages.BAN_COMMAND_USAGE.get());
            return false;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND.get());
            return false;
        }

        if (args.length == 1) {
            return this.banPlayer(player, sender, null, null);
        }

        Duration duration = null;
        String reason;

        try {
            duration = TimeUtils.stringToDuration(args[1]);
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        } catch (Exception ignored) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        return this.banPlayer(player, sender, duration, reason);
    }

    private boolean banPlayer(Player bannedPlayer, CommandSender bannedBy, Duration duration, String reason) {
        Ban ban = this.plugin.getBanHandler().getBan(bannedPlayer.getUniqueId());
        if (ban != null) {
            bannedBy.sendMessage(Messages.PLAYER_ALREADY_BANNED.get());
            return false;
        }

        UUID bannedByUuid = (bannedBy instanceof Player player) ? player.getUniqueId() : Ban.CONSOLE_UUID;
        this.plugin.getBanHandler().addBan(bannedPlayer.getUniqueId(), bannedByUuid, duration, reason, () -> {
            String formattedDuration = (duration != null) ? TimeUtils.formatDuration(duration) : Messages.PERMANENT.get();
            String formattedReason = (reason != null) ? reason : Messages.NO_REASON_SPECIFIED.get();

            Bukkit.getScheduler().runTask(this.plugin, () -> {
                bannedPlayer.kickPlayer(Messages.PLAYER_BAN_KICK_MESSAGE.get(
                        PlaceholderResolver.builder()
                                .placeholder("duration", formattedDuration)
                                .placeholder("reason", formattedReason)
                                .placeholder("banned_by", bannedBy.getName())
                                .build()
                ));
            });

            bannedBy.sendMessage(Messages.PLAYER_BANNED.get(
                    PlaceholderResolver.builder()
                            .placeholder("player", bannedPlayer.getName())
                            .placeholder("duration", formattedDuration)
                            .placeholder("reason", formattedReason)
                            .build()
            ));
        });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || args.length != 0) return null;

        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }
}