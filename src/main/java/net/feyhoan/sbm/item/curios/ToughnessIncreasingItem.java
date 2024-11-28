package net.feyhoan.sbm.item.curios;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface ToughnessIncreasingItem {
    AttributeModifier getToughnessModifier();

    default void increaseToughness(Player player) {
        if (!player.getAttribute(Attributes.ARMOR).hasModifier(getToughnessModifier())) {
            player.getAttribute(Attributes.ARMOR).addPermanentModifier(getToughnessModifier());
        }
    }

    default void decreaseToughness(Player player) {
        if (player.getAttribute(Attributes.ARMOR).hasModifier(getToughnessModifier())) {
            player.getAttribute(Attributes.ARMOR).removeModifier(getToughnessModifier());
        }
    }
}