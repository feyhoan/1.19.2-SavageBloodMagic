package net.feyhoan.sbm.loot;

import net.feyhoan.sbm.SBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = SBM.MOD_ID)
public class ModLootModifiers {

    // Определяем ресурс-идентификаторы для лутовых таблиц
    public static final ResourceLocation NETHER_FORTRESS_CHEST = new ResourceLocation("minecraft", "chests/nether_bridge");

    // Все лутовые таблицы, кроме крепости Незера
    public static final ResourceLocation[] ALL_CHESTS_EXCLUDING_NETHER = new ResourceLocation[]{
            new ResourceLocation("minecraft", "chests/village/village_armorer"),
            new ResourceLocation("minecraft", "chests/village/village_butcher"),
            new ResourceLocation("minecraft", "chests/village/village_cartographer"),
            new ResourceLocation("minecraft", "chests/village/village_desert_house"),
            new ResourceLocation("minecraft", "chests/village/village_fisher"),
            new ResourceLocation("minecraft", "chests/village/village_fletcher"),
            new ResourceLocation("minecraft", "chests/village/village_mason"),
            new ResourceLocation("minecraft", "chests/village/village_plains_house"),
            new ResourceLocation("minecraft", "chests/village/village_savanna_house"),
            new ResourceLocation("minecraft", "chests/village/village_shepherd"),
            new ResourceLocation("minecraft", "chests/village/village_snowy_house"),
            new ResourceLocation("minecraft", "chests/village/village_taiga_house"),
            new ResourceLocation("minecraft", "chests/village/village_tannery"),
            new ResourceLocation("minecraft", "chests/village/village_temple"),
            new ResourceLocation("minecraft", "chests/village/village_toolsmith"),
            new ResourceLocation("minecraft", "chests/village/village_weaponsmith"),
            new ResourceLocation("minecraft", "chests/abandoned_mineshaft"),
            new ResourceLocation("minecraft", "chests/ancient_city"),
            new ResourceLocation("minecraft", "chests/ancient_city_ice_box"),
            new ResourceLocation("minecraft", "chests/bastion_bridge"),
            new ResourceLocation("minecraft", "chests/bastion_hoglin_stable"),
            new ResourceLocation("minecraft", "chests/bastion_other"),
            new ResourceLocation("minecraft", "chests/bastion_treasure"),
            new ResourceLocation("minecraft", "chests/buried_treasure"),
            new ResourceLocation("minecraft", "chests/desert_pyramid"),
            new ResourceLocation("minecraft", "chests/end_city_treasure"),
            new ResourceLocation("minecraft", "chests/igloo_chest"),
            new ResourceLocation("minecraft", "chests/jungle_temple"),
            new ResourceLocation("minecraft", "chests/shipwreck_supply"),
            new ResourceLocation("minecraft", "chests/shipwreck_treasure"),
            new ResourceLocation("minecraft", "chests/simple_dungeon"),
            new ResourceLocation("minecraft", "chests/stronghold_corridor"),
            new ResourceLocation("minecraft", "chests/stronghold_crossing"),
            new ResourceLocation("minecraft", "chests/stronghold_library"),
            new ResourceLocation("minecraft", "chests/underwater_ruin_big"),
            new ResourceLocation("minecraft", "chests/underwater_ruin_small"),
            new ResourceLocation("minecraft", "chests/woodland_mansion"),
    };

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableName = event.getName();

        // Добавляем обычные свитки во все сундуки, кроме крепости Незера
        if (isInArray(tableName)) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("sbm:common_scroll_of_savage_blood")))).setWeight(4).setQuality(1))
                    .add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("sbm:beginner_scroll_of_savage_blood")))).setWeight(3).setQuality(2))
                    .add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("sbm:advanced_scroll_of_savage_blood")))).setWeight(2).setQuality(3))
                    .add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("sbm:master_scroll_of_savage_blood")))).setWeight(1).setQuality(4))
                    .add(LootItem.lootTableItem(Items.AIR).setWeight(5)); // Пустой предмет для большей вероятности невыдачи свитков


            // Добавление нового пула к существующей LootTable
            event.getTable().addPool(poolBuilder.build());
        }

        // Добавляем древний свиток только в сундуки крепостей Незера
        if (tableName.equals(NETHER_FORTRESS_CHEST)) {
            LootPool.Builder ancientPoolBuilder = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("sbm:ancient_scroll_of_savage_blood")))).setWeight(1).setQuality(5))
                    .add(LootItem.lootTableItem(Items.AIR).setWeight(5)); // Пустой предмет для большей вероятности невыдачи свитков

            // Добавление нового пула к сундукам крепости Незера
            event.getTable().addPool(ancientPoolBuilder.build());
        }
    }

    // Метод для проверки, содержится ли ресурс в массиве
    private static boolean isInArray(ResourceLocation tableName) {
        for (ResourceLocation resourceLocation : ModLootModifiers.ALL_CHESTS_EXCLUDING_NETHER) {
            if (resourceLocation.equals(tableName)) {
                return true;
            }
        }
        return false;
    }
}