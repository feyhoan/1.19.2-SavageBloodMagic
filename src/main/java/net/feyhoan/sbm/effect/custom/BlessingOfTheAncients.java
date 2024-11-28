package net.feyhoan.sbm.effect.custom;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlessingOfTheAncients extends MobEffect {
    public BlessingOfTheAncients(MobEffectCategory category, int duration) {
        super(category, duration);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity user, LivingEntity target, int amplifier, double modifiers) {
        super.applyInstantenousEffect(source, user, target, amplifier, modifiers);

        if (target instanceof Player player) {
            boolean isMag = player.getCapability(PlayerBloodProvider.PLAYER_BLOOD)
                    .map(blood -> blood.getLevel() > 0)
                    .orElse(false);

            if (isMag) {
                applyMageModifiers(target, amplifier);
            } else {
                applyNonMageModifiers(target, amplifier);
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.removeAttributeModifiers(entity, map, amplifier);
        // Убедитесь, что вы удаляете модификаторы при этом
    }

    private void applyMageModifiers(LivingEntity entity, int amplifier) {
        // Применяем модификаторы для магов
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + 2 * amplifier);
        entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() + 0.05 * amplifier);
        entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + 1 * amplifier);
    }

    private void applyNonMageModifiers(LivingEntity entity, int amplifier) {
        // Применяем модификаторы для немагов
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - 1 * amplifier);
        entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 0.02 * amplifier);
        entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() - 2 * amplifier);
    }
}