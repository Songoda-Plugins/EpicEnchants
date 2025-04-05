package com.songoda.epicenchants.effect.effects;

import com.songoda.epicenchants.effect.EffectExecutor;
import com.songoda.epicenchants.enums.EventType;
import com.songoda.epicenchants.objects.LeveledModifier;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.songoda.epicenchants.objects.LeveledModifier.of;
import static com.songoda.epicenchants.utils.single.GeneralUtils.color;
import static java.util.concurrent.ThreadLocalRandom.current;

public class SpawnMob extends EffectExecutor {
    private final LeveledModifier attackDamage;
    private final String displayName;
    private final EntityType entityType;
    private final LeveledModifier equipmentDropChance;
    private final LeveledModifier health;
    private final ItemBuilder helmet;
    private final ItemBuilder chestPlate;
    private final ItemBuilder leggings;
    private final ItemBuilder boots;
    private final ItemBuilder handItem;
    private final boolean hostile;
    private final LeveledModifier amount;

    public SpawnMob(ConfigurationSection section) {
        super(section);

        this.entityType = EntityType.valueOf(section.getString("mob-type"));
        this.amount = of(section.getString("amount"));
        this.health = of(section.getString("health"));
        this.attackDamage = of(section.getString("attack-damage"));
        this.equipmentDropChance = LeveledModifier.of(section.getString("equipment-drop-chance"));
        this.hostile = section.getBoolean("hostile", false);
        this.displayName = section.isString("display-name") ? color(section.getString("display-name")) : "";
        this.helmet = section.isConfigurationSection("equipment.helmet") ? new ItemBuilder(section.getConfigurationSection("equipment.helmet")) : null;
        this.chestPlate = section.isConfigurationSection("equipment.chestplate") ? new ItemBuilder(section.getConfigurationSection("equipment.chestplate")) : null;
        this.leggings = section.isConfigurationSection("equipment.leggings") ? new ItemBuilder(section.getConfigurationSection("equipment.leggings")) : null;
        this.boots = section.isConfigurationSection("equipment.boots") ? new ItemBuilder(section.getConfigurationSection("equipment.boots")) : null;
        this.handItem = section.isConfigurationSection("equipment.hand-item") ? new ItemBuilder(section.getConfigurationSection("equipment.hand-item")) : null;
    }

    @Override
    public void execute(@NotNull Player user, @Nullable LivingEntity opponent, int level, EventType eventType) {
        Location location = user.getLocation();

        for (int i = 0; i < this.amount.get(level, 1, user, opponent); i++) {
            Location spawnLocation = location.clone().add(current().nextInt(-3, 3), 0, current().nextInt(-3, 3));
            int y = location.getWorld().getHighestBlockAt(spawnLocation).getY();

            if (y < location.getY() - 10 || y > location.getY() + 10) {
                continue;
            }

            Entity entity = location.getWorld().spawnEntity(spawnLocation, this.entityType);

            entity.setCustomName(this.displayName.replace("{level}", "" + level));
            entity.setCustomNameVisible(true);

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.setRemoveWhenFarAway(true);
                int dropChance = (int) this.equipmentDropChance.get(level, 0, user, opponent);

                if (this.helmet != null) {
                    livingEntity.getEquipment().setHelmet(this.helmet.buildWithWrappers(level, user, opponent));
                }
                if (this.chestPlate != null) {
                    livingEntity.getEquipment().setChestplate(this.chestPlate.buildWithWrappers(level, user, opponent));
                }
                if (this.leggings != null) {
                    livingEntity.getEquipment().setLeggings(this.leggings.buildWithWrappers(level, user, opponent));
                }
                if (this.boots != null) {
                    livingEntity.getEquipment().setBoots(this.boots.buildWithWrappers(level, user, opponent));
                }
                livingEntity.getEquipment().setHelmetDropChance(dropChance);
                livingEntity.getEquipment().setLeggingsDropChance(dropChance);
                livingEntity.getEquipment().setHelmetDropChance(dropChance);
                livingEntity.getEquipment().setChestplateDropChance(dropChance);

                if (this.handItem != null) {
                    livingEntity.getEquipment().setItemInHand(this.handItem.buildWithWrappers(level, user, opponent));
                }
                livingEntity.getEquipment().setItemInHandDropChance(dropChance);
            }

            if (this.hostile && entity instanceof Monster && opponent != null) {
                ((Monster) entity).setTarget(opponent);
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

//                AttributeInstance attack = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
//                attack.setBaseValue(attackDamage.get(level, (int) Math.round(attack.getBaseValue()), user, opponent));

                double maxHealth = livingEntity.getMaxHealth();
                livingEntity.setMaxHealth(this.health.get(level, (int) Math.round(maxHealth), user, opponent));
            }
        }
    }
}
