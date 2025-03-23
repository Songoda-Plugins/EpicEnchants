package com.craftaro.epicenchants.utils.single;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.craftaro.epicenchants.utils.single.GeneralUtils.getHeldItem;

public class Placeholders {
    private static final Map<String, Function<Event, String>> EVENT_FUNCTIONS = new HashMap<String, Function<Event, String>>() {{
        put("{block_type}", event -> {
            if (event instanceof BlockBreakEvent) {
                return ((BlockBreakEvent) event).getBlock().getType().toString();
            } else if (event instanceof BlockPlaceEvent) {
                return ((BlockPlaceEvent) event).getBlockPlaced().getType().toString();
            } else if (event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).hasBlock()) {
                return ((PlayerInteractEvent) event).getClickedBlock().getType().toString();
            } else return "N/A";
        });

        put("{clicked_type}", event -> {
            if (event instanceof PlayerInteractEntityEvent) {
                return ((PlayerInteractEntityEvent) event).getRightClicked().getType().toString();
            } else return "N/A";
        });
    }};

    private static final Map<String, BiFunction<Player, LivingEntity, Object>> PLAYER_FUNCTIONS = new HashMap<String, BiFunction<Player, LivingEntity, Object>>() {{
        put("{user_name}", (user, opponent) -> user.getName());
        put("{opponent_name}", (user, opponent) -> opponent == null ? "" : opponent.getName());

        put("{user_health}", (user, opponent) -> user.getHealth());
        put("{opponent_health}", (user, opponent) -> opponent == null ? -1 : opponent.getHealth());

        put("{user_food}", (user, opponent) -> user.getFoodLevel());
        put("{opponent_food}", (user, opponent) -> opponent instanceof Player ? ((Player) opponent).getFoodLevel() : 0);

        put("{user_is_sneaking}", (user, opponent) -> user.isSneaking());
        put("{opponent_is_sneaking}", (user, opponent) -> opponent instanceof Player && ((Player) opponent).isSneaking());

        put("{user_holding}", (user, opponent) -> Optional.ofNullable(getHeldItem(user, null)).map(ItemStack::getType).orElse(Material.AIR));
        put("{opponent_holding}", (user, opponent) -> opponent != null ? Optional.ofNullable(getHeldItem(opponent, null)).map(ItemStack::getType).orElse(Material.AIR) : "N/A");

        put("{user_is_swimming}", (user, opponent) -> user.getLocation().getBlock().isLiquid());
        put("{opponent_is_swimming}", (user, opponent) -> opponent != null && opponent.getLocation().getBlock().isLiquid());

        put("{world}", (user, opponent) -> user.getWorld().getName());
        put("{players_near}", (user, opponent) -> user.getNearbyEntities(4, 4, 4).size());
        put("{user_on_fire}", (user, opponent) -> user.getFireTicks() != 0);
        put("{opponent_on_fire}", (user, opponent) -> opponent != null && opponent.getFireTicks() != 0);
    }};

    private static final Set<Consumer<AtomicReference<String>>> REGEX_CONSUMERS = new HashSet<Consumer<AtomicReference<String>>>() {{
        add(reference -> {
            Pattern pattern = Pattern.compile("\\{random\\((.*)\\)}");
            Matcher matcher = pattern.matcher(reference.get());

            if (!matcher.find()) {
                return;
            }

            Map<String, Integer> args = Arrays.stream(matcher.group(1).replaceAll("\\s", "").split(","))
                    .collect(Collectors.toMap(s -> s.split("=")[0], s -> Integer.parseInt(s.split("=")[1])));

            reference.getAndUpdate(s -> s.replaceAll(pattern.pattern(), "" + ThreadLocalRandom.current().nextInt(args.get("low"), args.get("up"))));
        });
    }};

    public static String setPlaceholders(String input, Player user, LivingEntity opponent, int level) {
        return setPlaceholders(input, user, opponent, level, null);
    }

    public static String setPlaceholders(String in, Player user, LivingEntity opponent, int level, Event event) {
        String input = in.replace("{level}", String.valueOf(level));

        for (Map.Entry<String, BiFunction<Player, LivingEntity, Object>> entry : PLAYER_FUNCTIONS.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue().apply(user, opponent).toString());
        }

        AtomicReference<String> output = new AtomicReference<>(input);

        REGEX_CONSUMERS.forEach(consumer -> consumer.accept(output));
        Optional.ofNullable(event).ifPresent(e -> EVENT_FUNCTIONS.forEach((toReplace, function) -> output.updateAndGet(string -> string.replace(toReplace, "'" + function.apply(e) + "'"))));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            output.updateAndGet(string -> PlaceholderAPI.setPlaceholders(user, string));

            final String inputFinal = input;
            if (opponent instanceof Player)
                output.updateAndGet(string -> PlaceholderAPI.setRelationalPlaceholders(user, (Player) opponent, inputFinal));
        }
        return output.get();
    }
}
