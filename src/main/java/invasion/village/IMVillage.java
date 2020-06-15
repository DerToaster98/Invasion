package invasion.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class IMVillage
{

	private World world;
	private final List<IMVillageDoorInfo> doors = new ArrayList<>();
	/** 
	 * This is the sum of all door coordinates and used to calculate the actual village center by dividing by the number of doors.
	 */
	private BlockPos centerHelper = BlockPos.ORIGIN;
	/** This is the actual village center. */
	private BlockPos center = BlockPos.ORIGIN;
	private int villageRadius;
	private int numVillagers;
	private int numSnowGolems = 0;
	private int numIronGolems = 0;
	private int lastAddDoorTimestamp;
	private int tickCounter;
	private BlockPos nexusPos;

	public IMVillage()
	{
	}

	public IMVillage(World worldIn)
	{
		this.world = worldIn;
	}

	public IMVillage(World worldIn, BlockPos nexusPos)
	{
		this(worldIn);
		this.nexusPos = nexusPos;
	}

	public void setWorld(World worldIn)
	{
		this.world = worldIn;
	}

	public void setNexus(World worldIn, BlockPos nexusPos)
	{
		this.world = worldIn;
		this.nexusPos = nexusPos;
	}

	protected void tick(int tickCount)
	{
		this.tickCounter = tickCount;
	}

	protected void updateNumVillagers()
	{

	}

	protected void updateNumGolems()
	{

	}

	private Vec3d findRandomSpawnPos(BlockPos pos, int x, int y, int z)
	{
		for (int i = 0; i < 10; i++)
		{
			BlockPos blockpos = pos.add(this.world.rand.nextInt(16) - 8, this.world.rand.nextInt(6) - 3, this.world.rand.nextInt(16) - 8);

			if (this.isBlockPosWithinSqVillageRadius(blockpos) && this.isAreaClearAround(new BlockPos(x, y, z), blockpos))
			{
				return new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
			}
		}

		return null;
	}

	public boolean isBlockPosWithinSqVillageRadius(BlockPos pos)
	{
		return this.center.distanceSq(pos) < (double)(this.villageRadius * this.villageRadius);
	}

	public boolean isAreaClearAround(BlockPos blockSize, BlockPos blockLocation)
	{
		if (!this.world.getBlockState(blockLocation.down()).isSideSolid(this.world, blockLocation.down(), EnumFacing.UP))
		{
			return false;
		}
		else
		{
			int i = blockLocation.getX() - blockSize.getX() / 2;
			int j = blockLocation.getZ() - blockSize.getZ() / 2;

			for (int k = i; k < i + blockSize.getX(); ++k)
			{
				for (int l = blockLocation.getY(); l < blockLocation.getY() + blockSize.getY(); ++l)
				{
					for (int i1 = j; i1 < j + blockSize.getZ(); ++i1)
					{
						if (this.world.getBlockState(new BlockPos(k, l, i1)).isNormalCube())
						{
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	protected void updateVillageRadiusAndCenter()
	{
		int i = this.doors.size();

		if (i == 0)
		{
			this.center = BlockPos.ORIGIN;
			this.villageRadius = 0;
		}
		else
		{
			this.center = new BlockPos(this.centerHelper.getX() / i, this.centerHelper.getY() / i, this.centerHelper.getZ() / i);
			int j = 0;

			for (IMVillageDoorInfo villagedoorinfo : this.doors)
			{
				j = Math.max(villagedoorinfo.getDistanceToDoorBlockSq(this.center), j);
			}

			this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
		}
	}

	public void addDoor(IMVillageDoorInfo door)
	{
		this.doors.add(door);
		this.centerHelper = this.centerHelper.add(door.getDoorBlockPos());
		this.updateVillageRadiusAndCenter();
		this.lastAddDoorTimestamp = door.getInsidePosY();
	}

	public void removeDeadAndOutOfRangeDoors()
	{
		boolean flag0 = false;
		boolean flag1 = this.world.rand.nextInt(50) == 0;
		Iterator<IMVillageDoorInfo> i = this.doors.iterator();

		while (i.hasNext())
		{
			IMVillageDoorInfo door = i.next();
			if (flag1) door.resetDoorOpeningRestrictionCounter();
			if (!this.isWoodDoor(door.getDoorBlockPos()) || Math.abs(this.tickCounter - door.getInsidePosY()) > 1200)
			{
				this.centerHelper = this.centerHelper.subtract(door.getDoorBlockPos());
				flag0 = true;
				door.setIsDetachedFromVillageFlag(true);
				i.remove();
			}
		}

		if (flag0) this.updateVillageRadiusAndCenter();
	}

	private boolean isWoodDoor(BlockPos pos)
	{
		IBlockState iblockstate = this.world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block instanceof BlockDoor ? iblockstate.getMaterial() == Material.WOOD : false;
	}

	public boolean isAnnihilated()
	{
		return this.doors.isEmpty();
	}

	/**
	 * Read this village's data from NBT.
	 */
	public void readVillageDataFromNBT(NBTTagCompound tag)
	{
		this.numVillagers = tag.getInteger("PopSize");
		this.villageRadius = tag.getInteger("Radius");
		this.numIronGolems = tag.getInteger("Golems");
		this.lastAddDoorTimestamp = tag.getInteger("Stable");
		this.tickCounter = tag.getInteger("Tick");
		this.center = new BlockPos(tag.getInteger("CX"), tag.getInteger("CY"), tag.getInteger("CZ"));
		this.centerHelper = new BlockPos(tag.getInteger("ACX"), tag.getInteger("ACY"), tag.getInteger("ACZ"));
		this.nexusPos = new BlockPos(tag.getInteger("NX"), tag.getInteger("NY"), tag.getInteger("NZ"));

		NBTTagList doorList = tag.getTagList("Doors", 10);

		for (int i = 0; i < doorList.tagCount(); i++)
		{
			NBTTagCompound doorTag = doorList.getCompoundTagAt(i);
			IMVillageDoorInfo door = new IMVillageDoorInfo(new BlockPos(doorTag.getInteger("X"), doorTag.getInteger("Y"), doorTag.getInteger("Z")), doorTag.getInteger("IDX"), doorTag.getInteger("IDZ"), doorTag.getInteger("TS"));
			this.doors.add(door);
		}


	}

	/**
	 * Write this village's data to NBT.
	 */
	public void writeVillageDataToNBT(NBTTagCompound tag)
	{
		tag.setInteger("PopSize", this.numVillagers);
		tag.setInteger("Radius", this.villageRadius);
		tag.setInteger("IronGolems", this.numIronGolems);
		tag.setInteger("SnowGolems", this.numSnowGolems);
		tag.setInteger("Stable", this.lastAddDoorTimestamp);
		tag.setInteger("Tick", this.tickCounter);
		tag.setInteger("CX", this.center.getX());
		tag.setInteger("CY", this.center.getY());
		tag.setInteger("CZ", this.center.getZ());
		tag.setInteger("ACX", this.centerHelper.getX());
		tag.setInteger("ACY", this.centerHelper.getY());
		tag.setInteger("ACZ", this.centerHelper.getZ());
		tag.setInteger("NX", this.nexusPos.getX());
		tag.setInteger("NY", this.nexusPos.getY());
		tag.setInteger("NZ", this.nexusPos.getZ());

		NBTTagList doorList = new NBTTagList();

		for (IMVillageDoorInfo door : this.doors)
		{
			NBTTagCompound doorTag = new NBTTagCompound();
			doorTag.setInteger("X", door.getDoorBlockPos().getX());
			doorTag.setInteger("Y", door.getDoorBlockPos().getY());
			doorTag.setInteger("Z", door.getDoorBlockPos().getZ());
			doorTag.setInteger("IDX", door.getInsideOffsetX());
			doorTag.setInteger("IDZ", door.getInsideOffsetZ());
			doorTag.setInteger("TS", door.getInsidePosY());
			doorList.appendTag(doorTag);
		}

		tag.setTag("Doors", doorList);
	}

	//---------------------------------------------------- Getter methods ----------------------------------------------------\\

	public BlockPos getCenter()
	{
		return this.center;
	}

	public int getVillageRadius()
	{
		return this.villageRadius;
	}

	public int getNumVillagers()
	{
		return this.numVillagers;
	}

	public int getNumSnowGolems()
	{
		return this.numSnowGolems;
	}

	public int getNumIronGolems()
	{
		return this.numIronGolems;
	}

	public List<IMVillageDoorInfo> getVillageDoorInfo()
	{
		return this.doors;
	}

	public IMVillageDoorInfo getNearestDoor(BlockPos pos)
	{
		IMVillageDoorInfo door = null;
		int i = Integer.MAX_VALUE;

		for (IMVillageDoorInfo door0 : this.doors)
		{
			int j = door0.getDistanceToDoorBlockSq(pos);
			if (j < i)
			{
				door = door0;
				i = j;
			}
		}
		return door;
	}

}
