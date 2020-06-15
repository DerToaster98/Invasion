package invasion.entity;

import invasion.INotifyTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public interface ITerrainBuild
{

	public boolean askBuildScaffoldLayer(BlockPos pos, INotifyTask paramINotifyTask);

	public boolean askBuildLadderTower(BlockPos pos, int paramInt1, int paramInt2, INotifyTask paramINotifyTask);

	public boolean askBuildLadder(BlockPos pos, INotifyTask paramINotifyTask);

	public boolean askBuildBridge(BlockPos pos, INotifyTask paramINotifyTask);

	public default boolean askBuildScaffoldLayer(Vec3d vec, INotifyTask paramINotifyTask)
	{
		return this.askBuildScaffoldLayer(new BlockPos(vec), paramINotifyTask);
	}

	public default boolean askBuildLadderTower(Vec3d vec, int paramInt1, int paramInt2, INotifyTask paramINotifyTask)
	{
		return this.askBuildLadderTower(new BlockPos(vec), paramInt1, paramInt2, paramINotifyTask);
	}

	public default boolean askBuildLadder(Vec3d vec, INotifyTask paramINotifyTask)
	{
		return this.askBuildLadder(new BlockPos(vec), paramINotifyTask);
	}

	public default boolean askBuildBridge(Vec3d vec, INotifyTask paramINotifyTask)
	{
		return this.askBuildBridge(new BlockPos(vec), paramINotifyTask);
	}

}