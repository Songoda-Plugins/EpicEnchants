package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.songoda.epicenchants.utils.single.Experience.changeExp;
import static com.songoda.epicenchants.utils.single.Experience.getExp;

public class StealExp extends EffectExecutor {
    public StealExp(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        double amount = getAmount().get(level, 0, user, opponent);

        if (!(opponent instanceof Player)) {
            return;
        }

        Player opponentPlayer = (Player) opponent;

        if (amount > getExp(opponentPlayer)) {
            amount = getExp(opponentPlayer);
        }

        if (getExp(opponentPlayer) - amount <= 0) {
            changeExp(opponentPlayer, 0);
        } else {
            changeExp(opponentPlayer, (int) -amount);
        }

        if (getExp(user) + amount <= 0) {
            changeExp(user, 0);
        } else {
            changeExp(user, (int) amount);
        }
    }
}
