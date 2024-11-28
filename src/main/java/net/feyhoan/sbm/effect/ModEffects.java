package net.feyhoan.sbm.effect;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.effect.custom.BlessingOfTheAncients;
import net.feyhoan.sbm.effect.custom.BloodMarkEffect;
import net.minecraft.world.effect.HealthBoostMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SBM.MOD_ID);

    public static final RegistryObject<MobEffect> BLESSING_OF_THE_ANCIENTS =
           EFFECTS.register("blessing_of_the_ancients", () -> new BlessingOfTheAncients(MobEffectCategory.NEUTRAL, 16284963));

    public static final RegistryObject<MobEffect> BLOOD_MARK =
            EFFECTS.register("blood_mark", () -> new BloodMarkEffect(MobEffectCategory.HARMFUL, 330000));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}