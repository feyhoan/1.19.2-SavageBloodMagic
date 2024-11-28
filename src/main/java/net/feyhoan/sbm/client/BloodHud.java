package net.feyhoan.sbm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.systems.RenderSystem;


@Mod.EventBusSubscriber(modid = SBM.MOD_ID)
public class BloodHud {

    private static final ResourceLocation[] manaTextures = new ResourceLocation[]{
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_0.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_1.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_2.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_3.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_4.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_5.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_6.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_7.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_8.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_9.png"),
            new ResourceLocation(SBM.MOD_ID, "textures/gui/mana_10.png"),
    };

    private static int currentMana;
    private static int maxMana;
    private static int level;
    private static int currentBloodLevel;

    public static void updateValuesFromData() {
        currentMana = ClientBloodData.getPlayerMana();
        maxMana = ClientBloodData.getPlayerMaxMana();
        level = ClientBloodData.getPlayerLevel();

        if (maxMana > 0) {
            currentBloodLevel = (int) ((double) currentMana / maxMana * 10);
        } else {
            currentBloodLevel = 0; // Установить по умолчанию, если maxMana ноль
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            updateValuesFromData();
            if ((level > 0)) {
                renderCustomBloodBar(event.getPoseStack());
            }
        }
    }

    public static void renderCustomBloodBar(PoseStack poseStack) {
        if (currentBloodLevel < 0 || currentBloodLevel >= manaTextures.length) {
            currentBloodLevel = 0; // Установить на 0, если уровень выходит за пределы
        }

        ResourceLocation texture = manaTextures[currentBloodLevel];

        RenderSystem.setShaderTexture(0, texture);

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = (screenWidth - 171) / 2;

        int y;
        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player.isCreative()) {
            y = screenHeight - 35; // Высота для креативного режима
        } else {
            y = screenHeight - 65; // Высота для обычного режима
        }

        GuiComponent.blit(poseStack, x, y, 0, 0, 171, 5, 171, 5);

        Font font = Minecraft.getInstance().font;

        // Получаем текст "Уровень: " из файла локализации
        String levelText = I18n.get("sbm.hud.level") + ": " + (level > 0 ? level : "N/A");

        font.draw(poseStack, levelText, x + 85 - (float) font.width(levelText) / 2, y - 10, 0xFFFFFF);

        String currentManaText = currentMana > 0 ? String.valueOf((int) currentMana) : "0";
        font.draw(poseStack, currentManaText, x - 5, y - 10, 0xFFFFFF);

        String maxManaText = maxMana > 0 ? String.valueOf((int) maxMana) : "0";
        font.draw(poseStack, maxManaText, x + 160, y - 10, 0xFFFFFF);
    }
}