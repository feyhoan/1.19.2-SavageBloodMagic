package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import static net.feyhoan.sbm.particle.ModParticles.BLOOD_LEAP_PARTICLE;
import static net.feyhoan.sbm.particle.ModParticles.BLOOD_MARK_PARTICLE;

public class SpawnParticlePacket {
    private final UUID playerId;
    private final double x;
    private final double y;
    private final double z;
    private final String particleName;

    public SpawnParticlePacket(UUID playerId, double x, double y, double z, String particleName) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleName = particleName;
    }

    public static void toBytes(SpawnParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerId);
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
        buf.writeUtf(packet.particleName);
    }

    public static SpawnParticlePacket fromBytes(FriendlyByteBuf buf) {
        UUID playerId = buf.readUUID();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        String particleName = buf.readUtf();
        return new SpawnParticlePacket(playerId, x, y, z, particleName);
    }

    public static void handle(SpawnParticlePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            assert level != null;
            Player player = level.getPlayerByUUID(packet.playerId);

            SimpleParticleType particleType = getParticleTypeByName(packet.particleName); // Получаем тип частицы
            if (particleType != null) {
                level.addParticle(particleType, packet.x, packet.y, packet.z, 0, 0, 0);
            }

        });
        context.get().setPacketHandled(true);
    }

    public static SimpleParticleType getParticleTypeByName(String name) {
        switch (name) {
            case "blood_leap":
                return BLOOD_LEAP_PARTICLE.get();
            case "blood_mark":
                return BLOOD_MARK_PARTICLE.get();
            default:
                return null;
        }
    }
}