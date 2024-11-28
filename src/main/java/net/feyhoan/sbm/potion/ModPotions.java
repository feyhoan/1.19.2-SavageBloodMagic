package net.feyhoan.sbm.potion;

import net.feyhoan.sbm.SBM;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, SBM.MOD_ID);

    //public static final RegistryObject<Potion> NEGR_POTION = POTIONS.register("negr_potion",
    //        () -> new Potion(new MobEffectInstance(MobEffects.DARKNESS, 60, 3), new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
