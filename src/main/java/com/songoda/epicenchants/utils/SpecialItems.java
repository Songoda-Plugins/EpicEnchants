package com.songoda.epicenchants.utils;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.objects.Placeholder;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import com.songoda.epicenchants.utils.settings.Settings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

import static com.songoda.epicenchants.utils.single.GeneralUtils.color;

public class SpecialItems {
    private final EpicEnchants instance;

    public SpecialItems(EpicEnchants instance) {
        this.instance = instance;
    }

    public ItemStack getWhiteScroll(int amount) {
        NBTItem nbtItem = new ItemBuilder(this.instance.getFileManager().getConfiguration("items/special-items").getConfigurationSection("white-scroll")).nbt();
        nbtItem.setBoolean("white-scroll", true);
        ItemStack itemStack = nbtItem.getItem();

        itemStack.setAmount(amount);

        return itemStack;
    }

    public ItemStack getBlackScroll(int amount, int chance) {
        int successRate = chance == -1 ? ThreadLocalRandom.current().nextInt(Settings.BLACK_MIN.getInt(), Settings.BLACK_MAX.getInt() + 1) : chance;
        NBTItem nbtItem = new ItemBuilder(this.instance.getFileManager().getConfiguration("items/special-items").getConfigurationSection("black-scroll"), Placeholder.of("success-rate", successRate)).nbt();

        nbtItem.setBoolean("black-scroll", true);
        nbtItem.setInteger("success-rate", successRate);

        ItemStack itemStack = nbtItem.getItem();

        itemStack.setAmount(amount);

        return itemStack;
    }

    public ItemStack getMysteryBook(Group group) {
        NBTItem nbtItem = new ItemBuilder(this.instance.getFileManager().getConfiguration("items/special-items").getConfigurationSection("mystery-book"),
                Placeholder.of("group-color", group.getColor()),
                Placeholder.of("group-name", group.getName())).nbt();

        nbtItem.setBoolean("mystery-book", true);
        nbtItem.setString("group", group.getIdentifier());
        return nbtItem.getItem();
    }

    public ItemStack getSecretDust(NBTItem book) {
        Group group = this.instance.getEnchantManager().getValueUnsafe(book.getString("enchant")).getGroup();
        return getSecretDust(group, (int) Math.floor(book.getInteger("success-rate") / 10.0));
    }

    public ItemStack getSecretDust(Group group, int max) {
        NBTItem nbtItem = new ItemBuilder(this.instance.getFileManager().getConfiguration("items/dusts").getConfigurationSection("secret-dust"),
                Placeholder.of("group-color", group.getColor()),
                Placeholder.of("group-name", group.getName()),
                Placeholder.of("max-rate", max),
                Placeholder.of("min-rate", 0)).nbt();

        nbtItem.setBoolean("secret-dust", true);
        nbtItem.setString("group", group.getIdentifier());
        nbtItem.setInteger("max-rate", max + 1);
        nbtItem.setInteger("min-rate", 1);
        return nbtItem.getItem();
    }

    public ItemStack getDust(Group group, @Nullable String type, int percentage, boolean command) {
        FileConfiguration dustConfig = this.instance.getFileManager().getConfiguration("items/dusts");
        int random = ThreadLocalRandom.current().nextInt(101);
        int counter = 0;

        if (type == null) {
            for (String s : dustConfig.getConfigurationSection("dusts").getKeys(false)) {
                int chance = dustConfig.getConfigurationSection("dusts." + s).getInt("chance");
                if (random < (chance + counter) && random >= counter) {
                    type = s;
                }
                counter += chance;
            }
        }

        type = type == null ? "mystery" : type;

        ConfigurationSection config = dustConfig.getConfigurationSection("dusts." + type);

        if (!command && config.isInt("min-rate") && config.isInt("max-rate")) {
            int minRate = config.getInt("min-rate");
            int maxRate = config.getInt("max-rate");
            percentage = ThreadLocalRandom.current().nextInt(minRate, maxRate + 1);
        } else if (percentage == -1) {
            percentage = ThreadLocalRandom.current().nextInt(0, 10);
        }

        NBTItem nbtItem = new ItemBuilder(config,
                Placeholder.of("group-color", group.getColor()),
                Placeholder.of("group-name", group.getName()),
                Placeholder.of("percentage", percentage)).nbt();

        if (type.equalsIgnoreCase("mystery")) {
            return nbtItem.getItem();
        }

        nbtItem.setBoolean("dust", true);
        nbtItem.setInteger("percentage", percentage);
        nbtItem.setString("group", group.getIdentifier());

        return nbtItem.getItem();
    }

    public String getWhiteScrollLore() {
        return color(this.instance.getFileManager().getConfiguration("items/special-items").getString("white-scroll.format"));
    }
}
