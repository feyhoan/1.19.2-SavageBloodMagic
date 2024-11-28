package net.feyhoan.sbm.magic;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public abstract class BloodAbilities {
    private String name;
    private int cooldown;
    private int manaCost;
    private boolean isActive;
    private boolean isOnCooldown;
    public BloodAbilities(String name, int manaCost, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
    }
    public abstract void activate(ServerPlayer player);
    public String getName() {
        return name;
    }
    public int getCooldown() {
        return cooldown;
    }
    public int getManaCost() {
        return manaCost;
    }
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
    public boolean isOnCooldown() {
        return isOnCooldown;
    }
    public void setOnCooldown(boolean onCooldown) {
        this.isOnCooldown = onCooldown;
    }
}