package invasion.entity;

import invasion.INotifyTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public interface ITerrainDig
{

	public boolean askRemoveBlock(BlockPos pos, INotifyTask onFinished, float costMultiplier);

	public boolean askClearPosition(BlockPos pos, INotifyTask onFinished, float costMultiplier);

	public default boolean askRemoveBlock(Vec3d vec, INotifyTask onFinished, float costMultiplier)
	{
		return this.askRemoveBlock(new BlockPos(vec), onFinished, costMultiplier);
	}

	public default boolean askClearPosition(Vec3d vec, INotifyTask onFinished, float costMultiplier)
	{
		return this.askClearPosition(new BlockPos(vec), onFinished, costMultiplier);
	}

}