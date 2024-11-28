package net.feyhoan.sbm.sound;

import net.feyhoan.sbm.SBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SBM.MOD_ID);

    public static final RegistryObject<SoundEvent> BLOOD_MERGING = build("abilities.blood_merging");
    public static final RegistryObject<SoundEvent> HEAL_WOUNDS = build("abilities.heal_wounds");
    public static final RegistryObject<SoundEvent> BLOOD_PUNCH = build("abilities.blood_punch");
    public static final RegistryObject<SoundEvent> BLOOD_PURIFICATION = build("abilities.blood_purification");
    public static final RegistryObject<SoundEvent> BLOOD_MARK = build("abilities.blood_mark");
    public static final RegistryObject<SoundEvent> ARTERIAL_RUPTURE = build("abilities.arterial_rupture");
    public static final RegistryObject<SoundEvent> ABSORBING_SHIELD = build("abilities.absorbing_shield");
    public static final RegistryObject<SoundEvent> CANCEL = build("abilities.cancel");

    private static RegistryObject<SoundEvent> build(String id) {
        return SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation(SBM.MOD_ID, id)));
    }
}