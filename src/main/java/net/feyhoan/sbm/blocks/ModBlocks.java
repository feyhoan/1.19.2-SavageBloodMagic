package net.feyhoan.sbm.blocks;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blocks.custom.*;
import net.feyhoan.sbm.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SBM.MOD_ID);


    public static final RegistryObject<Block> FROZEN_BLOOD_BLOCK = registryBlock("frozen_blood_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL)
                    .strength(0.5f)), ModItems.MYTAB);

    public static final RegistryObject<Block> ANCIENT_COLUMN = registryBlock("ancient_column",
            () -> new AncientColumn(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .strength(6f)), ModItems.MYTAB);

    public static final RegistryObject<Block> SAVAGE_BLOOD_ALTAR = registryBlock("savage_blood_altar",
            () -> new SavageBloodAltar(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .strength(7f)), ModItems.MYTAB);

    public static final RegistryObject<Block> HYACINTH_ORE = registryBlock("hyacinth_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5f)), ModItems.MYTAB);
    public static final RegistryObject<Block> DEEPSLATE_HYACINTH_ORE = registryBlock("deepslate_hyacinth_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5f)), ModItems.MYTAB);

    public static final RegistryObject<Block> TOPAZ_ORE = registryBlock("topaz_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(7f)), ModItems.MYTAB);
    public static final RegistryObject<Block> DEEPSLATE_TOPAZ_ORE = registryBlock("deepslate_topaz_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(7f)), ModItems.MYTAB);

    public static final RegistryObject<Block> CHRYSOLITE_ORE = registryBlock("chrysolite_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5f)), ModItems.MYTAB);
    public static final RegistryObject<Block> DEEPSLATE_CHRYSOLITE_ORE = registryBlock("deepslate_chrysolite_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5f)), ModItems.MYTAB);

    public static final RegistryObject<Block> SAPPHIRE_ORE = registryBlock("sapphire_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(7f)), ModItems.MYTAB);
    public static final RegistryObject<Block> DEEPSLATE_SAPPHIRE_ORE = registryBlock("deepslate_sapphire_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(7f)), ModItems.MYTAB);


    public static final RegistryObject<Block> ANCIENT_REFUGE_BLOCK = registryBlock("ancient_refuge_block",
            () -> new AncientRefugeBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5)), ModItems.MYTAB);
    public static final RegistryObject<StairBlock> ANCIENT_REFUGE_STAIRS = registryBlock("ancient_refuge_stairs",
            () -> new AncientRefugeStairs(() -> ANCIENT_REFUGE_BLOCK.get().defaultBlockState()),ModItems.MYTAB);
    public static final RegistryObject<SlabBlock> ANCIENT_REFUGE_SLAB = registryBlock("ancient_refuge_slab",
            () -> new AncientRefugeSlab(BlockBehaviour.Properties.copy(ANCIENT_REFUGE_BLOCK.get())), ModItems.MYTAB);



    public static final RegistryObject<Block> ANCIENT_REFUGE_BLOODY_BLOCK = registryBlock("ancient_refuge_bloody_block",
            () -> new Block(BlockBehaviour.Properties.copy(ANCIENT_REFUGE_BLOCK.get())), ModItems.MYTAB);

    public static final RegistryObject<Block> UNBREAKABLE_ANCIENT_REFUGE_BLOCK = registryBlock("unbreakable_ancient_refuge_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.BARRIER).strength(99999)), ModItems.MYTAB);



    private static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
