package net.feyhoan.sbm.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import static net.minecraft.world.effect.MobEffects.*;

public class AbilityUtils {

    public static final MobEffect[] BLOOD_MERGING_EFFECTS = {
            DAMAGE_BOOST, MOVEMENT_SPEED, HEALTH_BOOST,
            DIG_SPEED, DAMAGE_RESISTANCE
    };

    public static int getEffectAmplifierBloodMerging(MobEffect effect) {
        if (effect.equals(MobEffects.DAMAGE_BOOST)) {
            return 2;
        } else if (effect.equals(MobEffects.MOVEMENT_SPEED)) {
            return 2;
        } else if (effect.equals(MobEffects.DIG_SPEED)) {
            return 1;
        } else if (effect.equals(MobEffects.DAMAGE_RESISTANCE)) {
            return 1;
        } else if (effect.equals(HEALTH_BOOST)) {
            return 3;
        } else {
            return -1; // Пропускаем другие эффекты
        }
    }


}
