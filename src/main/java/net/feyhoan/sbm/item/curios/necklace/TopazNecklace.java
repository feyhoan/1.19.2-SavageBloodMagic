package net.feyhoan.sbm.item.curios.necklace;

import net.feyhoan.sbm.item.curios.ToughnessIncreasingItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

public class TopazNecklace extends Item implements ICurioItem, ToughnessIncreasingItem {

    private static final UUID TOUGHNESS_MODIFIER_UUID = UUID.fromString("d3f5c8f5-6f7b-4b8a-bbc6-8e69d69d1235"); // Уникальный UUID для ожерелья
    private static final AttributeModifier TOUGHNESS_MODIFIER = new AttributeModifier(TOUGHNESS_MODIFIER_UUID, "TopazNecklaceEffect", 10, AttributeModifier.Operation.ADDITION); // Увеличиваем стойкость на 10

    public TopazNecklace() {
        super(new Properties().stacksTo(1).defaultDurability(20).tab(MYTAB));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.topaz_necklace.dressed").withStyle(ChatFormatting.YELLOW));
        increaseToughness(player); // Увеличиваем стойкость
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // Нет необходимости в добавлении модификатора каждый тик
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.topaz_necklace.undressed").withStyle(ChatFormatting.YELLOW));
        decreaseToughness(player); // Уменьшаем стойкость
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {components.add(Component.translatable("sbm.curios.topaz.info").withStyle(ChatFormatting.AQUA));}
        else{components.add(Component.translatable("sbm.items.tooltips").withStyle(ChatFormatting.YELLOW));}
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public AttributeModifier getToughnessModifier() {
        return TOUGHNESS_MODIFIER; // Возвращаем модификатор стойкости
    }
}