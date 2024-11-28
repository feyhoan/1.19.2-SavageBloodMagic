package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AbilitySubManaC2SPacket {
    private final int manacost;

    private final UUID playerUUID;

    public AbilitySubManaC2SPacket(int manacost, UUID playerUUID) {
        this.manacost = manacost;
        this.playerUUID = playerUUID;
    }

    public static AbilitySubManaC2SPacket fromBytes(FriendlyByteBuf buf) {
        int manacost = buf.readInt(); // Читаем длительность
        UUID playerUUID = buf.readUUID();


        return new AbilitySubManaC2SPacket(manacost, playerUUID); // Возвращаем новый экземпляр пакета
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.manacost);
        buf.writeUUID(this.playerUUID);
    }

    public static void handle(AbilitySubManaC2SPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender().getServer().getPlayerList().getPlayer(packet.playerUUID);// Получаем игрока по UUID
            if (player != null) {
                player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                    blood.subMana(packet.manacost);
                    ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);
                });
                SBM.LOGGER.info("submana sender {}", player.getName().getString());
            }
        });
        context.get().setPacketHandled(true);
    }
}