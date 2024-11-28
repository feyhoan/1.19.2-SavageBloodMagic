package net.feyhoan.sbm.event;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBlood;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.item.ModItems;
import net.feyhoan.sbm.item.curios.FullSetBuff;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.magic.BloodAbilitiesCapability;
import net.feyhoan.sbm.magic.BloodAbilitiesProvider;
import net.feyhoan.sbm.magic.abilities.nether.BloodMark;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.feyhoan.sbm.network.packet.ManaRegenerationPacket;
import net.feyhoan.sbm.network.packet.ScrollUseC2SPacket;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static net.feyhoan.sbm.SBM.LOGGER;
import static net.feyhoan.sbm.item.ModItems.*;
import static net.feyhoan.sbm.item.curios.FullSetBuff.amethystSet;
import static net.feyhoan.sbm.util.Utils.*;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = "sbm")
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerBloodProvider.PLAYER_BLOOD).isPresent()) {
                    event.addCapability(new ResourceLocation(SBM.MOD_ID, "properties"), new PlayerBloodProvider());
                }
                if (!event.getObject().getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).isPresent()) {
                    event.addCapability(new ResourceLocation(SBM.MOD_ID, "ability-properties"), new BloodAbilitiesProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().reviveCaps();
                event.getOriginal().getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(oldStore -> {
                    event.getEntity().getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
                event.getOriginal().getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(oldStore -> {
                    event.getEntity().getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
                event.getOriginal().invalidateCaps();
            }
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(PlayerBlood.class);
            event.register(BloodAbilitiesCapability.class);
        }

        @SubscribeEvent
        public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
            if (!event.getLevel().isClientSide()) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                        ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), player);
                    });
                }
            }
        }

        @SubscribeEvent
        public static void manaRegeneration(TickEvent.ServerTickEvent event) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> {
                    int manaRegenTicks = blood.getDefaultManaRegenTicks();
                    if (Utils.hasAncientColumnAroundThem(player, player.getLevel())) {
                        manaRegenTicks -= 10;
                    }
                    if (FullSetBuff.hasFullSet(player, amethystSet)) {
                        manaRegenTicks -= 7;
                    }
                    if (blood.getManaRegenTicks() <= 0) {
                        // добавляем ману
                        blood.addMana(1);
                        ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), player);
                        blood.setManaRegenTicks(manaRegenTicks);
                    } else {
                        blood.subManaRegenTick();
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Level level = event.getEntity().getLevel();
            LivingEntity entity = event.getEntity();

            LOGGER.info("Атакован: {} :{}", entity.getName().getString(), event.getAmount());
            if(event.getSource().getEntity() instanceof Player player) {
                if (player.getMainHandItem().getItem() == KNIFE.get()) {
                    // Проверяем, какой тип крови можно получить от этого моба
                    if (HUMAN_BLOOD_ENTITIES.contains(entity.getClass())) {
                        LOGGER.info("Атакован человек: {}", entity.getName().getString());
                        // Если это человек, то получаем человеческую кровь с шансом 40%
                        if (level.getRandom().nextInt(100) < 20) {
                            LOGGER.info("Прокатило");
                            // Проверяем, есть ли в инвентаре игрока бутылка пустая
                            if (hasEmptyBottle(player)) {
                                player.addItem(BOTTLE_OF_HUMAN_BLOOD.get().getDefaultInstance());
                            } else {
                                LOGGER.info("У игрока нет бутылки пустой");
                            }
                        }
                        LOGGER.info("Не прокатило");
                    } else if (NETHER_BLOOD_ENTITIES.contains(entity.getClass())) {
                        // Если это адский моб, то получаем адскую кровь с шансом 40%
                        LOGGER.info("Атакован адский моб: {}", entity.getName().getString());
                        if (level.getRandom().nextInt(100) < 20) {
                            LOGGER.info("Прокатило");
                            // Проверяем, есть ли в инвентаре игрока бутылка пустая
                            if (hasEmptyBottle(player)) {
                                player.addItem(BOTTLE_OF_NETHER_BLOOD.get().getDefaultInstance());
                            } else {
                                LOGGER.info("У игрока нет бутылки пустой");
                            }
                        }
                        LOGGER.info("Не прокатило");
                    } else if (END_BLOOD_ENTITIES.contains(entity.getClass())) {
                        LOGGER.info("Атакован енд моб: {}", entity.getName().getString());
                        // Если это дракон, то получаем драконью кровь с шансом 40%
                        if (level.getRandom().nextInt(100) < 20) {
                            LOGGER.info("Прокатило");
                            // Проверяем, есть ли в инвентаре игрока бутылка пустая
                            if (hasEmptyBottle(player)) {
                                player.addItem(BOTTLE_OF_ENDER_BLOOD.get().getDefaultInstance());
                            } else {
                                LOGGER.info("У игрока нет бутылки пустой");
                            }
                        }
                        LOGGER.info("Не прокатило");
                    }
                }
                player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {
                    for (BloodAbilities ability : cap.getAbilities()) {
                        if (ability.getName().equals("BloodMark") && ability.isActive()) {
                            ((BloodMark) ability).onHit(entity, (ServerPlayer) player);
                        }
                    }
                });
            }
        }

        // Метод для проверки наличия бутылки пустой в инвентаре игрока
        private static boolean hasEmptyBottle(Player player) {
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() == Items.GLASS_BOTTLE) {
                    return true;
                }
            }
            return false;
        }

        @SubscribeEvent
        public static void onScrollUse(PlayerInteractEvent.RightClickItem event) {
            if (event.getEntity() != null) {
                if (!event.getLevel().isClientSide() && event.getHand() == InteractionHand.MAIN_HAND) {
                    Player player = event.getEntity();
                    ItemStack stack = player.getItemInHand(event.getHand());

                    if (isScroll(stack)) {
                        ModMessages.sendToServer(new ScrollUseC2SPacket(player.getUUID()));
                    }
                }
            }
        }

        private static boolean isScroll(ItemStack stack) {
            return stack.getItem() == COMMON_SCROLL.get() ||
                    stack.getItem() == BEGINNER_SCROLL.get() ||
                    stack.getItem() == ADVANCED_SCROLL.get() ||
                    stack.getItem() == MASTER_SCROLL.get() ||
                    stack.getItem() == ANCIENT_SCROLL.get();
        }
    }
}