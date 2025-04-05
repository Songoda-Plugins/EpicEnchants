package com.songoda.epicenchants.managers;

import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.menus.InfoMenu;
import com.songoda.epicenchants.menus.MainInfoMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Optional;

public class InfoManager extends Manager<Group, InfoMenu> {
    private final EpicEnchants instance;
    private MainInfoMenu mainInfoMenu;

    public InfoManager(EpicEnchants instance) {
        super(instance);
        this.instance = instance;
    }

    public Optional<InfoMenu> getMenu(Group group) {
        return getValue(group);
    }

    public void loadMenus() {
        this.mainInfoMenu = new MainInfoMenu(this.instance, this.instance.getFileManager().getConfiguration("menus/main-info-menu"));
        this.instance.getFileManager().getYmlFiles("menus/groups").forEach(file -> {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                add(this.instance
                                .getGroupManager()
                                .getValue(config.getString("group"))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid group: " + config.getString("group"))),
                        new InfoMenu(this.instance, config));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("Something went wrong loading the menu from file " + file.getName());
                Bukkit.getConsoleSender().sendMessage("Please check to make sure there are no errors in the file.");
                e.printStackTrace();
            }
        });
    }

    public MainInfoMenu getMainInfoMenu() {
        return this.mainInfoMenu;
    }
}
