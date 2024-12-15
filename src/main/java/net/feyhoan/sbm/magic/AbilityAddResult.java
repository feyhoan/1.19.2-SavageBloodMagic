package net.feyhoan.sbm.magic;

public class AbilityAddResult {
    private final boolean added;
    private final String abilityName;

    public AbilityAddResult(boolean added, String abilityName) {
        this.added = added;
        this.abilityName = abilityName;
    }

    public boolean isAdded() {
        return added;
    }

    public String getAbilityName() {
        return abilityName;
    }
}