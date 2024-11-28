package net.feyhoan.sbm.item.curios;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface ManaIncreasingItem {
    int getMaxManaIncrease();
    int getMinimumManaLevel();

    default void increaseMana(Player player) {
        player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
            if (blood.getLevel() != 0) {
                blood.setMaxMana(blood.getMaxMana() + getMaxManaIncrease());
                ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);            }
        });
    }

    default void decreaseMana(Player player) {
        player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
            if (blood.getLevel() != 0) {
                int newMaxMana = blood.getMaxMana() - getMaxManaIncrease();
                newMaxMana = Math.max(newMaxMana, getMinimumManaLevel());
                blood.setMaxMana(newMaxMana);

                ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);            }
        });
    }
    default boolean isMage(Player player) {
        return player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getLevel() > 1).orElse(false);
    }
}