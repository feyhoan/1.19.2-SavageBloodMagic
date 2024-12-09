package net.feyhoan.sbm.util;

import com.mojang.brigadier.arguments.ArgumentType;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityBindingsConfig {
    private static final Map<AbilityBindingsKeys, String> abilityBindings = new HashMap<>();

    public enum AbilityBindingsKeys {
        FIRST, SECOND, THIRD, FOURTH
    }

    public static void setKeyAbility(AbilityBindingsKeys key, String abilityName,Player player) {
        abilityBindings.put(key, abilityName);
        AbilityBindingsConfig.saveToPlayerData(player);
    }

    public static String getAbilityName(AbilityBindingsKeys key) {
        return abilityBindings.get(key);
    }

    public static Map<AbilityBindingsKeys, String> getBinds() {
        return abilityBindings;
    }

    public static CompoundTag saveToNBT() {
        CompoundTag compoundTag = new CompoundTag();
        for (AbilityBindingsKeys key : AbilityBindingsKeys.values()) {
            String abilityName = abilityBindings.get(key);
            if (abilityName != null) {
                compoundTag.putString(key.name(), abilityName);
            }
        }
        return compoundTag;
    }

    public static void loadFromNBT(CompoundTag compound) {
        for (AbilityBindingsKeys key : AbilityBindingsKeys.values()) {
            if (compound.contains(key.name())) {
                String abilityName = compound.getString(key.name());
                abilityBindings.put(key, abilityName);
            } else {
                abilityBindings.put(key, "defaultAbilityName"); // Значение по умолчанию
            }
        }
    }

    // Сохранение данных
    public static void saveToPlayerData(Player player) {
        CompoundTag nbtData = saveToNBT();
        player.getPersistentData().put("AbilityBindings", nbtData); // Сохраняем в данных игрока
    }

    // Загрузка данных
    public static void loadFromPlayerData(Player player) {
        if (player.getPersistentData().contains("AbilityBindings")) {
            CompoundTag nbtData = player.getPersistentData().getCompound("AbilityBindings");
            loadFromNBT(nbtData);
        }
    }
}