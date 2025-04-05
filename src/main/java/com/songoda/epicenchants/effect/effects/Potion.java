package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.enums.TriggerType;
import com.songoda.epicenchants.objects.LeveledModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Potion extends EffectExecutor {
    public Potion(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        if (!getSection().isString("potion-type")) {
            return;
        }

        LeveledModifier amplifier = LeveledModifier.of(getSection().getString("amplifier"));
        PotionEffectType effectType = PotionEffectType.getByName(getSection().getString("potion-type"));

        if (effectType == null) {
            return;
        }

        if (getTriggerTypes().contains(TriggerType.STATIC_EFFECT) || getTriggerTypes().contains(TriggerType.HELD_ITEM)) {
            if (eventType == EventType.ON) {
                consume(entity -> entity.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, (int) amplifier.get(level - 1, 0, user, opponent), false, false)), user, opponent);
            } else if (eventType == EventType.OFF) {
                consume(entity -> entity.removePotionEffect(effectType), user, opponent);
            }

            return;
        }

        LeveledModifier duration = LeveledModifier.of(getSection().getString("duration"));

        consume(
                entity -> entity.addPotionEffect(new PotionEffect(effectType,
                        (int) duration.get(level, 60, user, opponent) * 20,
                        (int) amplifier.get(level - 1, 0, user, opponent),
                        false,
                        false)),
                user,
                opponent
        );
    }
}
