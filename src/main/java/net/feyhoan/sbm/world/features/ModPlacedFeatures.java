package net.feyhoan.sbm.world.features;

import net.feyhoan.sbm.SBM;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, SBM.MOD_ID);


    public static final RegistryObject HYACINTH_ORE_PLACED = PLACED_FEATURES.register("hyacinth_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.HYACINTH_ORE.getHolder().get(), commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-150), VerticalAnchor.absolute(30)))));
    public static final RegistryObject TOPAZ_ORE_PLACED = PLACED_FEATURES.register("topaz_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.TOPAZ_ORE.getHolder().get(), commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-150), VerticalAnchor.absolute(40)))));
    public static final RegistryObject CHRYSOLITE_ORE_PLACED = PLACED_FEATURES.register("chrysolite_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.CHRYSOLITE_ORE.getHolder().get(), commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-150), VerticalAnchor.absolute(30)))));
    public static final RegistryObject SAPPHIRE_ORE_PLACED = PLACED_FEATURES.register("sapphire_ore_placed", () -> new PlacedFeature(ModConfiguredFeatures.SAPPHIRE_ORE.getHolder().get(), commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.absolute(-150), VerticalAnchor.absolute(30)))));



    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}