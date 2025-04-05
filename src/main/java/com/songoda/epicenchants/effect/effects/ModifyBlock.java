package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectEventExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ModifyBlock extends EffectEventExecutor {
    public ModifyBlock(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player user, LivingEntity opponent, int level, Event event, EventType eventType) {
        if (!(event instanceof BlockEvent) && !(event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).hasBlock())) {
            return;
        }

        Block block = event instanceof BlockEvent ? ((BlockEvent) event).getBlock() : ((PlayerInteractEvent) event).getClickedBlock();

        if (getSection().getBoolean("break-naturally")) {
            block.breakNaturally();
            return;
        }
        block.setType(Material.getMaterial(getSection().getString("material")));
    }
}
