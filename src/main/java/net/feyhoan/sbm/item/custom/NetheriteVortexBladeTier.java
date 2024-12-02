package net.feyhoan.sbm.item.custom;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class NetheriteVortexBladeTier implements Tier {

    public static final NetheriteVortexBladeTier INSTANCE = new NetheriteVortexBladeTier();

    @Override
    public int getUses() {
        return 4500;
    }

    @Override
    public float getSpeed() {
        return 7.1f;
    }

    @Override
    public float getAttackDamageBonus() {
        return 1.1f;
    }

    @Override
    public int getLevel() {
        return 5;
    }

    @Override
    public int getEnchantmentValue() {
        return 12;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}