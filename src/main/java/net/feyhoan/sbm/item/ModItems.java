package net.feyhoan.sbm.item;

import net.feyhoan.sbm.SBM;
import net.feyhoan.sbm.item.curios.bracelet.*;
import net.feyhoan.sbm.item.curios.necklace.*;
import net.feyhoan.sbm.item.curios.ring.AmethystRing;
import net.feyhoan.sbm.item.curios.ring.*;
import net.feyhoan.sbm.item.custom.AncientGift;
import net.feyhoan.sbm.item.custom.AncientReaperTier;
import net.feyhoan.sbm.item.custom.BloodBottleItem;
import net.feyhoan.sbm.item.custom.FrozenBloodArmor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SBM.MOD_ID);

    // Создание кастомного Creative Tab
    public static final CreativeModeTab MYTAB = new CreativeModeTab("my_custom_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TAB_ICO.get()); // Предмет, который будет показан как иконка
        }
    };

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> TAB_ICO = ITEMS.register("tabicon",
            () -> new Item(new Item.Properties()));

    //Крафты?

    public static final RegistryObject<Item> PIECE_OF_FROZEN_BLOOD = ITEMS.register("piece_of_frozen_blood",
            () -> new Item(new Item.Properties().tab(MYTAB)));

    //Разное

    public static final RegistryObject<Item> KNIFE = ITEMS.register("knife",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    //Свитки уровня

    public static final RegistryObject<Item> COMMON_SCROLL = ITEMS.register("common_scroll_of_savage_blood",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> BEGINNER_SCROLL = ITEMS.register("beginner_scroll_of_savage_blood",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> ADVANCED_SCROLL = ITEMS.register("advanced_scroll_of_savage_blood",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> MASTER_SCROLL = ITEMS.register("master_scroll_of_savage_blood",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> ANCIENT_SCROLL = ITEMS.register("ancient_scroll_of_savage_blood",
            () -> new Item(new Item.Properties().stacksTo(1).tab(MYTAB)));

    //Необработанные руды

    public static final RegistryObject<Item> RAW_HYACINTH = ITEMS.register("raw_hyacinth",
            () -> new Item(new Item.Properties().stacksTo(32).tab(MYTAB)));

    public static final RegistryObject<Item> RAW_TOPAZ = ITEMS.register("raw_topaz",
            () -> new Item(new Item.Properties().stacksTo(32).tab(MYTAB)));

    public static final RegistryObject<Item> RAW_CHRYSOLIE = ITEMS.register("raw_chrysolite",
            () -> new Item(new Item.Properties().stacksTo(32).tab(MYTAB)));

    public static final RegistryObject<Item> RAW_SAPPHIRE = ITEMS.register("raw_sapphire",
            () -> new Item(new Item.Properties().stacksTo(32).tab(MYTAB)));

    //Руды

    public static final RegistryObject<Item> HYACINTH = ITEMS.register("hyacinth",
            () -> new Item(new Item.Properties().tab(MYTAB)));

    public static final RegistryObject<Item> TOPAZ = ITEMS.register("topaz",
            () -> new Item(new Item.Properties().tab(MYTAB)));

    public static final RegistryObject<Item> CHRYSOLIE = ITEMS.register("chrysolite",
            () -> new Item(new Item.Properties().tab(MYTAB)));

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties().tab(MYTAB)));

    //Артефакты

    public static final RegistryObject<Item> HYACINTH_RING = ITEMS.register("hyacinth_ring", HyacinthRing::new);
    public static final RegistryObject<Item> TOPAZ_RING = ITEMS.register("topaz_ring", TopazRing::new);
    public static final RegistryObject<Item> CHRYSOLITE_RING = ITEMS.register("chrysolite_ring", ChrysoliteRing::new);
    public static final RegistryObject<Item> AMETHYST_RING = ITEMS.register("amethyst_ring", AmethystRing::new);
    public static final RegistryObject<Item> SAPPHIRE_RING = ITEMS.register("sapphire_ring", SapphireRing::new);

    public static final RegistryObject<Item> HYACINTH_NECKLACE = ITEMS.register("hyacinth_necklace", HyacinthNecklace::new);
    public static final RegistryObject<Item> TOPAZ_NECKLACE = ITEMS.register("topaz_necklace", TopazNecklace::new);
    public static final RegistryObject<Item> CHRYSOLITE_NECKLACE = ITEMS.register("chrysolite_necklace", ChrysoliteNecklace::new);
    public static final RegistryObject<Item> AMETHYST_NECKLACE = ITEMS.register("amethyst_necklace", AmethystNecklace::new);
    public static final RegistryObject<Item> SAPPHIRE_NECKLACE = ITEMS.register("sapphire_necklace", SapphireNecklace::new);

    public static final RegistryObject<Item> HYACINTH_BRACELET = ITEMS.register("hyacinth_bracelet", HyacinthBracelet::new);
    public static final RegistryObject<Item> TOPAZ_BRACELET = ITEMS.register("topaz_bracelet", TopazBracelet::new);
    public static final RegistryObject<Item> CHRYSOLITE_BRACELET = ITEMS.register("chrysolite_bracelet", ChrysoliteBracelet::new);
    public static final RegistryObject<Item> AMETHYST_BRACELET = ITEMS.register("amethyst_bracelet", AmethystBracelet::new);
    public static final RegistryObject<Item> SAPPHIRE_BRACELET = ITEMS.register("sapphire_bracelet", SapphireBracelet::new);

    //Оружие
    public static final RegistryObject<Item> ANCIENT_REAPER = ITEMS.register("ancient_reaper",
            () -> new SwordItem(AncientReaperTier.INSTANCE, 7, 5f,
                    new Item.Properties().tab(MYTAB).stacksTo(1)));

    //Бутылки крови
    public static final RegistryObject<Item> BOTTLE_OF_HUMAN_BLOOD = ITEMS.register("bottle_of_human_blood",
            () -> new BloodBottleItem(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> BOTTLE_OF_NETHER_BLOOD = ITEMS.register("bottle_of_nether_blood",
            () -> new BloodBottleItem(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> BOTTLE_OF_ENDER_BLOOD = ITEMS.register("bottle_of_end_blood",
            () -> new BloodBottleItem(new Item.Properties().stacksTo(1).tab(MYTAB)));

    public static final RegistryObject<Item> ANCIENT_GIFT = ITEMS.register("ancient_gift",
            () -> new AncientGift(new Item.Properties().stacksTo(1).tab(MYTAB)));


    //Броня
    public static final RegistryObject<ArmorItem> FROZEN_BLOOD_CHEST = ITEMS.register("frozen_blood_chestplate",
            () -> new FrozenBloodArmor(ModArmorMaterials.FROZEN_BLOOD, EquipmentSlot.CHEST, new Item.Properties().stacksTo(1).tab(MYTAB)));
    public static final RegistryObject<ArmorItem> FROZEN_BLOOD_HELMET = ITEMS.register("frozen_blood_helmet",
            () -> new FrozenBloodArmor(ModArmorMaterials.FROZEN_BLOOD, EquipmentSlot.HEAD, new Item.Properties().stacksTo(1).tab(MYTAB)));
    public static final RegistryObject<ArmorItem> FROZEN_BLOOD_LEGS = ITEMS.register("frozen_blood_leggings",
            () -> new FrozenBloodArmor(ModArmorMaterials.FROZEN_BLOOD, EquipmentSlot.LEGS, new Item.Properties().stacksTo(1).tab(MYTAB)));
    public static final RegistryObject<ArmorItem> FROZEN_BLOOD_BOOTS = ITEMS.register("frozen_blood_boots",
            () -> new FrozenBloodArmor(ModArmorMaterials.FROZEN_BLOOD, EquipmentSlot.FEET, new Item.Properties().stacksTo(1).tab(MYTAB)));
}
