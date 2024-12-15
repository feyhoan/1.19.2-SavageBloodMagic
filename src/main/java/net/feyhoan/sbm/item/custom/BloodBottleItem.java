package net.feyhoan.sbm.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static net.feyhoan.sbm.item.ModItems.*;

public class BloodBottleItem extends BottleItem {
    public BloodBottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        // Проверяем, какой тип крови находится в бутылке
        if (this == BOTTLE_OF_HUMAN_BLOOD.get()) {
            // Если это человеческая кровь, то выпиваем ее
            player.heal(4);
            player.getItemInHand(hand).shrink(1);
            player.swing(hand);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        } else if (this == BOTTLE_OF_NETHER_BLOOD.get()) {
            // Если это адская кровь, то выпиваем ее
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100));
            player.getItemInHand(hand).shrink(1);
            player.swing(hand);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        } else if (this == BOTTLE_OF_ENDER_BLOOD.get()) {
            // Если это драконья кровь, то выпиваем ее
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100));
            player.getItemInHand(hand).shrink(1);
            player.swing(hand);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}