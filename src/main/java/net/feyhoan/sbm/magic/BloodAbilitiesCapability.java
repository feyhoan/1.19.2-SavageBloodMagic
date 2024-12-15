package net.feyhoan.sbm.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class BloodAbilitiesCapability {
    private List<BloodAbilities> abilities = new ArrayList<>();
    private static final List<String> enderAbilities = Arrays.asList("BloodLeap", "Highlighting");
    private static final List<String> netherAbilities = Arrays.asList("ArterialRupture", "BloodPunch", "BloodMark");
    private static final List<String> humanAbilities = Arrays.asList("BloodMerging", "AbsorbingShield", "BloodPurification", "HealWounds");
    private static final Random RANDOM = new Random();

    public void addAbility(BloodAbilities ability) {
        // Проверяем, есть ли уже такая способность по имени
        if (abilities.stream().noneMatch(existingAbility -> existingAbility.getName().equals(ability.getName()))) {
            abilities.add(ability);
        } else {
            // Выводим сообщение, если способность уже есть
            System.out.println("Способность уже добавлена: " + ability.getName());
        }
    }
    public void removeAbility(String abilityName) {
        Iterator<BloodAbilities> iterator = abilities.iterator();
        while (iterator.hasNext()) {
            BloodAbilities ability = iterator.next();
            if (ability.getName().equals(abilityName)) {
                iterator.remove();
                System.out.println("Способность удалена: " + abilityName);
                return;
            }
        }
        System.out.println("Способность не найдена: " + abilityName);
    }
    public void removeAllAbilities() {
        abilities.clear();
    }
    public List<BloodAbilities> getAbilities() {
        return abilities;
    }
    public void copyFrom(BloodAbilitiesCapability source) {
        this.abilities = new ArrayList<>(source.abilities); // Копируем по значению
    }
    public void saveNBTData(CompoundTag nbt) {
        ListTag abilitiesTag = new ListTag();
        for (BloodAbilities ability : abilities) {
            CompoundTag abilityTag = new CompoundTag();
            abilityTag.putString("class", ability.getClass().getName());
            abilityTag.putInt("cooldown", ability.getCooldown());
            abilityTag.putInt("manaCost", ability.getManaCost());
            abilityTag.putBoolean("isActive", ability.isActive());
            abilityTag.putBoolean("isOnCooldown", ability.isOnCooldown());
            abilitiesTag.add(abilityTag);
        }
        nbt.put("abilities", abilitiesTag);
    }
    public void loadNBTData(CompoundTag nbt) {
        ListTag abilitiesTag = nbt.getList("abilities", Tag.TAG_COMPOUND);
        for (Tag abilityTag : abilitiesTag) {
            CompoundTag compoundTag = (CompoundTag) abilityTag;
            String className = compoundTag.getString("class");
            try {
                Class<?> clazz = Class.forName(className);
                BloodAbilities ability = (BloodAbilities) clazz.getDeclaredConstructor().newInstance();
                ability.setCooldown(compoundTag.getInt("cooldown"));
                ability.setManaCost(compoundTag.getInt("manaCost"));
                ability.setActive(compoundTag.getBoolean("isActive"));
                ability.setOnCooldown(compoundTag.getBoolean("isOnCooldown"));
                abilities.add(ability);
            } catch (Exception e) {
                // Обработка исключений
                e.printStackTrace();
            }
        }
    }

    public void activateAbility(ServerPlayer player, String abilityName) {
        for (BloodAbilities ability : abilities) {
            if (ability.getName().equals(abilityName) && !ability.isOnCooldown() && !ability.isActive()) {
                ability.activate(player);
                break;
            }
        }
    }
    public boolean hasAbility(String abilityName) {
        for (BloodAbilities ability : abilities) {
            if (ability.getName().equals(abilityName)) {
                return true;
            }
        }
        return false;
    }

    public AbilityAddResult addRandomAbility() {
        List<String> allAbilities = new ArrayList<>();
        allAbilities.addAll(enderAbilities);
        allAbilities.addAll(netherAbilities);
        allAbilities.addAll(humanAbilities);

        List<String> availableAbilities = new ArrayList<>();
        for (String abilityName : allAbilities) {
            if (!hasAbility(abilityName)) {
                availableAbilities.add(abilityName);
            }
        }

        if (!availableAbilities.isEmpty()) {
            String randomAbilityName = availableAbilities.get(RANDOM.nextInt(availableAbilities.size()));
            BloodAbilities newAbility = createAbilityInstance(randomAbilityName);
            if (newAbility != null) {
                addAbility(newAbility);
                return new AbilityAddResult(true, randomAbilityName); // Возвращаем true и имя способности
            }
        }
        return new AbilityAddResult(false, null); // Возвращаем false и null если способность не добавлена
    }

    private static String getAbilityClassName(String className) {
        if (enderAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.ender." + className; // Подставляем пакет для Ender
        } else if (netherAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.nether." + className; // Подставляем пакет для Nether
        } else if (humanAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.human." + className; // Подставляем пакет для Human
        }
        return null; // Если не найдено, вернуть null
    }

    private BloodAbilities createAbilityInstance(String abilityName) {
        try {
            Class<?> clazz = Class.forName(getAbilityClassName(abilityName)); // Замените на ваш пакет
            return (BloodAbilities) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}