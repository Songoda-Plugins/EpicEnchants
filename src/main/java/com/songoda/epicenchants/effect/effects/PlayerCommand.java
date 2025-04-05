package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import com.songoda.epicenchants.utils.single.Placeholders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.songoda.epicenchants.enums.EventType.NONE;
import static com.songoda.epicenchants.enums.EventType.ON;

public class PlayerCommand extends EffectExecutor {
    public PlayerCommand(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        if (eventType != ON && eventType != NONE) {
            return;
        }

        if (who() == Who.OPPONENT && !(opponent instanceof Player)) {
            return;
        }

        consume(entity -> GeneralUtils.getString(getSection(), "message")
                .stream()
                .map(s -> Placeholders.setPlaceholders(s, user, opponent, level))
                .forEach(((Player) entity)::performCommand), user, opponent);
    }
}
