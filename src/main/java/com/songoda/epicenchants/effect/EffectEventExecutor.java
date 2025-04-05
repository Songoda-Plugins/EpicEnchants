package com.songoda.epicenchants.effect;

import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class EffectEventExecutor extends EffectExecutor {
    public EffectEventExecutor(ConfigurationSection section) {
        super(section);
    }

    public abstract void execute(Player user, LivingEntity opponent, int level, Event event, EventType eventType);

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        throw new UnsupportedOperationException("This method can not be called on EventEffects");
    }
}
