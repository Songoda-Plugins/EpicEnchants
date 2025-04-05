package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StealFood extends EffectExecutor {
    public StealFood(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        int amount = (int) Math.floor(getAmount().get(level, 0, user, opponent));

        if (!(opponent instanceof Player)) {
            return;
        }

        Player opponentPlayer = ((Player) opponent);

        int opponentFood = opponentPlayer.getFoodLevel() - amount;
        int userFood = user.getFoodLevel() + amount;

        if (opponentFood <= 0) {
            opponentPlayer.setFoodLevel(0);
        } else {
            opponentPlayer.setFoodLevel(Math.min(opponentFood, 20));
        }

        if (userFood <= 0) {
            user.setFoodLevel(0);
        } else {
            user.setFoodLevel(Math.min(userFood, 20));
        }
    }
}
