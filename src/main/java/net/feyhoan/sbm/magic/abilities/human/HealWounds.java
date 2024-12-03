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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.HEALWOUNDS_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.HEALWOUNDS_MANACOST;
import static net.minecraft.world.effect.MobEffects.*;


public class HealWounds extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public HealWounds() {
        super("HealWounds", HEALWOUNDS_MANACOST, HEALWOUNDS_COOLDOWN);
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

        HealWoundsEffects(player); // Применение эффектов
        spawnHealParticles(player);
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.HEAL_WOUNDS.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        // Запуск таймера
        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false);
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

    private static final MobEffect[] EFFECTS_TO_APPLY = {
            REGENERATION, NIGHT_VISION, FIRE_RESISTANCE, CONFUSION
    };

    private void HealWoundsEffects(ServerPlayer player) {
        // Проверка на наличие игрока
        if (player == null) return;

        // Цикл по эффектам
        for (MobEffect effect : EFFECTS_TO_APPLY) {
            int duration = getEffectDuration(effect);
            int amplifier = getEffectAmplifier(effect); // Получаем уровень усиления

            if (amplifier < 0) continue; // Пропускаем, если не совпадает


            // Создаем пакет и отправляем его на сервер
            ModMessages.sendToServer(new EffectsC2SPacket(effect, duration, amplifier, false, false, player.getUUID(), false));
        }
    }

    private int getEffectDuration(MobEffect effect) {
        if (effect.equals(MobEffects.REGENERATION)) {
            return 200;
        } else if (effect.equals(MobEffects.HEALTH_BOOST)) {
            return 200;
        } else if (effect.equals(MobEffects.NIGHT_VISION)) {
            return 200;
        } else if (effect.equals(MobEffects.FIRE_RESISTANCE)) {
            return 200;
        } else if (effect.equals(MobEffects.CONFUSION)) {
            return 30;
        } else {
            return -1; // Пропускаем другие эффекты
        }
    }

    private int getEffectAmplifier(MobEffect effect) {
        if (effect.equals(MobEffects.REGENERATION)) {
            return 2;
        } else if (effect.equals(MobEffects.HEALTH_BOOST)) {
            return 2;
        } else if (effect.equals(MobEffects.NIGHT_VISION)) {
            return 1;
        } else if (effect.equals(MobEffects.FIRE_RESISTANCE)) {
            return 1;
        } else if (effect.equals(MobEffects.CONFUSION)) {
            return 1;
        } else {
            return -1; // Пропускаем другие эффекты
        }
    }

    private void spawnHealParticles(ServerPlayer player) {
        ServerLevel level = player.getLevel();

        // Параметры для частиц
        int particleCount = 12; // Количество частиц
        double x = player.getX();
        double y = player.getY() + player.getBbHeight() / 2; // Можно поднять их немного выше, чтобы они были на уровне игрока
        double z = player.getZ();

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 2; // Случайный смещение по X
            double offsetY = level.getRandom().nextDouble(); // Случайный смещение по Y
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 2; // Случайный смещение по Z

            // Вызов метода для создания частиц. Используйте нужный вам тип частиц
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0); // Например, частицы "сердца"
        }
    }
}