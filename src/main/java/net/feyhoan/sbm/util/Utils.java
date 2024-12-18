package net.feyhoan.sbm.util;

import net.feyhoan.sbm.blocks.ModBlocks;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.SpawnParticlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static net.feyhoan.sbm.SBM.MOD_ID;

public class Utils {
    public static final List<Class<? extends LivingEntity>> HUMAN_BLOOD_ENTITIES = Arrays.asList(Villager.class, WanderingTrader.class, ZombifiedPiglin.class ,Player.class);
    public static final List<Class<? extends LivingEntity>> NETHER_BLOOD_ENTITIES = Arrays.asList(
            Blaze.class, Piglin.class,
            PiglinBrute.class, Hoglin.class,
            Strider.class, Zoglin.class,
            MagmaCube.class
    );
    public static final List<Class<? extends LivingEntity>> END_BLOOD_ENTITIES = Arrays.asList(EnderDragon.class, EnderMan.class);

    public enum AbilityBindingsKeys {
        FIRST, SECOND, THIRD, FOURTH
    }

    public static boolean hasAncientColumnAroundThem(Player player, Level level) {
        return level.getBlockStates(player.getBoundingBox().inflate(13))
                .anyMatch(state -> state.is(ModBlocks.ANCIENT_COLUMN.get()));
    }

    public static ResourceLocation createRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static String getAbilityName(String fullClassName) {
        // Удаляем пакет и оставляем только название класса
        String simpleName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1); // Получаем имя класса после последней точки

        // Удаляем суффикс, если он существует
        if (simpleName.contains("@")) {
            simpleName = simpleName.substring(0, simpleName.indexOf("@")); // Оставляем только часть до @
        }

        return simpleName; // Возвращаем только имя способности
    }

    public static int randomColor() {
        return (int) (Math.random() * 0xFFFFFF); // Генерирует случайный 24-битный цвет
    }


    ///Code taken from the mod Vampirism///
    public static @NotNull HitResult getPlayerLookingSpot(@NotNull Player player, double restriction) {
        float scale = 1.0F;
        float pitch = player.xRotO + (player.getXRot() - player.xRotO) * scale;
        float yaw = player.yRotO + (player.getYRot() - player.yRotO) * scale;
        double x = player.xo + (player.getX() - player.xo) * scale;
        double y = player.yo + (player.getY() - player.yo) * scale + 1.62D;
        double z = player.zo + (player.getZ() - player.zo) * scale;
        Vec3 vector1 = new Vec3(x, y, z);
        float cosYaw = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -Mth.cos(-pitch * 0.017453292F);
        float sinPitch = Mth.sin(-pitch * 0.017453292F);
        float pitchAdjustedSinYaw = sinYaw * cosPitch;
        float pitchAdjustedCosYaw = cosYaw * cosPitch;
        double distance = 500D;
        if (restriction == 0 && player instanceof ServerPlayer) {
            distance = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() - 0.5f;
        } else if (restriction > 0) {
            distance = restriction;
        }

        Vec3 vector2 = vector1.add(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.getCommandSenderWorld().clip(new ClipContext(vector1, vector2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    public static void LevelUpParticles(ServerPlayer player) {
        int particleCount = 10;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 1;
            double offsetY = (Math.random() - 0.5) * 1;
            double offsetZ = (Math.random() - 0.5) * 1;

            ModMessages.sendToPlayer(new SpawnParticlePacket(player.getUUID(), player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ, "level_up"), player);
        }
    }

    public static void LevelDownParticles(ServerPlayer player) {
        int particleCount = 10;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 1;
            double offsetY = (Math.random() - 0.5) * 1;
            double offsetZ = (Math.random() - 0.5) * 1;

            ModMessages.sendToPlayer(new SpawnParticlePacket(player.getUUID(), player.getX() + offsetX, player.getY() + offsetY+ 0.5, player.getZ() + offsetZ, "level_down"), player);
        }
    }
}
