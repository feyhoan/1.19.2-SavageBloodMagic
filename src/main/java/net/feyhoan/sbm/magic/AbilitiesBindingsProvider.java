package net.feyhoan.sbm.magic;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AbilitiesBindingsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<AbilityBindingsCapability> ABILITIES_BINDINGS = CapabilityManager.get(new CapabilityToken<>() { });

    private final AbilityBindingsCapability abilityBindingsCapability = new AbilityBindingsCapability(); // Храним все данные в одном экземпляре
    private final LazyOptional<AbilityBindingsCapability> optional = LazyOptional.of(() -> abilityBindingsCapability);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ABILITIES_BINDINGS) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        abilityBindingsCapability.saveNBTData(nbt); // Сохранение всех данных из abilityBindingsCapability
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        abilityBindingsCapability.loadNBTData(nbt); // Загрузка всех данных в abilityBindingsCapability
    }
}