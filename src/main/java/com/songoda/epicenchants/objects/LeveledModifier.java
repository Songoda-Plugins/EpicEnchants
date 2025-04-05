package com.songoda.epicenchants.objects;

import com.songoda.epicenchants.utils.single.GeneralUtils;
import com.songoda.epicenchants.utils.single.Placeholders;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LeveledModifier {
    private final String string;

    private LeveledModifier(String string) {
        this.string = string;
    }

    public static LeveledModifier of(String string) {
        return new LeveledModifier(string);
    }

    public double get(int level, double def, Player user, LivingEntity opponent) {
        if (this.string == null || this.string.isEmpty()) {
            return def;
        }

        if (this.string.equalsIgnoreCase("MAX")) {
            return Integer.MAX_VALUE;
        }

        String toTest = Placeholders.setPlaceholders(this.string, user, opponent, level);

        Object value = GeneralUtils.parseJS(toTest, "LeveledModifier", def);
        return value instanceof Double ? (double) value : (int) value;
    }
}
