package net.feyhoan.sbm.magic;

import net.feyhoan.sbm.blood.PlayerBlood;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BloodAbilitiesProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<BloodAbilitiesCapability> BLOOD_ABILITIES = CapabilityManager.get(new CapabilityToken<BloodAbilitiesCapability>() {});
    private BloodAbilitiesCapability bloodAbilities; // Храним все данные в одном экземпляре
    private final LazyOptional<BloodAbilitiesCapability> optional = LazyOptional.of(() -> bloodAbilities);
    public BloodAbilitiesProvider() {
        this.bloodAbilities = new BloodAbilitiesCapability(); // Инициализируем экземпляр BloodAbilitiesCapability
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == BLOOD_ABILITIES) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        bloodAbilities.saveNBTData(nbt); // Сохранение всех данных из bloodAbilities
        return nbt;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        bloodAbilities.loadNBTData(nbt); // Загрузка всех данных в bloodAbilities
    }
}