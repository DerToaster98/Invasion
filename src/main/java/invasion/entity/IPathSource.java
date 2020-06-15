package invasion.entity;

import invasion.IPathfindable;
import invasion.entity.ai.navigator.Path;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public interface IPathSource
{

	public Path createPath(IPathfindable paramIPathfindable, Vec3d pos0In, Vec3d pos1In, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

	public Path createPath(EntityIMLiving paramEntityIMLiving, Entity paramEntity, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

	public Path createPath(EntityIMLiving paramEntityIMLiving, Vec3d vec, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

	public void createPath(IPathResult paramIPathResult, IPathfindable paramIPathfindable, BlockPos pos0, BlockPos pos1, float paramFloat, IBlockAccess paramIBlockAccess);

	public void createPath(IPathResult paramIPathResult, EntityIMLiving paramEntityIMLiving, Entity paramEntity, float paramFloat, IBlockAccess paramIBlockAccess);

	public void createPath(IPathResult paramIPathResult, EntityIMLiving paramEntityIMLiving, BlockPos pos, float paramFloat, IBlockAccess paramIBlockAccess);

	public int getSearchDepth();

	public int getQuickFailDepth();

	public void setSearchDepth(int paramInt);

	public void setQuickFailDepth(int paramInt);

	public boolean canPathfindNice(PathPriority paramPathPriority, float paramFloat, int paramInt1, int paramInt2);

	public static enum PathPriority
	{
		LOW, MEDIUM, HIGH;
	}

}