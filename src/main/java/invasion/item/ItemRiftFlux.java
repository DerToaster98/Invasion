/*package invasion.item;

import invasion.Main;
import invasion.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ItemRiftFlux extends Item
{

	private final String name = "riftFlux";

	public ItemRiftFlux()
	{
		this.setRegistryName(this.name);
		GameRegistry.register(this);
		this.setMaxDamage(0);
		this.setUnlocalizedName(Reference.MODID + "_" + this.name);
		this.setMaxStackSize(64);
		this.setCreativeTab(Main.tabInvmod);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		return EnumActionResult.FAIL;
	}

	public String getName()
	{
		return this.name;
	}
}*/