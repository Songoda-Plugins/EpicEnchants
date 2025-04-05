package com.songoda.epicenchants.objects;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import com.songoda.epicenchants.utils.settings.Settings;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import com.songoda.epicenchants.utils.single.ItemGroup;
import com.songoda.epicenchants.utils.single.RomanNumber;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.songoda.epicenchants.utils.single.GeneralUtils.color;
import static java.util.concurrent.ThreadLocalRandom.current;

public class BookItem {
    private final EpicEnchants instance;
    private final Material material;
    private final String displayName;
    private final List<String> lore;

    BookItem(EpicEnchants instance, Material material, String displayName, List<String> lore) {
        this.instance = instance;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public static BookItemBuilder builder() {
        return new BookItemBuilder();
    }

    public ItemStack get(Enchant enchant) {
        return get(enchant, current().nextInt(enchant.getMaxLevel()) + 1);
    }

    public ItemStack get(Enchant enchant, int level) {
        return get(enchant, level,
                current().nextInt(enchant.getGroup().getSuccessRateMin(), enchant.getGroup().getSuccessRateMax()),
                current().nextInt(enchant.getGroup().getDestroyRateMin(), enchant.getGroup().getDestroyRateMax()));
    }

    public ItemStack get(Enchant enchant, int level, int successRate, int destroyRate) {
        successRate = successRate == -1 ? current().nextInt(101) : successRate;
        destroyRate = destroyRate == -1 ? current().nextInt(101) : destroyRate;
        level = level == -1 ? current().nextInt(1, enchant.getMaxLevel() + 1) : level;

        int finalSuccessRate = successRate;
        int finalDestroyRate = destroyRate;

        List<String> toSet = new ArrayList<>(this.lore);

        for (int i = toSet.size() - 1; i >= 0; i--) {
            String string = toSet.get(i);

            if (string.contains("{description}")) {
                toSet.remove(i);
                toSet.addAll(i, enchant.getDescription().stream().map(s -> enchant.getGroup().getDescriptionColor() + s).map(GeneralUtils::color).collect(Collectors.toList()));
                continue;
            }

            string = string
                    .replace("{item_group}", this.instance.getItemGroup().getGroup(enchant.getItemWhitelist()).map(ItemGroup.Group::getName).orElse("N/A"))
                    .replace("{success_rate}", String.valueOf(finalSuccessRate))
                    .replace("{destroy_rate}", String.valueOf(finalDestroyRate));

            toSet.set(i, string);
        }

        ItemBuilder itemBuilder = new ItemBuilder(this.material)
                .name(color(this.displayName
                        .replace("{level}", String.valueOf(Settings.ROMAN.getBoolean() ? RomanNumber.toRoman(level) : level))
                        .replace("{enchant}", enchant.getIdentifier())
                        .replace("{group_color}", enchant.getGroup().getColor())
                        .replace("{group_name}", enchant.getGroup().getName())
                ))
                .lore(toSet);

        NBTItem nbtItem = itemBuilder.nbt();
        nbtItem.setBoolean("book-item", true);
        nbtItem.setInteger("success-rate", successRate);
        nbtItem.setInteger("destroy-rate", destroyRate);
        nbtItem.setInteger("level", level);
        nbtItem.setString("enchant", enchant.getIdentifier());

        return nbtItem.getItem();
    }

    public static class BookItemBuilder {
        private EpicEnchants instance;
        private Material material;
        private String displayName;
        private List<String> lore;

        BookItemBuilder() {
        }

        public BookItemBuilder instance(EpicEnchants instance) {
            this.instance = instance;
            return this;
        }

        public BookItemBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public BookItemBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public BookItemBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public BookItem build() {
            return new BookItem(this.instance, this.material, this.displayName, this.lore);
        }

        public String toString() {
            return "BookItem.BookItemBuilder(instance=" + this.instance + ", material=" + this.material + ", displayName=" + this.displayName + ", lore=" + this.lore + ")";
        }
    }
}
