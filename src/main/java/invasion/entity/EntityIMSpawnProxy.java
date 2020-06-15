package invasion.entity;

import invasion.Invasion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;


public class EntityIMSpawnProxy extends EntityLiving
{

	public EntityIMSpawnProxy(World world)
	{
		super(world);
	}

	@Override
	public void onEntityUpdate()
	{
		if (this.world != null)
		{
			Entity[] entities = Invasion.getNightMobSpawns1(this.world);
			for (Entity entity : entities)
			{
				entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				this.world.spawnEntity(entity);
			}
		}
		this.setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
	}

	public float getBlockPathWeight(int i, int j, int k)
	{
		return 0.5F - this.world.getLightBrightness(new BlockPos(i, j, k));
	}

	protected boolean darkEnoughToSpawn()
	{
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		if (this.world.getLightFor(EnumSkyBlock.SKY, new BlockPos(i, j, k)) > this.rand.nextInt(32))
		{
			return false;
		}
		int l = this.world.getBlockLightOpacity(new BlockPos(i, j, k));
		if (this.world.isThundering())
		{
			int i1 = this.world.getSkylightSubtracted();
			this.world.setSkylightSubtracted(10);
			l = this.world.getBlockLightOpacity(new BlockPos(i, j, k));
			this.world.setSkylightSubtracted(i1);
		}
		return l <= this.rand.nextInt(8);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		return (this.darkEnoughToSpawn()) && (super.getCanSpawnHere()) && (this.getBlockPathWeight(i, j, k) >= 0.0F);
	}
}