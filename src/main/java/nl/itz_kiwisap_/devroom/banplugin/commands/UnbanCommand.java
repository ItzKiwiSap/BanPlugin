package nl.itz_kiwisap_.devroom.banplugin.commands;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.utils.Messages;
import nl.itz_kiwisap_.devroom.banplugin.utils.MojangFetcher;
import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public final class UnbanCommand implements CommandExecutor {

    private final BanPlugin plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Messages.UNBAN_COMMAND_USAGE.get());
            return false;
        }

        String playerName = args[0];
        if (!this.isValidMinecraftName(playerName)) {
            sender.sendMessage(Messages.INVALID_PLAYER_NAME.get());
            return false;
        }

        CompletableFuture.runAsync(() -> {
            UUID uuid = MojangFetcher.getUUID(playerName);
            if (uuid == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND.get());
                return;
            }

            Ban ban = this.plugin.getBanHandler().getBan(uuid);
            if (ban == null) {
                sender.sendMessage(Messages.PLAYER_NOT_BANNED.get());
                return;
            }

            this.plugin.getBanHandler().unban(ban, (sender instanceof Player player) ? player.getUniqueId() : Ban.CONSOLE_UUID, () -> {
                sender.sendMessage(Messages.PLAYER_UNBANNED.get(
                        PlaceholderResolver.builder()
                                .placeholder("player", MojangFetcher.getName(uuid))
                                .build()
                ));
            });
        });

        return true;
    }

    private boolean isValidMinecraftName(String name) {
        return name.matches("^\\w{3,16}$");
    }
}