package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ModifyHealth extends EffectExecutor {

    public ModifyHealth(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        consume(entity -> {
            double amount = getAmount().get(level, 0, user, opponent);
            if (entity.getHealth() + amount > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            } else if (entity.getHealth() + amount < 0) {
                entity.setHealth(0D);
            } else {
                entity.setHealth(entity.getHealth() + amount);
            }
        }, user, opponent);
    }
}
