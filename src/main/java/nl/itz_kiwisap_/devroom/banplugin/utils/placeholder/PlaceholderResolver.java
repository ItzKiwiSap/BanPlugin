package nl.itz_kiwisap_.devroom.banplugin.utils.placeholder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class PlaceholderResolver {

    public static @NotNull PlaceholderResolver EMPTY = new PlaceholderResolver(Set.of());

    @Contract(" -> new")
    public static @NotNull PlaceholderResolverBuilder builder() {
        return new PlaceholderResolverBuilderImpl();
    }

    private final Collection<Placeholder> placeholders;

    public @NotNull String resolve(@NotNull String text) {
        for (Placeholder placeholder : this.placeholders) {
            String replacement = placeholder.replacement();
            text = text.replace("%" + placeholder.placeholder() + "%", replacement);
        }

        return text;
    }
}