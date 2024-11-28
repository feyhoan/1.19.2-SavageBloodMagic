package net.feyhoan.sbm.util;

import net.feyhoan.sbm.blocks.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
}
