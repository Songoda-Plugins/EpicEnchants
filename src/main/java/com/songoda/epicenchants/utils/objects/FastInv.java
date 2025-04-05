package com.songoda.epicenchants.utils.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A fast API to easily create advanced GUI.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>
 *
 * @author MrMicky
 * @version 2.0.3 - Now supports async operations
 */
public class FastInv implements InventoryHolder {

    private static Plugin plugin = null;
    private boolean cancelTasksOnClose = true;
    private final Set<FastInvClickListener> clickListeners = new HashSet<>();
    private final Set<FastInvCloseListener> closeListeners = new HashSet<>();
    private Inventory inventory;
    private final Map<Integer, FastInvClickListener> itemListeners = new HashMap<>();
    private final Set<BukkitTask> tasks = new HashSet<>();

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  The size of the menus.
     * @param title The title (name) of the menus.
     */
    public FastInv(int size, String title) {
        this(size, InventoryType.CHEST, title);
    }

    private FastInv(int size, InventoryType type, String title) {
        if (plugin == null) {
            throw new IllegalStateException("FastInv is not initialised");
        }

        runSync(() -> {
            if (type == InventoryType.CHEST && size > 0) {
                this.inventory = Bukkit.createInventory(this, size, title);
            } else {
                this.inventory = Bukkit.createInventory(this, type, title);
            }
        });
    }

    /**
     * Register your FastInv instance.
     *
     * @param plugin The plugin that uses FastInv.
     */
    public static void init(Plugin plugin) {
        if (FastInv.plugin == null) {
            FastInv.plugin = plugin;
            Bukkit.getPluginManager().registerEvents(getListener(), plugin);
        }
    }

    public static class FastInvClickEvent extends FastInvEvent {
        private final InventoryClickEvent event;
        private final InventoryAction action;
        private final ClickType clickType;
        private final ItemStack item;
        private final int slot;

        private FastInvClickEvent(Player player, FastInv inventory, InventoryClickEvent event, int slot, ItemStack item,
                                  boolean cancelled, InventoryAction action, ClickType clickType) {
            super(player, inventory, cancelled);
            this.event = event;
            this.slot = slot;
            this.item = item;
            this.action = action;
            this.clickType = clickType;
        }

        /**
         * @return The action of the event
         */
        public InventoryAction getAction() {
            return this.action;
        }

        /**
         * @return The click type
         */
        public ClickType getClickType() {
            return this.clickType;
        }

        /**
         * Get the clicked {@link ItemStack}
         *
         * @return The clicked item
         */
        public ItemStack getItem() {
            return this.item;
        }

        /**
         * Get the number of the clicked slot
         *
         * @return The slot number
         */
        public int getSlot() {
            return this.slot;
        }

        public InventoryClickEvent getEvent() {
            return this.event;
        }
    }

    /**
     * Add an {@link ItemStack} to the menus.
     *
     * @param item The item to add
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(ItemStack item) {
        return addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the menus with a {@link FastInvClickListener} to handle clicks.
     *
     * @param item     The item to add.
     * @param listener The {@link FastInvClickListener} for the item.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(ItemStack item, FastInvClickListener listener) {
        runSync(() -> {
            int slot = this.inventory.firstEmpty();
            if (slot >= 0) {
                addItem(slot, item, listener);
            }
        });
        return this;
    }

    /**
     * Add an {@link ItemStack} to the menus on a specific slot.
     *
     * @param slot The slot of the item.
     * @param item The item to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int slot, ItemStack item) {
        return addItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the menus on specific slot with a {@link FastInvClickListener} to handle clicks.
     *
     * @param slot     The slot of the item.
     * @param item     The item to add.
     * @param listener The FastInvClickListener for the item.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int slot, ItemStack item, FastInvClickListener listener) {
        runSync(() -> {
            this.inventory.setItem(slot, item);

            if (listener != null) {
                this.itemListeners.put(slot, listener);
            } else {
                this.itemListeners.remove(slot);
            }
        });

        return this;
    }

    /**
     * Add an {@link ItemStack} to the menus on a range of slots.
     *
     * @param slotFrom Starting slot to put the item in.
     * @param slotTo   Ending slot to put the item in.
     * @param item     The item to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int slotFrom, int slotTo, ItemStack item) {
        return addItem(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the menus on a range of slots with a {@link FastInvClickListener} to handle clicks.
     *
     * @param slotFrom Starting slot to put the item in.
     * @param slotTo   Ending slot to put the item in.
     * @param item     The item to add.
     * @param listener The FastInvClickListener for the item.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int slotFrom, int slotTo, ItemStack item, FastInvClickListener listener) {
        for (int i = slotFrom; i <= slotTo; i++) {
            addItem(i, item, listener);
        }
        return this;
    }

    /**
     * Add an {@link ItemStack} to the menus on multiple slots.
     *
     * @param slots The slot of the item.
     * @param item  The item to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int[] slots, ItemStack item) {
        return addItem(slots, item, null);
    }

    /**
     * Clear a spot in the inventory.
     *
     * @param slot The slot to clear.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv clear(int slot) {
        return addItem(slot, null);
    }

    /**
     * Add an {@link ItemStack} to the menus on the edges.
     *
     * @param item The item to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv edge(ItemStack item) {
        int height = this.inventory.getSize() / 9;

        addItem(0, 9, item);
        addItem(this.inventory.getSize() - 9, this.inventory.getSize() - 1, item);

        for (int i = 0; i < height; i++) {
            addItem(i * 9, item);
            addItem(i * 9 + 8, item);
        }

        return this;
    }

    /**
     * Add an {@link ItemStack} to the menus on multiples slots with a {@link FastInvClickListener} to handle click.
     *
     * @param slots    The slots to place the item.
     * @param item     The item to add.
     * @param listener The FastInvClickListener for the item.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv addItem(int[] slots, ItemStack item, FastInvClickListener listener) {
        for (int slot : slots) {
            addItem(slot, item, listener);
        }
        return this;
    }

    public FastInv fill(ItemStack itemStack) {
        runSync(() -> {
            for (int i = 0; i < this.inventory.getSize(); i++) {
                if (this.inventory.getItem(i) == null) {
                    addItem(i, itemStack);
                }
            }
        });
        return this;
    }

    /**
     * Add a {@link FastInvCloseListener} to listen on menus close.
     *
     * @param listener The {@link FastInvCloseListener} to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv onClose(FastInvCloseListener listener) {
        this.closeListeners.add(listener);
        return this;
    }

    /**
     * Add a {@link FastInvClickListener} to listen on menus click.
     *
     * @param listener The {@link FastInvClickListener} to add.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv onClick(FastInvClickListener listener) {
        this.clickListeners.add(listener);
        return this;
    }

    /**
     * Schedule a task to run.
     *
     * @param period   Delay between each run.
     * @param runnable The {@link Runnable} task to run.
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv onUpdate(long period, Runnable runnable) {
        return onUpdate(period, period, runnable);
    }

    /**
     * Schedule a task to run with a delay before starting.
     *
     * @param delay    Ticks to wait before starting the task.
     * @param period   Delay between each run.
     * @param runnable The {@link Runnable} task to run.
     *
     * @return This FastInv instance, for chaining
     */
    public FastInv onUpdate(long delay, long period, Runnable runnable) {
        this.tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
        return this;
    }

    /**
     * Open the menus to a player.
     *
     * @param player The player to open the menu.
     */
    public void open(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(this.inventory));
    }

    /**
     * Cancel all tasks.
     */
    public void cancelTasks() {
        this.tasks.forEach(BukkitTask::cancel);
        this.tasks.clear();
    }

    /**
     * Run a task on the server primary thread.
     *
     * @param runnable The runnable to run on the main thread
     */
    public void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Get the Bukkit menus associated with this FastInv instance.
     *
     * @return The Bukkit {@link Inventory}.
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Set if the tasks will be canceled on menus close.
     *
     * @param cancelTasksOnClose Set if the tasks will be canceled
     *
     * @return This FastInv instance, for chaining.
     */
    public FastInv setCancelTasksOnClose(boolean cancelTasksOnClose) {
        this.cancelTasksOnClose = cancelTasksOnClose;
        return this;
    }

    public interface FastInvClickListener {
        void onClick(FastInvClickEvent event);
    }

    public interface FastInvCloseListener {
        void onClose(FastInvCloseEvent event);
    }

    public static abstract class FastInvEvent {
        private boolean cancelled;
        private final FastInv inventory;
        private final Player player;

        FastInvEvent(Player player, FastInv inventory, boolean cancelled) {
            this.player = player;
            this.inventory = inventory;
            this.cancelled = cancelled;
        }

        /**
         * Get the FastInv menus.
         *
         * @return This associated FastInv instance.
         */
        public FastInv getInventory() {
            return this.inventory;
        }

        /**
         * Get the {@link Player} who clicked.
         *
         * @return the player who clicked.
         */
        public Player getPlayer() {
            return this.player;
        }

        /**
         * Get if the event is cancelled or not.
         *
         * @return Whether the event was cancelled.
         */
        public boolean isCancelled() {
            return this.cancelled;
        }

        /**
         * Set if the event will be canceled or not.
         *
         * @param cancel Whether the event should be cancelled.
         */
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }
    }

    private static Listener getListener() {
        return new Listener() {

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (event.getInventory().getHolder() instanceof FastInv && event.getWhoClicked() instanceof Player) {
                    int slot = event.getRawSlot();
                    FastInv inv = (FastInv) event.getInventory().getHolder();

                    FastInvClickEvent clickEvent = new FastInvClickEvent((Player) event.getWhoClicked(), inv, event, slot,
                            event.getCurrentItem(), true, event.getAction(), event.getClick());

                    if (inv.itemListeners.containsKey(slot)) {
                        inv.itemListeners.get(slot).onClick(clickEvent);
                    }

                    inv.clickListeners.forEach(listener -> listener.onClick(clickEvent));

                    if (clickEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                if (event.getInventory().getHolder() instanceof FastInv && event.getPlayer() instanceof Player) {
                    Player player = (Player) event.getPlayer();
                    FastInv inv = (FastInv) event.getInventory().getHolder();

                    FastInvCloseEvent closeEvent = new FastInvCloseEvent(player, inv, false);
                    inv.closeListeners.forEach(listener -> listener.onClose(closeEvent));

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        // Tiny delay to prevent errors.
                        if (closeEvent.isCancelled() && player.isOnline()) {
                            player.openInventory(inv.getInventory());
                        } else if (inv.getInventory().getViewers().isEmpty() && inv.cancelTasksOnClose) {
                            inv.cancelTasks();
                        }
                    });
                }
            }

            @EventHandler
            public void onDisable(PluginDisableEvent event) {
                if (event.getPlugin().equals(plugin)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getOpenInventory().getTopInventory().getHolder() instanceof FastInv) {
                            player.closeInventory();
                        }
                    }
                }
            }
        };
    }

    public static class FastInvCloseEvent extends FastInvEvent {
        private FastInvCloseEvent(Player player, FastInv inventory, boolean cancelled) {
            super(player, inventory, cancelled);
        }
    }
}
