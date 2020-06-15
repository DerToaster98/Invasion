package invasion.client.render.animation.util;

import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathNode;
import invasion.entity.monster.InvadingEntity;
import invasion.util.Coords;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;


public class IMMoveHelperSpider extends IMMoveHelper
{
	public IMMoveHelperSpider(InvadingEntity par1EntityLiving)
	{
		super(par1EntityLiving);
	}

	@Override
	protected EnumFacing getClimbFace(BlockPos blockPos)
	{
		int mobX = MathHelper.floor(blockPos.getX() - this.entity.width / 2.0F);
		int mobY = MathHelper.floor(blockPos.getY());
		int mobZ = MathHelper.floor(blockPos.getZ() - this.entity.width / 2.0F);

		int index = 0;
		Path path = this.entity.getNavigatorNew().getPath();
		if ((path != null) && (!path.isFinished()))
		{
			PathNode currentPoint = path.getPathPointFromIndex(path.getCurrentPathIndex());
			int pathLength = path.getCurrentPathLength();
			for (int i = path.getCurrentPathIndex(); i < pathLength; i++)
			{
				PathNode point = path.getPathPointFromIndex(i);
				if (point.pos.x > currentPoint.pos.x) break;
				if (point.pos.x < currentPoint.pos.x)
				{
					index = 2;
					break;
				}
				if (point.pos.z > currentPoint.pos.z)
				{
					index = 4;
					break;
				}
				if (point.pos.z < currentPoint.pos.z)
				{
					index = 6;
					break;
				}
			}

		}

		for (int count = 0; count < 8; count++)
		{
			BlockPos pos = new BlockPos(mobX + Coords.offsetAdj2X[index], mobY, mobZ + Coords.offsetAdj2Z[index]);
			IBlockState blockState = this.entity.world.getBlockState(pos);
			boolean isSolidBlock = true;
			for (EnumFacing side : EnumFacing.values())
			{
				if (!blockState.getBlock().isSideSolid(blockState, this.entity.world, pos, side))
				{
					isSolidBlock = false;
					break;
				}
			}
			//if (blockState.getBlock().isSideSolid(blockState, this.entity.world, pos, side)/*.isSolidFullCube()*/) {
			if (isSolidBlock)
			{
				// TODO: Fix this, I just typed something random here
				return EnumFacing.getFront(index / 2);
				//return index / 2;
			}
			index++;
			if (index > 7) index = 0;
		}
		return null;
	}
}