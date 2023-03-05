package nl.itz_kiwisap_.devroom.banplugin.menus;

import lombok.AccessLevel;
import lombok.Getter;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.utils.ItemBuilder;
import nl.itz_kiwisap_.devroom.banplugin.utils.Messages;
import nl.itz_kiwisap_.devroom.banplugin.utils.MojangFetcher;
import nl.itz_kiwisap_.devroom.banplugin.utils.TimeUtils;
import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import nl.odalitadevelopments.menus.items.DisplayItem;
import nl.odalitadevelopments.menus.items.MenuItem;
import nl.odalitadevelopments.menus.items.buttons.CloseItem;
import nl.odalitadevelopments.menus.items.buttons.PageItem;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import nl.odalitadevelopments.menus.pagination.Pagination;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class HistoryMenuReader {

    private final BanPlugin plugin;

    @Getter(AccessLevel.PACKAGE)
    private HistoryMenuData data;

    public HistoryMenuReader(BanPlugin plugin) {
        this.plugin = plugin;

        this.readMenuConfiguration();

        if (this.data == null) {
            throw new IllegalStateException("History menu configuration could not be read!");
        }
    }

    public void readMenuConfiguration() {
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("history-menu");
        if (section == null) return;

        String title = section.getString("title", "History of %player%");

        char closeItemSymbol = section.getString("items.close-item.symbol", " ").charAt(0);
        char nextPageItemSymbol = section.getString("items.next-page-item.symbol", " ").charAt(0);
        char previousPageItemSymbol = section.getString("items.previous-page-item.symbol", " ").charAt(0);
        boolean showNextPageItemOnLastPage = section.getBoolean("items.next-page-item.show-on-last-page", false);
        boolean showPreviousPageItemOnFirstPage = section.getBoolean("items.previous-page-item.show-on-first-page", false);

        List<Integer> historyItemSlots = new ArrayList<>();
        Map<Integer, Function<Pagination, MenuItem>> staticItems = new HashMap<>();

        Function<Ban, MenuItem> historyItemCreator = (ban) -> {
            ConfigurationSection itemSection = section.getConfigurationSection("items.history-item");
            if (itemSection == null) return DisplayItem.of(new ItemStack(Material.AIR));

            return DisplayItem.of(ItemBuilder.fromConfig(
                    itemSection,
                    PlaceholderResolver.builder()
                            .placeholder("%player%", MojangFetcher.getName(ban.getBannedUuid()))
                            .placeholder("banned_by", MojangFetcher.getName(ban.getBannedByUuid()))
                            .placeholder("banned_at", TimeUtils.formatDate(ban.getBannedAt().toEpochMilli()))
                            .placeholder("reason", (ban.getReason() == null) ? Messages.NO_REASON_SPECIFIED.get() : ban.getReason())
                            .placeholder("duration", (ban.getDuration() == null) ? Messages.PERMANENT.get() : TimeUtils.formatDuration(ban.getDuration()))
                            .build()
            ).build());
        };

        Function<Ban, MenuItem> historyExpiredItemCreator = (ban) -> {
            ConfigurationSection itemSection = section.getConfigurationSection("items.history-expired-item");
            if (itemSection == null) return DisplayItem.of(new ItemStack(Material.AIR));

            return DisplayItem.of(ItemBuilder.fromConfig(
                    itemSection,
                    PlaceholderResolver.builder()
                            .placeholder("%player%", MojangFetcher.getName(ban.getBannedUuid()))
                            .placeholder("banned_by", MojangFetcher.getName(ban.getBannedByUuid()))
                            .placeholder("banned_at", TimeUtils.formatDate(ban.getBannedAt().toEpochMilli()))
                            .placeholder("reason", (ban.getReason() == null) ? Messages.NO_REASON_SPECIFIED.get() : ban.getReason())
                            .placeholder("duration", (ban.getDuration() == null) ? Messages.PERMANENT.get() : TimeUtils.formatDuration(ban.getDuration()))
                            .build()
            ).build());
        };

        Function<Ban, MenuItem> historyRemovedItemCreator = (ban) -> {
            ConfigurationSection itemSection = section.getConfigurationSection("items.history-unbanned-item");
            if (itemSection == null) return DisplayItem.of(new ItemStack(Material.AIR));

            return DisplayItem.of(ItemBuilder.fromConfig(
                    itemSection,
                    PlaceholderResolver.builder()
                            .placeholder("%player%", MojangFetcher.getName(ban.getBannedUuid()))
                            .placeholder("banned_by", MojangFetcher.getName(ban.getBannedByUuid()))
                            .placeholder("banned_at", TimeUtils.formatDate(ban.getBannedAt().toEpochMilli()))
                            .placeholder("reason", (ban.getReason() == null) ? Messages.NO_REASON_SPECIFIED.get() : ban.getReason())
                            .placeholder("duration", (ban.getDuration() == null) ? Messages.PERMANENT.get() : TimeUtils.formatDuration(ban.getDuration()))
                            .placeholder("unbanned_by", MojangFetcher.getName(ban.getRemovedByUuid()))
                            .placeholder("unbanned_at", TimeUtils.formatDate(ban.getRemovedAt().toEpochMilli()))
                            .build()
            ).build());
        };

        List<String> pattern = section.getStringList("pattern");
        for (int i = 0; i < pattern.size(); i++) {
            String line = pattern.get(i);

            for (int j = 0; j < line.length(); j++) {
                char character = line.charAt(j);
                if (character == ' ') continue;

                if (character == '*') {
                    historyItemSlots.add(i * 9 + j);
                    continue;
                }

                if (character == closeItemSymbol) {
                    staticItems.put(i * 9 + j, (pagination) -> CloseItem.get());
                    continue;
                }

                if (character == nextPageItemSymbol) {
                    staticItems.put(i * 9 + j, (pagination) -> PageItem.next(pagination, showNextPageItemOnLastPage));
                    continue;
                }

                if (character == previousPageItemSymbol) {
                    staticItems.put(i * 9 + j, (pagination) -> PageItem.previous(pagination, showPreviousPageItemOnFirstPage));
                    continue;
                }

                String path = "items." + character;
                if (!section.contains(path)) continue;

                ConfigurationSection itemSection = section.getConfigurationSection(path);
                if (itemSection == null) continue;

                staticItems.put(i * 9 + j, (pagination) -> DisplayItem.of(ItemBuilder.fromConfig(itemSection).build()));
            }
        }

        MenuType menuType = MenuType.valueOf("CHEST_" + (Math.min(6, Math.max(1, pattern.size()))) + "_ROW");
        this.data = new HistoryMenuData(
                title,
                menuType,
                staticItems,
                historyItemSlots,
                historyItemCreator,
                historyExpiredItemCreator,
                historyRemovedItemCreator
        );
    }

    record HistoryMenuData(String title, MenuType menuType, Map<Integer, Function<Pagination, MenuItem>> staticItems,
                           List<Integer> historyItemSlots,
                           Function<Ban, MenuItem> historyItemCreator,
                           Function<Ban, MenuItem> historyExpiredItemCreator,
                           Function<Ban, MenuItem> historyRemovedItemCreator) {
    }
}