package nl.itz_kiwisap_.devroom.banplugin.utils;

import nl.itz_kiwisap_.devroom.banplugin.utils.placeholder.PlaceholderResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class ItemBuilder {

    private final ItemStack stack;
    private ItemMeta meta;

    private ItemBuilder(@NotNull ItemStack stack) {
        this.stack = stack;
        this.meta = this.stack.getItemMeta();

        if (this.meta == null) {
            this.meta = Bukkit.getItemFactory().getItemMeta(this.stack.getType());
        }
    }

    public static @NotNull ItemBuilder of(@NotNull ItemStack stack) {
        return new ItemBuilder(stack);
    }

    public static @NotNull ItemBuilder of(@NotNull ItemStack stack, @NotNull String displayName) {
        return of(stack).displayName(displayName);
    }

    public static @NotNull ItemBuilder of(@NotNull Material material) {
        return of(new ItemStack(material));
    }

    public static @NotNull ItemBuilder of(@NotNull Material material, @NotNull String displayName) {
        return of(material).displayName(displayName);
    }

    public static @NotNull ItemBuilder clone(@NotNull ItemStack stack) {
        return of(stack.clone());
    }

    public static @NotNull ItemBuilder fromConfig(@NotNull ConfigurationSection section, @NotNull PlaceholderResolver resolver) {
        ItemBuilder builder = of(Material.valueOf(section.getString("material")));

        if (section.contains("name")) {
            String name = section.getString("name", "");
            builder.displayName((name.isEmpty()) ? "&0" : resolver.resolve(name));
        }

        if (section.contains("lore")) {
            builder.lore((lore) -> {
                List<String> configLore = section.getStringList("lore");
                for (String line : configLore) {
                    lore.add(resolver.resolve(line));
                }

                return lore;
            });
        }

        return builder;
    }

    public static @NotNull ItemBuilder fromConfig(@NotNull ConfigurationSection section) {
        return fromConfig(section, PlaceholderResolver.EMPTY);
    }

    public ItemBuilder meta(@NotNull Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(this.meta);
        return this;
    }

    public <T extends ItemMeta> ItemBuilder meta(@NotNull Class<T> metaClass, @NotNull UnaryOperator<T> metaUnaryOperator) {
        this.meta = metaUnaryOperator.apply(metaClass.cast(this.meta));
        return this;
    }

    public <T extends ItemMeta> ItemBuilder metaConsumer(@NotNull Class<T> metaClass, @NotNull Consumer<T> metaConsumer) {
        return this.meta(metaClass, (meta) -> {
            metaConsumer.accept(meta);
            return meta;
        });
    }

    public ItemBuilder displayName(@NotNull String displayName) {
        return this.meta((meta) -> meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName)));
    }

    public ItemBuilder data(byte data) {
        this.stack.getData().setData(data);
        return this;
    }

    public ItemBuilder lore(@NotNull List<String> lore) {
        return this.meta((meta) -> {
            List<String> loreList = new ArrayList<>();

            for (String s : lore) {
                loreList.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            this.meta.setLore(loreList);
        });
    }

    public ItemBuilder lore(@NotNull String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(@NotNull UnaryOperator<@NotNull List<String>> loreUnaryOperator) {
        return this.lore(loreUnaryOperator.apply((this.meta.getLore() == null) ? new ArrayList<>() : this.meta.getLore()));
    }

    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
        return this.stack;
    }
}