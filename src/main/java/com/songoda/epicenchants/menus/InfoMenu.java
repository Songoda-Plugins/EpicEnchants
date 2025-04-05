package com.songoda.epicenchants.menus;

import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.objects.Placeholder;
import com.songoda.epicenchants.utils.objects.FastInv;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.songoda.epicenchants.utils.single.GeneralUtils.color;
import static com.songoda.epicenchants.utils.single.GeneralUtils.getSlots;
import static java.util.Arrays.stream;

public class InfoMenu extends FastInv {
    public InfoMenu(EpicEnchants instance, FileConfiguration config) {
        super(config.getInt("rows") * 9, color(config.getString("title")));

        Group group = instance.getGroupManager().getValue(config.getString("group")).orElseThrow(() -> new IllegalArgumentException("Invalid group: " + config.getString("group")));

        Set<Integer> slots;

        if (config.getString("slots").equalsIgnoreCase("ALL_SLOTS")) {
            slots = IntStream.range(0, config.getInt("rows") * 9).boxed().collect(Collectors.toSet());
        } else {
            String[] split = config.getString("slots").split(",");
            slots = stream(split, 0, split.length).filter(StringUtils::isNumeric).map(Integer::parseInt).collect(Collectors.toSet());
        }

        if (config.isConfigurationSection("contents"))
            config.getConfigurationSection("contents").getKeys(false)
                    .stream()
                    .map(s -> "contents." + s)
                    .map(config::getConfigurationSection)
                    .forEach(section -> addItem(getSlots(section.getString("slot")), new ItemBuilder(section).build(), event -> {
                        if (section.getName().contains("back")) {
                            instance.getInfoManager().getMainInfoMenu().open(event.getPlayer());
                        }
                    }));

        Iterator<Enchant> enchantIterator = instance.getEnchantManager().getEnchants(group).iterator();
        slots.stream().filter(slot -> enchantIterator.hasNext()).forEach(slot -> {
            Enchant enchant = enchantIterator.next();

            String whitelist = instance.getItemGroup().getGroups(enchant.getItemWhitelist())
                    .stream()
                    .map(s -> StringUtils.capitalize(s.toLowerCase()))
                    .collect(Collectors.joining(", "));

            addItem(slot, new ItemBuilder(config.getConfigurationSection("enchant-item"),
                    Placeholder.of("group_color", enchant.getGroup().getColor()),
                    Placeholder.of("enchant", enchant.getIdentifier()),
                    Placeholder.of("max_level", enchant.getMaxLevel()),
                    Placeholder.of("applicable_to", whitelist),
                    Placeholder.of("enchant", enchant.getIdentifier()),
                    Placeholder.of("description", enchant
                            .getDescription()
                            .stream()
                            .map(s -> config.getString("enchant-item.description-color") + s)
                            .collect(Collectors.toList())))
                    .build());
        });
    }
}
