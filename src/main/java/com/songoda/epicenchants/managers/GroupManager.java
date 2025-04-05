package com.songoda.epicenchants.managers;

import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.utils.single.ConfigParser;
import org.bukkit.configuration.ConfigurationSection;

public class GroupManager extends Manager<String, Group> {
    public GroupManager(EpicEnchants instance) {
        super(instance);
    }

    public void loadGroups() {
        ConfigurationSection config = this.instance.getFileManager().getConfiguration("groups").getConfigurationSection("groups");
        config.getKeys(false).forEach(key -> {
            Group group = ConfigParser.parseGroup(this.instance, config.getConfigurationSection(key));
            add(group.getIdentifier().toUpperCase(), group);
        });
    }
}
