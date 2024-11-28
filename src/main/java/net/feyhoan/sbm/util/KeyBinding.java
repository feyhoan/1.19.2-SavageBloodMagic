package net.feyhoan.sbm.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String KEY_CATEGORY_SBM = "key.category.sbm.sbm";
    public static final String KEY_OPEN_ABILITY_PRESETS = "key.sbm.open_presets";
    public static final String KEY_FIRST_ABILITY = "key.sbm.first_ability";
    public static final String KEY_SECOND_ABILITY = "key.sbm.second_ability";
    public static final String KEY_THIRD_ABILITY = "key.sbm.third_ability";
    public static final String KEY_FOURTH_ABILITY = "key.sbm.fourth_ability";

    public static final KeyMapping OPEN_ABILITY_PRESETS_KEY = new KeyMapping(KEY_OPEN_ABILITY_PRESETS, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, KEY_CATEGORY_SBM);

    public static final KeyMapping FIRST_ABILITY_KEY = new KeyMapping(KEY_FIRST_ABILITY, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_SBM);
    public static final KeyMapping SECOND_ABILITY_KEY = new KeyMapping(KEY_SECOND_ABILITY, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_SBM);
    public static final KeyMapping THIRD_ABILITY_KEY = new KeyMapping(KEY_THIRD_ABILITY, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KEY_CATEGORY_SBM);
    public static final KeyMapping FOURTH_ABILITY_KEY = new KeyMapping(KEY_FOURTH_ABILITY, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, KEY_CATEGORY_SBM);
}