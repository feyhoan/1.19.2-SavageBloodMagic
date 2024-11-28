package net.feyhoan.sbm.magic.abilities.human;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.network.packet.EffectsC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.BLOODPURIFICATION_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.BLOODPURIFICATION_MANACOST;
import static net.minecraft.world.effect.MobEffects.*;

public class BloodPurification extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BloodPurification() {
        super("BloodPurification", BLOODPURIFICATION_MANACOST, BLOODPURIFICATION_COOLDOWN);
    }

    @Override
    public void activate(ServerPlayer player) {
        if (isOnCooldown()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.cooldown", getName()));
            player.playSound(ModSounds.CANCEL.get());
            return;
        }
        if (isActive()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.already_active", getName()));
            player.playSound(ModSounds.CANCEL.get());
            return;
        }
        if (!player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getMana() >= getManaCost()).orElse(false)) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.not_enough_mana"));
            player.playSound(ModSounds.CANCEL.get());
            return;
        }

        setActive(true);
        applyBloodPurificationEffects(player);
        spawnPurificationParticles(player);
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.BLOOD_PURIFICATION.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false); // Разрешаем повторное использование
                startCooldown();
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }


    // Массив эффектов, которые будут удалены
    private static final MobEffect[] EFFECTS_TO_REMOVE = {
            WITHER, DIG_SLOWDOWN, MOVEMENT_SLOWDOWN, POISON,
            BLINDNESS, DARKNESS, WEAKNESS, UNLUCK, HUNGER, CONFUSION,
    };

    private void applyBloodPurificationEffects(ServerPlayer player) {
        if (player == null) return;

        for (MobEffect effect : EFFECTS_TO_REMOVE) {
            ModMessages.sendToServer(new EffectsC2SPacket(effect, 0, 0, false, false, player.getUUID(), true));
        }
    }
    private void spawnPurificationParticles(ServerPlayer player) {
        ServerLevel level = player.getLevel();

        // Параметры для частиц
        int particleCount = 50; // Количество частиц
        double x = player.getX();
        double y = player.getY() + player.getBbHeight() / 2; // Можно поднять их немного выше, чтобы они были на уровне игрока
        double z = player.getZ();

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2; // Случайный смещение по X
            double offsetY = level.getRandom().nextDouble(); // Случайный смещение по Y
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2; // Случайный смещение по Z

            // Вызов метода для создания частиц. Используйте нужный вам тип частиц
            level.sendParticles(ParticleTypes.SOUL, x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0); // Например, частицы "сердца"
        }
    }
}