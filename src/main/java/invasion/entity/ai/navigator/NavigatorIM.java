package invasion.entity.ai.navigator;

import invasion.INotifyTask;
import invasion.entity.EntityIMLiving;
import invasion.entity.INavigation;
import invasion.entity.IPathSource;
import invasion.nexus.Nexus;
import invasion.util.Distance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;


public class NavigatorIM implements INotifyTask, INavigation
{

	protected static final int XZPATH_HORIZONTAL_SEARCH = 1;
	protected static final double ENTITY_TRACKING_TOLERANCE = 0.1D;
	protected static final double MINIMUM_PROGRESS = 0.01D;
	protected final EntityIMLiving theEntity;
	protected IPathSource pathSource;
	protected Path path;
	protected PathNode activeNode;
	protected Vec3d entityCentre;
	protected Entity pathEndEntity;
	protected Vec3d pathEndEntityLastPos;
	protected float moveSpeed;
	protected float pathSearchLimit;
	protected boolean noSunPathfind;
	protected int totalTicks;
	protected Vec3d lastPos;
	private Vec3d holdingPos;
	protected boolean nodeActionFinished;
	private boolean canSwim;
	protected boolean waitingForNotify;
	protected boolean actionCleared;
	protected double lastDistance;
	protected int ticksStuck;
	private boolean maintainPosOnWait;
	private int lastActionResult;
	private boolean haltMovement;
	private boolean autoPathToEntity;

	public NavigatorIM(EntityIMLiving entity, IPathSource pathSource)
	{
		this.theEntity = entity;
		this.pathSource = pathSource;
		this.noSunPathfind = false;
		this.lastPos = new Vec3d(0.0D, 0.0D, 0.0D);
		this.pathEndEntityLastPos = new Vec3d(0.0D, 0.0D, 0.0D);
		this.lastDistance = 0.0D;
		this.ticksStuck = 0;
		this.canSwim = false;
		this.waitingForNotify = false;
		this.actionCleared = true;
		this.nodeActionFinished = true;
		this.maintainPosOnWait = false;
		this.haltMovement = false;
		this.lastActionResult = 0;
		this.autoPathToEntity = false;
	}

	@Override
	public PathAction getCurrentWorkingAction()
	{
		if ((!this.nodeActionFinished) && (!this.noPath()))
		{
			return this.activeNode.action;
		}
		return PathAction.NONE;
	}

	protected boolean isMaintainingPos()
	{
		return this.maintainPosOnWait;
	}

	protected void setNoMaintainPos()
	{
		this.maintainPosOnWait = false;
	}

	protected void setMaintainPosOnWait(Vec3d pos)
	{
		this.holdingPos = pos;
		this.maintainPosOnWait = true;
	}

	@Override
	public void setSpeed(float par1)
	{
		this.moveSpeed = par1;
	}

	public boolean isAutoPathingToEntity()
	{
		return this.autoPathToEntity;
	}

	@Override
	public Entity getTargetEntity()
	{
		return this.pathEndEntity;
	}

	@Override
	public Path getPathToXYZ(double x, double y, double z, float targetRadius)
	{
		if (!this.canNavigate()) return null;
		//return this.createPath(this.theEntity, MathHelper.floor(x), (int) y, MathHelper.floor(z), targetRadius);
		return this.createPath(this.theEntity, new Vec3d(x, y, z), targetRadius);
	}

	@Override
	public boolean tryMoveToXYZ(double x, double y, double z, float targetRadius, float speed)
	{
		this.ticksStuck = 0;
		Path newPath = this.getPathToXYZ(MathHelper.floor(x), (int)y, MathHelper.floor(z), targetRadius);
		if (newPath != null)
		{
			return this.setPath(newPath, speed);
		}
		return false;
	}

	@Override
	public Path getPathTowardsXZ(double x, double z, int min, int max, int verticalRange)
	{
		if (this.canNavigate())
		{
			Vec3d target = this.findValidPointNear(x, z, min, max, verticalRange);
			if (target != null)
			{
				Path entityPath = this.getPathToXYZ(target.x, target.y, target.z, 0.0F);
				if (entityPath != null)
					return entityPath;
			}
		}
		return null;
	}

	@Override
	public boolean tryMoveTowardsXZ(double x, double z, int min, int max, int verticalRange, float speed)
	{
		this.ticksStuck = 0;
		Path newPath = this.getPathTowardsXZ(MathHelper.floor(x), MathHelper.floor(z), min, max, verticalRange);
		if (newPath != null)
		{
			return this.setPath(newPath, speed);
		}
		return false;
	}

	@Override
	public Path getPathToEntity(Entity targetEntity, float targetRadius)
	{
		if (!this.canNavigate()) return null;
		//return createPath(this.theEntity, MathHelper.floor(targetEntity.posX), MathHelper.floor(targetEntity.getEntityBoundingBox().minY), MathHelper.floor(targetEntity.posZ), targetRadius);
		return this.createPath(this.theEntity, targetEntity, targetRadius);
	}

	@Override
	public boolean tryMoveToEntity(Entity targetEntity, float targetRadius, float speed)
	{
		Path newPath = this.getPathToEntity(targetEntity, targetRadius);
		if (newPath != null)
		{
			if (this.setPath(newPath, speed))
			{
				this.pathEndEntity = targetEntity;
				return true;
			}

			this.pathEndEntity = null;
			return false;
		}

		return false;
	}

	@Override
	public void autoPathToEntity(Entity target)
	{
		this.autoPathToEntity = true;
		this.pathEndEntity = target;
	}

	@Override
	public boolean setPath(Path newPath, float speed)
	{
		if (newPath == null)
		{
			this.path = null;
			this.theEntity.onPathSet();
			return false;
		}

		this.moveSpeed = speed;
		this.lastDistance = this.getDistanceToActiveNode();
		this.ticksStuck = 0;
		this.resetStatus();

		BlockPos size = this.theEntity.getCollideSize();
		this.entityCentre = new Vec3d(size.getX() * 0.5D, 0.0D, size.getZ() * 0.5D);

		this.path = newPath;
		this.activeNode = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());

		if (this.activeNode.action != PathAction.NONE)
		{
			this.nodeActionFinished = false;
		}
		else if ((size.getX() <= 1) && (size.getZ() <= 1))
		{
			this.path.incrementPathIndex();
			if (!this.path.isFinished())
			{
				this.activeNode = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());
				//ModLogger.logInfo("(Set) Moving " + this.theEntity.getName() + " from " + this.theEntity.getPosition() + " to " + this.activeNode.toString());
				//this.theEntity.setVelocity(this.activeNode.xCoord, this.activeNode.yCoord, this.activeNode.zCoord);
				if (this.activeNode.action != PathAction.NONE)
				{
					this.nodeActionFinished = false;
				}
			}
		}
		else
		{
			//UnstoppableN Custom Code
			//changed < to > this seems to have fixed some stuffs, not sure why
			while (this.theEntity.getDistanceSq(this.activeNode.pos.x + this.entityCentre.x, this.activeNode.pos.y + this.entityCentre.y, this.activeNode.pos.z + this.entityCentre.z) > this.theEntity.width)
			{
				this.path.incrementPathIndex();
				if (!this.path.isFinished())
				{
					this.activeNode = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());
					//ModLogger.logInfo("(UnstoppableN) Moving " + this.theEntity.getName() + " to " + this.activeNode.toString());
					//this.theEntity.setVelocity(this.activeNode.xCoord, this.activeNode.yCoord, this.activeNode.zCoord);
					if (this.activeNode.action != PathAction.NONE)
					{
						this.nodeActionFinished = false;
					}
				}
				else
				{
					//this is the part where it gets cheaty!
					//System.out.println("Finished! : "+this.path.getCurrentPathIndex()+" / "+this.path.points.length);
					//NEVER USE BREAKS! unless you are retarded like me and don't know how to convert a float width to double getDistance
					break;
				}

			}

		}

		if (this.noSunPathfind) this.removeSunnyPath();

		this.theEntity.onPathSet();
		return true;
	}

	@Override
	public Path getPath()
	{
		return this.path;
	}

	@Override
	public boolean isWaitingForTask()
	{
		return this.waitingForNotify;
	}

	@Override
	public void onUpdateNavigation()
	{
		this.totalTicks += 1;
		if (this.autoPathToEntity)
		{
			this.updateAutoPathToEntity();
		}

		if (this.noPath())
		{
			this.noPathFollow();
			return;
		}

		if (this.waitingForNotify)
		{
			if (this.isMaintainingPos())
			{
				this.theEntity.getMoveHelper().setMoveTo(this.holdingPos.x, this.holdingPos.y, this.holdingPos.z, this.moveSpeed);
			}
			return;
		}

		if ((this.canNavigate()) && (this.nodeActionFinished))
		{
			double distance = this.getDistanceToActiveNode();
			if (this.lastDistance - distance > 0.01D)
			{
				this.lastDistance = distance;
				this.ticksStuck -= 1;
			}
			else
			{
				this.ticksStuck += 1;
			}

			int pathIndex = this.path.getCurrentPathIndex();
			this.pathFollow();
			this.doMoveAlongPath();
			if (this.noPath()) return;

			//If the entity reached the path point and it has now changed to the next one
			if (this.path.getCurrentPathIndex() != pathIndex)
			{
				this.lastDistance = this.getDistanceToActiveNode();
				this.ticksStuck = 0;
				this.activeNode = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());
				if (this.activeNode.action != PathAction.NONE)
				{
					this.nodeActionFinished = false;
				}
			}
		}

		if (this.nodeActionFinished)
		{
			if (!this.isPositionClearFrom(this.theEntity.getPosition(), new BlockPos(this.activeNode.pos), this.theEntity))
			{
				if (this.theEntity.onPathBlocked(this.path, this))
				{
					this.setDoingTaskAndHoldOnPoint();
				}

			}

			if (!this.haltMovement)
			{
				if ((this.pathEndEntity != null) && (this.pathEndEntity.posY - this.theEntity.posY <= 0.0D) && (this.theEntity.getDistanceSq(this.pathEndEntity.posX, this.pathEndEntity.getEntityBoundingBox().minY, this.pathEndEntity.posZ) < 4.5D))
					this.theEntity.getMoveHelper().setMoveTo(this.pathEndEntity.posX, this.pathEndEntity.getEntityBoundingBox().minY, this.pathEndEntity.posZ, this.moveSpeed);
				else
				{
					this.theEntity.getMoveHelper().setMoveTo(this.activeNode.pos.x + this.entityCentre.x, this.activeNode.pos.y + this.entityCentre.y, this.activeNode.pos.z + this.entityCentre.z, this.moveSpeed);
				}
			}
			else
			{
				this.haltMovement = false;
			}

		}
		else if (!this.handlePathAction())
		{
			this.clearPath();
		}
	}

	//DarthXenon
	protected void doMoveAlongPath()
	{
		//Copied from net.minecraft.pathfinding.PathNavigate
		if (!this.noPath())
		{
			if (this.path != null && this.path.getCurrentPathIndex() < this.path.getCurrentPathLength())
			{

				//If entity is at the target path point, set the target path point to the next one on the path.
				Vec3d vec0 = this.getEntityPosition();
				Vec3d vec1 = this.path.getPositionAtIndex(this.theEntity, this.path.getCurrentPathIndex());
				if (vec0.y > vec1.y && this.theEntity.onGround &&
					MathHelper.floor(vec0.x) == MathHelper.floor(vec1.x) &&
					MathHelper.floor(vec0.z) == MathHelper.floor(vec1.z))
				{
					this.path.incrementPathIndex();
					//this.activeNode = this.path.points[this.path.getCurrentPathIndex()];
				}

				//If the entity is not at the end of the path, move along it.
				if (!this.noPath())
				{
					Vec3d vec2 = this.path.getPositionAtIndex(this.theEntity, this.path.getCurrentPathIndex());
					if (vec2 != null)
					{
						BlockPos pos = (new BlockPos(vec2)).down();
						AxisAlignedBB axisalignedbb = this.theEntity.world.getBlockState(pos).getBoundingBox(this.theEntity.world, pos);
						vec2 = vec2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
						this.theEntity.getMoveHelper().setMoveTo(vec2.x, vec2.y, vec2.z, this.moveSpeed);
					}
				}

				//Old code
				/*ModLogger.logInfo("Moving " + this.theEntity.getName() + " from " + this.theEntity.getPosition() + " to " + this.activeNode.getPos());
				this.theEntity.setVelocity(this.activeNode.xCoord - this.theEntity.posX, 0/*this.activeNode.yCoord - this.theEntity.posY*///, this.activeNode.zCoord - this.theEntity.posZ);
				/*this.path.setCurrentPathIndex(this.path.getCurrentPathIndex()+1);
				if(!this.path.isFinished()){
					this.activeNode = this.path.points[this.path.getCurrentPathIndex()];
				} else {
					this.path = null;
				}*/
			}
		}
	}

	@Override
	public void notifyTask(int result)
	{
		this.waitingForNotify = false;
		this.lastActionResult = result;
	}

	@Override
	public int getLastActionResult()
	{
		return this.lastActionResult;
	}

	@Override
	public boolean noPath()
	{
		return (this.path == null) || (this.path.isFinished());
	}

	@Override
	public int getStuckTime()
	{
		return this.ticksStuck;
	}

	@Override
	public float getLastPathDistanceToTarget()
	{
		if (this.noPath())
		{
			if ((this.path != null) && (this.path.getIntendedTarget() != null))
			{
				PathNode node = this.path.getIntendedTarget();
				return (float)this.theEntity.getDistance(node.pos.x, node.pos.y, node.pos.z);
			}
			return 0.0F;
		}

		return this.path.getFinalPathPoint().distanceTo(this.path.getIntendedTarget());
	}

	@Override
	public void clearPath()
	{
		this.path = null;
		this.autoPathToEntity = false;
		this.resetStatus();
	}

	@Override
	public void haltForTick()
	{
		this.haltMovement = true;
	}

	@Override
	public String getStatus()
	{
		String s = "";
		if (this.autoPathToEntity) s += "Auto:";
		if (this.noPath())
		{
			s += "NoPath:";
			return s;
		}
		s += "Pathing:";
		s += "Node[" + this.path.getCurrentPathIndex() + "/" + this.path.getCurrentPathLength() + "]:";
		if ((!this.nodeActionFinished) && (this.activeNode != null))
		{
			s += "Action[" + this.activeNode.action + "]:";
		}
		return s;
	}

	protected Path createPath(EntityIMLiving entity, Entity target, float targetRadius)
	{
		//return createPath(entity, MathHelper.floor(target.posX), (int) target.posY, MathHelper.floor(target.posZ), targetRadius);
		return this.createPath(entity, new Vec3d(target.posX, target.posY, target.posZ), targetRadius);
	}

	/*protected Path createPath(EntityIMLiving entity, int x, int y, int z, float targetRadius) {
		return this.createPath(entity, new BlockPos(x, y, z), targetRadius);
	}*/

	protected Path createPath(EntityIMLiving entity, Vec3d vec, float targetRadius)
	{
		this.theEntity.setCurrentTargetPos(new BlockPos(vec));
		//IBlockAccess terrainCache = this.getChunkCache(entity.getPosition(), pos, 16.0F);
		IBlockAccess terrainCache = entity.world != null ? entity.world : this.getChunkCache(entity.getPosition(), new BlockPos(vec), 16.0F);
		Nexus nexus = entity.getNexus();
		if (nexus != null) terrainCache = nexus.getAttackerAI().wrapEntityData(terrainCache);
		float maxSearchRange = 12.0F + (float)Distance.distanceBetween(entity.getPosition(), vec);
		if (this.pathSource.canPathfindNice(IPathSource.PathPriority.HIGH, maxSearchRange, this.pathSource.getSearchDepth(), this.pathSource.getQuickFailDepth()))
		{
			return this.pathSource.createPath(entity, vec, targetRadius, maxSearchRange, terrainCache);
		}
		return null;
	}

	protected void pathFollow()
	{
		Vec3d vec3d = this.getEntityPosition();
		int maxNextLegIndex = this.path.getCurrentPathIndex() - 1;

		PathNode nextPoint = this.path.getPathPointFromIndex(this.path.getCurrentPathIndex());
		if ((nextPoint.pos.y == vec3d.y) && (maxNextLegIndex < this.path.getCurrentPathLength() - 1))
		{
			maxNextLegIndex++;

			boolean canConsolidate = true;
			int prevIndex = maxNextLegIndex - 2;
			if ((prevIndex >= 0) && (this.path.getPathPointFromIndex(prevIndex).action != PathAction.NONE))
			{
				canConsolidate = false;
			}
			if ((canConsolidate) && (this.theEntity.canStandAt(this.theEntity.world, this.theEntity.getPosition())))
			{//MathHelper.floor(this.theEntity.posX), MathHelper.floor(this.theEntity.posY), MathHelper.floor(this.theEntity.posZ)))) {
				while ((maxNextLegIndex < this.path.getCurrentPathLength() - 1) && (this.path.getPathPointFromIndex(maxNextLegIndex).pos.y == vec3d.y) && (this.path.getPathPointFromIndex(maxNextLegIndex).action == PathAction.NONE))
				{
					maxNextLegIndex++;
				}
			}

		}

		float fa = this.theEntity.width * 0.5F;
		fa *= fa;
		for (int j = this.path.getCurrentPathIndex(); j <= maxNextLegIndex; j++)
		{
			if (vec3d.squareDistanceTo(this.path.getPositionAtIndex(this.theEntity, j)) < fa)
			{
				this.path.setCurrentPathIndex(j + 1);
			}
		}

		int xSize = (int)Math.ceil(this.theEntity.width);
		int ySize = (int)this.theEntity.height + 1;
		int zSize = xSize;
		int index = maxNextLegIndex;

		while (index > this.path.getCurrentPathIndex())
		{
			if (this.isDirectPathBetweenPoints(vec3d, this.path.getPositionAtIndex(this.theEntity, index), xSize, ySize, zSize))
			{
				break;
			}
			index--;
		}

		for (int i = this.path.getCurrentPathIndex() + 1; i < index; i++)
		{
			if (this.path.getPathPointFromIndex(i).action != PathAction.NONE)
			{
				index = i;
				break;
			}

		}

		if (this.path.getCurrentPathIndex() < index)
			this.path.setCurrentPathIndex(index);
	}

	protected void noPathFollow()
	{
	}

	protected void updateAutoPathToEntity()
	{
		if (this.pathEndEntity == null)
			return;
		boolean wantsUpdate;
		if (this.noPath())
		{
			wantsUpdate = true;
		}
		else
		{
			double d1 = Distance.distanceBetween(this.pathEndEntity, this.pathEndEntityLastPos);
			double d2 = 6.0D + Distance.distanceBetween((Entity)this.theEntity, this.pathEndEntityLastPos);
			if (d1 / d2 > 0.1D)
				wantsUpdate = true;
			else
			{
				wantsUpdate = false;
			}
		}
		if (wantsUpdate)
		{
			Path newPath = this.getPathToEntity(this.pathEndEntity, 0.0F);
			if (newPath != null)
			{
				if (this.setPath(newPath, this.moveSpeed))
				{
					this.pathEndEntityLastPos = new Vec3d(this.pathEndEntity.posX, this.pathEndEntity.posY, this.pathEndEntity.posZ);
				}
			}
		}
	}

	protected double getDistanceToActiveNode()
	{
		if (this.activeNode != null)
		{
			double dX = this.activeNode.pos.x + 0.5D - this.theEntity.posX;
			double dY = this.activeNode.pos.y - this.theEntity.posY;
			double dZ = this.activeNode.pos.z + 0.5D - this.theEntity.posZ;
			return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
		}
		return 0.0D;
	}

	/**
	 * Handles any actions that should be executed while moving along the path, such as building bridges in the case of the Pig Engineer.
	 * @return {@code false} if you want the entity to stop moving along the path
	 */
	protected boolean handlePathAction()
	{
		this.nodeActionFinished = true;
		return true;
	}

	protected boolean setDoingTask()
	{
		this.waitingForNotify = true;
		this.actionCleared = false;
		return true;
	}

	protected boolean setDoingTaskAndHold()
	{
		this.waitingForNotify = true;
		this.actionCleared = false;
		this.setMaintainPosOnWait(new Vec3d(this.theEntity.posX, this.theEntity.posY, this.theEntity.posZ));
		this.theEntity.setIsHoldingIntoLadder(true);
		return true;
	}

	protected boolean setDoingTaskAndHoldOnPoint()
	{
		this.waitingForNotify = true;
		this.actionCleared = false;
		this.setMaintainPosOnWait(this.activeNode.pos.addVector(0.5d, 0d, 0.5d));//new Vec3d(this.activeNode.getXCoord() + 0.5D, this.activeNode.getYCoord(), this.activeNode.getZCoord() + 0.5D));
		this.theEntity.setIsHoldingIntoLadder(true);
		return true;
	}

	protected void resetStatus()
	{
		this.setNoMaintainPos();
		this.theEntity.setIsHoldingIntoLadder(false);
		this.nodeActionFinished = true;
		this.actionCleared = true;
		this.waitingForNotify = false;
	}

	protected Vec3d getEntityPosition()
	{
		return new Vec3d(this.theEntity.posX, this.getPathableYPos(), this.theEntity.posZ);
	}

	public EntityIMLiving getEntity()
	{
		return this.theEntity;
	}

	private int getPathableYPos()
	{
		if ((!this.theEntity.isInWater()) || (!this.canSwim))
		{
			return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D);
		}

		int i = (int)this.theEntity.getEntityBoundingBox().minY;
		Block block = this.theEntity.world.getBlockState(new BlockPos(MathHelper.floor(this.theEntity.posX), i, MathHelper.floor(this.theEntity.posZ))).getBlock();
		int k = 0;

		while ((block == Blocks.WATER) || (block == Blocks.FLOWING_WATER))
		{
			i++;
			block = this.theEntity.world.getBlockState(new BlockPos(MathHelper.floor(this.theEntity.posX), i, MathHelper.floor(this.theEntity.posZ))).getBlock();

			k++;
			if (k > 16)
			{
				return (int)this.theEntity.getEntityBoundingBox().minY;
			}
		}

		return i;
	}

	protected boolean canNavigate()
	{
		return true;
	}

	protected boolean isInLiquid()
	{
		return (this.theEntity.isInWater()) || (this.theEntity.isInLava()); //(this.theEntity.handleLavaMovement());
	}

	protected Vec3d findValidPointNear(double x, double z, int min, int max, int verticalRange)
	{
		double xOffset = x - this.theEntity.posX;
		double zOffset = z - this.theEntity.posZ;
		double h = Math.sqrt(xOffset * xOffset + zOffset * zOffset);

		if (h < 0.5D)
		{
			return null;
		}

		double distance = min + this.theEntity.getRNG().nextInt(max - min);
		int xi = MathHelper.floor(xOffset * (distance / h) + this.theEntity.posX);
		int zi = MathHelper.floor(zOffset * (distance / h) + this.theEntity.posZ);
		int y = MathHelper.floor(this.theEntity.posY);

		Path entityPath = null;
		for (int vertical = 0; vertical < verticalRange; vertical = vertical > 0 ? vertical * -1 : vertical * -1 + 1)
		{
			for (int i = -1; i <= 1; i++)
			{
				for (int j = -1; j <= 1; j++)
				{
					if (this.theEntity.canStandAtAndIsValid(this.theEntity.world, new BlockPos(xi + i, y + vertical, zi + j)))
					{
						return new Vec3d(xi + i, y + vertical, zi + j);
					}
				}
			}
		}

		return null;
	}

	protected void removeSunnyPath()
	{
		if (this.theEntity.world.canBlockSeeSky(new BlockPos(MathHelper.floor(this.theEntity.posX), (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.theEntity.posZ))))
		{
			return;
		}

		for (int i = 0; i < this.path.getCurrentPathLength(); i++)
		{
			PathNode pathpoint = this.path.getPathPointFromIndex(i);

			if (this.theEntity.world.canBlockSeeSky(new BlockPos(pathpoint.pos)))
			{
				this.path.setCurrentPathLength(i - 1);
				return;
			}
		}
	}

	protected boolean isDirectPathBetweenPoints(Vec3d pos1, Vec3d pos2, int xSize, int ySize, int zSize)
	{
		int x = MathHelper.floor(pos1.x);
		int z = MathHelper.floor(pos1.z);
		double dX = pos2.x - pos1.x;
		double dZ = pos2.z - pos1.z;
		double dXZsq = dX * dX + dZ * dZ;

		if (dXZsq < 1.0E-008D)
		{
			return false;
		}

		double scale = 1.0D / Math.sqrt(dXZsq);
		dX *= scale;
		dZ *= scale;
		xSize += 2;
		zSize += 2;

		if (!this.isSafeToStandAt(x, (int)pos1.y, z, xSize, ySize, zSize, pos1, dX, dZ))
		{
			return false;
		}

		xSize -= 2;
		zSize -= 2;
		double xIncrement = 1.0D / Math.abs(dX);
		double zIncrement = 1.0D / Math.abs(dZ);
		double xOffset = x * 1 - pos1.x;
		double zOffset = z * 1 - pos1.z;

		if (dX >= 0.0D)
		{
			xOffset += 1.0D;
		}

		if (dZ >= 0.0D)
		{
			zOffset += 1.0D;
		}

		xOffset /= dX;
		zOffset /= dZ;
		byte xDirection = (byte)(dX >= 0.0D ? 1 : -1);
		byte zDirection = (byte)(dZ >= 0.0D ? 1 : -1);
		int x2 = MathHelper.floor(pos2.x);
		int z2 = MathHelper.floor(pos2.z);
		int xDiff = x2 - x;

		for (int i = z2 - z; (xDiff * xDirection > 0) || (i * zDirection > 0);)
		{
			if (xOffset < zOffset)
			{
				xOffset += xIncrement;
				x += xDirection;
				xDiff = x2 - x;
			}
			else
			{
				zOffset += zIncrement;
				z += zDirection;
				i = z2 - z;
			}

			if (!this.isSafeToStandAt(x, (int)pos1.y, z, xSize, ySize, zSize, pos1, dX, dZ))
			{
				return false;
			}
		}

		return true;
	}

	protected boolean isSafeToStandAt(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, Vec3d entityPostion, double par8, double par10)
	{
		int i = xOffset - xSize / 2;
		int j = zOffset - zSize / 2;

		if (!this.isPositionClear(i, yOffset, j, xSize, ySize, zSize, entityPostion, par8, par10))
		{
			return false;
		}

		for (int k = i; k < i + xSize; k++)
		{
			for (int l = j; l < j + zSize; l++)
			{
				double d = k + 0.5D - entityPostion.x;
				double d1 = l + 0.5D - entityPostion.z;

				if (d * par8 + d1 * par10 >= 0.0D)
				{
					IBlockState blockState = this.theEntity.world.getBlockState(new BlockPos(k, yOffset - 1, l));

					if (blockState.getBlock() == Blocks.AIR)
					{
						return false;
					}

					Material material = blockState.getMaterial();

					if ((material == Material.WATER) && (!this.theEntity.isInWater())) return false;
					if (material == Material.CACTUS) return false;
					if (material == Material.LAVA) return false;
					if (!material.isSolid()) return false;
				}
			}
		}
		return true;
	}

	protected boolean isPositionClear(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, Vec3d entityPostion, double vecX, double vecZ)
	{
		for (int i = xOffset; i < xOffset + xSize; i++)
		{
			for (int j = yOffset; j < yOffset + ySize; j++)
			{
				for (int k = zOffset; k < zOffset + zSize; k++)
				{
					double d = i + 0.5D - entityPostion.x;
					double d1 = k + 0.5D - entityPostion.z;

					if (d * vecX + d1 * vecZ >= 0.0D)
					{
						Block block = this.theEntity.world.getBlockState(new BlockPos(i, j, k)).getBlock();

						if ((block != Blocks.AIR) && (!block.isPassable(this.theEntity.world, new BlockPos(i, j, k))))
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	protected boolean isPositionClearFrom(BlockPos pos0, BlockPos pos1, EntityIMLiving entity)
	{
		return this.isPositionClearFrom(pos0.getX(), pos0.getY(), pos0.getZ(), pos1.getX(), pos1.getY(), pos1.getZ(), entity);
	}

	protected boolean isPositionClearFrom(int x1, int y1, int z1, int x2, int y2, int z2, EntityIMLiving entity)
	{
		if (y2 > y1)
		{
			Block block = this.theEntity.world.getBlockState(new BlockPos(x1, y1 + entity.getCollideSize().getY(), z1)).getBlock();
			if ((block != Blocks.AIR) && (!block.isPassable(this.theEntity.world, new BlockPos(x1, y1 + entity.getCollideSize().getY(), z1))))
			{
				return false;
			}
		}

		return this.isPositionClear(x2, y2, z2, entity);
	}

	protected boolean isPositionClear(int x, int y, int z, EntityIMLiving entity)
	{
		BlockPos size = entity.getCollideSize();
		return this.isPositionClear(x, y, z, size.getX(), size.getY(), size.getZ());
	}

	protected boolean isPositionClear(BlockPos pos, EntityIMLiving entity)
	{
		return this.isPositionClear(pos.getX(), pos.getY(), pos.getZ(), entity);
	}

	protected boolean isPositionClear(int x, int y, int z, int xSize, int ySize, int zSize)
	{
		for (int i = x; i < x + xSize; i++)
		{
			for (int j = y; j < y + ySize; j++)
			{
				for (int k = z; k < z + zSize; k++)
				{
					Block block = this.theEntity.world.getBlockState(new BlockPos(i, j, k)).getBlock();

					if ((block != Blocks.AIR) && (!block.isPassable(this.theEntity.world, new BlockPos(i, j, k))))
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	protected ChunkCache getChunkCache(BlockPos pos0, BlockPos pos1, float axisExpand)
	{
		return this.getChunkCache(pos0.getX(), pos0.getY(), pos0.getZ(), pos1.getX(), pos1.getY(), pos1.getZ(), axisExpand);
	}

	protected ChunkCache getChunkCache(int x1, int y1, int z1, int x2, int y2, int z2, float axisExpand)
	{
		int d = (int)axisExpand;
		int cX2;
		int cX1;
		if (x1 < x2)
		{
			cX1 = x1 - d;
			cX2 = x2 + d;
		}
		else
		{
			cX2 = x1 + d;
			cX1 = x2 - d;
		}
		int cY2;
		int cY1;
		if (y1 < y2)
		{
			cY1 = y1 - d;
			cY2 = y2 + d;
		}
		else
		{
			cY2 = y1 + d;
			cY1 = y2 - d;
		}
		int cZ2;
		int cZ1;
		if (z1 < z2)
		{
			cZ1 = z1 - d;
			cZ2 = z2 + d;
		}
		else
		{
			cZ2 = z1 + d;
			cZ1 = z2 - d;
		}

		BlockPos blockPos1 = new BlockPos(cX1, cY1, cZ1);
		BlockPos blockPos2 = new BlockPos(cX1, cY2, cZ2);
		return new ChunkCache(this.theEntity.world, blockPos1, blockPos2, 0);
	}
}
