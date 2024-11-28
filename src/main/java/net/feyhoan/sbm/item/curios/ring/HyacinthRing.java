package net.feyhoan.sbm.item.curios.ring;

import net.feyhoan.sbm.item.curios.DamageIncreasingItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

import static net.feyhoan.sbm.item.ModItems.MYTAB;

public class HyacinthRing extends Item implements ICurioItem,DamageIncreasingItem {

    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("f1a81166-1ccc-4e47-9bbc-82fd06f8675a");
    private static final AttributeModifier STRENGTH_MODIFIER = new AttributeModifier(STRENGTH_MODIFIER_UUID, "HyacinthRingEffect", 2.5, AttributeModifier.Operation.ADDITION);

    public HyacinthRing() {
        super(new Properties().stacksTo(1).defaultDurability(20).tab(MYTAB));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.hyacinth_ring.dressed").withStyle(ChatFormatting.RED));

        increaseDamage(player); // Увеличиваем урон
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // Нет необходимости в добавлении модификатора каждый тик
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.hyacinth_ring.undressed").withStyle(ChatFormatting.RED));

        decreaseDamage(player); // Уменьшаем урон
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {components.add(Component.translatable("sbm.curios.hyacinth.info").withStyle(ChatFormatting.AQUA));}
        else{components.add(Component.translatable("sbm.items.tooltips").withStyle(ChatFormatting.YELLOW));}
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public AttributeModifier getDamageModifier() {
        return STRENGTH_MODIFIER; // Возвращаем модификатор урона
    }
}