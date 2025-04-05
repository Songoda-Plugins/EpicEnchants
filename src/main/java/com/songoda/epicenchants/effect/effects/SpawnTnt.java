package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.objects.LeveledModifier;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.ThreadLocalRandom.current;

public class SpawnTnt extends EffectExecutor {
    public SpawnTnt(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        consume(player -> {
            for (int i = 0; i < LeveledModifier.of(getSection().getString("amount")).get(level, 1, user, opponent); i++) {
                Location spawnLocation = player.getLocation().clone().add(current().nextInt(-3, 3), 0, current().nextInt(-3, 3));
                int y = player.getLocation().getWorld().getHighestBlockAt(spawnLocation).getY();

                if (y < player.getLocation().getY() - 10 || y > player.getLocation().getY() + 10) {
                    continue;
                }

                TNTPrimed tntPrimed = (TNTPrimed) player.getWorld().spawnEntity(spawnLocation, EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks((int) LeveledModifier.of(getSection().getString("fuse")).get(level, 60, user, opponent));
                tntPrimed.setCustomName("ee");
                tntPrimed.setCustomNameVisible(false);
            }
        }, user, opponent);
    }
}
