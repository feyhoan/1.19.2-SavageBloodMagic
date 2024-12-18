package net.feyhoan.sbm.network;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.network.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(SBM.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.1")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(BloodDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BloodDataSyncS2CPacket::new)
                .encoder(BloodDataSyncS2CPacket::toBytes)
                .consumerMainThread(BloodDataSyncS2CPacket::handle)
                .add();

        net.messageBuilder(SpawnParticlePacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SpawnParticlePacket::fromBytes)
                .encoder(SpawnParticlePacket::toBytes)
                .consumerMainThread(SpawnParticlePacket::handle)
                .add();

        net.messageBuilder(ScrollUseC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ScrollUseC2SPacket::fromBytes)
                .encoder(ScrollUseC2SPacket::toBytes)
                .consumerMainThread(ScrollUseC2SPacket::handle)
                .add();

        net.messageBuilder(EffectsC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(EffectsC2SPacket::fromBytes)
                .encoder(EffectsC2SPacket::toBytes)
                .consumerMainThread(EffectsC2SPacket::handle)
                .add();

        net.messageBuilder(AbilitySubManaC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AbilitySubManaC2SPacket::fromBytes)
                .encoder(AbilitySubManaC2SPacket::toBytes)
                .consumerMainThread(AbilitySubManaC2SPacket::handle)
                .add();

        net.messageBuilder(ManaRegenerationPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ManaRegenerationPacket::new)
                .encoder(ManaRegenerationPacket::toBytes)
                .consumerMainThread(ManaRegenerationPacket::handle)
                .add();


        //Ability
        net.messageBuilder(AbilityActionPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AbilityActionPacket::fromBytes)
                .encoder(AbilityActionPacket::toBytes)
                .consumerMainThread(AbilityActionPacket::handle)
                .add();

        net.messageBuilder(SetBindPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetBindPacket::fromBytes)
                .encoder(SetBindPacket::toBytes)
                .consumerMainThread(SetBindPacket::handle)
                .add();

        net.messageBuilder(AbilityActionRequestPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AbilityActionRequestPacket::fromBytes)
                .encoder(AbilityActionRequestPacket::toBytes)
                .consumerMainThread(AbilityActionRequestPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
