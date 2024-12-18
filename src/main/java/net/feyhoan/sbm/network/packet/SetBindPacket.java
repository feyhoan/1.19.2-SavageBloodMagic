package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.magic.AbilitiesBindingsProvider;
import net.feyhoan.sbm.magic.AbilityAddResult;
import net.feyhoan.sbm.magic.BloodAbilitiesProvider;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SetBindPacket {
    private final Utils.AbilityBindingsKeys key;
    private final String abilityName;
    private final UUID playerId;

    public SetBindPacket(Utils.AbilityBindingsKeys key, String abilityName, UUID playerId) {
        this.key = key;
        this.abilityName = abilityName;
        this.playerId = playerId;
    }

    // Метод для записи данных в буфер
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.key); // Записываем ключ способности
        buf.writeUtf(this.abilityName); // Записываем имя способности
        buf.writeUUID(this.playerId); // Записываем UUID игрока
    }

    // Метод для считывания данных из буфера
    public static SetBindPacket fromBytes(FriendlyByteBuf buf) {
        Utils.AbilityBindingsKeys key = buf.readEnum(Utils.AbilityBindingsKeys.class); // Считываем ключ способности
        String abilityName = buf.readUtf(); // Считываем имя способности
        UUID playerId = buf.readUUID(); // Считываем UUID игрока
        return new SetBindPacket(key, abilityName, playerId); // Возвращаем новый экземпляр пакета
    }

    // Метод для обработки пакета на сервере
    public static void handle(SetBindPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender().getServer().getPlayerList().getPlayer(packet.playerId);
            if (player != null) {
                player.getCapability(AbilitiesBindingsProvider.ABILITIES_BINDINGS).ifPresent(cap -> {
                    cap.setKeyAbility(packet.key, packet.abilityName);
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}