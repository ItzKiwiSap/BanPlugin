package nl.itz_kiwisap_.devroom.banplugin.commands;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.menus.HistoryMenu;
import nl.itz_kiwisap_.devroom.banplugin.utils.Messages;
import nl.itz_kiwisap_.devroom.banplugin.utils.MojangFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public final class HistoryCommand implements CommandExecutor, TabCompleter {

    private final BanPlugin plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.ONLY_PLAYERS_CAN_EXECUTE_COMMAND.get());
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Messages.HISTORY_COMMAND_USAGE.get());
            return false;
        }

        String playerName = args[0];
        if (!this.isValidMinecraftName(playerName)) {
            player.sendMessage(Messages.INVALID_PLAYER_NAME.get());
            return false;
        }

        CompletableFuture.runAsync(() -> {
            UUID uuid = MojangFetcher.getUUID(playerName);
            if (uuid == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND.get());
                return;
            }

            this.plugin.getOdalitaMenus().openMenu(new HistoryMenu(this.plugin, uuid), player);
        });

        return true;
    }

    private boolean isValidMinecraftName(String name) {
        return name.matches("^\\w{3,16}$");
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