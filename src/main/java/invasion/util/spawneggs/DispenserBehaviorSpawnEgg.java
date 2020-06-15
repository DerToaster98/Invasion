package invasion.util.spawneggs;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;


public class DispenserBehaviorSpawnEgg extends BehaviorDefaultDispenseItem
{

	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack)
	{
		//EnumFacing enumfacing = BlockDispenser.getFacing(blockSource.getBlockMetadata());
		IPosition direction = BlockDispenser.getDispensePosition(blockSource);
		double x = blockSource.getX() + direction.getX(); //enumfacing.getFrontOffsetX();
		double y = (float)blockSource.getY() + 0.2F;
		double z = blockSource.getZ() + direction.getZ(); //enumfacing.getFrontOffsetZ();
		Entity entity = ItemSpawnEgg.spawnCreature(blockSource.getWorld(), stack, new BlockPos(x, y, z));
		if (entity instanceof EntityLiving && stack.hasDisplayName())
			((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());

		stack.splitStack(1);

		return stack;
	}
}
