package net.feyhoan.sbm.potion;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, SBM.MOD_ID);

    public static final RegistryObject<Potion> BLOODY_WITHERING_POTION = POTIONS.register("bloody_withering_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.BLOODY_WITHERING.get(), 60, 3)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
