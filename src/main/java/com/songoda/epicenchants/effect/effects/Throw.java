package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.objects.LeveledModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Throw extends EffectExecutor {
    public Throw(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(@NotNull Player user, LivingEntity opponent, int level, EventType eventType) {
        if (!getSection().isString("direction")) {
            return;
        }

        if (who() == Who.OPPONENT && opponent == null) {
            return;
        }

        Vector vector;
        double magnitude = LeveledModifier.of(getSection().getString("magnitude")).get(level, 0.1, user, opponent);
        LivingEntity livingEntity = who() == Who.USER ? user : opponent;
        String relativeToStr = getSection().getString("relative-to");
        LivingEntity relative = relativeToStr == null || relativeToStr.equalsIgnoreCase("user") ? user : opponent;

        switch (getSection().getString("direction").toLowerCase()) {
            case "up":
                vector = new Vector(0, magnitude, 0);
                break;
            case "down":
                vector = new Vector(0, -magnitude, 0);
                break;
            case "backward":
                vector = relative.getLocation().getDirection().multiply(-magnitude);
                break;
            case "forward":
                vector = relative.getLocation().getDirection().multiply(magnitude);
                break;
            default:
                vector = new Vector();
        }

        if (vector.length() != 0) {
            livingEntity.setVelocity(vector);
        }
    }
}
