package net.feyhoan.sbm.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BloodMarkEffect extends MobEffect {
    public BloodMarkEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    @Override
    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
        // Увеличиваем поглощение здоровья
        p_19467_.setAbsorptionAmount(p_19467_.getAbsorptionAmount() - 1f);

        // Применяем эффект слабой слепоты
        p_19467_.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false, false));

        // Замедляем движение
        p_19467_.setDeltaMovement(p_19467_.getDeltaMovement().multiply(0.9, 1, 0.9)); // Замедляет по оси X и Z
    }

    @Override
    public boolean isDurationEffectTick(int p_19469_, int p_19470_) {
        return true; // Эффект срабатывает каждый тик
    }
}