package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaRegenerationPacket {

    public ManaRegenerationPacket() {
    }

    public ManaRegenerationPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static void handle(ManaRegenerationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                    if (blood.getMana() < blood.getMaxMana()) { // Проверяем, не достигнуто ли максимальное значение
                        blood.addMana(1);
                        ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), player);
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
