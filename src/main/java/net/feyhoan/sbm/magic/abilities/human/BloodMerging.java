package net.feyhoan.sbm.magic.abilities.human;

import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.network.packet.EffectsC2SPacket;
import net.feyhoan.sbm.network.packet.SpawnParticlePacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static net.feyhoan.sbm.CONSTANTS.BLOODMERGING_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.BLOODMERGING_MANACOST;
import static net.feyhoan.sbm.util.AbilityUtils.BLOOD_MERGING_EFFECTS;
import static net.feyhoan.sbm.util.AbilityUtils.getEffectAmplifierBloodMerging;

public class BloodMerging extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> faceParticlesTask; // для хранения задачи по частицам лица

    public BloodMerging() {
        super("BloodMerging", BLOODMERGING_MANACOST, BLOODMERGING_COOLDOWN);
    }

    @Override
    public void activate(ServerPlayer player) {
        setActive(true);

        applyBloodMergingEffects(player); // Применение эффектов
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.BLOOD_MERGING.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        spawnBloodParticles(player);
        startFaceParticles(player); // Запускаем постоянное создание частиц вокруг головы
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        // Запуск таймера для окончания действия
        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false);
                stopFaceParticles(); // Останавливаем создание частиц лица
                startCooldown();
            }
        }, 15, TimeUnit.SECONDS);
    }

    private void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }

    private static void applyBloodMergingEffects(ServerPlayer player) {
        if (player == null) return;
        for (MobEffect effect : BLOOD_MERGING_EFFECTS) {
            int duration = 300; // 30 секунд
            int amplifier = getEffectAmplifierBloodMerging(effect);
            if (amplifier < 0) continue;

            ModMessages.sendToServer(new EffectsC2SPacket(effect, duration, amplifier, false, false, player.getUUID(), false));
        }
    }

    private void spawnBloodParticles(ServerPlayer player) {
        ServerLevel level = player.getLevel();
        int particleCount = 50;
        double x = player.getX();
        double y = player.getY() + player.getBbHeight() / 2;
        double z = player.getZ();

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = level.getRandom().nextDouble();
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE, x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0);
        }
    }

    // Метод для запуска постоянных частиц вокруг головы
    private void startFaceParticles(ServerPlayer player) {
        if (faceParticlesTask != null && !faceParticlesTask.isCancelled()) {
            faceParticlesTask.cancel(true);
        }
        faceParticlesTask = scheduler.scheduleAtFixedRate(() -> spawnFaceParticles(player), 0, 90, TimeUnit.MILLISECONDS);
    }

    // Метод для остановки задачи по созданию частиц лица
    private void stopFaceParticles() {
        if (faceParticlesTask != null && !faceParticlesTask.isCancelled()) {
            faceParticlesTask.cancel(true);
            faceParticlesTask = null;
        }
    }

    // Создаем частицы вокруг головы игрока
    private void spawnFaceParticles(ServerPlayer player) {
        ServerLevel level = player.getLevel();
        int particlesPerCall = 4; // число частиц за вызов

        for (int i = 0; i < particlesPerCall; i++) {
            final int index = i;

            double offsetX = (Math.random() - 0.5) * 0.4;
            double offsetY = (Math.random() - 0.5) * 0.4;
            double offsetZ = (Math.random() - 0.5) * 0.4;

            double particleX = player.getX() + offsetX + Math.cos(index * Math.PI / 2); // немного вариации по X
            double particleY = player.getY() + player.getEyeHeight() + offsetY;
            double particleZ = player.getZ() + offsetZ + Math.sin(index * Math.PI / 2); // вариация по Z

            ModMessages.sendToPlayer(new SpawnParticlePacket(player.getUUID(), particleX, particleY, particleZ, "blood_merging"), null); // отправляем всем или конкретному игроку
        }
    }
}