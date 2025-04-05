package com.songoda.epicenchants.menus;

import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.utils.objects.FastInv;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import static com.songoda.epicenchants.utils.single.GeneralUtils.color;
import static com.songoda.epicenchants.utils.single.GeneralUtils.getSlots;

public class MainInfoMenu extends FastInv implements Listener {
    public MainInfoMenu(EpicEnchants instance, FileConfiguration config) {
        super(config.getInt("rows") * 9, color(config.getString("title")));
        config.getConfigurationSection("contents").getKeys(false)
                .stream()
                .map(s -> "contents." + s)
                .map(config::getConfigurationSection)
                .forEach(section -> addItem(getSlots(section.getString("slot")), new ItemBuilder(section).build(), event -> {
                    if (section.getString("group") == null) {
                        return;
                    }

                    Group group = instance
                            .getGroupManager()
                            .getValue(section.getString("group"))
                            .orElseThrow(() -> new IllegalArgumentException("Invalid group: " + section.getString("group")));

                    instance.getInfoManager()
                            .getMenu(group)
                            .ifPresent(menu -> menu.open(event.getPlayer()));
                }));
    }
}
