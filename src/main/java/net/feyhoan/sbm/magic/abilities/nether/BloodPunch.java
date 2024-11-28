package net.feyhoan.sbm.magic.abilities.nether;


import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.BLOODPUNCH_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.BLOODPUNCH_MANACOST;

public class BloodPunch extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BloodPunch() {
        super("BloodPunch", BLOODPUNCH_MANACOST, BLOODPUNCH_COOLDOWN);
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
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.BLOOD_PUNCH.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));

        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false);
                startCooldown();
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }
}