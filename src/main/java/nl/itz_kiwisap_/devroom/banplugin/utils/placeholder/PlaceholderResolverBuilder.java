package nl.itz_kiwisap_.devroom.banplugin.utils.placeholder;

import org.jetbrains.annotations.NotNull;

public interface PlaceholderResolverBuilder {

    @NotNull PlaceholderResolverBuilder placeholder(@NotNull String placeholder, @NotNull String replacement);

    @NotNull PlaceholderResolverBuilder placeholders(Placeholder @NotNull ... placeholders);

    @NotNull PlaceholderResolver build();
}