package invasion.entity.ai.navigator;

import invasion.entity.IPathSource;
import invasion.entity.monster.InvadingEntity;
import invasion.util.PosRotate3D;
import net.minecraft.util.math.BlockPos;


public abstract class  NavigatorParametric extends NavigatorIM
{

	protected double minMoveToleranceSq;
	protected int timeParam;

	public NavigatorParametric(InvadingEntity entity, IPathSource pathSource)
	{
		super(entity, pathSource);
		this.minMoveToleranceSq = 21.0D;
		this.timeParam = 0;
	}

	public void onUpdateNavigation(int paramElapsed)
	{
		this.totalTicks += 1;
		if ((this.noPath()) || (this.waitingForNotify))
		{
			return;
		}
		if ((this.canNavigate()) && (this.nodeActionFinished))
		{
			int pathIndex = this.path.getCurrentPathIndex();
			this.pathFollow(this.timeParam + paramElapsed);
			this.doMovementTo(this.timeParam);

			if (this.path.getCurrentPathIndex() != pathIndex)
			{
				this.ticksStuck = 0;
				if (this.activeNode.action != PathAction.NONE)
				{
					this.nodeActionFinished = false;
				}
			}
		}
		if (this.nodeActionFinished)
		{
			if (!this.isPositionClear(new BlockPos(this.activeNode.pos), this.theEntity))
			{
				if (this.theEntity.onPathBlocked(this.path, this))
				{
					this.setDoingTaskAndHold();
				}
				else
				{
					this.clearPath();
				}
			}
		}
		else
		{
			this.handlePathAction();
		}
	}

	@Override
	public void onUpdateNavigation()
	{
		this.onUpdateNavigation(1);
	}

	protected void doMovementTo(int param)
	{
		PosRotate3D movePos = this.entityPositionAtParam(param);
		this.theEntity.setVelocity(movePos.getPosX(), movePos.getPosY(), movePos.getPosZ());

		if (Math.abs(this.theEntity.getDistanceSq(movePos.getPosX(), movePos.getPosY(), movePos.getPosZ())) < this.minMoveToleranceSq)
		{
			this.timeParam = param;
			this.ticksStuck -= 1;
		}
		else
		{
			this.ticksStuck += 1;
		}
	}

	protected abstract PosRotate3D entityPositionAtParam(int paramInt);

	protected abstract boolean isReadyForNextNode(int paramInt);

	protected void pathFollow(int param)
	{
		int nextIndex = this.path.getCurrentPathIndex() + 1;
		if (this.isReadyForNextNode(param))
		{
			if (nextIndex < this.path.getCurrentPathLength())
			{
				this.timeParam = 0;
				this.path.setCurrentPathIndex(nextIndex);
				this.activeNode = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());
			}
		}
		else
		{
			this.timeParam = param;
		}
	}

	@Override
	protected void pathFollow()
	{
		this.pathFollow(0);
	}

}