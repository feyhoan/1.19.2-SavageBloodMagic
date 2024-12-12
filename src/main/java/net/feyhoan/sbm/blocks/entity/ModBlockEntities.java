package net.feyhoan.sbm.blocks.entity;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SBM.MOD_ID);

    public static final RegistryObject<BlockEntityType<ConcentrateExtractorBlockEntity>> CONCENTRATE_EXTRACTOR =
            BLOCK_ENTITIES.register("concentrate_extractor", () ->
                    BlockEntityType.Builder.of(ConcentrateExtractorBlockEntity::new,
                            ModBlocks.CONCENTRATE_EXTRACTOR.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}