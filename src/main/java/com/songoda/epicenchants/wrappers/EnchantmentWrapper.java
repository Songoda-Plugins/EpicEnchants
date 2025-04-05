package com.songoda.epicenchants.wrappers;

import com.songoda.epicenchants.objects.LeveledModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantmentWrapper {
    private final LeveledModifier amplifier;
    private final Enchantment enchantment;

    EnchantmentWrapper(LeveledModifier amplifier, Enchantment enchantment) {
        this.amplifier = amplifier;
        this.enchantment = enchantment;
    }

    public static EnchantmentWrapperBuilder builder() {
        return new EnchantmentWrapperBuilder();
    }

    public int getAmplifier(int level, @NotNull Player user, @Nullable LivingEntity opponent) {
        return (int) this.amplifier.get(level, 0, user, opponent);
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public static class EnchantmentWrapperBuilder {
        private LeveledModifier amplifier;
        private Enchantment enchantment;

        EnchantmentWrapperBuilder() {
        }

        public EnchantmentWrapperBuilder amplifier(LeveledModifier amplifier) {
            this.amplifier = amplifier;
            return this;
        }

        public EnchantmentWrapperBuilder enchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public EnchantmentWrapper build() {
            return new EnchantmentWrapper(this.amplifier, this.enchantment);
        }

        public String toString() {
            return "EnchantmentWrapper.EnchantmentWrapperBuilder(amplifier=" + this.amplifier + ", enchantment=" + this.enchantment + ")";
        }
    }
}
