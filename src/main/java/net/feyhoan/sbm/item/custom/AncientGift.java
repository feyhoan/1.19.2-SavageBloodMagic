package net.feyhoan.sbm.item.custom;

import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AncientGift extends Item {
    private static final Random random = new Random();
    private static final int PRECIOUS_ORE_CHANCE = 5; // Вероятность в процентах для драгоценных руд (например, 5%)

    public AncientGift(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!player.level.isClientSide) { // Убедитесь, что код выполняется на сервере
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            int chance = random.nextInt(100); // Генерируем случайное число от 0 до 99
            if (chance < PRECIOUS_ORE_CHANCE) {
                // Логика получения драгоценных руд
                player.addItem(Items.DIAMOND.getDefaultInstance()); // Пример получения алмаза, вы можете изменить его на нужные руды
                player.sendSystemMessage(Component.translatable("item.sbm.ancient_gift_ore"));
            } else {
                // Логика для 95% шанса (например, получение другого предмета)
                ModMessages.sendToServer(new AbilityActionPacket(AbilityActionPacket.AbilityAction.ADD_RANDOM, "none", player.getUUID()));
                player.sendSystemMessage(Component.translatable("item.sbm.ancient_gift_ability"));
            }
            // Удаляем гифт после использования
            itemInHand.shrink(1); // Уменьшаем количество предмета на 1
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}