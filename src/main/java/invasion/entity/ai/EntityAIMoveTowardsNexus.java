package invasion.entity.ai;

import invasion.entity.EntityIMLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;


public class EntityAIMoveTowardsNexus extends EntityAIBase
{

	private final EntityIMLiving theEntityIM;
	private BlockPos target = BlockPos.ORIGIN;
	private int pathSetTicks = 0;

	public EntityAIMoveTowardsNexus(EntityIMLiving creature)
	{
		this.theEntityIM = creature;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute()
	{
		return this.theEntityIM.getNexus() != null;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.theEntityIM.getDistanceSq(this.target) >= 16d;
	}

	@Override
	public void startExecuting()
	{
		this.target = this.theEntityIM.getNexus().getPos();
		this.pathSetTicks = 0;
	}

	@Override
	public void resetTask()
	{
		this.target = BlockPos.ORIGIN;
		this.pathSetTicks = 0;
	}

	@Override
	public void updateTask()
	{
		if (this.pathSetTicks-- == 0)
		{
			boolean pathSet = this.theEntityIM.getNavigator().tryMoveToXYZ(this.target.getX(), this.target.getY(), this.target.getZ(), 1f);
			this.pathSetTicks = 40;
		}
	}

}
