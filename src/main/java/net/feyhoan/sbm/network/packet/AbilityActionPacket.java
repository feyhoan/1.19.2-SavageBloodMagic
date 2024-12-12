package net.feyhoan.sbm.network.packet;

import net.feyhoan.sbm.CONSTANTS;
import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.magic.BloodAbilitiesCapability;
import net.feyhoan.sbm.magic.BloodAbilitiesProvider;
import net.feyhoan.sbm.sound.ModSounds;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import static net.feyhoan.sbm.CONSTANTS.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;


public class AbilityActionPacket {

    public enum AbilityAction {
        ACTIVATE, ADD, ADD_RANDOM, GET, REMOVE, REMOVE_ALL
    }

    private final AbilityAction action; // Действие
    private final String abilityName; // Имя способности
    private final UUID playerUUID; // UUID игрока

    public AbilityActionPacket(AbilityAction action, String abilityName, UUID playerUUID) {
        this.action = action;
        this.abilityName = abilityName;
        this.playerUUID = playerUUID;
    }

    // Метод для десериализации пакета
    public static AbilityActionPacket fromBytes(FriendlyByteBuf buf) {
        AbilityAction action = AbilityAction.valueOf(buf.readUtf().toUpperCase());
        String abilityName = null;
        if (action == AbilityAction.ACTIVATE || action == AbilityAction.ADD || action == AbilityAction.REMOVE) {
            abilityName = buf.readUtf(); // Чтение имени способности только если это актуально
        }
        UUID playerUUID = buf.readUUID();
        return new AbilityActionPacket(action, abilityName, playerUUID);
    }

    // Метод для сериализации пакета
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.action.name()); // Запись действия
        if (this.action == AbilityAction.ACTIVATE || this.action == AbilityAction.ADD || this.action == AbilityAction.REMOVE) {
            buf.writeUtf(this.abilityName); // Запись имени способности
        }
        buf.writeUUID(this.playerUUID); // Запись UUID игрока
    }

    // Обработчик пакета
    public static void handle(AbilityActionPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender().getServer().getPlayerList().getPlayer(packet.playerUUID);
            if (player != null && player.getUUID().equals(packet.playerUUID)) {
                switch (packet.action) {
                    case ACTIVATE:
                        SBM.LOGGER.warn("Пришел пакет: {} для способности {}", packet.action, packet.abilityName);
                        activateAbility(packet.abilityName, player);
                        break;
                    case ADD:
                        addAbility(packet.abilityName, player);
                        break;
                    case ADD_RANDOM:
                        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(BloodAbilitiesCapability::addRandomAbility);
                        break;
                    case GET:
                        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {
                            List<BloodAbilities> abilities = cap.getAbilities(); // Получаем список способностей
                            List<Component> coloredAbilities = new ArrayList<>();

                            // Итерация по способностям для создания цветных компонентов
                            for (BloodAbilities ability : abilities) {
                                String abilityName = Utils.getAbilityName(String.valueOf(ability)); // Получаем только имя способности
                                TextColor color = TextColor.fromRgb(Utils.randomColor()); // Генерируем случайный цвет или установите свой
                                Component coloredAbility = Component.literal(abilityName).setStyle(Style.EMPTY.withColor(color));
                                coloredAbilities.add(coloredAbility);
                            }

                            // Объединяем все компоненты в один
                            MutableComponent message = Component.translatable("sbm.abilities.get_abilities")
                                    .append(Component.literal(": "))
                                    .append(coloredAbilities.get(0));

                            for (int i = 1; i < coloredAbilities.size(); i++) {
                                message.append(Component.literal(", ")) // Добавить запятую между способностями
                                        .append(coloredAbilities.get(i));
                            }

                            player.sendSystemMessage(message); // Отправляем сообщение с цветными именами способностей
                        });
                        break;
                    case REMOVE:
                        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {
                            cap.removeAbility(packet.abilityName);
                        });
                        player.sendSystemMessage(Component.translatable("sbm.abilities.remove", packet.abilityName));
                        break;
                    case REMOVE_ALL:
                        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(BloodAbilitiesCapability::removeAllAbilities);
                        player.sendSystemMessage(Component.translatable("sbm.abilities.remove_all.success"));
                        break;
                    default:
                        player.sendSystemMessage(Component.translatable("sbm.abilities.unknown_action", packet.action));
                }
            }
            context.get().setPacketHandled(true);
        });
    }

    private static void activateAbility(String abilityName, ServerPlayer player) {
        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {
            for (BloodAbilities ability : cap.getAbilities()) {
                if (ability.getName().equals(abilityName)) {
                    if (ability.isOnCooldown()) {
                        player.sendSystemMessage(Component.translatable("sbm.abilities.cooldown", ability.getName()));
                        player.playSound(ModSounds.CANCEL.get());
                        return;
                    }
                    if (ability.isActive()) {
                        player.sendSystemMessage(Component.translatable("sbm.abilities.already_active", ability.getName()));
                        player.playSound(ModSounds.CANCEL.get());
                        return;
                    }
                    if (!player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).map(blood -> blood.getMana() >= ability.getManaCost()).orElse(false)) {
                        player.sendSystemMessage(Component.translatable("sbm.abilities.not_enough_mana", ability.getManaCost()));
                        player.playSound(ModSounds.CANCEL.get());
                        return;
                    }

                    cap.activateAbility(player, abilityName);
                    return;
                }
            }
        });
    }

    private static void addAbility(String abilityName, ServerPlayer player) {
        String abilityClassName = getAbilityClassName(abilityName);
        if (abilityClassName != null) {
            try {
                Class<?> abilityClass = Class.forName(abilityClassName);
                BloodAbilities ability = (BloodAbilities) abilityClass.getDeclaredConstructor().newInstance();
                player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {cap.addAbility(ability);});
                player.sendSystemMessage(Component.translatable("sbm.abilities.add.success", ability.getName()));
            } catch (Exception e) {
                player.sendSystemMessage(Component.translatable("sbm.abilities.add.error", e.getMessage()));
            }
        }
    }

    private static String getAbilityClassName(String className) {
        if (CONSTANTS.enderAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.ender." + className; // Подставляем пакет для Ender
        } else if (CONSTANTS.netherAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.nether." + className; // Подставляем пакет для Nether
        } else if (CONSTANTS.humanAbilities.contains(className)) {
            return "net.feyhoan.sbm.magic.abilities.human." + className; // Подставляем пакет для Human
        }
        return null; // Если не найдено, вернуть null
    }
}