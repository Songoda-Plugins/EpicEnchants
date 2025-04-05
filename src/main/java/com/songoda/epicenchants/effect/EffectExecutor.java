package com.songoda.epicenchants.effect;

import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.enums.TriggerType;
import com.songoda.epicenchants.objects.Condition;
import com.songoda.epicenchants.objects.LeveledModifier;
import com.songoda.epicenchants.utils.single.GeneralUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.songoda.epicenchants.effect.EffectExecutor.Who.OPPONENT;
import static com.songoda.epicenchants.effect.EffectExecutor.Who.USER;

public abstract class EffectExecutor {
    private final ConfigurationSection section;
    private final Set<TriggerType> triggerTypes;
    private final Set<EffectExecutor> simultaneous;
    private final Condition condition;

    public EffectExecutor(ConfigurationSection section) {
        this.section = section;
        this.triggerTypes = GeneralUtils.parseTrigger(section.getString("trigger"));
        this.condition = Condition.of(section.getString("condition"));
        this.simultaneous = section.isConfigurationSection("simultaneous") ? section.getConfigurationSection("simultaneous").getKeys(false).stream()
                .map(s -> "simultaneous." + s)
                .map(section::getConfigurationSection)
                .map(EffectManager::getEffect)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()) : Collections.emptySet();
    }

    public void testAndRun(@NotNull Player user, @Nullable LivingEntity opponent, int level, TriggerType type, Event event, EventType eventType) {
        testAndRun(user, opponent, level, type, event, eventType, false);
    }

    public void testAndRun(@NotNull Player user, @Nullable LivingEntity opponent, int level, TriggerType type, Event event, EventType eventType, boolean simul) {
        if (!simul && !this.triggerTypes.contains(type)) {
            return;
        }

        if (this.section.isString("chance") && !GeneralUtils.chance(LeveledModifier.of(this.section.getString("chance")).get(level, 100, user, opponent))) {
            return;
        }

        if (!this.condition.get(user, opponent, level, event, false)) {
            return;
        }

        if (this instanceof EffectEventExecutor) {
            ((EffectEventExecutor) this).execute(user, opponent, level, event, eventType);
        } else {
            execute(user, opponent, level, eventType);
        }

        this.simultaneous.forEach(e -> e.testAndRun(user, opponent, level, type, event, eventType, true));
    }

    public abstract void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType);

    protected Who who() {
        if (this.section.isString("who")) {
            if (this.section.getString("who").equalsIgnoreCase("user")) {
                return USER;
            }

            if (this.section.getString("who").equalsIgnoreCase("opponent")) {
                return OPPONENT;
            }
        }

        return USER;
    }

    public LeveledModifier getAmount() {
        return LeveledModifier.of(this.section.getString("amount"));
    }

    public void consume(Consumer<LivingEntity> playerConsumer, Player user, @Nullable LivingEntity opponent) {
        if (this.triggerTypes.contains(TriggerType.HELD_ITEM) || this.triggerTypes.contains(TriggerType.STATIC_EFFECT)) {
            playerConsumer.accept(user);
            return;
        }

        switch (who()) {
            case USER:
                playerConsumer.accept(user);
                break;
            case OPPONENT:
                if (opponent != null) {
                    playerConsumer.accept(opponent);
                }
        }
    }

    public ConfigurationSection getSection() {
        return this.section;
    }

    public Set<TriggerType> getTriggerTypes() {
        return this.triggerTypes;
    }

    public enum Who {
        USER, OPPONENT
    }
}
