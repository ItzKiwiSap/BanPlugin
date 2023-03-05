package nl.itz_kiwisap_.devroom.banplugin.utils.placeholder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Placeholder(@NotNull String placeholder, @NotNull String replacement) {

    @Contract("_, _ -> new")
    public static @NotNull Placeholder of(@NotNull String placeholder, @NotNull String replacement) {
        return new Placeholder(placeholder, replacement);
    }
}