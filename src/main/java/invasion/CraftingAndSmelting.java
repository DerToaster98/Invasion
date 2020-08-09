/*package invasion;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;


public class CraftingAndSmelting
{

	private static final ItemStack DIAMOND_SWORD = new ItemStack(Items.DIAMOND_SWORD, 1, OreDictionary.WILDCARD_VALUE);
	private static final ItemStack BOW = new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE);
	private static final ItemStack LAPIS_LAZULI = new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage());
	private static final ItemStack NEXUS_ADJUSTER = new ItemStack(BlocksAndItems.itemProbe, 1, 0);
	private static final ItemStack MATERIAL_PROBE = new ItemStack(BlocksAndItems.itemProbe, 1, 1);

	//Register Recipes
	static void addRecipes()
	{

		//Nexus block
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.blockNexus), new Object[] { " X ", "#D#", " # ", 'X', BlocksAndItems.itemPhaseCrystal, '#', Items.REDSTONE, 'D', Blocks.OBSIDIAN });

		//Items
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemPhaseCrystal), new Object[] { " X ", "#D#", " X ", 'X', LAPIS_LAZULI, '#', Items.REDSTONE, 'D', Items.DIAMOND });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemPhaseCrystal), new Object[] { " # ", "XDX", " # ", 'X', LAPIS_LAZULI, '#', Items.REDSTONE, 'D', Items.DIAMOND });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemRiftFlux), new Object[] { "XXX", "XXX", "XXX", 'X', BlocksAndItems.itemSmallRemnants });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemInfusedSword), new Object[] { "X ", "X#", "X ", 'X', BlocksAndItems.itemRiftFlux, '#', DIAMOND_SWORD });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemCatalystMixture), new Object[] { "D#H", " X ", 'X', Items.BOWL, '#', Items.REDSTONE, 'D', Items.BONE, 'H', Items.ROTTEN_FLESH });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemCatalystMixture), new Object[] { "H#D", " X ", 'X', Items.BOWL, '#', Items.REDSTONE, 'D', Items.BONE, 'H', Items.ROTTEN_FLESH });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStableCatalystMixture), new Object[] { "D#D", " X ", 'X', Items.BOWL, '#', Items.COAL, 'D', Items.BONE, 'H', Items.ROTTEN_FLESH });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStrongCatalystMixture), new Object[] { "D#H", " X ", 'X', Items.BOWL, '#', Blocks.REDSTONE_BLOCK, 'D', Items.BONE, 'H', Items.ROTTEN_FLESH });
		-GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStrongCatalystMixture), new Object[] { "H#D", " X ", 'X', Items.BOWL, '#', Blocks.REDSTONE_BLOCK, 'D', Items.BONE, 'H', Items.ROTTEN_FLESH });
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemDampingAgent), new Object[] { "#X#", 'X', BlocksAndItems.itemRiftFlux, '#', LAPIS_LAZULI });
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStrongDampingAgent), new Object[] { "X", "X", "X", 'X', BlocksAndItems.itemDampingAgent });
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStrongDampingAgent), new Object[] { "XXX", 'X', BlocksAndItems.itemDampingAgent });
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemStrangeBone), new Object[] { "X#X", 'X', BlocksAndItems.itemRiftFlux, '#', Items.BONE });
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemSearingBow), new Object[] { "XXX", "X# ", "X  ", 'X', BlocksAndItems.itemRiftFlux, '#', BOW });

		//Vanilla items
		GameRegistry.addRecipe(new ItemStack(Items.ARROW, 64), new Object[] { "X", "X", "X", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.GUNPOWDER, 16), new Object[] { "XXX", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.DIAMOND), new Object[] { " X ", "X X", " X ", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.IRON_INGOT, 4), new Object[] { "X", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.REDSTONE, 24), new Object[] { "X X", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.DYE, 12, 4), new Object[] { "X", " ", "X", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.STRING, 16), new Object[] { " X ", "X  ", " X ", 'X', BlocksAndItems.itemRiftFlux });
		GameRegistry.addRecipe(new ItemStack(Items.STRING, 16), new Object[] { " X ", "  X", " X ", 'X', BlocksAndItems.itemRiftFlux });

		//Traps
		GameRegistry.addRecipe(new ItemStack(BlocksAndItems.itemEmptyTrap), new Object[] { " X ", "X#X", " X ", 'X', Items.IRON_INGOT, '#', BlocksAndItems.itemRiftFlux });
		GameRegistry.addShapelessRecipe(new ItemStack(BlocksAndItems.itemFlameTrap), new Object[] { BlocksAndItems.itemEmptyTrap, Items.LAVA_BUCKET });

		//Probes
		GameRegistry.addRecipe(NEXUS_ADJUSTER, new Object[] { " X", "XX", "XX", 'X', Items.IRON_INGOT });
		GameRegistry.addRecipe(NEXUS_ADJUSTER, new Object[] { "X ", "XX", "XX", 'X', Items.IRON_INGOT });
		GameRegistry.addRecipe(MATERIAL_PROBE, new Object[] { "D", "#", "X", 'X', Items.STICK, '#', BlocksAndItems.itemPhaseCrystal, 'D', NEXUS_ADJUSTER });

		//Smelting
		GameRegistry.addSmelting(BlocksAndItems.itemCatalystMixture, new ItemStack(BlocksAndItems.itemNexusCatalyst), 1f);
		GameRegistry.addSmelting(BlocksAndItems.itemStableCatalystMixture, new ItemStack(BlocksAndItems.itemStableNexusCatalyst), 1f);
		GameRegistry.addSmelting(BlocksAndItems.itemStrongCatalystMixture, new ItemStack(BlocksAndItems.itemStrongCatalyst), 10f);
	}

}
*/