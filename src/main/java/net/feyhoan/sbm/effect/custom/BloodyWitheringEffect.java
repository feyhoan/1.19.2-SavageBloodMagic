package net.feyhoan.sbm.effect.custom;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BloodyWitheringEffect extends MobEffect {
    public BloodyWitheringEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    static float MANA_SUB = 1.5F;

    @Override
    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
        // Проверяем, является ли игрок магом крови
        boolean isBloodMage = p_19467_.getCapability(PlayerBloodProvider.PLAYER_BLOOD)
                .map(blood -> blood.getLevel() != 0).orElse(false);

        // Проверяем, есть ли у игрока мана
        boolean hasMana = p_19467_.getCapability(PlayerBloodProvider.PLAYER_BLOOD)
                .map(blood -> blood.getMana() >= MANA_SUB).orElse(false);

        if (isBloodMage && hasMana) {
            // Если игрок маг крови и у него есть мана, снимаем ману и наносим урон
            p_19467_.hurt(DamageSource.GENERIC, (float) (p_19467_.getHealth() * 0.1));
            ModMessages.sendToServer(new AbilitySubManaC2SPacket((int) MANA_SUB, p_19467_.getUUID()));
        } else {
            // Если игрок не маг крови, замедляем его движения
            p_19467_.setDeltaMovement(p_19467_.getDeltaMovement().multiply(0.5, 1, 0.5)); // Уменьшаем скорость
        }
    }

    @Override
    public boolean isDurationEffectTick(int p_19469_, int p_19470_) {
        return true; // Эффект срабатывает каждый тик
    }
}