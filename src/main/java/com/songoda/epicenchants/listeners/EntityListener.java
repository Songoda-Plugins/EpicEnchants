package com.songoda.epicenchants.listeners;

import com.songoda.core.nms.Nms;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.enums.TriggerType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import static com.songoda.epicenchants.enums.TriggerType.*;

public class EntityListener implements Listener {
    private final EpicEnchants instance;

    public EntityListener(EpicEnchants instance) {
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster && event.getEntity().getKiller() != null) {
            this.instance.getEnchantUtils().handlePlayer(event.getEntity().getKiller(), event.getEntity(), event, KILLED_MOB);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity hitEntity = (LivingEntity) event.getEntity();

        //Hit by projectile
        if (event.getDamager() instanceof Projectile) {
            ProjectileSource source = ((Projectile) event.getDamager()).getShooter();

            if (hitEntity instanceof Player) {
                LivingEntity opponent = source instanceof LivingEntity ? ((LivingEntity) source) : null;
                TriggerType type = source instanceof Player ? DEFENSE_PLAYER_RANGE : DEFENSE_MOB_RANGE;
                this.instance.getEnchantUtils().handlePlayer(((Player) hitEntity), opponent, event, type);
            }

            if (source instanceof Player) {
                TriggerType type = event.getEntity() instanceof Player ? ATTACK_PLAYER_RANGE : ATTACK_MOB_RANGE;
                this.instance.getEnchantUtils().handlePlayer(((Player) source), hitEntity, event, type);
            }
        }

        //Player got hit
        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            TriggerType triggerType = null;
            LivingEntity opponent = null;

            if (event.getDamager() instanceof Player) {
                opponent = ((LivingEntity) event.getDamager());
                triggerType = DEFENSE_PLAYER_MELEE;
            } else if (event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof Player)) {
                opponent = ((LivingEntity) event.getDamager());
                triggerType = DEFENSE_MOB_MELEE;
            }

            if (triggerType != null) {
                this.instance.getEnchantUtils().handlePlayer(defender, opponent, event, triggerType);
            }
        }

        //Player damaged an entity
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            TriggerType triggerType = null;

            if (event.getEntity() instanceof Player) {
                triggerType = ATTACK_PLAYER_MELEE;
            } else if (event.getEntity() instanceof LivingEntity) {
                triggerType = ATTACK_MOB_MELEE;
            }

            if (triggerType != null) {
                this.instance.getEnchantUtils().handlePlayer(attacker, ((LivingEntity) event.getEntity()), event, triggerType);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        switch (event.getCause()) {
            case FALL:
                this.instance.getEnchantUtils().handlePlayer(((Player) event.getEntity()), null, event, FALL_DAMAGE);
                break;
            case FIRE:
            case FIRE_TICK:
                this.instance.getEnchantUtils().handlePlayer(((Player) event.getEntity()), null, event, FIRE_DAMAGE);
                break;
            case LAVA:
                this.instance.getEnchantUtils().handlePlayer(((Player) event.getEntity()), null, event, LAVA_DAMAGE);
                break;
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                this.instance.getEnchantUtils().handlePlayer(((Player) event.getEntity()), null, event, EXPLOSION_DAMAGE);
                break;
            case POISON:
                this.instance.getEnchantUtils().handlePlayer(((Player) event.getEntity()), null, event, POISON_DAMAGE);
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null) {
            return;
        }

        if (Nms.getImplementations().getNbt().of(event.getEntity()).has(event.getTarget().getUniqueId().toString())) {
            //TODO: Add team support.
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals("ee")) {
            event.blockList().clear();
        }
    }
}
