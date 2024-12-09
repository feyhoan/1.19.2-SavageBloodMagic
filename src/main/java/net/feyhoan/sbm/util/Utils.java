package net.feyhoan.sbm.util;

import net.feyhoan.sbm.blocks.ModBlocks;
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
}
