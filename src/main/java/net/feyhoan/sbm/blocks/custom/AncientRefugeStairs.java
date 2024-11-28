package net.feyhoan.sbm.blocks.custom;

import net.feyhoan.sbm.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static net.feyhoan.sbm.blocks.ModBlocks.ANCIENT_REFUGE_BLOCK;


public class AncientRefugeStairs extends StairBlock {

    public AncientRefugeStairs(@NotNull Supplier<BlockState> state) {
        super(state, BlockBehaviour.Properties.copy(ANCIENT_REFUGE_BLOCK.get()));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.BLESSING_OF_THE_ANCIENTS.get(), 20, 5, true, true));
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("block.sbm.ancient_refuge.tooltip").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("sbm.items.tooltips").withStyle(ChatFormatting.YELLOW));
        }
    }
}