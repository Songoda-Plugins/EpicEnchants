package com.songoda.epicenchants.utils.objects;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.objects.Placeholder;
import com.songoda.epicenchants.wrappers.EnchantmentWrapper;
import com.songoda.epicenchants.utils.single.ConfigParser;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.songoda.epicenchants.utils.single.GeneralUtils.color;

public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta meta;
    private final Set<EnchantmentWrapper> enchantmentWrappers;

    /*
     * Constructors:
     */
    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material, byte data) {
        this(new ItemStack(material, 1, data));
    }

    public ItemBuilder(Material material, int amount, byte data) {
        this(new ItemStack(material, amount, data));
    }

    public ItemBuilder(ConfigurationSection section, Player player, Placeholder... placeholders) {
        this(section, placeholders);

        if (XMaterial.PLAYER_HEAD.isSimilar(this.item)) {
            ((SkullMeta) this.item.getItemMeta()).setOwner(player.getName());
        }
    }

    public ItemBuilder(ConfigurationSection section, Placeholder... placeholders) {
        this(Material.valueOf(section.getString("material")), (byte) (section.contains("data") ? section.getInt("data") : 0));

        if (section.contains("enchants")) {
            section.getStringList("enchants").stream()
                    .map(ConfigParser::parseEnchantmentWrapper)
                    .forEach(this::addEnchantWrapper);
        }

        if (section.contains("display-name")) {
            String displayName = section.getString("display-name");
            for (Placeholder placeholder : placeholders) {
                displayName = displayName.replace(placeholder.getPlaceholder(), placeholder.getToReplace().toString());
            }
            name(color(displayName));
        }

        if (section.contains("lore")) {
            List<String> lore = section.getStringList("lore");
            outer:
            for (int i = 0; i < lore.size(); i++) {
                String string = lore.get(i);

                for (Placeholder placeholder : placeholders) {
                    if (placeholder.getToReplace() instanceof ArrayList && string.contains(placeholder.getPlaceholder())) {
                        lore.remove(i);
                        lore.addAll(i, (ArrayList<String>) placeholder.getToReplace());
                        continue outer;
                    } else {
                        string = string.replace(placeholder.getPlaceholder(), placeholder.getToReplace().toString());
                    }
                }

                lore.set(i, string);
            }
            lore(lore.stream().map(GeneralUtils::color).collect(Collectors.toList()));
        }
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.enchantmentWrappers = new HashSet<>();
    }

    /*
     * Meta:
     */
    public boolean hasMeta() {
        return getMeta() != null;
    }

    public ItemBuilder meta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    /*
     * Name:
     */
    public boolean hasName() {
        return this.meta.hasDisplayName();
    }

    public ItemBuilder name(String name) {
        this.meta.setDisplayName(name);
        return this;
    }

    /*
     * Lore:
     */
    public boolean hasLore() {
        return this.meta.hasLore();
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        if (!this.meta.hasLore()) {
            this.meta.setLore(lore);
            return this;
        }

        List<String> toAdd = this.meta.getLore();
        toAdd.addAll(lore);
        this.meta.setLore(toAdd);
        return this;
    }

    public ItemBuilder removeLore(String string) {
        if (this.meta == null || !this.meta.hasLore()) {
            return this;
        }

        this.meta.setLore(this.meta.getLore().stream().filter(s -> !s.startsWith(string)).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder removeLore(int index) {
        if (!this.meta.hasLore()) {
            return this;
        }

        List<String> lore = this.meta.getLore();

        if (index >= lore.size()) {
            return this;
        }

        lore.remove(index);

        this.meta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    /*
     * Enchantments:
     */
    public boolean hasEnchants() {
        return this.meta.hasEnchants();
    }

    public boolean hasEnchant(Enchantment enchantment) {
        return this.meta.hasEnchant(enchantment);
    }

    public boolean hasConflictingEnchant(Enchantment enchantment) {
        return this.meta.hasConflictingEnchant(enchantment);
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        this.meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder addEnchantWrapper(EnchantmentWrapper enchantmentWrapper) {
        this.enchantmentWrappers.add(enchantmentWrapper);
        return this;
    }

    /*
     * Skulls:
     */
    public boolean hasSkullOwner() {
        return ((SkullMeta) this.meta).hasOwner();
    }

    public String getSkullOwner() {
        return ((SkullMeta) this.meta).getOwner();
    }

    public ItemBuilder skullOwner(String owner) {
        this.item.setDurability((short) 3);
        ((SkullMeta) this.meta).setOwner(owner);
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.item.setDurability((short) durability);
        return this;
    }

    /*
     * Flags:
     */
    public boolean hasFlag(ItemFlag flag) {
        return this.meta.hasItemFlag(flag);
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        this.meta.removeItemFlags(flags);
        return this;
    }

    public NBTItem nbt() {
        return new NBTItem(build());
    }

    /*
     * Build the ItemStack.
     */
    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }

    public ItemStack buildWithWrappers(int level, Player user, LivingEntity opponent) {
        this.item.setItemMeta(this.meta);
        this.enchantmentWrappers.forEach(enchant -> this.item.addUnsafeEnchantment(enchant.getEnchantment(), enchant.getAmplifier(level, user, opponent)));
        return this.item;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return this.meta.getEnchants();
    }

    public Set<ItemFlag> getFlags() {
        return this.meta.getItemFlags();
    }

    public List<String> getLore() {
        return this.meta.getLore();
    }

    public ItemMeta getMeta() {
        return this.meta;
    }

    /*
     * NBT
     */

    public String getName() {
        return this.meta.getDisplayName();
    }
}
