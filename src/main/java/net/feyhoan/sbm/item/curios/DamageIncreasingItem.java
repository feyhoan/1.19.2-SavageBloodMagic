package net.feyhoan.sbm.item.curios;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface DamageIncreasingItem {
    AttributeModifier getDamageModifier();

    default void increaseDamage(Player player) {
        if (!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(getDamageModifier())) {
            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(getDamageModifier());
        }
    }

    default void decreaseDamage(Player player) {
        if (player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(getDamageModifier())) {
            player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(getDamageModifier());
        }
    }
}