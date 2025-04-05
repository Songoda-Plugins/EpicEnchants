package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectEventExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ModifyOxygen extends EffectEventExecutor {
    public ModifyOxygen(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player user, LivingEntity opponent, int level, Event event, EventType eventType) {
        consume(entity -> {
            double amount = getAmount().get(level, 0, user, opponent);
            if (entity.getRemainingAir() + amount > entity.getMaximumAir()) {
                entity.setRemainingAir(entity.getMaximumAir());
            } else if (entity.getRemainingAir() + amount < 0) {
                entity.setRemainingAir(0);
            } else {
                entity.setRemainingAir(entity.getRemainingAir() + (int) amount);
            }
        }, user, opponent);
    }
}
