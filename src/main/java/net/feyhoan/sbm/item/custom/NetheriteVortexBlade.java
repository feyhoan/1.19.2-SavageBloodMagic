package net.feyhoan.sbm.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class NetheriteVortexBlade extends SwordItem {

    public NetheriteVortexBlade(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        player.playSound(SoundEvents.FIRE_EXTINGUISH);
        return super.onLeftClickEntity(stack, player, entity);
    }
}