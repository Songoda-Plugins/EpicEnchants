package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveEffect extends EffectExecutor {
    public RemoveEffect(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        consume(entity -> {
            if (!getSection().isString("potion-type")) {
                entity.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(entity::removePotionEffect);
            } else {
                entity.removePotionEffect(PotionEffectType.getByName(getSection().getString("potion-type")));
            }
        }, user, opponent);
    }
}
