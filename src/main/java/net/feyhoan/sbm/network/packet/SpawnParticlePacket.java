package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SpawnParticlePacket {
    private final UUID playerId;
    private final double x;
    private final double y;
    private final double z;

    public SpawnParticlePacket(UUID playerId, double x, double y, double z) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void toBytes(SpawnParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerId);
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
    }

    public static SpawnParticlePacket fromBytes(FriendlyByteBuf buf) {
        UUID playerId = buf.readUUID();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new SpawnParticlePacket(playerId, x, y, z);
    }

    public static void handle(SpawnParticlePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Обработка на клиенте
            Level level = Minecraft.getInstance().level;
            assert level != null;
            Player player = level.getPlayerByUUID(packet.playerId);
            if (player != null) {
                level.addParticle(ModParticles.BLOOD_LEAP_PARTICLE.get(),
                        packet.x, packet.y, packet.z,
                        0, 0, 0);
            }
        });
        context.get().setPacketHandled(true);
    }
}