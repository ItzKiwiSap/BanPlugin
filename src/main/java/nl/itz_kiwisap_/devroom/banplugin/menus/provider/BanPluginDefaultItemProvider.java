package nl.itz_kiwisap_.devroom.banplugin.menus.provider;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.utils.ItemBuilder;
import nl.odalitadevelopments.menus.menu.providers.MenuProvider;
import nl.odalitadevelopments.menus.pagination.Pagination;
import nl.odalitadevelopments.menus.providers.providers.DefaultItemProvider;
import nl.odalitadevelopments.menus.scrollable.Scrollable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public final class BanPluginDefaultItemProvider implements DefaultItemProvider {

    private final BanPlugin plugin;

    @Override
    public @NotNull ItemStack closeItem() {
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("history-menu.items.close-item");
        if (section == null) return new ItemStack(Material.AIR);

        return ItemBuilder.fromConfig(section)
                .build();
    }

    @Override
    public @NotNull ItemStack backItem(@NotNull MenuProvider menuProvider) {
        return new ItemStack(Material.AIR); // Not used
    }

    @Override
    public @NotNull ItemStack nextPageItem(@NotNull Pagination pagination) {
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("history-menu.items.next-page-item");
        if (section == null) return new ItemStack(Material.AIR);

        return ItemBuilder.fromConfig(section)
                .build();
    }

    @Override
    public @NotNull ItemStack previousPageItem(@NotNull Pagination pagination) {
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("history-menu.items.previous-page-item");
        if (section == null) return new ItemStack(Material.AIR);

        return ItemBuilder.fromConfig(section)
                .build();
    }

    @Override
    public @NotNull ItemStack scrollUpItem(@NotNull Scrollable scrollable) {
        return new ItemStack(Material.AIR); // Not used
    }

    @Override
    public @NotNull ItemStack scrollDownItem(@NotNull Scrollable scrollable) {
        return new ItemStack(Material.AIR); // Not used
    }

    @Override
    public @NotNull ItemStack scrollLeftItem(@NotNull Scrollable scrollable) {
        return new ItemStack(Material.AIR); // Not used
    }

    @Override
    public @NotNull ItemStack scrollRightItem(@NotNull Scrollable scrollable) {
        return new ItemStack(Material.AIR); // Not used
    }
}