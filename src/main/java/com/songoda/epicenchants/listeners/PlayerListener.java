package com.songoda.epicenchants.listeners;

import com.songoda.epicenchants.events.ArmorEquipEvent;
import com.songoda.epicenchants.events.HeldItemChangedEvent;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

import static com.songoda.epicenchants.enums.EventType.OFF;
import static com.songoda.epicenchants.enums.EventType.ON;
import static com.songoda.epicenchants.enums.TriggerType.*;

public class PlayerListener implements Listener {
    private final EpicEnchants instance;

    public PlayerListener(EpicEnchants instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorEquip(ArmorEquipEvent event) {
        Map<Enchant, Integer> oldArmorMap = this.instance.getEnchantUtils().getEnchants(event.getOldArmorPiece());
        Map<Enchant, Integer> newArmorMap = this.instance.getEnchantUtils().getEnchants(event.getNewArmorPiece());

        oldArmorMap.forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, STATIC_EFFECT, OFF));
        newArmorMap.forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, STATIC_EFFECT, ON));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHeldItemChanged(HeldItemChangedEvent event) {
        Map<Enchant, Integer> oldItemMap = this.instance.getEnchantUtils().getEnchants(event.getOldItem());
        Map<Enchant, Integer> newItemMap = this.instance.getEnchantUtils().getEnchants(event.getNewItem());

        oldItemMap.forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, HELD_ITEM, OFF));
        newItemMap.forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, HELD_ITEM, ON));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        this.instance.getEnchantUtils().getEnchants(event.getPlayer().getInventory().getItem(event.getPreviousSlot()))
                .forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, HELD_ITEM, OFF));

        this.instance.getEnchantUtils().getEnchants(event.getPlayer().getInventory().getItem(event.getNewSlot()))
                .forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, HELD_ITEM, ON));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.instance.getEnchantUtils().handlePlayer(event.getEntity(), event.getEntity().getKiller(), event, DEATH);

        if (event.getEntity().getKiller() != null) {
            this.instance.getEnchantUtils().handlePlayer(event.getEntity().getKiller(), event.getEntity(), event, KILLED_PLAYER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, RIGHT_CLICK);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
            this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, LEFT_CLICK);
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, RIGHT_CLICK_BLOCK);
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, LEFT_CLICK_BLOCK);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, BLOCK_BREAK);
        if (event.getExpToDrop() != 0) {
            this.instance.getEnchantUtils().handlePlayer(event.getPlayer(), null, event, EXPERIENCE_BLOCK_BREAK);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer()
                .getActivePotionEffects()
                .stream()
                .filter(potion -> potion.getDuration() >= 32760)
                .forEach(potionEffect -> event.getPlayer().removePotionEffect(potionEffect.getType()));

        Arrays.stream(event.getPlayer().getInventory().getArmorContents())
                .forEach(itemStack -> this.instance
                        .getEnchantUtils()
                        .getEnchants(itemStack)
                        .forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, STATIC_EFFECT, ON)));
        ItemStack mainHand = event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot());
        if (isItem(mainHand)) {
            this.instance
                    .getEnchantUtils()
                    .getEnchants(mainHand)
                    .forEach((enchant, level) -> enchant.onAction(event.getPlayer(), null, event, level, HELD_ITEM, ON));
        }
    }

    private boolean isItem(ItemStack is) {
        return is != null && is.getType() != Material.AIR && is.getAmount() > 0;
    }
}
