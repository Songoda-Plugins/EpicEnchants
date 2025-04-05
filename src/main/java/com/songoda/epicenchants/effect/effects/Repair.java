package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Repair extends EffectExecutor {
    public Repair(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        consume(livingEntity -> {
            if (livingEntity instanceof Player) {
                ((Player) livingEntity).getItemInHand().setDurability((short) 0);
            }
        }, user, opponent);
    }
}
