package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectEventExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class CancelEvent extends EffectEventExecutor {
    public CancelEvent(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player user, LivingEntity opponent, int level, Event event, EventType eventType) {
        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
    }
}
