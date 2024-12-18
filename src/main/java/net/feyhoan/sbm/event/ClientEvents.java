package net.feyhoan.sbm.event;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.magic.AbilitiesBindingsProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.feyhoan.sbm.network.packet.AbilityActionRequestPacket;
import net.feyhoan.sbm.util.KeyBinding;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
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

            Utils.AbilityBindingsKeys[] keys = Utils.AbilityBindingsKeys.values();
            KeyMapping[] keyBindings = new KeyMapping[]{
                    KeyBinding.FIRST_ABILITY_KEY,
                    KeyBinding.SECOND_ABILITY_KEY,
                    KeyBinding.THIRD_ABILITY_KEY,
                    KeyBinding.FOURTH_ABILITY_KEY
            };

            for (int i = 0; i < keys.length; i++) {
                if (keyBindings[i].consumeClick()) {
                    // Отправляем индекс способности на сервер
                    ModMessages.sendToServer(new AbilityActionRequestPacket(i));
                    SBM.LOGGER.info("Запрос на активацию способности {} отправлен на сервер.", keys[i].name());
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