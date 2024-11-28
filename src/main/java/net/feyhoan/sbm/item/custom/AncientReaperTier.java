package net.feyhoan.sbm.item.custom;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class AncientReaperTier implements Tier {

    public static final AncientReaperTier INSTANCE = new AncientReaperTier();

    @Override
    public int getUses() {
        return 2000;
    }

    @Override
    public float getSpeed() {
        return 5.5f;
    }

    @Override
    public float getAttackDamageBonus() {
        return 1.5f;
    }

    @Override
    public int getLevel() {
        return 5;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}