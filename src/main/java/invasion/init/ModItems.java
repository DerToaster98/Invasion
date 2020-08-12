package invasion.init;

import invasion.Invasion;
import invasion.item.BlackArrowItem;
import invasion.item.InfusedSwordItem;
import invasion.item.SearingBowItem;
import invasion.item.StrangeBoneItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Invasion.MOD_ID);


    public static final RegistryObject<Item> SEARING_BOW = ITEMS.register("searing_bow", () -> new SearingBowItem(new Item.Properties().group(ModItemGroups.INVASION)));
    public static final RegistryObject<Item> INFUSED_SWORD = ITEMS.register("infused_sword", () -> new InfusedSwordItem(new Item.Properties().group(ModItemGroups.INVASION)));
    public static final RegistryObject<Item> BLACK_ARROW = ITEMS.register("black_arrow", () -> new BlackArrowItem(new Item.Properties().group(ModItemGroups.INVASION)));

    public static final RegistryObject<Item> NEXUS = ITEMS.register("nexus", () -> new BlockItem(ModBlocks.NEXUS.get(), new Item.Properties().group(ModItemGroups.INVASION)));

    public static final RegistryObject<Item> CATALYST_MIXTURE = ITEMS.register("catalyst_mixture", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> STABLE_CATALYST_MIXTURE = ITEMS.register("stable_catalyst_mixture", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> STRONG_CATALYST_MIXTURE = ITEMS.register("strong_catalyst_mixture", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));

    public static final RegistryObject<Item> CATALYST = ITEMS.register("catalyst", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> STABLE_CATALYST = ITEMS.register("stable_catalyst", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> STRONG_CATALYST = ITEMS.register("strong_catalyst", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));

    public static final RegistryObject<Item> DAMPING_AGENT = ITEMS.register("damping_agent", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> STRONG_DAMPING_AGENT = ITEMS.register("strong_damping_agent", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));

    public static final RegistryObject<Item> PHASE_CRYSTAL = ITEMS.register("phase_crystal", () -> new SimpleFoiledItem(new Item.Properties().group(ModItemGroups.INVASION).maxStackSize(1)));
    public static final RegistryObject<Item> RIFT_FLUX = ITEMS.register("rift_flux", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION)));
    public static final RegistryObject<Item> SMALL_REMNANTS = ITEMS.register("small_remnants", () -> new Item(new Item.Properties().group(ModItemGroups.INVASION)));
    public static final RegistryObject<Item> PROBE = null;//RM ITEMS.register("probe", () -> new ProbeItem(new Item.Properties().group(ModItemGroups.INVASION)));
    public static final RegistryObject<Item> DEBUG_WAND = null;//RM  ITEMS.register("debug_wand", DebugWandItem::new);
    public static final RegistryObject<Item> ENGY_HAMMER = ITEMS.register("engy_hammer", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STRANGE_BONE = ITEMS.register("strange_bone",() -> new StrangeBoneItem(new Item.Properties().group(ModItemGroups.INVASION)));
}
