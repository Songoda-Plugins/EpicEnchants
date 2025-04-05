package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifyFood extends EffectExecutor {
    public ModifyFood(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        consume(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).setFoodLevel((int) (((Player) entity).getFoodLevel() + getAmount().get(level, 0, user, opponent)));
            }
        }, user, opponent);
    }
}
