package net.feyhoan.sbm.magic.abilities.human;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.feyhoan.sbm.CONSTANTS.ABSORBINGSHIELD_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.ABSORBINGSHIELD_MANACOST;

public class AbsorbingShield extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AbsorbingShield() {
        super("AbsorbingShield", ABSORBINGSHIELD_MANACOST, ABSORBINGSHIELD_COOLDOWN);
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
            player.playSound(ModSounds.CANCEL.get());
            player.sendSystemMessage(Component.translatable("sbm.abilities.not_enough_mana"));
            return;
        }

        setActive(true);

        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 6, false, false)); // Вечная регенерация
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.ABSORBING_SHIELD.get(), SoundSource.PLAYERS, 0.6F, 1.0F);


        scheduler.schedule(() -> {
            synchronized (this) {
                setActive(false);
                startCooldown();
            }
        }, 15, TimeUnit.SECONDS);
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