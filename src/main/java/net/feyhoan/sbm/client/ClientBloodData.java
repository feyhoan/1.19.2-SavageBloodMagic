package net.feyhoan.sbm.client;

public class ClientBloodData {
    private static int playerMana;
    private static int playerLevel;
    private static int playerMaxMana;
    private static int playerManaRegenTicks;

    public static void set(int mana, int maxmana, int level, int manaRegenTicks) {
        ClientBloodData.playerMana = mana;
        ClientBloodData.playerLevel = level;
        ClientBloodData.playerMaxMana = maxmana;
        ClientBloodData.playerManaRegenTicks = manaRegenTicks;
    }

    public static int getPlayerMana() {
        return playerMana;
    }

    public static int getPlayerLevel() {
        return playerLevel;
    }

    public static int getPlayerMaxMana() {
        return playerMaxMana;
    }

    public static int getPlayerManaRegenTicks() {
        return playerManaRegenTicks;
    }

}
