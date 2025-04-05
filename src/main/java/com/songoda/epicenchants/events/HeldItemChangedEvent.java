package com.songoda.epicenchants.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public final class HeldItemChangedEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ItemStack oldItem, newItem;

    /**
     * Constructor for the HeldItemChangedEvent.
     *
     * @param who     The player who put (un)equipped an item.
     * @param oldItem The ItemStack removed.
     * @param newItem The ItemStack added.
     */
    public HeldItemChangedEvent(final Player who, final EquipMethod equipType, final ItemStack oldItem, final ItemStack newItem) {
        super(who);
        this.equipType = equipType;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    /**
     * Gets a list of handlers handling this event
     *
     * @return A list of handlers handling this event
     */
    @Override
    public final HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Sets if this event should be cancelled
     *
     * @param cancel If this event should be cancelled
     */
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets if this event is cancelled
     *
     * @return If this event is cancelled
     */
    public final boolean isCancelled() {
        return cancel;
    }

    /**
     * Returns the last equipped item, could be an item, {@link Material#AIR}, or null.
     */
    public final ItemStack getOldItem() {
        return oldItem;
    }

    /**
     * Returns the newly equipped item, could be an item, {@link Material#AIR}, or null.
     */
    public final ItemStack getNewItem() {
        return newItem;
    }

    /**
     * Gets the method used to either equip or unequip an item.
     */
    public EquipMethod getMethod() {
        return equipType;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum EquipMethod {// These have got to be the worst documentations ever.
        /**
         * When you shift-click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        DRAG,
        /**
         * When you manually equip or unequip the item. Use to be DRAG
         */
        PICK_DROP,
        /**
         * When you press the hotbar slot number while hovering over the held item slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * When you press the offhand swap key while hovering over the held item slot to equip or unequip
         */
        OFFHAND_SWAP,
        /**
         * When an item, e.g., tool, is removed due to it losing all durability
         */
        BROKE,
        /**
         * When you die causing all items to unequip
         */
        DEATH,
        /**
         * When an item is picked up into the selected hotbar slot by the player
         */
        DROP_ITEM,
        /**
         * When an item is dropped from the selected hotbar slot by the player
         */
        PICKUP_ITEM,
        ;
    }
}
