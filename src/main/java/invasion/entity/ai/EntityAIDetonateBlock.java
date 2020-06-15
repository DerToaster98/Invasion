package invasion.entity.ai;

import invasion.entity.Objective;
import invasion.entity.monster.MoulderingCreeperEntity;
import invasion.nexus.Nexus;
import invasion.util.Distance;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class EntityAIDetonateBlock extends EntityAIBase
{

	public final MoulderingCreeperEntity theEntity;
	public final boolean overridePath;

	private double targetRange;
	private double detonateRange;

	private BlockPos blockTarget = null;
	private BlockPos lastPathRequestPos = new BlockPos(0, -128, 0);
	private int pathRequestTimer = 0;
	private int pathFailedCount = 0;

	public EntityAIDetonateBlock(MoulderingCreeperEntity creeper, double targetRange, double detonateRange)
	{
		this(creeper, targetRange, detonateRange, false);
	}

	public EntityAIDetonateBlock(MoulderingCreeperEntity creeper, double targetRange, double detonateRange, boolean overridePath)
	{
		this.theEntity = creeper;
		this.targetRange = targetRange > detonateRange ? targetRange : detonateRange;
		this.detonateRange = targetRange > detonateRange ? detonateRange : targetRange;
		this.overridePath = overridePath;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		BlockPos pos = this.getNearestBlockOnSight();
		if (pos == null) return false;
		if (pos.distanceSq(this.theEntity.posX, this.theEntity.posY, this.theEntity.posZ) > (this.targetRange * this.targetRange)) return false;
		return true;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.shouldExecute();
	}

	@Override
	public void startExecuting()
	{
		this.blockTarget = this.getNearestBlockOnSight();
		if (this.overridePath || this.theEntity.getNavigatorNew().noPath()) this.setPathToTarget();
	}

	@Override
	public void resetTask()
	{
		this.blockTarget = null;
	}

	@Override
	public void updateTask()
	{
		this.blockTarget = this.getNearestBlockOnSight();
		if (this.blockTarget != null)
		{
			if (this.theEntity.getDistanceSq(this.blockTarget) <= (this.detonateRange * this.detonateRange))
			{
				this.theEntity.setCreeperState(1);
			}
			this.theEntity.setCreeperState(-1);
			if (this.pathFailedCount > 1) this.wanderToTarget();
			if (this.overridePath || this.theEntity.getNavigatorNew().noPath() || this.theEntity.getNavigatorNew().getStuckTime() > 60)
			{
				this.setPathToTarget();
			}
		}
		else
		{
			this.theEntity.setCreeperState(-1);
		}
	}

	protected void setPathToTarget()
	{
		if (this.blockTarget != null && this.pathRequestTimer-- <= 0)
		{
			boolean pathSet = false;
			double distance = this.theEntity.findDistanceToNexus();
			double x = this.blockTarget.getX() + 0.5d;
			double y = this.blockTarget.getY() + 1d;
			double z = this.blockTarget.getZ() + 0.5d;
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

	protected void wanderToTarget()
	{
		if (this.blockTarget == null) return;
		this.theEntity.getMoveHelper().setMoveTo(this.blockTarget, this.theEntity.getAIMoveSpeed());
	}

	private BlockPos getNearestBlockOnSight()
	{
		Vec3d pos;
		if (this.theEntity.getAIGoal() == Objective.BREAK_NEXUS)
		{
			Nexus nexus = this.theEntity.getNexus();
			if (nexus == null) return null;
			pos = new Vec3d(nexus.getPos());
		}
		else if (this.theEntity.getAIGoal() == Objective.TARGET_ENTITY)
		{
			EntityLivingBase target = this.theEntity.getAttackTarget();
			if (target == null) return null;
			pos = new Vec3d(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
		}
		else
		{
			return null;
		}
		Vec3d entityPos = new Vec3d(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.getEyeHeight(), this.theEntity.posZ);
		RayTraceResult rtr = this.theEntity.world.rayTraceBlocks(entityPos, pos, false, true, true);
		return rtr != null ? rtr.getBlockPos() : null;
	}

}
