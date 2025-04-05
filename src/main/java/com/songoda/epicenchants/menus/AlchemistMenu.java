package com.songoda.epicenchants.menus;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.objects.Placeholder;
import com.songoda.epicenchants.utils.objects.FastInv;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static com.songoda.epicenchants.utils.single.Experience.changeExp;
import static com.songoda.epicenchants.utils.single.Experience.getExp;
import static com.songoda.epicenchants.utils.single.GeneralUtils.color;
import static com.songoda.epicenchants.utils.single.GeneralUtils.getSlots;

public class AlchemistMenu extends FastInv {
    private final EpicEnchants instance;
    private final FileConfiguration config;
    private final int LEFT_SLOT, RIGHT_SLOT, PREVIEW_SLOT, ACCEPT_SLOT;
    private final ItemStack PREVIEW_ITEM, ACCEPT_ITEM;

    public AlchemistMenu(EpicEnchants instance, FileConfiguration config) {
        super(config.getInt("rows") * 9, color(config.getString("title")));

        this.instance = instance;
        this.config = config;

        this.LEFT_SLOT = config.getInt("left-slot");
        this.RIGHT_SLOT = config.getInt("right-slot");
        this.PREVIEW_SLOT = config.getInt("preview-slot");
        this.ACCEPT_SLOT = config.getInt("accept-slot");

        this.PREVIEW_ITEM = new ItemBuilder(config.getConfigurationSection("contents.preview")).build();
        this.ACCEPT_ITEM = new ItemBuilder(config.getConfigurationSection("contents.accept-before")).build();

        if (config.isConfigurationSection("fill")) {
            fill(new ItemBuilder(config.getConfigurationSection("fill")).build());
        }

        Set<String> filter = new HashSet<String>() {{
            add("preview");
            add("accept-before");
            add("accept-after");
        }};

        config.getConfigurationSection("contents").getKeys(false)
                .stream()
                .filter(s -> !filter.contains(s))
                .map(s -> "contents." + s)
                .map(config::getConfigurationSection)
                .forEach(section -> addItem(getSlots(section.getString("slot")), new ItemBuilder(section).build()));

        clear(this.RIGHT_SLOT);
        clear(this.LEFT_SLOT);

        updateSlots();

        // Player clicked an item in tinkerer
        onClick(event -> {
            if (event.getEvent().getClickedInventory() == null && event.getInventory().equals(this)) {
                return;
            }

            int slot = event.getSlot();

            if (slot != this.RIGHT_SLOT && slot != this.LEFT_SLOT) {
                return;
            }

            if (getInventory().getItem(slot) != null && getInventory().getItem(slot).getType() != Material.AIR) {
                event.getPlayer().getInventory().addItem(getInventory().getItem(slot));
                getInventory().clear(slot);
                updateSlots();
            }
        });

        // Player clicked his own inv
        onClick(event -> {
            if (event.getEvent().getClickedInventory() == null || event.getEvent().getClickedInventory().getType() != InventoryType.PLAYER) {
                return;
            }

            ItemStack itemStack = event.getItem();

            if (!handleItem(event.getPlayer(), itemStack)) {
                return;
            }

            if (itemStack.getAmount() > 1) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                return;
            }

            event.getEvent().getClickedInventory().clear(event.getEvent().getSlot());
        });

        // Player closed inventory
        onClose(event -> {
            if (getInventory().getItem(this.RIGHT_SLOT) != null)
                event.getPlayer().getInventory().addItem(getInventory().getItem(this.RIGHT_SLOT));
            if (getInventory().getItem(this.LEFT_SLOT) != null)
                event.getPlayer().getInventory().addItem(getInventory().getItem(this.LEFT_SLOT));
        });
    }

    private boolean handleItem(Player player, ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        ItemStack toHandle = itemStack.clone();
        toHandle.setAmount(1);

        NBTItem nbtItem = new NBTItem(toHandle);

        if (!nbtItem.hasTag("book-item") && !nbtItem.hasTag("dust")) {
            this.instance.getLocale().getMessage("alchemist.notinterested").sendPrefixedMessage(player);
            return false;
        }

        // Both slots occupied
        if (getInventory().getItem(this.LEFT_SLOT) != null && getInventory().getItem(this.RIGHT_SLOT) != null) {
            this.instance.getLocale().getMessage("alchemist.maxtwoitems").sendPrefixedMessage(player);
            return false;
        }

        int successRate = nbtItem.getInteger("success-rate");

        // Both slots empty
        if (getInventory().getItem(this.LEFT_SLOT) == null && getInventory().getItem(this.RIGHT_SLOT) == null) {
            if (nbtItem.hasTag("book-item")) {
                Enchant enchant = this.instance.getEnchantManager().getValue(nbtItem.getString("enchant")).orElseThrow(() -> new IllegalStateException("Book without enchant!"));
                int level = nbtItem.getInteger("level");

                if (enchant.getMaxLevel() == level) {
                    this.instance.getLocale().getMessage("alchemist.maxlevelbook")
                            .sendPrefixedMessage(player);
                    return false;
                }
            } else {
                Group group = this.instance.getGroupManager().getValue(nbtItem.getString("group")).orElseThrow(() -> new IllegalStateException("Dust without group!"));

                if (group.getOrder() == this.instance.getGroupManager().getValues().stream().mapToInt(Group::getOrder).max().orElse(0) || successRate == 100) {
                    this.instance.getLocale().getMessage("alchemist." + (successRate == 100 ? "maxpercentagedust" : "highestgroupdust"))
                            .sendPrefixedMessage(player);
                    return false;
                }
            }

            getInventory().setItem(this.LEFT_SLOT, toHandle);
            return true;
        }

        NBTItem other = new NBTItem(getInventory().getItem(getInventory().getItem(this.LEFT_SLOT) == null ? this.RIGHT_SLOT : this.LEFT_SLOT));
        int emptySlot = getInventory().getItem(this.LEFT_SLOT) == null ? this.LEFT_SLOT : this.RIGHT_SLOT;

        if (other.hasTag("book-item")) {
            if (!nbtItem.getString("enchant").equals(other.getString("enchant"))) {
                this.instance.getLocale().getMessage("alchemist.differentenchantment").sendPrefixedMessage(player);
                return false;
            }

            if (nbtItem.getInteger("level") != other.getInteger("level")) {
                this.instance.getLocale().getMessage("alchemist.differentlevels").sendPrefixedMessage(player);
                return false;
            }
        } else {
            if (!nbtItem.getString("group").equals(other.getString("group"))) {
                this.instance.getLocale().getMessage("alchemist.differentgroups").sendPrefixedMessage(player);
                return false;
            }

            if (successRate >= 100) {
                this.instance.getLocale().getMessage("alchemist.maxpercentagedust").sendPrefixedMessage(player);
                return false;
            }
        }

        getInventory().setItem(emptySlot, toHandle);
        updateSlots();
        return true;
    }

    private void updateSlots() {
        if (getInventory().getItem(this.LEFT_SLOT) == null || getInventory().getItem(this.RIGHT_SLOT) == null) {
            addItem(this.ACCEPT_SLOT, this.ACCEPT_ITEM);
            addItem(this.PREVIEW_SLOT, this.PREVIEW_ITEM);
            return;
        }

        NBTItem leftItem = new NBTItem(getInventory().getItem(this.LEFT_SLOT));
        NBTItem rightItem = new NBTItem(getInventory().getItem(this.RIGHT_SLOT));
        int ecoCost;
        int expCost;

        if (leftItem.hasTag("book-item")) {
            int level = leftItem.getInteger("level");
            Enchant enchant = this.instance.getEnchantManager().getValue(leftItem.getString("enchant")).orElseThrow(() -> new IllegalStateException("Book without enchant!"));
            int leftSuccess = leftItem.getInteger("success-rate");
            int rightSuccess = rightItem.getInteger("success-rate");
            int leftDestroy = leftItem.getInteger("destroy-rate");
            int rightDestroy = rightItem.getInteger("destroy-rate");

            Placeholder[] placeholders = new Placeholder[] {
                    Placeholder.of("left_success_rate", leftSuccess),
                    Placeholder.of("right_success_rate", rightSuccess),
                    Placeholder.of("left_destroy_rate", leftDestroy),
                    Placeholder.of("right_destroy_rate", rightDestroy),
                    Placeholder.of("max_destroy_rate", Math.max(leftDestroy, rightDestroy)),
                    Placeholder.of("min_destroy_rate", Math.min(leftDestroy, rightDestroy)),
                    Placeholder.of("max_success_rate", Math.max(leftSuccess, rightSuccess)),
                    Placeholder.of("min_success_rate", Math.min(leftSuccess, rightSuccess))
            };

            int successRate = getFromFormula("book.success-rate-formula", placeholders);
            int destroyRate = getFromFormula("book.destroy-rate-formula", placeholders);

            Placeholder[] costPlaceholders = new Placeholder[] {
                    Placeholder.of("group_order_index", enchant.getGroup().getOrder()),
                    Placeholder.of("final_success_rate", successRate),
                    Placeholder.of("final_destroy_rate", destroyRate),
            };

            ecoCost = getFromFormula("book.eco-cost-formula", costPlaceholders);
            expCost = getFromFormula("book.exp-cost-formula", costPlaceholders);

            getInventory().setItem(this.PREVIEW_SLOT, enchant.getBook().get(enchant, level + 1, successRate, destroyRate));
        } else {
            Group group = this.instance.getGroupManager().getValue(leftItem.getString("group")).orElseThrow(() -> new IllegalStateException("Dust without group!"));

            Placeholder[] placeholders = new Placeholder[] {
                    Placeholder.of("left_percentage", leftItem.getInteger("percentage")),
                    Placeholder.of("right_percentage", rightItem.getInteger("percentage"))
            };

            int successRate = getFromFormula("dust.percentage-formula", placeholders);

            Placeholder[] costPlaceholders = new Placeholder[] {
                    Placeholder.of("group_order_index", group.getOrder()),
                    Placeholder.of("final_success_rate", successRate),
            };

            ecoCost = getFromFormula("dust.eco-cost-formula", costPlaceholders);
            expCost = getFromFormula("dust.exp-cost-formula", costPlaceholders);

            Group newGroup = this.instance
                    .getGroupManager()
                    .getValues()
                    .stream()
                    .filter(s -> s.getOrder() == group.getOrder() + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No group higher than: " + group.getIdentifier()));

            getInventory().setItem(this.PREVIEW_SLOT, this.instance.getSpecialItems().getDust(newGroup, "magic", successRate, true));
        }

        addItem(this.ACCEPT_SLOT, new ItemBuilder(this.config.getConfigurationSection("contents.accept-after"),
                Placeholder.of("eco_cost", ecoCost),
                Placeholder.of("exp_cost", expCost)
        ).build(), event -> {
            if (!EconomyManager.hasBalance(event.getPlayer(), ecoCost) || getExp(event.getPlayer()) < expCost) {
                this.instance.getLocale().getMessage("alchemist.cannotafford").sendPrefixedMessage(event.getPlayer());
                return;
            }

            EconomyManager.withdrawBalance(event.getPlayer(), ecoCost);
            changeExp(event.getPlayer(), -expCost);
            this.instance.getLocale().getMessage("alchemist.success")
                    .processPlaceholder("eco_cost", ecoCost)
                    .processPlaceholder("exp_cost", expCost)
                    .sendPrefixedMessage(event.getPlayer());

            event.getPlayer().getInventory().addItem(getInventory().getItem(this.PREVIEW_SLOT));
            clear(this.RIGHT_SLOT);
            clear(this.LEFT_SLOT);
            event.getPlayer().closeInventory();
        });
    }

    private int getFromFormula(String path, Placeholder... placeholders) {
        String toTest = this.config.getString(path);

        for (Placeholder placeholder : placeholders)
            toTest = toTest.replace(placeholder.getPlaceholder(), placeholder.getToReplace().toString());

        return (int) Double.parseDouble(GeneralUtils.parseJS(toTest, "alchemist expression", 0).toString());
    }
}
