package net.feyhoan.sbm.blocks.custom;

import net.feyhoan.sbm.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SavageBloodAltar extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final Map<BlockPos, Set<String>> interactedPlayersByAltar = new HashMap<>();

    public SavageBloodAltar(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 28, 16);

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public CompoundTag saveSavageAltarToNBT(Set<String> interactedPlayers) {
        CompoundTag compoundTag = new CompoundTag();
        for (String playerUUID : interactedPlayers) {
            compoundTag.putString(playerUUID, playerUUID); // Сохраняем UUID игрока
        }
        return compoundTag;
    }

    // Метод для загрузки данных о взаимодействии с алтарем
    public static Set<String> loadSavageAltarFromNBT(CompoundTag compound) {
        Set<String> interactedPlayers = new HashSet<>();
        for (String playerUUID : compound.getAllKeys()) {
            interactedPlayers.add(compound.getString(playerUUID));
        }
        return interactedPlayers;
    }

    // Сохранение данных игрока
    public void saveSavageAltarData(Player player) {
        BlockPos pos = player.blockPosition(); // Получаем позицию игрока
        Set<String> interactedPlayers = interactedPlayersByAltar.getOrDefault(pos, new HashSet<>());
        CompoundTag nbtData = saveSavageAltarToNBT(interactedPlayers); // Сохраняем текущее состояние
        player.getPersistentData().put("SavageBloodAltarData", nbtData); // Сохраняем в данных игрока
    }

    // Загрузка данных игрока
    public static void loadSavageAltarData(Player player) {
        if (player.getPersistentData().contains("SavageBloodAltarData")) {
            CompoundTag nbtData = player.getPersistentData().getCompound("SavageBloodAltarData");
            Set<String> interactedPlayers = loadSavageAltarFromNBT(nbtData);
            interactedPlayersByAltar.put(player.blockPosition(), interactedPlayers); // Загружаем для текущей позиции игрока
        }
    }

    // Обновление метода use
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            String playerUUID = player.getStringUUID();
            Set<String> interactedPlayers = interactedPlayersByAltar.computeIfAbsent(pos, k -> new HashSet<>());

            // Проверяем, не получал ли игрок подарок от этого алтаря
            if (!interactedPlayers.contains(playerUUID)) {
                // Логика получения подарка от алтаря (звуки, награды и так далее)
                world.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.addItem(ModItems.ANCIENT_GIFT.get().getDefaultInstance());

                // Сохраняем данные о взаимодействии
                interactedPlayers.add(playerUUID);
                saveSavageAltarData(player); // Сохранение состояния для игрока
                player.sendSystemMessage(Component.translatable("block.sbm.savage_blood_altar.gift_received"));
            } else {
                player.sendSystemMessage(Component.translatable("block.sbm.savage_blood_altar.gift_already_received"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}