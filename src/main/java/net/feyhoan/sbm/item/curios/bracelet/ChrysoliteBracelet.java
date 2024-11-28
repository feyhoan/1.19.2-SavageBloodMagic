package net.feyhoan.sbm.item.curios.bracelet;

import net.feyhoan.sbm.item.curios.HealthIncreasingItem;
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

public class ChrysoliteBracelet extends Item implements ICurioItem,HealthIncreasingItem {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("e4f6a28d-5d47-4def-a46b-0e659cbf6f20");
    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(HEALTH_MODIFIER_UUID, "ChrysoliteBraceletEffect", 4, AttributeModifier.Operation.ADDITION);

    public ChrysoliteBracelet() {
        super(new Properties().stacksTo(1).defaultDurability(20).tab(MYTAB));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.chrysolite_bracelet.dressed").withStyle(ChatFormatting.GREEN));
        increaseHealth(player); // Увеличиваем здоровье
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // Нет необходимости в добавлении модификатора каждый тик
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        player.sendSystemMessage(Component.translatable("sbm.curios.chrysolite_bracelet.undressed").withStyle(ChatFormatting.GREEN));
        decreaseHealth(player); // Уменьшаем здоровье
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {components.add(Component.translatable("sbm.curios.chrysolite.info").withStyle(ChatFormatting.AQUA));}
        else{components.add(Component.translatable("sbm.items.tooltips").withStyle(ChatFormatting.YELLOW));}
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public AttributeModifier getHealthModifier() {
        return HEALTH_MODIFIER; // Возвращаем модификатор здоровья
    }
}