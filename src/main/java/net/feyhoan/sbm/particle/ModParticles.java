package net.feyhoan.sbm.particle;

import net.feyhoan.sbm.SBM;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SBM.MOD_ID);

    public static final RegistryObject<SimpleParticleType> BLOOD_LEAP_PARTICLE =
            PARTICLE_TYPES.register("blood_leap_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLOOD_MARK_PARTICLE =
            PARTICLE_TYPES.register("blood_mark_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FAIL_PARTICLE =
            PARTICLE_TYPES.register("fail_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> LEVEL_UP_PARTICLE =
            PARTICLE_TYPES.register("level_up_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> LEVEL_DOWN_PARTICLE =
            PARTICLE_TYPES.register("level_down_particle", () -> new SimpleParticleType(true));


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}