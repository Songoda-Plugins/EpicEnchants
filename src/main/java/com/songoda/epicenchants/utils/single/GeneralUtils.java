package com.songoda.epicenchants.utils.single;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.math.MathUtils;
import com.songoda.epicenchants.enums.EnchantResult;
import com.songoda.epicenchants.enums.TriggerType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GeneralUtils {

    public static boolean chance(int chance) {
        return chance((double) chance);
    }

    public static boolean chance(double chance) {
        return ThreadLocalRandom.current().nextDouble(101) < chance;
    }

    public static String color(String input) {
        return format(input, "", null);
    }

    public static List<String> getString(ConfigurationSection section, String path) {
        return section.isList(path) ? section.getStringList(path) : Collections.singletonList(section.getString(path));
    }

    public static String format(String input, String placeholder, Object toReplace) {
        return ChatColor.translateAlternateColorCodes('&', input).replaceAll(placeholder, toReplace == null ? "" : toReplace.toString());
    }

    public static String getMessageFromResult(EnchantResult result) {
        return "enchants." + result.toString().toLowerCase().replace("_", "");
    }

    public static <X> X getRandomElement(Set<X> set) {
        int item = ThreadLocalRandom.current().nextInt(set.size());
        int i = 0;
        for (X obj : set) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    public static int[] getSlots(String string) {
        return Arrays.stream(string.split(",")).filter(StringUtils::isNumeric).mapToInt(Integer::parseInt).toArray();
    }

    public static Set<TriggerType> parseTrigger(String triggers) {
        return triggers == null ? Collections.emptySet() : Arrays.stream(triggers.replaceAll("\\s+", "").split(",")).map(TriggerType::valueOf).collect(Collectors.toSet());
    }

    public static ItemStack getHeldItem(LivingEntity entity, Event event) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            int slot = getHeldItemSlot(player, event);
            return player.getInventory().getItem(slot);
        } else if (entity.getEquipment() != null) {
            ItemStack item = entity.getEquipment().getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                return CompatibleHand.OFF_HAND.getItem(entity);
            }
            return item;
        }
        return null;
    }

    public static int getHeldItemSlot(Player player, Event event) {
        if (event == null) {
            return player.getInventory().getHeldItemSlot();
        }
        if (CompatibleHand.getHand(event) == CompatibleHand.OFF_HAND) {
            return 40;
        }
        return player.getInventory().getHeldItemSlot();
    }

    public static Object parseJS(String toParse, String type, Object def) {
        toParse = toParse.trim();
        // Handle logical AND (&&) and OR (||)
        if (toParse.contains("&&") || toParse.contains("||")) {
            String[] parts;
            boolean isAnd = toParse.contains("&&");

            if (isAnd) {
                parts = toParse.split("\\s*&&\\s*");
            } else {
                parts = toParse.split("\\s*\\|\\|\\s*");
            }
            boolean result = isAnd; // Start with true for AND, false for OR

            for (String part : parts) {
                Object evalResult = parseJS(part, type, def); // Recursively evaluate each condition

                if (!(evalResult instanceof Boolean)) {
                    throw new RuntimeException("[EpicEnchants] Invalid boolean expression: " + part);
                }

                if (isAnd) {
                    result &= (boolean) evalResult; // AND logic
                } else {
                    result |= (boolean) evalResult; // OR logic
                }
            }
            return result;
        }
        // Handle strict equality (===) for numbers and strings
        if (toParse.matches("^(\\S+)\\s*===\\s*(\\S+)$")) {
            String[] parts = toParse.split("\\s*===\\s*", 2);
            String left = parts[0];
            String right = parts[1];
            // Try parsing as numbers first
            try {
                double leftNum = Double.parseDouble(left);
                double rightNum = Double.parseDouble(right);
                return leftNum == rightNum;
            } catch (NumberFormatException ignored) {
                // Not numbers, compare as strings
            }
            return left.equals(right);
        }
        // Handle > and < by returning the greater or smaller number
        if (toParse.matches("^\\s*\\d+(?:\\.\\d+)?\\s*(>|<)\\s*\\d+(?:\\.\\d+)?\\s*$")) {
            toParse = toParse.replaceAll("\\s+", ""); // Remove spaces for easier parsing
            char operator = toParse.contains(">") ? '>' : '<';
            String[] parts = toParse.split("[<>]");

            double left = Double.parseDouble(parts[0]);
            double right = Double.parseDouble(parts[1]);

            return (operator == '>') ? Math.max(left, right) : Math.min(left, right);
        }
        // Forward everything else to Eval
        return MathUtils.eval(toParse, "[EpicEnchants] One of your " + type + " expressions is not properly formatted.");
    }


}
