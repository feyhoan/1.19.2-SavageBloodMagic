package net.feyhoan.sbm.magic.abilities.ender;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.feyhoan.sbm.CONSTANTS;

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
        if (isOnCooldown()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.cooldown", getName()));
            return;
        }
        if (isActive()) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.already_active", getName()));
            return;
        }
        if (!player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getMana() >= getManaCost()).orElse(false)) {
            player.sendSystemMessage(Component.translatable("sbm.abilities.not_enough_mana"));
            return;
        }

        setActive(true);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));
        BloodParticles(player.getLevel(), player);

        setActive(false);
        startCooldown();
    }

    public void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }

    private static void BloodParticles(Level level, ServerPlayer player) {
        int particleCount = 30;
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = (Math.random() - 0.5) * 0.5; // Возможно, стоит использовать player.getEyeHeight() вместо случайного смещения
            double offsetZ = (Math.random() - 0.5) * 0.5;

            level.addParticle(ParticleTypes.ENCHANTED_HIT,
                    player.getX() + offsetX,
                    player.getY() + offsetY + player.getEyeHeight(),
                    player.getZ() + offsetZ, 0, 0, 0);
        }
    }
}
