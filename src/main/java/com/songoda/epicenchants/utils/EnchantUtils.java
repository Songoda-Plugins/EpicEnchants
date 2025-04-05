package com.songoda.epicenchants.utils;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTCompound;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.core.utils.NumberUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.enums.EnchantResult;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.enums.TriggerType;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import com.songoda.epicenchants.utils.settings.Settings;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchantUtils {

    private final EpicEnchants instance;

    public EnchantUtils(EpicEnchants instance) {
        this.instance = instance;
    }

    public Tuple<ItemStack, EnchantResult> apply(ItemStack itemStack, Enchant enchant, int level, int successRate, int destroyRate) {
        boolean hasProtection = new NBTItem(itemStack).hasTag("protected");

        Map<Enchant, Integer> currentEnchantMap = getEnchants(itemStack);
        Set<String> currentIds = currentEnchantMap.keySet().stream().map(Enchant::getIdentifier).collect(Collectors.toSet());
        Set<String> currentConflicts = currentEnchantMap.keySet().stream().map(Enchant::getConflict).flatMap(Collection::stream).collect(Collectors.toSet());

        if (enchant.getConflict().stream().anyMatch(currentIds::contains) || currentConflicts.contains(enchant.getIdentifier())) {
            return Tuple.of(itemStack, EnchantResult.CONFLICT);
        }

        if (currentEnchantMap.entrySet().stream().anyMatch(entry -> entry.getKey().equals(enchant) && entry.getValue() == enchant.getMaxLevel())) {
            return Tuple.of(itemStack, EnchantResult.MAXED_OUT);
        }

        if (currentEnchantMap.entrySet().stream().anyMatch(entry -> entry.getKey().equals(enchant) && entry.getValue() >= level)) {
            return Tuple.of(itemStack, EnchantResult.ALREADY_APPLIED);
        }

        if (!GeneralUtils.chance(successRate)) {
            if (GeneralUtils.chance(destroyRate)) {
                if (hasProtection) {
                    NBTItem nbtItem = new ItemBuilder(itemStack).removeLore(this.instance.getSpecialItems().getWhiteScrollLore()).nbt();
                    nbtItem.removeKey("protected");
                    return Tuple.of(nbtItem.getItem(), EnchantResult.PROTECTED);
                }
                return Tuple.of(new ItemStack(Material.AIR), EnchantResult.BROKEN_FAILURE);
            }
            return Tuple.of(itemStack, EnchantResult.FAILURE);
        }

        ItemBuilder itemBuilder = new ItemBuilder(itemStack);

        if (hasProtection) {
            itemBuilder.removeLore(this.instance.getSpecialItems().getWhiteScrollLore());
        }

        itemBuilder.removeLore(enchant.getFormat(-1, Settings.ROMAN.getBoolean()).replace("-1", "").trim());
        itemBuilder.addLore(enchant.getFormat(level, Settings.ROMAN.getBoolean()));

        if (hasProtection) {
            itemBuilder.addLore(this.instance.getSpecialItems().getWhiteScrollLore());
        }

        NBTItem nbtItem = itemBuilder.nbt();

        NBTCompound compound = nbtItem.getOrCreateCompound("enchants");
        compound.setInteger(enchant.getIdentifier(), level);

        return Tuple.of(nbtItem.getItem(), EnchantResult.SUCCESS);
    }

    public Map<Enchant, Integer> getEnchants(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return Collections.emptyMap();
        }
        NBTItem nbtItem = new NBTItem(itemStack);

        if (!nbtItem.hasTag("enchants")) {
            return Collections.emptyMap();
        }

        NBTCompound compound = nbtItem.getCompound("enchants");

        if (compound == null) {
            return Collections.emptyMap();
        }

        return compound.getKeys().stream().filter(key -> this.instance.getEnchantManager().getValueUnsafe(key) != null)
                .collect(Collectors.toMap(key -> this.instance.getEnchantManager().getValueUnsafe(key), compound::getInteger));
    }

    public void handlePlayer(@NotNull Player player, @Nullable LivingEntity opponent, Event event, TriggerType triggerType) {
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        stacks.add(GeneralUtils.getHeldItem(player, event));
        stacks.removeIf(Objects::isNull);

        if (triggerType == TriggerType.HELD_ITEM) {
            stacks = Collections.singletonList(player.getItemInHand());
        }

        stacks.stream().map(this::getEnchants).forEach(list -> list.forEach((enchant, level) -> {
            enchant.onAction(player, opponent, event, level, triggerType, EventType.NONE);
        }));
    }

    public ItemStack removeEnchant(ItemStack itemStack, Enchant enchant) {
        if (itemStack == null) {
            return null;
        }

        NBTItem nbtItem = new NBTItem(itemStack);

        if (nbtItem.getCompound("enchants") == null) {
            return itemStack;
        }

        String format = enchant.getFormat().replace("{level}", "").trim();
        String text = format.isEmpty() ? enchant.getColoredIdentifier(false) : format;

        nbtItem.getCompound("enchants").removeKey(enchant.getIdentifier());
        ItemBuilder output = new ItemBuilder(nbtItem.getItem());
        output.removeLore(TextUtils.formatText(text));
        return output.build();
    }

    public int getMaximumEnchantsCanApplyItem(ItemStack itemStack, Player p) {
        int max;
        if (p.isOp()) {
            return 100; // in theory, no single item will have 100 enchantments at a time.
        }
        if (this.instance.getFileManager().getConfiguration("items/item-limits").contains("limits." + itemStack.getType().toString())) {
            max = this.instance.getFileManager().getConfiguration("items/item-limits").getInt("limits." + itemStack.getType().toString());
        } else {
            max = this.instance.getFileManager().getConfiguration("items/item-limits").getInt("default");
        }
        return max;
    }

    public int getMaximumEnchantsCanApply(Player p) {
        int max = 0;
        if (p.isOp()) {
            return 100; // in theory, no single item will have 100 enchantments at a time.
        }
        for (PermissionAttachmentInfo effectivePermission : p.getEffectivePermissions()) {
            if (!effectivePermission.getPermission().startsWith("epicenchants.maxapply.")) {
                continue;
            }

            String[] node = effectivePermission.getPermission().split("\\.");

            if (NumberUtils.isInt(node[node.length - 1])) {
                int num = Integer.parseInt(node[node.length - 1]);
                if (num > max) {
                    max = num;
                }
            }
        }
        return max;
    }
}
