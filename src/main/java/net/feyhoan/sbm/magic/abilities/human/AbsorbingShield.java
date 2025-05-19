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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static net.feyhoan.sbm.CONSTANTS.ABSORBINGSHIELD_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.ABSORBINGSHIELD_MANACOST;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AbsorbingShield extends BloodAbilities {
    private static final double DAMAGE_ABSORPTION_PERCENTAGE = 0.5; // Процент урона, который будет поглощен
    private static final int MANA_RESTORE_PER_DAMAGE_POINT = 5; // Количество маны, восстанавливаемое за каждую единицу урона

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Set<UUID> activePlayers = new HashSet<>();

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
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 4, false, false)); // Увеличение сопротивления урону
        player.getLevel().playSound(null, player.blockPosition(), ModSounds.ABSORBING_SHIELD_START.get(), SoundSource.PLAYERS, 0.6F, 1.0F);

        activePlayers.add(player.getUUID());

        MinecraftForge.EVENT_BUS.register(this);

        scheduler.schedule(() -> {
            synchronized (this) {
                player.getLevel().playSound(null, player.blockPosition(), ModSounds.ABSORBING_SHIELD_END.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
                deactivate(player);
            }
        }, 15, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!activePlayers.contains(player.getUUID())) return;

        float damage = event.getAmount();
        float absorbedDamage = damage * (float) DAMAGE_ABSORPTION_PERCENTAGE;
        float remainingDamage = damage - absorbedDamage;

        event.setAmount(remainingDamage); // Уменьшаем урон, который игрок получит

        // Преобразуем поглощенный урон в ману
        int manaRestore = (int) (absorbedDamage * MANA_RESTORE_PER_DAMAGE_POINT);
        player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).ifPresent(blood -> blood.addMana(manaRestore));
    }

    private void deactivate(ServerPlayer player) {
        setActive(false);
        startCooldown();

        activePlayers.remove(player.getUUID());
        MinecraftForge.EVENT_BUS.unregister(this);
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