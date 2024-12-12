package net.feyhoan.sbm.util;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.particle.ModParticles;
import net.feyhoan.sbm.particle.custom.BloodLeapParticle;
import net.feyhoan.sbm.particle.custom.BloodMarkParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SBM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEventHandler {
    @SubscribeEvent()
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(ModParticles.BLOOD_LEAP_PARTICLE.get(), BloodLeapParticle.Factory::new);
        manager.register(ModParticles.BLOOD_MARK_PARTICLE.get(), BloodMarkParticle.Factory::new);
        SBM.LOGGER.info("Registered Particle Factories");
    }
}