package net.feyhoan.sbm.data;

import net.feyhoan.sbm.SBM;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerDataStorage {
    // Хранилище данных игроков, где ключ - это UUID игрока, а значение - данные игрока
    private static final Map<UUID, CompoundTag> playerDataMap = new HashMap<>();

    public static void savePlayerData(UUID uuid, CompoundTag data) {
        playerDataMap.put(uuid, data);
        SBM.LOGGER.info("Player data saved for UUID: {}", uuid);
    }

    public static Optional<CompoundTag> loadPlayerData(UUID uuid) {
        return Optional.ofNullable(playerDataMap.get(uuid));
    }

    public static void clearPlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
        SBM.LOGGER.info("Player data cleared for UUID: {}", uuid);
    }
}