package net.feyhoan.sbm.blood;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PlayerBlood {
    private int mana;
    private int maxmana;
    private int level;
    private int manaRegenTicks;
    private int defaultManaRegenTicks = 20;

    public int getMana() {
        return mana;
    }
    public int getMaxMana() {
        return maxmana;
    }
    public int getLevel() {
        return level;
    }
    public int getManaRegenTicks() {
        return manaRegenTicks;
    }

    public void addMana(int add) {
        this.mana = Math.min(mana + add, maxmana); // Убедимся, что текущая мана не превышает maxmana
    }

    public void subMana(int sub) {
        this.mana = Math.max(mana - sub, 0);
    }

    public void levelUp() {
        this.level = Math.min(level + 1, 5);
        this.maxmana = level * 100; // Максимальная мана должна быть level * 100
        this.mana = Math.min(mana, maxmana); // Убедитесь, что текущая мана не превышает maxmana
    }

    public void levelDown() {
        this.level = Math.max(level - 1, 0);
        this.maxmana = level * 100; // Максимальная мана должна быть level * 100
        this.mana = Math.min(mana, maxmana); // Убедитесь, что текущая мана не превышает maxmana
    }

    public void setMana(int mana) {
        this.mana = Math.min(mana, maxmana); // Убедитесь, что мана не превышает maxmana
    }

    public void setLevel(int level) {
        this.level = Math.max(0, Math.min(level, 5));
        this.maxmana = Math.min(level * 100, 500); // Максимальная мана должна быть level * 100
        this.mana = Math.min(mana, maxmana); // Убедитесь, что текущая мана не превышает maxmana
    }

    public void setMaxMana(int maxmana) {
        this.maxmana = Math.max(0, maxmana);
        this.mana = Math.min(mana, maxmana); // Убедитесь, что текущая мана не превышает maxmana
    }

    public void setManaRegenSpeed(int tick) {
        this.manaRegenTicks = Math.max(0, tick);
    }

    public void subManaRegenTick() {
        this.manaRegenTicks--;
    }

    public int getDefaultManaRegenTicks() {
        return defaultManaRegenTicks;
    }

    public void setManaRegenTicks(int ticks) {
        this.manaRegenTicks = ticks;
    }


    public void copyFrom(PlayerBlood source) {
        this.mana = source.mana;
        this.maxmana = source.maxmana;
        this.level = source.level;
        this.manaRegenTicks = source.manaRegenTicks;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("mana", mana);
        nbt.putInt("maxmana", maxmana);
        nbt.putInt("level", level);
        nbt.putInt("manaRegenTicks", manaRegenTicks);
    }

    public void loadNBTData(CompoundTag nbt) {
        mana = nbt.getInt("mana");
        maxmana = nbt.getInt("maxmana");
        level = nbt.getInt("level");
        manaRegenTicks = nbt.getInt("manaRegenTicks");
    }
}