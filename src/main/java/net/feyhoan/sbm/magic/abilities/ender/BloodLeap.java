package net.feyhoan.sbm.magic.abilities.ender;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.network.packet.SpawnParticlePacket;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.feyhoan.sbm.CONSTANTS;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BloodLeap extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BloodLeap() {
        super("BloodLeap", CONSTANTS.BLOODLEAP_MANACOST, CONSTANTS.BLOODLEAP_COOLDOWN);
    }

    @Override
    public void activate(ServerPlayer player) {
        // Проверка на кулдаун
        if (isOnCooldown()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.cooldown", getName()));
            return;
        }

        // Проверка, активна ли способность
        if (isActive()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.already_active", getName()));
            return;
        }

        // Проверка маны
        if (!player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getMana() >= getManaCost()).orElse(false)) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.not_enough_mana"));
            return;
        }



        setActive(true);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        spawnParticles(player);
        teleportPlayer(player);
        spawnParticles(player);

        setActive(false);
        startCooldown();
    }
    // Метод для создания частиц за игроком
    private void spawnParticles(ServerPlayer player) {
        for (int i = 0; i < 4; i++) {
            final int index = i; // Локальная переменная для использования в лямбде
            scheduler.schedule(() -> {
                double offsetX = (Math.random() - 0.5) * 0.2; // Случайные смещения по X
                double offsetY = (Math.random() - 0.5) * 0.2; // Случайные смещения по Y
                double offsetZ = (Math.random() - 0.5) * 0.2; // Случайные смещения по Z

                // Позиция для частиц: В основе движения добавляем смещения
                double particleX = player.getX() + (index * 0.2) + offsetX; // Удаляем X с учетом направления
                double particleY = player.getY() + player.getEyeHeight() + offsetY; // Высота глаза игрока
                double particleZ = player.getZ() + (index * 0.2) + offsetZ; // Удаляем Z с учетом направления

                // Отправляем пакет на создание частицы
                ModMessages.sendToPlayer(new SpawnParticlePacket(player.getUUID(), particleX, particleY, particleZ, "blood_leap"), player);
            }, index*90, TimeUnit.MILLISECONDS); // 90 мс между частицами
        }
    }
    public void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }

    public void teleportPlayer(ServerPlayer player) {
        HitResult target = Utils.getPlayerLookingSpot(player, 20);
        double ox = player.getX();
        double oy = player.getY();
        double oz = player.getZ();
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS, 1, 1);
            return;
        }
        BlockPos pos = null;
        if (target.getType() == HitResult.Type.BLOCK) {
            if (player.getCommandSenderWorld().getBlockState(((BlockHitResult) target).getBlockPos()).getMaterial().blocksMotion()) {
                pos = ((BlockHitResult) target).getBlockPos().above();
            }
        } else {
            if (player.getCommandSenderWorld().getBlockState(((EntityHitResult) target).getEntity().blockPosition()).getMaterial().blocksMotion()) {
                pos = ((EntityHitResult) target).getEntity().blockPosition();
            }
        }

        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.getCommandSenderWorld().containsAnyLiquid(player.getBoundingBox()) || !player.getCommandSenderWorld().isUnobstructed(player)) { //isEntityColliding
                pos = null;
            }
        }
        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.getCommandSenderWorld().containsAnyLiquid(player.getBoundingBox()) || !player.getCommandSenderWorld().isUnobstructed(player)) { //isEntityColliding
                pos = null;
            }
        }


        if (pos == null) {
            player.setPos(ox, oy, oz);
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM, 1, 1);
            return;
        }

        player.disconnect();
        player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
    }
}