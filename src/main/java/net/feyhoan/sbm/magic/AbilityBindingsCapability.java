package net.feyhoan.sbm.magic;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbilityBindingsCapability {
    private Map<Utils.AbilityBindingsKeys, String> abilityBindings = new HashMap<>();

    public void setKeyAbility(Utils.AbilityBindingsKeys key, String abilityName) {
        abilityBindings.put(key, abilityName);
    }

    public String getAbilityName(Utils.AbilityBindingsKeys key) {
        return abilityBindings.get(key);
    }

    public Map<Utils.AbilityBindingsKeys, String> getBinds() {
        return abilityBindings;
    }

    public void copyFrom(AbilityBindingsCapability source) {
        this.abilityBindings = new HashMap<>(source.abilityBindings); // Копируем по значению
    }

    public void saveNBTData(CompoundTag compoundTag) {
        for (Utils.AbilityBindingsKeys key : Utils.AbilityBindingsKeys.values()) {
            String abilityName = abilityBindings.get(key);
            if (abilityName != null) {
                compoundTag.putString(key.name(), abilityName);
            }
        }
    }

    public void loadNBTData(CompoundTag compound) {
        for (Utils.AbilityBindingsKeys key : Utils.AbilityBindingsKeys.values()) {
            if (compound.contains(key.name())) {
                String abilityName = compound.getString(key.name());
                abilityBindings.put(key, abilityName);
                SBM.LOGGER.info("Загружена способность: {} для ключа: {}", abilityName, key.name());
            } else {
                abilityBindings.put(key, "defaultAbilityName"); // Значение по умолчанию
                SBM.LOGGER.info("Для ключа {} установлено значение по умолчанию", key.name());
            }
        }
    }
}