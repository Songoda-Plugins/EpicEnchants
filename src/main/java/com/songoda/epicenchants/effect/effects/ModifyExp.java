package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.objects.LeveledModifier;
import com.songoda.epicenchants.utils.single.Experience;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifyExp extends EffectExecutor {
    public ModifyExp(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        consume(entity -> {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (getSection().getString("amount").endsWith("L")) {
                    player.setLevel((int) (player.getLevel() + LeveledModifier.of(getSection().getString("amount").replace("L", "")).get(level, 0, user, opponent)));
                } else {
                    Experience.changeExp(player, (int) getAmount().get(level, 0, user, opponent));
                }
            }
        }, user, opponent);
    }
}
