package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StealHealth extends EffectExecutor {
    public StealHealth(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        double amount = getAmount().get(level, 0, user, opponent);

        if (opponent == null) {
            return;
        }

        double opponentHealth = opponent.getHealth() - amount;
        double userHealth = user.getHealth() + amount;

        if (opponentHealth <= 0) {
            opponent.setHealth(0);
        } else if (opponentHealth > opponent.getMaxHealth()) {
            opponent.setHealth(opponent.getMaxHealth());
        } else {
            opponent.setHealth(opponentHealth);
        }

        if (userHealth <= 0) {
            user.setHealth(0);
        } else if (userHealth > user.getMaxHealth()) {
            user.setHealth(user.getMaxHealth());
        } else {
            user.setHealth(userHealth);
        }
    }
}
