package net.feyhoan.sbm.magic.abilities.ender;

import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilitySubManaC2SPacket;
import net.feyhoan.sbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static net.feyhoan.sbm.CONSTANTS.HIGHLIGHTING_COOLDOWN;
import static net.feyhoan.sbm.CONSTANTS.HIGHLIGHTING_MANACOST;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import java.util.HashSet;
import java.util.UUID;

public class Highlighting extends BloodAbilities {
    private static final double HIGHLIGHT_RADIUS = 15.0;
    private static final int DURATION_SECONDS = 10;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Set<UUID> highlightedEntities = new HashSet<>();

    public Highlighting() {
        super("Highlighting", HIGHLIGHTING_MANACOST, HIGHLIGHTING_COOLDOWN);
    }

    @Override
    public void activate(ServerPlayer player) {
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
        List<Entity> entities = player.getLevel().getEntities(player, new AABB(player.blockPosition()).inflate(HIGHLIGHT_RADIUS));

        for (Entity entity : entities) {
            if (entity != player) {
                entity.setGlowingTag(true);
                highlightedEntities.add(entity.getUUID());
            }
        }

        // Schedule the removal of highlighting after 10 seconds
        scheduler.schedule(() -> {
            removeHighlighting(player);
            startCooldown();
        }, DURATION_SECONDS, TimeUnit.SECONDS);
    }

    private void removeHighlighting(ServerPlayer player) {
        for (UUID entityId : highlightedEntities) {
            Entity entity = player.getLevel().getEntity(entityId);
            if (entity != null) {
                entity.setGlowingTag(false);
            }
        }
        highlightedEntities.clear();
        setActive(false);
        scheduler.shutdown();
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