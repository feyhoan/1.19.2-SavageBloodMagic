package net.feyhoan.sbm.item.curios;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface SpeedIncreasingItem {
    AttributeModifier getSpeedModifier();

    default void increaseSpeed(Player player) {
        if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(getSpeedModifier())) {
            player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(getSpeedModifier());
        }
    }

    default void decreaseSpeed(Player player) {
        if (player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(getSpeedModifier())) {
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(getSpeedModifier());
        }
    }
}