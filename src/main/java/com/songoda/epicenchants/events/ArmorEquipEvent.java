package com.songoda.epicenchants.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorType type;
    private ItemStack oldArmorPiece, newArmorPiece;

    /**
     * Constructor for the ArmorEquipEvent.
     *
     * @param who        The player who put on / removed the armor.
     * @param type          The ArmorType of the armor added
     * @param oldArmorPiece The ItemStack of the armor removed.
     * @param newArmorPiece The ItemStack of the armor added.
     */
    public ArmorEquipEvent(final Player who, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece) {
        super(who);
        this.equipType = equipType;
        this.type = type;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled.
     */
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public boolean isCancelled() {
        return this.cancel;
    }

    public ArmorType getType() {
        return this.type;
    }

    /**
     * Returns the last equipped armor piece, could be a piece of armor, {@link Material#AIR}, or null.
     */
    public ItemStack getOldArmorPiece() {
        return this.oldArmorPiece;
    }

    public void setOldArmorPiece(final ItemStack oldArmorPiece) {
        this.oldArmorPiece = oldArmorPiece;
    }

    /**
     * Returns the newly equipped armor, could be a piece of armor, {@link Material#AIR}, or null.
     */
    public ItemStack getNewArmorPiece() {
        return this.newArmorPiece;
    }

    public void setNewArmorPiece(final ItemStack newArmorPiece) {
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets the method used to either equip or unequip an armor piece.
     */
    public EquipMethod getMethod() {
        return this.equipType;
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
         * When you right-click an armor piece in the hotbar without the inventory open to equip.
         */
        HOTBAR,
        /**
         * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * When in range of a dispenser that shoots an armor piece to equip
         */
        DISPENSER,
        /**
         * When an armor piece is removed due to it losing all durability
         */
        BROKE,
        /**
         * When you die causing all armor to unequip
         */
        DEATH,
    }

    public enum ArmorType {
        HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);

        private final int slot;

        ArmorType(int slot) {
            this.slot = slot;
        }

        /**
         * Attempts to match the ArmorType for the specified ItemStack.
         *
         * @param itemStack The ItemStack to parse the type of.
         *
         * @return The parsed ArmorType. (null if none were found.)
         */
        public static ArmorType matchType(final ItemStack itemStack) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return null;
            }

            String type = itemStack.getType().name();
            if (type.endsWith("_HELMET") || type.endsWith("_SKULL")) {
                return HELMET;
            } else if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA")) {
                return CHESTPLATE;
            } else if (type.endsWith("_LEGGINGS")) {
                return LEGGINGS;
            } else if (type.endsWith("_BOOTS")) {
                return BOOTS;
            } else {
                return null;
            }
        }

        public int getSlot() {
            return this.slot;
        }
    }
}
