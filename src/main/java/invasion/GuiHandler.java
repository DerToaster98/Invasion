/*
package invasion;

import invasion.client.gui.GuiNexus;
import invasion.invasion.Invasion;
import invasion.inventory.container.ContainerNexus;
import invasion.util.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == Config.NEXUS_GUI_ID)
		{
			Invasion nexus = (Invasion)world.getTileEntity(new BlockPos(x, y, z));
			if (nexus != null) return new GuiNexus(player.inventory, nexus);
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == Config.NEXUS_GUI_ID)
		{
			Invasion nexus = (Invasion)world.getTileEntity(new BlockPos(x, y, z));
			if (nexus != null) return new ContainerNexus(player.inventory, nexus);
		}
		return null;
	}
}

 */