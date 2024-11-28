package net.feyhoan.sbm.item.curios.ring;

import net.feyhoan.sbm.item.curios.ManaIncreasingItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static net.feyhoan.sbm.item.ModItems.MYTAB;

public class AmethystRing extends Item implements ICurioItem, ManaIncreasingItem {
    private static final int MAX_MANA_INCREASE = 50;
    private static final int MINIMUM_MANA_LEVEL = 1;

    public AmethystRing() {
        super(new Properties().stacksTo(1).defaultDurability(20).tab(MYTAB));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        if (isMage(player)) {
            increaseMana(player);
            player.sendSystemMessage(Component.translatable("sbm.curios.amethyst_ring.dressed").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        else{player.sendSystemMessage(Component.translatable("sbm.curios.amethyst_ring.warn").withStyle(ChatFormatting.RED));}
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) { }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        decreaseMana(player);
        player.sendSystemMessage(Component.translatable("sbm.curios.amethyst_ring.undressed").withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {components.add(Component.translatable("sbm.curios.amethyst.info").withStyle(ChatFormatting.AQUA));}
        else{components.add(Component.translatable("sbm.items.tooltips").withStyle(ChatFormatting.YELLOW));}
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public int getMaxManaIncrease() {
        return MAX_MANA_INCREASE;
    }

    @Override
    public int getMinimumManaLevel() {
        return MINIMUM_MANA_LEVEL;
    }
}