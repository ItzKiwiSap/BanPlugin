package nl.itz_kiwisap_.devroom.banplugin.menus;

import lombok.AllArgsConstructor;
import nl.itz_kiwisap_.devroom.banplugin.BanPlugin;
import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;
import nl.itz_kiwisap_.devroom.banplugin.utils.MojangFetcher;
import nl.odalitadevelopments.menus.annotations.Menu;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.items.MenuItem;
import nl.odalitadevelopments.menus.iterators.MenuIterator;
import nl.odalitadevelopments.menus.iterators.MenuIteratorType;
import nl.odalitadevelopments.menus.menu.providers.PlayerMenuProvider;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import nl.odalitadevelopments.menus.pagination.Pagination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@AllArgsConstructor
@Menu(
        title = "",
        type = MenuType.CHEST_6_ROW
)
public final class HistoryMenu implements PlayerMenuProvider {

    private final BanPlugin plugin;
    private final UUID bannedUuid;

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents contents) {
        HistoryMenuReader.HistoryMenuData data = this.plugin.getHistoryMenuReader().getData();

        contents.setMenuType(data.menuType());

        MenuIterator historyIterator = contents.createIterator("history_iterator", MenuIteratorType.HORIZONTAL, 0, 0);

        for (int i = 0; i < data.historyItemSlots().size(); i++) {
            if (i == 0) continue;

            Integer slot = data.historyItemSlots().get(i);
            Integer previousSlot = data.historyItemSlots().get(i - 1);

            for (int j = previousSlot + 1; j < slot; j++) {
                historyIterator.blacklist(j);
            }
        }

        Pagination pagination = contents.pagination("history_pagination", data.historyItemSlots().size())
                .iterator(historyIterator)
                .create();

        CompletableFuture.runAsync(() -> {
            String bannedPlayerName = MojangFetcher.getName(this.bannedUuid);
            contents.setTitle(data.title().replace("%player%", bannedPlayerName));

            List<Ban> playerHistory = new ArrayList<>(this.plugin.getBanHandler().getPlayerHistory(this.bannedUuid));
            playerHistory.sort(Comparator.comparing(Ban::getBannedAt).reversed());

            for (Ban ban : playerHistory) {
                if (ban.isRemoved()) {
                    pagination.addItem(() -> data.historyRemovedItemCreator().apply(ban));
                } else if (ban.isExpired()) {
                    pagination.addItem(() -> data.historyExpiredItemCreator().apply(ban));
                } else {
                    pagination.addItem(() -> data.historyItemCreator().apply(ban));
                }
            }
        });

        for (Map.Entry<Integer, Function<Pagination, MenuItem>> entry : data.staticItems().entrySet()) {
            contents.set(entry.getKey(), entry.getValue().apply(pagination));
        }
    }
}