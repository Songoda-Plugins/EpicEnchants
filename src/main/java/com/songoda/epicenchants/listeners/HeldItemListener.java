package com.songoda.epicenchants.listeners;

import com.songoda.epicenchants.events.ArmorEquipEvent;
import com.songoda.epicenchants.events.HeldItemChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class HeldItemListener implements Listener {
    private static final boolean SWAP_OFFHAND_SUPPORTED = Arrays.stream(ClickType.values()).anyMatch(e -> e.name().equals("SWAP_OFFHAND"));

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent e) {
        boolean shift = false, numberKey = false, swapoffhand = false;

        if (e.getAction() == InventoryAction.NOTHING) {
            return;
        }

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.QUICKBAR && e.getSlotType() != SlotType.CONTAINER) {
            return;
        }

        if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) {
            shift = true;
        } else if (e.getClick().equals(ClickType.NUMBER_KEY)) {
            numberKey = true;
        } else if (SWAP_OFFHAND_SUPPORTED && e.getClick().equals(ClickType.valueOf("SWAP_OFFHAND"))) {
            swapoffhand = true;
        }

        //todo offhand
        Player player = (Player) e.getWhoClicked();
        int heldslot = player.getInventory().getHeldItemSlot();

        if (shift) {
            boolean equipping;
            boolean playerinv = e.getView().getTopInventory().getType() == InventoryType.CRAFTING; //only player inventory open
            if (playerinv) { //items are put in the left most free
                if (0 <= e.getSlot() && e.getSlot() <= 8) { //move up from hotbar
                    if (e.getSlot() != heldslot) //we only care about heldslot
                        return;
                    boolean slotFreeInInv = !IntStream.range(9, 36).allMatch(i -> !isAirOrNull(e.getClickedInventory().getItem(i)));
                    if (!slotFreeInInv) //can only move item if there is a space in inventory (assuming maxstacksize of currentItem == 1, we do not care about other items)
                        return;
                    equipping = false;
                } else { //possible equipping, determine target slot
                    int targetslot;
                    boolean slotFreeInInv = !IntStream.range(9, 36).allMatch(i -> !isAirOrNull(e.getClickedInventory().getItem(i)));
                    boolean currentSlotInInv = 9 <= e.getSlot() && e.getSlot() <= 35;
                    if (!slotFreeInInv || currentSlotInInv) {
                        if (ArmorEquipEvent.ArmorType.matchType(e.getCurrentItem()) != null) {//we do not care about armor things here
                            return;
                        }
                        //shift click and no free space in inv -> moving to hotbar left most free slot
                        OptionalInt freehotbarslot = IntStream.range(0, 9).filter(i -> isAirOrNull(e.getClickedInventory().getItem(i))).findFirst();
                        if (!freehotbarslot.isPresent()) //no free slot in hotbar -> no move, no event
                            return;
                        targetslot = freehotbarslot.getAsInt();
                    } else {
                        return; //item will go to inventory not hotbar
                    }
                    if (targetslot != heldslot)
                        return;
                    equipping = true;
                }
            } else { //top inventory not a player inv but chest, etc
                if (e.getClickedInventory() == e.getView().getBottomInventory()) {
                    if (e.getSlot() != heldslot) //we only care about heldslot
                        return;
                    if (e.getView().getTopInventory().firstEmpty() == -1) //top inv full
                        return; //nothing happens
                    equipping = false;
                } else { //shift click in upper inv
                    //item will be moved to hotbar if possible, starting from right most hotbar slot
                    OptionalInt freehotbarslot = IntStream.range(0, 9).map(i -> 8 - i).filter(i -> isAirOrNull(e.getView().getBottomInventory().getItem(i))).findFirst();
                    if (!freehotbarslot.isPresent()) {
                        return; //will not be placed in hotbar
                    }
                    if (freehotbarslot.getAsInt() != heldslot)
                        return;
                    equipping = true;
                }
            }
            HeldItemChangedEvent heldItemChangedEvent = new HeldItemChangedEvent((Player) e.getWhoClicked(), HeldItemChangedEvent.EquipMethod.SHIFT_CLICK, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
            Bukkit.getServer().getPluginManager().callEvent(heldItemChangedEvent);
            if (heldItemChangedEvent.isCancelled()) {
                e.setCancelled(true);
            }
        } else {
            ItemStack newItem = e.getCursor();
            ItemStack oldItem = e.getCurrentItem();
            HeldItemChangedEvent.EquipMethod method;
            if (numberKey) {
                if (!(e.getClickedInventory() instanceof PlayerInventory)
                        || e.getHotbarButton() != heldslot) //we only care about heldslot
                    return;
                method = HeldItemChangedEvent.EquipMethod.HOTBAR_SWAP;
                newItem = e.getCurrentItem();
                oldItem = e.getClickedInventory().getItem(e.getHotbarButton());
            } else if (swapoffhand) {
                if (!(e.getClickedInventory() instanceof PlayerInventory)
                        || e.getSlot() != heldslot) //we only care about heldslot
                    return;
                method = HeldItemChangedEvent.EquipMethod.OFFHAND_SWAP;
                newItem = e.getClickedInventory().getItem(40);
                oldItem = e.getCurrentItem();
            } else {
                if (!(e.getClickedInventory() instanceof PlayerInventory)
                        || e.getSlot() != heldslot) //we only care about heldslot
                    return;
                method = HeldItemChangedEvent.EquipMethod.PICK_DROP;
            }
            HeldItemChangedEvent heldItemChangedEvent = new HeldItemChangedEvent((Player) e.getWhoClicked(), method, oldItem, newItem);
            Bukkit.getServer().getPluginManager().callEvent(heldItemChangedEvent);
            if (heldItemChangedEvent.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        if (event.getRawSlots().isEmpty())
            return;

        int rawslot = event.getRawSlots().stream().findFirst().orElse(0);
        int invslot = event.getView().convertSlot(rawslot);
        boolean bottominventory = rawslot != invslot;

        if (bottominventory && event.getWhoClicked().getInventory().getHeldItemSlot() == invslot) {
            HeldItemChangedEvent heldItemChangedEvent = new HeldItemChangedEvent((Player) event.getWhoClicked(), HeldItemChangedEvent.EquipMethod.DRAG, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(heldItemChangedEvent);
            if (heldItemChangedEvent.isCancelled()) {
                event.setResult(Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e) {
        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();

        //find in hotbar
        int hotbarslot = -1;
        ItemStack broken = inv.getItem(inv.getHeldItemSlot());
        if (!isAirOrNull(broken) && broken.equals(e.getBrokenItem()))
            hotbarslot = inv.getHeldItemSlot();

        if (hotbarslot != -1) {
            HeldItemChangedEvent heldItemChangedEvent = new HeldItemChangedEvent(p, HeldItemChangedEvent.EquipMethod.BROKE, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(heldItemChangedEvent);
            if (heldItemChangedEvent.isCancelled()) {
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                p.getInventory().setItem(hotbarslot, i);
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        ItemStack i = event.getEntity().getInventory().getItem(event.getEntity().getInventory().getHeldItemSlot());
        if (!isAirOrNull(i)) {
            Bukkit.getServer().getPluginManager().callEvent(new HeldItemChangedEvent(event.getEntity(), HeldItemChangedEvent.EquipMethod.DEATH, i, null));
        }
        // No way to cancel a death event.
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        //there is no clear way to determine whether the player pressed Q or dropped the item from and inventory view,
        //as Player.getOpenInventory().getType will always return CRAFTING whether the player has open their invntory or not
        //we try to make a best efford solution
        //when the item in the held slot is null we assume it was dropped by pressing Q
        if (isAirOrNull(event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot())))
            Bukkit.getServer().getPluginManager().callEvent(new HeldItemChangedEvent(event.getPlayer(), HeldItemChangedEvent.EquipMethod.DROP_ITEM, event.getItemDrop().getItemStack(), null));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getMaxStackSize() == 1) { //tools maxStackSize is 1, ignore other items
            int firstEmpty = event.getPlayer().getInventory().firstEmpty();
            if (0 <= firstEmpty && firstEmpty <= 8
                    && event.getPlayer().getInventory().getHeldItemSlot() == firstEmpty)
                Bukkit.getServer().getPluginManager().callEvent(new HeldItemChangedEvent(event.getPlayer(), HeldItemChangedEvent.EquipMethod.PICKUP_ITEM, null, event.getItem().getItemStack()));
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    private boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
