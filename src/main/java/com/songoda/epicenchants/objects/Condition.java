package com.songoda.epicenchants.objects;

import com.songoda.epicenchants.utils.single.GeneralUtils;
import com.songoda.epicenchants.utils.single.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class Condition {
    private final String string;

    private Condition(String string) {
        this.string = string;
    }

    public static Condition of(String string) {
        return new Condition(string);
    }

    public boolean get(Player user, @Nullable LivingEntity attacker, int level, @Nullable Event event, boolean def) {
        if (this.string == null || this.string.isEmpty()) {
            return true;
        }

        String toValidate = ChatColor.stripColor(Placeholders.setPlaceholders(this.string, user, attacker, level, event));

        return (boolean) GeneralUtils.parseJS(toValidate, "condition", def);
    }
}
