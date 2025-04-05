package com.songoda.epicenchants.listeners.item;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ItemListener implements Listener {
    final EpicEnchants instance;

    ItemListener(EpicEnchants instance) {
        this.instance = instance;
    }

    abstract void onApply(InventoryClickEvent event, NBTItem cursor, NBTItem current);

    void onClick(PlayerInteractEvent event, NBTItem clicked) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCursor() == null || event.getCurrentItem() == null || event.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
            return;
        }

        onApply(event, new NBTItem(event.getCursor()), new NBTItem(event.getCurrentItem()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
            return;
        }

        onClick(event, new NBTItem(event.getItem()));
    }

    void useItem(InventoryClickEvent event) {
        if (event.getCursor().getAmount() > 1) {
            ItemStack cursor = event.getCursor();
            cursor.setAmount(cursor.getAmount() - 1);
            event.getWhoClicked().setItemOnCursor(cursor);
            return;
        }

        event.getWhoClicked().setItemOnCursor(null);
    }

    void useItem(PlayerInteractEvent event) {
        int slot = event.getPlayer().getInventory().getHeldItemSlot();

        if (CompatibleHand.getHand(event) == CompatibleHand.OFF_HAND) {
            slot = 40;
        }

        if (event.getItem().getAmount() > 1) {
            ItemStack item = event.getItem();
            item.setAmount(item.getAmount() - 1);
            event.getPlayer().getInventory().setItem(slot, item);
            return;
        }

        event.getPlayer().getInventory().clear(slot);
    }
}
