package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.magic.AbilitiesBindingsProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AbilityActionRequestPacket {
    private final int index; // Индекс способности

    public AbilityActionRequestPacket(int index) {
        this.index = index;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index); // Сохраняем индекс в пакет
    }

    public static AbilityActionRequestPacket fromBytes(FriendlyByteBuf buf) {
        return new AbilityActionRequestPacket(buf.readInt()); // Читаем индекс из пакета
    }

    public int getIndex() {
        return index;
    }

    // Обработчик пакета на сервере
    public static void handle(AbilityActionRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender(); // Получаем игрока
            if (player != null) {
                Utils.AbilityBindingsKeys[] keys = Utils.AbilityBindingsKeys.values();
                String abilityName = player.getCapability(AbilitiesBindingsProvider.ABILITIES_BINDINGS)
                        .map(cap -> cap.getAbilityName(keys[packet.getIndex()])) // Получаем имя способности
                        .orElse(null);

                if (abilityName != null) {
                    // Отправляем обратно на клиент
                    ModMessages.sendToServer(new AbilityActionPacket(AbilityActionPacket.AbilityAction.ACTIVATE, abilityName, player.getUUID()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}