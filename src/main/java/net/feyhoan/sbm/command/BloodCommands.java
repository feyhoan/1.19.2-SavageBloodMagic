package net.feyhoan.sbm.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.feyhoan.sbm.CONSTANTS;
import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blood.PlayerBlood;
import net.feyhoan.sbm.blood.PlayerBloodProvider;
import net.feyhoan.sbm.magic.BloodAbilities;
import net.feyhoan.sbm.magic.BloodAbilitiesProvider;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.AbilityActionPacket;
import net.feyhoan.sbm.network.packet.BloodDataSyncS2CPacket;
import net.feyhoan.sbm.util.AbilityBindingsConfig;
import net.feyhoan.sbm.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


@Mod.EventBusSubscriber(modid = SBM.MOD_ID)
public class BloodCommands {

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 5;

    public static LiteralArgumentBuilder<CommandSourceStack> createBloodCommand() {
        return Commands.literal("sbm")
                .then(Commands.literal("get-stat").executes(BloodCommands::getStats))
                .then(Commands.literal("level")
                        .then(Commands.literal("up").executes(BloodCommands::levelUp))
                        .then(Commands.literal("down").executes(BloodCommands::levelDown))
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // Добавление подсказок для аргумента "action"
                                    return suggestSet(builder);
                                })
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            String type = StringArgumentType.getString(ctx, "type");
                                            return switch (type.toLowerCase()) {
                                                case "mana" -> setValue(ctx, PlayerBlood::setMana);
                                                case "maxmana" -> setValue(ctx, PlayerBlood::setMaxMana);
                                                case "level" -> setValue(ctx, PlayerBlood::setLevel);
                                                case "manaregenspeed" -> setValue(ctx, PlayerBlood::setManaRegenSpeed);
                                                default -> {
                                                    ctx.getSource().sendFailure(Component.translatable("sbm.command.unknown_command_type", type));
                                                    yield 0;
                                                }
                                            };
                                        })
                                )
                        )
                )
                .then(Commands.literal("ability")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // Добавление подсказок для аргумента "action"
                                    return suggestActions(builder);
                                })
                                .then(Commands.argument("abilityName", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            // Добавление подсказок для аргумента "action"
                                            return suggestAbilities(builder);
                                        })
                                        .executes(ctx -> {
                                            AbilityActionPacket.AbilityAction action =
                                                    AbilityActionPacket.AbilityAction.valueOf(
                                                            ctx.getArgument("action", String.class).toUpperCase()
                                                    );
                                            String abilityName = ctx.getArgument("abilityName", String.class);
                                            ModMessages.sendToServer(
                                                    new AbilityActionPacket(action, abilityName, ctx.getSource().getPlayer().getUUID())
                                            );
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("bind")
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // Добавление подсказок для аргумента "action"
                                    return suggestBind(builder);
                                })
                                .then(Commands.argument("abilityName", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            // Добавление подсказок для аргумента "action"
                                            return suggestPlayerAbilities(builder, context);
                                        })
                                        .executes(ctx -> bind(ctx, ctx.getArgument("key", String.class), ctx.getArgument("abilityName", String.class)))
                                )
                        )
                )
                .then(Commands.literal("get-binds")
                        .executes(BloodCommands::getBinds
                        )
                );
    }

    private static PlayerBlood getPlayerBlood(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return player.getCapability(PlayerBloodProvider.PLAYER_BLOOD).orElseThrow(() -> {
            try {
                throw new CommandSyntaxException(
                        CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand(),
                        Component.translatable("sbm.command.player_blood_not_present")
                );
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static int bind(CommandContext<CommandSourceStack> ctx, String key, String abilityName) {
        AbilityBindingsConfig.AbilityBindingsKeys abilityBindingsKeys = AbilityBindingsConfig.AbilityBindingsKeys.valueOf(key.toUpperCase());
        AbilityBindingsConfig.setKeyAbility(abilityBindingsKeys, abilityName);
        ctx.getSource().sendSuccess(Component.translatable("sbm.command.bind", abilityBindingsKeys, abilityName).withStyle(ChatFormatting.ITALIC), true);
        return 1;
    }

    private static int getBinds(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(Component.translatable("sbm.command.get_binds", AbilityBindingsConfig.getBinds()).withStyle(ChatFormatting.ITALIC), true);
        return 1;
    }

    private static int getStats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PlayerBlood blood = getPlayerBlood(context);
        context.getSource().sendSuccess(Component.translatable("sbm.command.stats", blood.getMana(), blood.getMaxMana(), blood.getLevel()).withStyle(ChatFormatting.ITALIC), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int levelUp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return changeLevel(context, true);
    }

    private static int levelDown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return changeLevel(context, false);
    }

    private static int changeLevel(CommandContext<CommandSourceStack> context, boolean levelUp) throws CommandSyntaxException {
        PlayerBlood blood = getPlayerBlood(context);
        try {
            if (levelUp) {
                blood.levelUp();
            } else {
                blood.levelDown();
            }
            int newLevel = blood.getLevel();
            if (newLevel < MIN_LEVEL) {
                context.getSource().sendSuccess(Component.translatable("sbm.command.level_cannot_be_less_than_min", MIN_LEVEL)
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            if (newLevel > MAX_LEVEL) {
                context.getSource().sendSuccess(Component.translatable("sbm.command.level_cannot_be_more_than_max", MAX_LEVEL)
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            if (newLevel == 0) {
                context.getSource().sendSuccess(Component.translatable("sbm.command.no_longer_blood_mage").withStyle(ChatFormatting.DARK_RED), true);
                blood.setMana(0);
            } else if (levelUp && newLevel == MAX_LEVEL) {
                context.getSource().sendSuccess(Component.translatable("sbm.command.maxlevelnotice").withStyle(ChatFormatting.YELLOW), true);
                context.getSource().sendSuccess(Component.translatable("sbm.command.levelup.congratulations", newLevel, blood.getMaxMana()).withStyle(ChatFormatting.YELLOW), false);
            } else {
                context.getSource().sendSuccess(Component.translatable(levelUp ? "sbm.command.levelup.congratulations" : "sbm.command.leveldown.level_decreased", newLevel, blood.getMaxMana()), true);
            }
            sendUpdateToServer(blood, context);
        } catch (Exception e) {
            context.getSource().sendSuccess(Component.translatable("sbm.command.error", e.getMessage()), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void sendUpdateToServer(PlayerBlood blood, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ModMessages.sendToPlayer(new BloodDataSyncS2CPacket(blood.getMana(), blood.getMaxMana(), blood.getLevel(), blood.getManaRegenTicks()), player);
    }

    private static int setValue(CommandContext<CommandSourceStack> context, BiConsumer<PlayerBlood, Integer> setter) throws CommandSyntaxException {
        PlayerBlood blood = getPlayerBlood(context);
        int amount = IntegerArgumentType.getInteger(context, "amount");
        setter.accept(blood, amount);
        context.getSource().sendSuccess(Component.translatable("sbm.command.set_value_success", amount)
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
        sendUpdateToServer(blood, context);
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestActions(SuggestionsBuilder builder) {
        // Добавляем доступные действия как предложения
        return builder.suggest("get")
                .suggest("remove")
                .suggest("remove_all")
                .suggest("add")
                .suggest("add_random")
                .buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSet(SuggestionsBuilder builder) {
        // Добавляем доступные действия как предложения
        return builder.suggest("mana")
                .suggest("maxmana")
                .suggest("level")
                .suggest("manaregenspeed")
                .buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBind(SuggestionsBuilder builder) {
        // Добавляем доступные действия как предложения
        return builder.suggest("first")
                .suggest("second")
                .suggest("third")
                .suggest("fourth")
                .buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestAbilities(SuggestionsBuilder builder) {
        List<String> abilities = CONSTANTS.AllAbilities;

        for (String ability : abilities) {
            builder.suggest(Utils.getAbilityName(String.valueOf(ability))); // Добавляем каждую способность в подсказки
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestPlayerAbilities(SuggestionsBuilder builder, CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer(); // Получаем UUID игрока

        assert player != null;
        player.getCapability(BloodAbilitiesProvider.BLOOD_ABILITIES).ifPresent(cap -> {
            List<BloodAbilities> abilities = cap.getAbilities();

            for (BloodAbilities ability : abilities) {
                builder.suggest(Utils.getAbilityName(String.valueOf(ability))); // Добавляем каждую способность в подсказки
            }
        });

        return builder.buildFuture();
    }
}