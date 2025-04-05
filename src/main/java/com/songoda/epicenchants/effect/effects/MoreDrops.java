package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectEventExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.objects.LeveledModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class MoreDrops extends EffectEventExecutor {
    public MoreDrops(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player user, LivingEntity opponent, int level, Event event, EventType type) {
        if (!(event instanceof EntityDeathEvent)) {
            return;
        }

        EntityDeathEvent deathEvent = (EntityDeathEvent) event;
        LeveledModifier modifier = getAmount();
        List<ItemStack> newDrops = deathEvent.getDrops()
                .stream()
                .peek(itemStack -> itemStack.setAmount(((int) (itemStack.getAmount() * modifier.get(level, 1, user, opponent)))))
                .collect(Collectors.toList());

        deathEvent.getDrops().clear();
        deathEvent.getDrops().addAll(newDrops);
    }
}
