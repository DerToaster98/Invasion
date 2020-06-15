package invasion.entity.ai.navigator;

import invasion.IBlockAccessExtended;
import invasion.entity.EntityIMLiving;
import invasion.entity.IPathSource;
import invasion.entity.monster.PigEngyEntity;
import invasion.nexus.Nexus;
import invasion.util.Distance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;


public class NavigatorEngy extends NavigatorIM
{

	private final PigEngyEntity pigEntity;

	public NavigatorEngy(PigEngyEntity entity, IPathSource pathSource)
	{
		super(entity, pathSource);
		this.pigEntity = entity;
		this.setNoMaintainPos();
	}

	@Override
	protected Path createPath(EntityIMLiving entity, Vec3d vec, float targetRadius)
	{
		IBlockAccess terrainCache = this.getChunkCache(entity.getPosition(), new BlockPos(vec), 16.0F);
		Nexus nexus = this.pigEntity.getNexus();
		if (nexus != null)
		{
			IBlockAccessExtended terrainCacheExt = nexus.getAttackerAI().wrapEntityData(terrainCache);

			nexus.getAttackerAI().addScaffoldDataTo(terrainCacheExt);
			terrainCache = terrainCacheExt;
		}
		float maxSearchRange = 12.0F + (float)Distance.distanceBetween(entity.getPosition(), vec);
		if (this.pathSource.canPathfindNice(IPathSource.PathPriority.HIGH, maxSearchRange, this.pathSource.getSearchDepth(), this.pathSource.getQuickFailDepth()))
		{
			return this.pathSource.createPath(entity, vec, targetRadius, maxSearchRange, terrainCache);
		}
		return null;
	}

	@Override
	protected boolean handlePathAction()
	{
		if (!this.actionCleared)
		{
			this.resetStatus();
			if (this.getLastActionResult() != 0) return false;
			return true;
		}

		if ((this.activeNode.action == PathAction.LADDER_UP_PX) || (this.activeNode.action == PathAction.LADDER_UP_NX) || (this.activeNode.action == PathAction.LADDER_UP_PZ) || (this.activeNode.action == PathAction.LADDER_UP_NZ))
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildLadder(this.activeNode.pos, this))
				return this.setDoingTaskAndHold();
		}
		else if (this.activeNode.action == PathAction.BRIDGE)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildBridge(this.activeNode.pos, this))
				return this.setDoingTaskAndHold();
		}
		else if (this.activeNode.action == PathAction.SCAFFOLD_UP)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildScaffoldLayer(this.activeNode.pos, this))
				return this.setDoingTaskAndHoldOnPoint();
		}
		else if (this.activeNode.action == PathAction.LADDER_TOWER_UP_PX)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildLadderTower(this.activeNode.pos, 0, 1, this))
				return this.setDoingTaskAndHold();
		}
		else if (this.activeNode.action == PathAction.LADDER_TOWER_UP_NX)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildLadderTower(this.activeNode.pos, 1, 1, this))
				return this.setDoingTaskAndHold();
		}
		else if (this.activeNode.action == PathAction.LADDER_TOWER_UP_PZ)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildLadderTower(this.activeNode.pos, 2, 1, this))
				return this.setDoingTaskAndHold();
		}
		else if (this.activeNode.action == PathAction.LADDER_TOWER_UP_NZ)
		{
			if (this.pigEntity.getTerrainBuildEngy().askBuildLadderTower(this.activeNode.pos, 3, 1, this))
			{
				return this.setDoingTaskAndHold();
			}
		}
		this.nodeActionFinished = true;
		return true;
	}

}