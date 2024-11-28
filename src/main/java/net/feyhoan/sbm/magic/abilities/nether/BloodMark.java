package net.feyhoan.sbm.magic.abilities.nether;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.effect.ModEffects;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.BLOODMARK_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.BLOODMARK_MANACOST;

public class BloodMark extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isMarkApplied = false;

    public BloodMark() {
        super("BloodMark", BLOODMARK_MANACOST, BLOODMARK_COOLDOWN);
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
        isMarkApplied = false;

        player.getLevel().playSound(null, player.blockPosition(), ModSounds.BLOOD_MARK.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false);
                if (!isMarkApplied) {
                    startCooldown();
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void onHit(LivingEntity target, ServerPlayer player) {
        if (isActive() && !isMarkApplied) {
            isMarkApplied = true;
            target.addEffect(new MobEffectInstance(ModEffects.BLOOD_MARK.get(), 100, 1));
            startCooldown();
        }
    }

    private void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }
}