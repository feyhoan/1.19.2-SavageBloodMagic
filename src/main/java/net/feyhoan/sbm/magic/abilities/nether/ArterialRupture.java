package net.feyhoan.sbm.magic.abilities.nether;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.ARTERIALRUPTURE_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.ARTERIALRUPTURE_MANACOST;

public class ArterialRupture extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ArterialRupture() {
        super("ArterialRupture", ARTERIALRUPTURE_MANACOST, ARTERIALRUPTURE_COOLDOWN);
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

        dealDamageToNearbyEntities(player);
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.ARTERIAL_RUPTURE.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

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

    private static void dealDamageToNearbyEntities(ServerPlayer player) {
        double radius = 10.0;
        Level level = player.getLevel();

        List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(radius));

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                double damage = livingEntity.getHealth() * 0.95;
                livingEntity.hurt(DamageSource.GENERIC, (float) damage);
            }
        }
    }
}