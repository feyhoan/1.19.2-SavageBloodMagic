package net.feyhoan.sbm.event;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.feyhoan.sbm.util.AbilityBindingsConfig;
import net.feyhoan.sbm.util.KeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = SBM.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            assert Minecraft.getInstance().player != null;

            AbilityBindingsConfig.AbilityBindingsKeys[] keys = AbilityBindingsConfig.AbilityBindingsKeys.values();
            KeyMapping[] keyBindings = new KeyMapping[]{ KeyBinding.FIRST_ABILITY_KEY, KeyBinding.SECOND_ABILITY_KEY, KeyBinding.THIRD_ABILITY_KEY, KeyBinding.FOURTH_ABILITY_KEY };

            for (int i = 0; i < keys.length; i++) {
                if (keyBindings[i].consumeClick()) {
                    UUID playerUUID = Minecraft.getInstance().player.getUUID();
                    String abilityName = AbilityBindingsConfig.getAbilityName(keys[i]);
                    if (abilityName != null) {
                        ModMessages.sendToServer(new AbilityActionPacket(AbilityActionPacket.AbilityAction.ACTIVATE, abilityName, playerUUID));
                        SBM.LOGGER.info("Пакет отправлен {}, АКТИВАТЕ!", Minecraft.getInstance().player.getName());
                    } else {
                        SBM.LOGGER.info("Клавиша {} не забинжена", keys[i].name());
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SBM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.OPEN_ABILITY_PRESETS_KEY);
            event.register(KeyBinding.FIRST_ABILITY_KEY);
            event.register(KeyBinding.SECOND_ABILITY_KEY);
            event.register(KeyBinding.THIRD_ABILITY_KEY);
            event.register(KeyBinding.FOURTH_ABILITY_KEY);
        }
    }
}