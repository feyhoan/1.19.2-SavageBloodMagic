package net.feyhoan.sbm.item.custom;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.item.ModItems;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;


@Mod.EventBusSubscriber
public class AncientReaperAbility {

    private static final int TAKEOFFMANA = 50;
    private static final float DAMAGE_MULTIPLIER = 3.0F; // Увеличение урона
    private static final long COOLDOWN_TIME = 15000; // 10 секунд в миллисекундах

    // Словарь для хранения времени последнего удара каждого игрока
    private static final Map<Player, Long> cooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        // Проверяем, кто атакует
        if (event.getEntity() != null && event.getSource().getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem(); // Получаем предмет в правой руке

            // Убедимся, что предмет - это ваш артефакт
            if (stack.getItem() == ModItems.ANCIENT_REAPER.get()) {
                // Получаем информацию о мане из капабилити
                player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                    long currentTime = System.currentTimeMillis(); // Текущее время

                    // Проверка кулдауна
                    if (cooldowns.getOrDefault(player, 0L) + COOLDOWN_TIME > currentTime) {
                        return;
                    }

                    if (blood.getMana() >= TAKEOFFMANA) {
                        float damage = event.getAmount() * DAMAGE_MULTIPLIER; // Увеличиваем урон
                        event.setAmount(damage); // Устанавливаем новый урон

                        player.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);

                        blood.subMana(TAKEOFFMANA); // Уменьшаем ману

                        ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);
                        cooldowns.put(player, currentTime); // Обновляем время последнего удара
                    }
                });
            }
        }
    }
}