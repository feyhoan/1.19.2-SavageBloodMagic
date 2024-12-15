package net.feyhoan.sbm.item.custom;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
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
            if (player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getLevel() > 0).orElse(false)) {
                int chance = random.nextInt(100); // Генерируем случайное число от 0 до 99
                if (chance < PRECIOUS_ORE_CHANCE) {
                    // Определяем количество выпадающих алмазов от 3 до 10
                    int diamondCount = 3 + (int)(Math.random() * 8); // Случайное число от 3 до 10

                    // Позиция рядом с игроком, на которую будут падать предметы
                    BlockPos playerPos = player.blockPosition();
                    Random random = new Random();

                    // Выдаем алмазы
                    for (int i = 0; i < diamondCount; i++) {
                        // Определяем случайные смещения для падения предметов
                        double offsetX = random.nextDouble() * 0.5 - 0.25; // Случайное смещение по X
                        double offsetZ = random.nextDouble() * 0.5 - 0.25; // Случайное смещение по Z

                        // Создаем предмет (алмаз) и помещаем его в мир
                        ItemEntity diamond = new ItemEntity(player.getCommandSenderWorld(),
                                player.getX() + offsetX+2,
                                player.getY() + 1, // Немного выше уровня земли
                                player.getZ() + offsetZ,
                                Items.DIAMOND.getDefaultInstance());
                        player.getCommandSenderWorld().addFreshEntity(diamond);
                    }

                    // Выпуск изумрудов (также от 3 до 10)
                    int emeraldCount = 3 + (int)(Math.random() * 8);
                    for (int i = 0; i < emeraldCount; i++) {
                        double offsetX = random.nextDouble() * 0.5 - 0.25; // Случайное смещение по X
                        double offsetZ = random.nextDouble() * 0.5 - 0.25; // Случайное смещение по Z

                        ItemEntity emerald = new ItemEntity(player.getCommandSenderWorld(),
                                player.getX() + offsetX+2,
                                player.getY() + 1,
                                player.getZ() + offsetZ,
                                Items.EMERALD.getDefaultInstance());
                        player.getCommandSenderWorld().addFreshEntity(emerald);
                    }

                    // Выпуск тотема бессмертия
                    ItemEntity totem = new ItemEntity(player.getCommandSenderWorld(),
                            player.getX()+2,
                            player.getY() + 1,
                            player.getZ(),
                            Items.TOTEM_OF_UNDYING.getDefaultInstance());
                    player.getCommandSenderWorld().addFreshEntity(totem);

                    // Выпуск золотого яблока
                    ItemEntity goldenApple = new ItemEntity(player.getCommandSenderWorld(),
                            player.getX()+2,
                            player.getY() + 1,
                            player.getZ(),
                            Items.GOLDEN_APPLE.getDefaultInstance());
                    player.getCommandSenderWorld().addFreshEntity(goldenApple);
                    player.sendSystemMessage(Component.translatable("item.sbm.ancient_gift_ore"));
                } else {
                    // Логика для 95% шанса (например, получение другого предмета)
                    ModMessages.sendToServer(new AbilityActionPacket(AbilityActionPacket.AbilityAction.ADD_RANDOM, "none", player.getUUID()));
                    player.sendSystemMessage(Component.translatable("item.sbm.ancient_gift_ability"));
                }
                // Удаляем гифт после использования
                itemInHand.shrink(1); // Уменьшаем количество предмета на 1
            }
            else {
                player.sendSystemMessage(Component.translatable("item.sbm.ancient_gift_not_a_mage"));
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}