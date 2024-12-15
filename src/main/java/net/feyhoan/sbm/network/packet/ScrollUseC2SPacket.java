package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static net.feyhoan.sbm.item.ModItems.*;
import static net.feyhoan.sbm.util.Utils.LevelUpParticles;

public class ScrollUseC2SPacket {

    private final UUID playerUUID; // UUID игрока

    private static final String LEVEL_UP = "sbm.scroll.level_up";
    private static final String ALREADY_REACHED = "sbm.scroll.already_reached";
    private static final String LEVEL_TOO_LOW = "sbm.scroll.level_too_low";
    private static final String MAX_LEVEL = "sbm.scroll.max_level";
    private static final String WAIT = "sbm.scroll.wait";

    public ScrollUseC2SPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public static ScrollUseC2SPacket fromBytes(FriendlyByteBuf buf) {
        UUID playerUUID = buf.readUUID(); // Чтение UUID игрока
        return new ScrollUseC2SPacket(playerUUID);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.playerUUID);

    }

    public static void handle(ScrollUseC2SPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender().getServer().getPlayerList().getPlayer(packet.playerUUID);
            assert player != null; // Проверка корректности игрока
            ServerLevel level = player.getLevel();
            // Получение данных о уровне крови игрока
            AtomicInteger bloodLevel = new AtomicInteger();
            player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                bloodLevel.set(blood.getLevel());
            });
            int currentLevel = bloodLevel.get();
            // Получаем свиток, требуемый для повышения уровня
            Item requiredScroll = getRequiredScrollForLevel(currentLevel);
            if (requiredScroll == null) {
                player.sendSystemMessage(Component.translatable(MAX_LEVEL).withStyle(ChatFormatting.RED));
                return; // Достигнут максимальный уровень
            }
            // Проверяем, имеет ли игрок свиток в руках
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (heldItem.getItem() == requiredScroll) {
                // Уровень свитка соответствует уровню игрока +1
                if (currentLevel < 5) { // Предполагаем, что 5 - максимальный уровень
                    levelUpPlayer(player, level);
                    heldItem.shrink(1);
                } else {
                    player.sendSystemMessage(Component.translatable(MAX_LEVEL).withStyle(ChatFormatting.RED));
                    FailParticles(player);
                }
            } else {
                // Проверим, ниже ли уровень игрока, чем уровень свитка
                int requiredLevel = currentLevel + 1; // Уровень, требуемый для свитка
                if (currentLevel >= requiredLevel) {
                    player.sendSystemMessage(Component.translatable(ALREADY_REACHED).withStyle(ChatFormatting.RED));
                    FailParticles(player);
                } else {
                    FailParticles(player);
                    player.sendSystemMessage(Component.translatable(LEVEL_TOO_LOW).withStyle(ChatFormatting.RED));
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    private static Item getRequiredScrollForLevel(int level) {
        return switch (level) {
            case 0 -> COMMON_SCROLL.get(); // Для уровня 0 требуется COMMON_SCROLL
            case 1 -> BEGINNER_SCROLL.get(); // Для уровня 1 требуется BEGINNER_SCROLL
            case 2 -> ADVANCED_SCROLL.get(); // Для уровня 2 требуется ADVANCED_SCROLL
            case 3 -> MASTER_SCROLL.get(); // Для уровня 3 требуется MASTER_SCROLL
            case 4 -> ANCIENT_SCROLL.get(); // Для уровня 4 требуется ANCIENT_SCROLL
            case 5 -> null; // Для уровня 5 свиток не требуется, максимальный уровень
            default -> null;
        };
    }

    private static void levelUpPlayer(ServerPlayer player, ServerLevel level) {
        player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                blood.levelUp(); // Повышаем уровень
                LevelUpParticles(player);
                player.sendSystemMessage(Component.translatable(LEVEL_UP, blood.getLevel()).withStyle(ChatFormatting.DARK_RED));
                // Синхронизация данных игрока
                ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), player);

        });
    }



    private static void FailParticles(ServerPlayer player) {
        int particleCount = 20;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = (Math.random() - 0.5) * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;

            ModMessages.sendToPlayer(new SpawnParticlePacket(player.getUUID(), player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ, "fail"), player);
            player.getLevel().playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1.0F);
        }
    }

}