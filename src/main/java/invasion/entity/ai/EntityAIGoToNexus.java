package invasion.entity.ai;

//NOOB HAUS:Done

import invasion.entity.Objective;
import invasion.entity.ai.navigator.Path;
import invasion.entity.monster.InvadingEntity;
import invasion.nexus.Nexus;
import invasion.util.Distance;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class EntityAIGoToNexus extends EntityAIBase
{
	private InvadingEntity theEntity;
	private BlockPos lastPathRequestPos;
	private int pathRequestTimer;
	private int pathFailedCount;

	public EntityAIGoToNexus(InvadingEntity entity)
	{
		this.theEntity = entity;
		this.lastPathRequestPos = new BlockPos(0, -128, 0);
		this.pathRequestTimer = 0;
		this.pathFailedCount = 0;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		return this.theEntity.getAIGoal() == Objective.BREAK_NEXUS && this.theEntity.getNexus() != null;
	}

	@Override
	public void startExecuting()
	{
		this.setPathToNexus();
	}

	@Override
	public void updateTask()
	{
		if (this.pathFailedCount > 1) this.wanderToNexus();
		if ((this.theEntity.getNavigatorNew().noPath()) || (this.theEntity.getNavigatorNew().getStuckTime() > 40))
			this.setPathToNexus();
	}

	private void setPathToNexus()
	{
		Nexus nexus = this.theEntity.getNexus();
		if ((nexus != null) && (this.pathRequestTimer-- <= 0))
		{
			boolean pathSet = false;
			double distance = this.theEntity.findDistanceToNexus();
			double x = nexus.getPos().getX() + 0.5d;
			double y = nexus.getPos().getY() + 1d;
			double z = nexus.getPos().getZ() + 0.5d;
			if (distance > 2000.0D)
			{
				pathSet = this.theEntity.getNavigatorNew().tryMoveTowardsXZ(x, z, 1, 6, 4, this.theEntity.getMoveSpeedStat());
			}
			else if (distance > 1.5D)
			{
				pathSet = this.theEntity.getNavigatorNew().tryMoveToXYZ(x, y, z, 1.0F, this.theEntity.getMoveSpeedStat());
				//if(!pathSet) pathSet = this.theEntity.getNavigator().tryMoveToXYZ(x, y, z, 1.0F);
			}

			if ((!pathSet) || ((this.theEntity.getNavigatorNew().getLastPathDistanceToTarget() > 3.0F) && (Distance.distanceBetween(this.lastPathRequestPos, this.theEntity.getPosition()) < 3.5D)))
			{
				this.pathFailedCount += 1;
				this.pathRequestTimer = (40 * this.pathFailedCount + this.theEntity.world.rand.nextInt(10));
			}
			else
			{
				this.pathFailedCount = 0;
				this.pathRequestTimer = 20;
			}

			this.lastPathRequestPos = this.theEntity.getPosition();
		}
	}

	private boolean pathTooShort()
	{
		Path path = this.theEntity.getNavigatorNew().getPath();
		if (path != null)
		{
			Vec3d vec = path.getFinalPathPoint().pos;
			return this.theEntity.getDistanceSq(vec.x, vec.y, vec.z) < 4.0D;
		}
		return true;
	}

	protected void wanderToNexus()
	{
		Nexus nexus = this.theEntity.getNexus();
		this.theEntity.getMoveHelper().setMoveTo(nexus.getPos().add(0.5d, 0d, 0.5d), this.theEntity.getMoveSpeedStat());
	}
}