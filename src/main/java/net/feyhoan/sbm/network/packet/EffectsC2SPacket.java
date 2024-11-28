package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.SBM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class EffectsC2SPacket {

    private final MobEffect effect;
    private final int duration; // Сохраним для удаления
    private final int amplifier; // Сохраним для добавления
    private final boolean particles; // Сохраним для добавления
    private final boolean custom; // Сохраним для добавления
    private final UUID playerUUID;
    private final boolean isRemoval; // Добавим флаг для определения операции

    public EffectsC2SPacket(MobEffect effect, int duration, int amplifier, boolean particles, boolean custom, UUID playerUUID, boolean isRemoval) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.particles = particles;
        this.custom = custom;
        this.playerUUID = playerUUID;
        this.isRemoval = isRemoval;
    }

    public static EffectsC2SPacket fromBytes(FriendlyByteBuf buf) {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(buf.readResourceLocation());
        boolean isRemoval = buf.readBoolean(); // Читаем, удалять ли эффект
        int duration = isRemoval ? 0 : buf.readInt(); // Читаем длительность только если это не удаление
        int amplifier = isRemoval ? 0 : buf.readInt(); // Читаем усилитель только если это не удаление
        boolean particles = isRemoval ? false : buf.readBoolean(); // Читаем частицы только если это не удаление
        boolean custom = isRemoval ? false : buf.readBoolean(); // Читаем кастомный эффект только если это не удаление
        UUID playerUUID = buf.readUUID();

        return new EffectsC2SPacket(effect, duration, amplifier, particles, custom, playerUUID, isRemoval);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(this.effect)));
        buf.writeBoolean(this.isRemoval); // Записываем флаг удаления
        if (!this.isRemoval) { // Записываем дополнительные данные только для добавления
            buf.writeInt(this.duration);
            buf.writeInt(this.amplifier);
            buf.writeBoolean(this.particles);
            buf.writeBoolean(this.custom);
        }
        buf.writeUUID(this.playerUUID);
    }

    public static void handle(EffectsC2SPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender().getServer().getPlayerList().getPlayer(packet.playerUUID);
            if (player != null) {
                if (packet.isRemoval) {
                    player.removeEffect(packet.effect); // Удаляем эффект
                } else {
                    MobEffectInstance effectInstance = new MobEffectInstance(packet.effect, packet.duration, packet.amplifier, packet.particles, packet.custom);
                    player.addEffect(effectInstance); // Добавляем эффект
                }
                SBM.LOGGER.info("Processed effect packet for player: {}", player.getName().getString());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}