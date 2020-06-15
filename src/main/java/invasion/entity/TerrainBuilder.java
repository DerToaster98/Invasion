package invasion.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import invasion.INotifyTask;
import invasion.util.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;


public class TerrainBuilder implements ITerrainBuild
{

	public static final float LADDER_COST = 25.0F;
	public static final float PLANKS_COST = 45.0F;
	public static final float COBBLE_COST = 65.0F;

	private final ICanBuild theEntity;
	private ITerrainModify modifier;
	private float buildRate;
	private float ladderCost;
	private float blockCost;
	private Block block;

	public TerrainBuilder(ICanBuild entity, ITerrainModify modifier, float buildRate)
	{
		this(entity, modifier, buildRate, LADDER_COST, PLANKS_COST, Blocks.PLANKS);
	}

	public TerrainBuilder(ICanBuild entity, ITerrainModify modifier, float buildRate, float ladderCost, float blockCost, Block block)
	{
		Validate.notNull(entity, "Entity cannot be null");
		Validate.notNull(entity.getEntity(), "Entity must be a subclass of EntityIMLiving");
		this.theEntity = entity;
		this.modifier = modifier;
		this.buildRate = buildRate;
		this.ladderCost = ladderCost;
		this.blockCost = blockCost;
		this.block = block;
	}

	public void setBuildRate(float buildRate)
	{
		this.buildRate = buildRate;
	}

	public float getBuildRate()
	{
		return this.buildRate;
	}

	@Override
	public boolean askBuildScaffoldLayer(BlockPos pos, INotifyTask asker)
	{
		World world = this.theEntity.getEntity().world;
		if (world == null) return false;

		if (this.modifier.isReadyForTask(asker))
		{
			Scaffold scaffold = this.theEntity.getEntity().getNexus().getAttackerAI().getScaffoldAt(pos);
			if (scaffold != null)
			{
				int height = pos.getY() - MathHelper.floor(scaffold.getPos().y);
				int xOffset = Coords.offsetAdjX[scaffold.getOrientation()];
				int zOffset = Coords.offsetAdjZ[scaffold.getOrientation()];
				//Block block = this.theEntity.world.getBlockState(new BlockPos(pos.getXCoord() + xOffset, pos.getYCoord() - 1, pos.getZCoord() + zOffset)).getBlock();
				IBlockState blockState = world.getBlockState(pos.add(xOffset, -1, zOffset));
				List modList = new ArrayList();

				if (height == 1)
				{
					if (!blockState.isNormalCube()/*!block.isNormalCube()*/)
					{
						modList.add(new ModifyBlockEntry(pos.add(xOffset, -1, zOffset), this.block, (int)(this.blockCost / this.buildRate)));
					}
					blockState = world.getBlockState(pos.down());
					if (blockState.getBlock() == Blocks.AIR)
					{
						modList.add(new ModifyBlockEntry(pos.down(), Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
					}
				}
				blockState = world.getBlockState(pos.add(xOffset, 0, zOffset));
				if (!blockState.isNormalCube())
				{
					modList.add(new ModifyBlockEntry(pos.add(xOffset, 0, zOffset), this.block, (int)(this.blockCost / this.buildRate)));
				}
				blockState = world.getBlockState(pos);
				if (blockState.getBlock() != Blocks.LADDER)
				{
					modList.add(new ModifyBlockEntry(pos, Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
				}

				if (scaffold.isLayerPlatform(height))
				{
					for (int i = 0; i < 8; i++)
					{
						if ((Coords.offsetRing1X[i] != xOffset) || (Coords.offsetRing1Z[i] != zOffset))
						{
							blockState = world.getBlockState(pos.add(Coords.offsetRing1X[i], 0, Coords.offsetRing1Z[i]));
							if (!blockState.isNormalCube())
								modList.add(new ModifyBlockEntry(pos.add(Coords.offsetRing1X[i], 0, Coords.offsetRing1Z[i]),
									this.block, (int)(this.blockCost / this.buildRate)));
						}
					}
				}
				if (modList.size() > 0) return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
			}
		}
		return false;
	}

	@Override
	public boolean askBuildLadderTower(BlockPos pos, int orientation, int layersToBuild, INotifyTask asker)
	{
		World world = this.theEntity.getEntity().world;
		if (world == null) return false;
		if (this.modifier.isReadyForTask(asker))
		{
			int xOffset = orientation == 1 ? -1 : orientation == 0 ? 1 : 0;
			int zOffset = orientation == 3 ? -1 : orientation == 2 ? 1 : 0;
			List modList = new ArrayList();

			IBlockState blockState = world.getBlockState(pos.add(xOffset, -1, zOffset));
			if (!blockState.isNormalCube())
			{
				modList.add(new ModifyBlockEntry(pos.add(xOffset, -1, zOffset), this.block, (int)(this.blockCost / this.buildRate)));
			}
			blockState = world.getBlockState(pos.down());
			if (blockState.getBlock() == Blocks.AIR)
			{
				modList.add(new ModifyBlockEntry(pos.down(), Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
			}
			for (int i = 0; i < layersToBuild; i++)
			{
				blockState = world.getBlockState(pos.add(xOffset, i, zOffset));
				if (!blockState.isNormalCube())
				{
					modList.add(new ModifyBlockEntry(pos.add(xOffset, i, zOffset),
						this.block, (int)(this.blockCost / this.buildRate)));
				}
				blockState = world.getBlockState(pos.up(i));
				if (blockState != Blocks.LADDER)
				{
					modList.add(new ModifyBlockEntry(pos.up(i), Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
				}
			}
			if (modList.size() > 0)
				return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(
					new ModifyBlockEntry[modList.size()]), asker, null);
		}
		return false;
	}

	@Override
	public boolean askBuildLadder(BlockPos pos, INotifyTask asker)
	{
		World world = this.theEntity.getEntity().world;
		if (world == null) return false;
		if (this.modifier.isReadyForTask(asker))
		{
			List<ModifyBlockEntry> modList = new ArrayList<>();
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() != Blocks.LADDER)
			{
				if (this.theEntity.canPlaceLadderAt(pos))
				{
					modList.add(new ModifyBlockEntry(pos, Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
				}
				else
				{
					return false;
				}
			}

			blockState = world.getBlockState(pos.down(2));
			if ((blockState.getBlock() != Blocks.AIR) && (blockState.getMaterial().isSolid()))
			{
				if (this.theEntity.canPlaceLadderAt(pos.down()))
				{
					modList.add(new ModifyBlockEntry(pos.down(), Blocks.LADDER, (int)(this.ladderCost / this.buildRate)));
				}
			}
			if (modList.size() > 0)
				return this.modifier.requestTask(modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
		}
		return false;
	}

	@Override
	public boolean askBuildBridge(BlockPos pos, INotifyTask asker)
	{
		World world = this.theEntity.getEntity().world;
		if (world == null) return false;
		if (this.modifier.isReadyForTask(asker))
		{
			List<ModifyBlockEntry> modList = new ArrayList<>();
			if (world.getBlockState(pos.down()).getBlock() == Blocks.AIR)
			{
				/*if ((this.theEntity.getEntity().avoidsBlock(world.getBlockState(pos.down(2)).getBlock()))
						|| (this.theEntity.getEntity().avoidsBlock(world.getBlockState(pos.down(3)).getBlock()))) {
					modList.add(new ModifyBlockEntry(pos.down(), Blocks.COBBLESTONE, (int) (65.0F / this.buildRate)));
				} else {*/
				modList.add(new ModifyBlockEntry(pos.down(), this.block, (int)(this.blockCost / this.buildRate)));
				//}

				if (modList.size() > 0)
					return this.modifier.requestTask(modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
			}
		}
		return false;
	}
}