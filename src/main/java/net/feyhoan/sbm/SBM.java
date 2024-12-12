package net.feyhoan.sbm;

import com.mojang.logging.LogUtils;
import net.feyhoan.sbm.blocks.ModBlocks;
import net.feyhoan.sbm.blocks.entity.ConcentrateExtractorScreen;
import net.feyhoan.sbm.blocks.entity.ModBlockEntities;
import net.feyhoan.sbm.blocks.entity.ModMenuTypes;
import net.feyhoan.sbm.effect.ModEffects;
import net.feyhoan.sbm.loot.ModLootModifiers;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.particle.ModParticles;
import net.feyhoan.sbm.particle.custom.BloodLeapParticle;
import net.feyhoan.sbm.potion.ModPotions;
import net.feyhoan.sbm.sound.ModSounds;
import net.feyhoan.sbm.world.features.ModConfiguredFeatures;
import net.feyhoan.sbm.world.features.ModPlacedFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.feyhoan.sbm.item.ModItems;
import net.feyhoan.sbm.command.BloodCommands;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(SBM.MOD_ID)
public class SBM {
    public static final String MOD_ID = "sbm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SBM() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Регистрация команд
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(ModLootModifiers.class);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModPotions.register(modEventBus);
        ModEffects.register(modEventBus);
        ModConfiguredFeatures.register(modEventBus);
        ModPlacedFeatures.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModParticles.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Регистрация пакетов
        event.enqueueWork(ModMessages::register);

        // Регистрация слота кольца
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.RING.getMessageBuilder().build());
        // Регистрация слота для ожерелья
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
        // Регистрация слота для чарма
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.BRACELET.getMessageBuilder().build());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(BloodCommands.createBloodCommand());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.CONCENTRATE_EXTRACTOR_MENU.get(), ConcentrateExtractorScreen::new);
        }
    }
}