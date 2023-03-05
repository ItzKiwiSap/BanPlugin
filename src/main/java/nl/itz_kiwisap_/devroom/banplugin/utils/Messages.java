package nl.itz_kiwisap_.devroom.banplugin.utils;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
public enum Messages {

    BAN_COMMAND_USAGE("ban-command-usage", "&cUsage: /ban <player> [duration] [reason]"),
    UNBAN_COMMAND_USAGE("unban-command-usage", "&cUsage: /unban <player>"),
    HISTORY_COMMAND_USAGE("history-command-usage", "&cUsage: /history <player>"),

    PERMANENT("permanent", "permanent"),
    NO_REASON_SPECIFIED("no-reason-specified", "No reason specified."),

    ONLY_PLAYERS_CAN_EXECUTE_COMMAND("only-players-can-execute-command", "&cOnly players can execute this command."),
    PLAYER_NOT_FOUND("player-not-found", "&cPlayer not found."),
    INVALID_PLAYER_NAME("invalid-player-name", "&cInvalid player name."),

    INVALID_DURATION("invalid-duration", "&cInvalid duration."),

    PLAYER_ALREADY_BANNED("player-already-banned", "&cPlayer is already banned."),
    PLAYER_NOT_BANNED("player-not-banned", "&cPlayer is not banned."),
    PLAYER_BANNED("player-banned", "&aBanned %player% for a duration of %duration% for %reason%."),
    PLAYER_UNBANNED("player-unbanned", "&aUnbanned %player%."),
    PLAYER_BAN_KICK_MESSAGE("player-ban-kick-message", "&4&lSERVER NAME\n&cYou have been banned.\n\n&cDuration: &f%duration%\n&cReason: &f%reason%\n&cBanned by: &f%banned_by%");

    private static final String KEY_PREFIX = "messages.";

    private final String key;
    private final String defaultMessage;

    public @NotNull String get(@NotNull PlaceholderResolver resolver) {
        FileConfiguration config = BanPlugin.getInstance().getConfig();

        String message;
        if (config.isList(KEY_PREFIX + this.key)) {
            message = String.join("\n", config.getList(KEY_PREFIX + this.key, List.of(this.defaultMessage.split("\n"))).stream()
                    .map(String::valueOf)
                    .toList());
        } else {
            message = config.getString(KEY_PREFIX + this.key, this.defaultMessage);
        }

        return ChatColor.translateAlternateColorCodes('&', resolver.resolve(message));
    }

    public @NotNull String get() {
        return this.get(PlaceholderResolver.EMPTY);
    }
}