package nl.itz_kiwisap_.devroom.banplugin.utils.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

final class PlaceholderResolverBuilderImpl implements PlaceholderResolverBuilder {

    private final Collection<Placeholder> placeholders = new HashSet<>();

    @Override
    public @NotNull PlaceholderResolverBuilder placeholder(@NotNull String placeholder, @NotNull String replacement) {
        Placeholder holder = Placeholder.of(placeholder, replacement);

        if (!this.placeholders.contains(holder)) {
            this.placeholders.add(holder);
        }

        return this;
    }

    @Override
    public @NotNull PlaceholderResolverBuilder placeholders(Placeholder @NotNull ... placeholders) {
        for (Placeholder placeholder : placeholders) {
            if (!this.placeholders.contains(placeholder)) {
                this.placeholders.add(placeholder);
            }
        }

        return this;
    }

    @Override
    public @NotNull PlaceholderResolver build() {
        return new PlaceholderResolver(this.placeholders);
    }
}