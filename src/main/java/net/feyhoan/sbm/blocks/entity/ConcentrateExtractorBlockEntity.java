package net.feyhoan.sbm.blocks.entity;

import net.feyhoan.sbm.item.ModItems;
import net.feyhoan.sbm.network.ModMessages;
import net.feyhoan.sbm.network.packet.SpawnParticlePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class ConcentrateExtractorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 400;

    public ConcentrateExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONCENTRATE_EXTRACTOR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ConcentrateExtractorBlockEntity.this.progress;
                    case 1 -> ConcentrateExtractorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ConcentrateExtractorBlockEntity.this.progress = value;
                    case 1 -> ConcentrateExtractorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Concentrate Extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ConcentrateExtractorMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("concentrate_extractor.progress", this.progress);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("concentrate_extractor.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ItemStack getRenderStack() {
        ItemStack stack;

        if (!itemHandler.getStackInSlot(1).isEmpty()) {
            stack = itemHandler.getStackInSlot(1);
        } else {
            stack = itemHandler.getStackInSlot(0);
        }

        return stack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ConcentrateExtractorBlockEntity pEntity) {
        if (level.isClientSide()) {
            return;
        }    if (hasAnyRecipe(pEntity)) { // Проверяем, есть ли хотя бы один действующий рецепт
            pEntity.progress++;        setChanged(level, pos, state);        // Получаем случайные смещения для спавна частиц
            double offsetX = (level.random.nextDouble() - 0.5) * 2; // Случайное смещение по X
            double offsetZ = (level.random.nextDouble() - 0.5) * 2; // Случайное смещение по Z        // Спавн частиц чуть выше блока и в центре
            ModMessages.sendToClients(new SpawnParticlePacket(UUID.randomUUID(), pos.getX() + offsetX - 0.8, pos.getY() + 1.5, pos.getZ() + offsetZ, "blood_mark"));        if (pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity); // Пытаемся создать предмет
            }
        } else {
            pEntity.resetProgress(); // Если нет рецептов, сбрасываем прогресс
        }
    }// Проверка на наличие хотя бы одного действующего рецепта
    private static boolean hasAnyRecipe(ConcentrateExtractorBlockEntity entity) {
        return hasRecipe(entity, "SOUL_CONCENTRATE") || hasRecipe(entity, "WITHERITE_CONCENTRATE");
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ConcentrateExtractorBlockEntity pEntity) {
        for (String recipe : new String[]{"SOUL_CONCENTRATE", "WITHERITE_CONCENTRATE"}) {
            if (hasRecipe(pEntity, recipe)) {
                // Удаляем один песок душ из первого слота (индекс 0)
                pEntity.itemHandler.extractItem(0, 1, false);
                // Удаляем одну бутылочку из третьего слота, если это необходимо
                pEntity.itemHandler.extractItem(2, 1, false);
                pEntity.itemHandler.extractItem(3, 1, false);

                ItemStack resultStack;

                // В зависимости от рецепта выбираем, какой предмет добавлять в выходной слот
                if (recipe.equals("SOUL_CONCENTRATE")) {
                    resultStack = new ItemStack(ModItems.SOUL_CONCENTRATE.get(),
                            pEntity.itemHandler.getStackInSlot(1).getCount() + 1);
                } else if (recipe.equals("WITHERITE_CONCENTRATE")) {
                    resultStack = new ItemStack(ModItems.WITHERITE_CONCENTRATE.get(),
                            pEntity.itemHandler.getStackInSlot(1).getCount() + 1);
                } else {
                    continue; // В случае если рецепт не распознан
                }

                // Добавляем сосредоточенное вещество в выходной слот (индекс 1)
                pEntity.itemHandler.setStackInSlot(1, resultStack);

                // Сбрасываем прогресс
                pEntity.resetProgress();
                return; // Выход из функции после успешного крафта
            }
        }
    }

    private static boolean hasRecipe(ConcentrateExtractorBlockEntity entity, String recipe) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        boolean hasFuelInFourthSlot =
                entity.itemHandler.getStackInSlot(3).getItem() == Items.COAL ||
                entity.itemHandler.getStackInSlot(3).getItem() == Items.CHARCOAL ||
                entity.itemHandler.getStackInSlot(3).getItem() == Items.LAVA_BUCKET;

        switch (recipe) {
            case "SOUL_CONCENTRATE":
                boolean hasSoulSandInFirstSlot = entity.itemHandler.getStackInSlot(0).getItem() == Items.SOUL_SAND;
                boolean hasSoulSoilInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == Items.SOUL_SOIL;


                return hasSoulSandInFirstSlot && hasSoulSoilInSecondSlot && hasFuelInFourthSlot &&
                        canInsertAmountIntoOutputSlot(inventory) &&
                        canInsertItemIntoOutputSlot(inventory, new ItemStack(ModItems.SOUL_CONCENTRATE.get(), 0));

            case "WITHERITE_CONCENTRATE":
                boolean hasSoulConcentrate = entity.itemHandler.getStackInSlot(0).getItem() == ModItems.SOUL_CONCENTRATE.get();
                boolean hasNetherStar = entity.itemHandler.getStackInSlot(2).getItem() == Items.NETHER_STAR;

                return hasSoulConcentrate && hasNetherStar && hasFuelInFourthSlot &&
                        canInsertAmountIntoOutputSlot(inventory) &&
                        canInsertItemIntoOutputSlot(inventory, new ItemStack(ModItems.WITHERITE_CONCENTRATE.get(), 0));
        }
        return false;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return inventory.getItem(1).getItem() == stack.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }
}