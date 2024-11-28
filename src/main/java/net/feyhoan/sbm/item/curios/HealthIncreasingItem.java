package net.feyhoan.sbm.item.curios;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface HealthIncreasingItem {
    AttributeModifier getHealthModifier();

    default void increaseHealth(Player player) {
        if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(getHealthModifier())) {
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(getHealthModifier());
        }
    }

    default void decreaseHealth(Player player) {
        if (player.getAttribute(Attributes.MAX_HEALTH).hasModifier(getHealthModifier())) {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(getHealthModifier());
        }
    }
}