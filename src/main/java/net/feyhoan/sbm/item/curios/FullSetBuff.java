package net.feyhoan.sbm.item.curios;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;


@Mod.EventBusSubscriber
public class FullSetBuff {

    private static final Random random = new Random();

    private static final Set<String> hyacinthSet = new HashSet<>();
    private static final Set<String> chrysoliteSet = new HashSet<>();
    private static final Set<String> topazSet = new HashSet<>();
    public static final Set<String> amethystSet = new HashSet<>();
    private static final Set<String> sapphireSet = new HashSet<>();

    static {
        hyacinthSet.add("item.sbm.hyacinth_ring");
        hyacinthSet.add("item.sbm.hyacinth_necklace");
        hyacinthSet.add("item.sbm.hyacinth_bracelet");

        chrysoliteSet.add("item.sbm.chrysolite_ring");
        chrysoliteSet.add("item.sbm.chrysolite_necklace");
        chrysoliteSet.add("item.sbm.chrysolite_bracelet");

        topazSet.add("item.sbm.topaz_ring");
        topazSet.add("item.sbm.topaz_necklace");
        topazSet.add("item.sbm.topaz_bracelet");

        amethystSet.add("item.sbm.amethyst_ring");
        amethystSet.add("item.sbm.amethyst_necklace");
        amethystSet.add("item.sbm.amethyst_bracelet");

        sapphireSet.add("item.sbm.sapphire_ring");
        sapphireSet.add("item.sbm.sapphire_necklace");
        sapphireSet.add("item.sbm.sapphire_bracelet");
    }

    private static final Map<Player, Set<String>> playerBuffs = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.START && !player.level.isClientSide) {

            Set<String> currentBuffs = playerBuffs.getOrDefault(player, new HashSet<>());

            // Проверка каждого баффа
            boolean hyacinthBuffActive = hasFullSet(player, hyacinthSet);
            boolean chrysoliteBuffActive = hasFullSet(player, chrysoliteSet);
            boolean topazBuffActive = hasFullSet(player, topazSet);
            boolean amethystBuffActive = hasFullSet(player, amethystSet);
            boolean sapphireBuffActive = hasFullSet(player, sapphireSet);

            // Логика для Hyacinth
            if (hyacinthBuffActive && !currentBuffs.contains("hyacinth")) {
                currentBuffs.add("hyacinth");
            } else if (!hyacinthBuffActive && currentBuffs.contains("hyacinth")) {
                removeHyacinthBuff(player);
                currentBuffs.remove("hyacinth");
            }

            // Логика для Chrysolite
            if (chrysoliteBuffActive && !currentBuffs.contains("chrysolite")) {
                applyChrysoliteBuff(player);
                currentBuffs.add("chrysolite");
            } else if (!chrysoliteBuffActive && currentBuffs.contains("chrysolite")) {
                removeChrysoliteBuff(player);
                currentBuffs.remove("chrysolite");
            }

            // Логика для Topaz
            if (topazBuffActive && !currentBuffs.contains("topaz")) {
                applyTopazBuff(player);
                currentBuffs.add("topaz");
            } else if (!topazBuffActive && currentBuffs.contains("topaz")) {
                removeTopazBuff(player);
                currentBuffs.remove("topaz");
            }

            // Логика для Amethyst
            if (amethystBuffActive && !currentBuffs.contains("amethyst")) {
                applyAmethystBuff(player);
                currentBuffs.add("amethyst");
            } else if (!amethystBuffActive && currentBuffs.contains("amethyst")) {
                removeAmethystBuff(player);
                currentBuffs.remove("amethyst");
            }

            // Логика для Sapphire
            if (sapphireBuffActive && !currentBuffs.contains("sapphire")) {
                applySapphireBuff(player);
                currentBuffs.add("sapphire");
            } else if (!sapphireBuffActive && currentBuffs.contains("sapphire")) {
                removeSapphireBuff(player);
                currentBuffs.remove("sapphire");
            }

            // Обновляем playerBuffs для игрока
            playerBuffs.put(player, currentBuffs);
        }
    }

    public static boolean hasFullSet(Player player, Set<String> requiredArtifacts) {
        Set<String> equippedArtifacts = new HashSet<>();

        // Получаем все артефакты, которые носит игрок
        LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        handlerOptional.ifPresent(handler -> {
            // Получаем все артефакты в инвентаре
            Map<String, ICurioStacksHandler> curios = handler.getCurios();
            curios.forEach((identifier, stacksHandler) -> {
                // Получаем стеки предметов
                // Предполагаем, что stacksHandler имеет метод getStacks()
                if (stacksHandler != null) {
                    // Здесь следите за тем, чтобы использовать правильный метод для получения стека
                    // В зависимости от реализации, используйте метод, возвращающий массив или список стаков.
                    int stackSize = stacksHandler.getSlots(); // Или аналогичный метод
                    for (int slotIndex = 0; slotIndex < stackSize; slotIndex++) {
                        ItemStack itemStack = stacksHandler.getStacks().getStackInSlot(slotIndex); // Получаем стек по индексу
                        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ICurioItem) {
                            // Получаем идентификатор предмета
                            String itemId = itemStack.getItem().getDescriptionId();
                            equippedArtifacts.add(itemId);
                        }
                    }
                }
            });
        });

        // Проверяем, собран ли нужный сет
        return equippedArtifacts.containsAll(requiredArtifacts);
    }

    @SubscribeEvent
    public static void onEntityAttack(AttackEntityEvent event) {
        Player player = event.getEntity();

        // Получаем цель атаки
        Entity target = event.getTarget();

        // Проверяем, является ли цель LivingEntity и если у игрока полный комплект
        if (target instanceof LivingEntity livingTarget && hasFullSet(player, hyacinthSet)) {
            applyHyacinthBuff(player, livingTarget); // Применяем бафф если комплект есть
        }
    }

    private static void applyHyacinthBuff(Player player, LivingEntity target) {
        // 30% шанс наложить отравление
        if (random.nextDouble() < 0.30) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false));
        }
    }

    private static void applyChrysoliteBuff(Player player) {
        //player.sendSystemMessage(Component.literal("Фул сет собран! Вам дарована вечная регенерация").withStyle(ChatFormatting.GREEN));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Integer.MAX_VALUE, 0, false, false)); // Вечная регенерация
    }

    private static void applyTopazBuff(Player player) {
        //player.sendSystemMessage(Component.literal("Фул сет собран! Вам дарована вечная огнеустойкость").withStyle(ChatFormatting.YELLOW));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
    }

    private static void applyAmethystBuff(Player player) {
        //player.sendSystemMessage(Component.literal("Фул сет собран! Вам дарована ускоренная регенерация маны").withStyle(ChatFormatting.LIGHT_PURPLE));
        //player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
        //    blood.setManaRegenTicks(10);
         //   ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);        });
    }

    private static void applySapphireBuff(Player player) {
        //player.sendSystemMessage(Component.literal("Фул сет собран! Вам дарован прыжок в 2 блока").withStyle(ChatFormatting.LIGHT_PURPLE));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, Integer.MAX_VALUE, 2, false, false));
    }

    // Методы для снятия эффекта
    private static void removeHyacinthBuff(Player player) {
        player.removeEffect(MobEffects.POISON);  // Удаляем эффект отравления
    }

    private static void removeChrysoliteBuff(Player player) {
        player.removeEffect(MobEffects.REGENERATION); // Удаляем эффект регенерации
    }

    private static void removeTopazBuff(Player player) {
        player.removeEffect(MobEffects.FIRE_RESISTANCE); // Удаляем эффект огнеустойчивости
    }

    private static void removeAmethystBuff(Player player) {
        //player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
        //    blood.setManaRegenSpeed(20);
        //    ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), (ServerPlayer) player);        });
    }

    private static void removeSapphireBuff(Player player) {
        player.removeEffect(MobEffects.JUMP); // Удаляем эффект прыжка
    }
}