package net.feyhoan.sbm.magic.abilities.ender;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static net.feyhoan.sbm.CONSTANTS.HIGHLIGHTING_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.HIGHLIGHTING_MANACOST;

public class Highlighting extends BloodAbilities {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final double HIGHLIGHT_RADIUS = 15.0;
    private final Set<Entity> highlightedEntities = new CopyOnWriteArraySet<>();

    public Highlighting() {
        super("Highlighting", HIGHLIGHTING_MANACOST, HIGHLIGHTING_COOLDOWN);
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
        applyHighlighting(player);
        ModMessages.sendToServer(new AbilitySubManaC2SPacket(getManaCost(), player.getUUID()));
    }

    private void applyHighlighting(ServerPlayer player) {
        Runnable highlightTask = () -> {
            List<Entity> entities = player.getLevel().getEntities(player, player.getBoundingBox().inflate(HIGHLIGHT_RADIUS));
            if (entities.isEmpty()) return;

            for (Entity entity : entities) {
                if (entity != player && !highlightedEntities.contains(entity)) {
                    entity.setGlowingTag(true);
                    highlightedEntities.add(entity);
                }
            }

            highlightedEntities.removeIf(entity -> {
                boolean isInRange = player.getBoundingBox().inflate(HIGHLIGHT_RADIUS).intersects(entity.getBoundingBox());
                if (!isInRange) {
                    entity.setGlowingTag(false);
                    return true;
                }
                return false;
            });
        };

        scheduler.scheduleAtFixedRate(highlightTask, 0, 1, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            highlightedEntities.forEach(entity -> entity.setGlowingTag(false));
            highlightedEntities.clear();
            setActive(false);
            startCooldown();
        }, 15, TimeUnit.SECONDS);
    }

    private void startCooldown() {
        setOnCooldown(true);
        scheduler.schedule(() -> {
            synchronized (this) {
                setOnCooldown(false);
                scheduler.shutdown();
            }
        }, getCooldown(), TimeUnit.SECONDS);
    }
}