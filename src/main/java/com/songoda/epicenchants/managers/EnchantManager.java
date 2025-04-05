package com.songoda.epicenchants.managers;

import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.songoda.epicenchants.utils.single.ConfigParser.parseEnchant;

public class EnchantManager extends Manager<String, Enchant> {
    public EnchantManager(EpicEnchants instance) {
        super(instance);
    }

    public Collection<Enchant> getEnchants(Group group) {
        return Collections.unmodifiableCollection(getValues().stream().filter(s -> s.getGroup().equals(group)).collect(Collectors.toList()));
    }

    public Optional<Enchant> getRandomEnchant(Group group) {
        Collection<Enchant> tierList = getEnchants(group);
        return tierList.stream().skip((int) (tierList.size() * Math.random())).findFirst();
    }

    public void loadEnchants() {
        this.instance.getFileManager().getYmlFiles("enchants").forEach(file -> {
            try {
                loadEnchant(file);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("Something went wrong loading the enchant from file " + file.getName());
                Bukkit.getConsoleSender().sendMessage("Please check to make sure there are no errors in the file.");
                e.printStackTrace();
            }
        });
    }

    public void loadEnchant(File file) {
        Enchant enchant = parseEnchant(this.instance, YamlConfiguration.loadConfiguration(file));
        add(enchant.getIdentifier(), enchant);
    }
}
