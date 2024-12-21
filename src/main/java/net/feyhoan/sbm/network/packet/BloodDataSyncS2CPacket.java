package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.client.ClientBloodData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BloodDataSyncS2CPacket {
    private final int mana;
    private final int maxmana;
    private final int level;
    private final int manaRegenTicks;

    public BloodDataSyncS2CPacket(int mana, int maxmana, int level, int manaRegenTicks) {
        this.mana = mana;
        this.maxmana = maxmana;
        this.level = level;
        this.manaRegenTicks = manaRegenTicks;
    }

    public BloodDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.mana = buf.readInt();
        this.maxmana = buf.readInt();
        this.level = buf.readInt();
        this.manaRegenTicks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(mana);
        buf.writeInt(maxmana);
        buf.writeInt(level);
        buf.writeInt(manaRegenTicks);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT!
            ClientBloodData.set(mana, maxmana, level, manaRegenTicks);
        });
        return true;
    }
}