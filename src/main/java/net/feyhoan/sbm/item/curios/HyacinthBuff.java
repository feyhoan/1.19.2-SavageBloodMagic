package net.feyhoan.sbm.item.curios;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;



public class HyacinthBuff {
    private static final Random random = new Random();
    @SubscribeEvent
    public static void onEntityHurtPoising(LivingHurtEvent event) {
        // Проверяем, что урон наносит игрок
        if (event.getSource().getEntity() instanceof Player) {
            LivingEntity target = event.getEntity();
                // 30% шанс наложить отравление
            if (random.nextDouble() < 0.30) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false));
            }
        }
    }
}
