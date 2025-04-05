package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DropHeld extends EffectExecutor {
    public DropHeld(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        consume(entity -> {
            Player player = ((Player) entity);
            if (player.getItemInHand().getType() != Material.AIR) {
                entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemBuilder(player.getItemInHand()).build());
                player.setItemInHand(null);
            }
        }, user, opponent);
    }
}
