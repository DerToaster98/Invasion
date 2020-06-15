package invasion.item.trap;

import invasion.Invasion;
import invasion.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ItemEmptyTrap extends Item
{

	private final String name = "emptyTrap";

	public ItemEmptyTrap()
	{
		this.setRegistryName(this.name);
		GameRegistry.register(this);
		this.setMaxStackSize(64);
		this.setUnlocalizedName(Reference.MODID + "_" + this.name);
		this.setCreativeTab(Invasion.tabInvmod);
	}

	public String getName()
	{
		return this.name;
	}

}
