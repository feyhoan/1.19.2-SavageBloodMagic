package net.feyhoan.sbm.util;

import com.mojang.brigadier.arguments.ArgumentType;
import net.feyhoan.sbm.magic.BloodAbilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityBindingsConfig {
    private static final Map<AbilityBindingsKeys, String> abilityBindings = new HashMap<>();

    public enum AbilityBindingsKeys {
        FIRST, SECOND, THIRD, FOURTH
    }

    public static void setKeyAbility(AbilityBindingsKeys key, String abilityName) {
        abilityBindings.put(key, abilityName);
    }

    public static String getAbilityName(AbilityBindingsKeys key) {
        return abilityBindings.get(key);
    }

    public static Map<AbilityBindingsKeys, String> getBinds() {
        return abilityBindings;
    }
}