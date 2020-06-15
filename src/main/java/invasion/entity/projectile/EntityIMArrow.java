package invasion.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class EntityIMArrow extends EntityTippedArrow
{

	private int posX = -1;
	private int posY = -1;
	private int posZ = -1;
	private Block inBlock;
	private boolean inGround;
	/** 1 if the player can pick up the arrow */
	public int canBePickedUp;
	/** Seems to be some sort of timer for animating an arrow. */
	public int arrowShake;
	/** The owner of this arrow. */
	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 2.0D;
	/** The amount of knockback an arrow applies when it hits a mob. */
	private int knockbackStrength;

	public EntityIMArrow(World world)
	{
		super(world);
	}

	public EntityIMArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityIMArrow(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
	}

	/*public EntityIMArrow(World world, EntityLivingBase livingBase1, EntityLivingBase livingBase2, float value1, float value2) {
		super(world, livingBase1, livingBase2, value1, value2);
	}
	
	public EntityIMArrow(World world, EntityLivingBase livingBase, float speed) {
		super(world, livingBase, speed);
	}*/

	@Override
	public void onUpdate()
	{
		this.onEntityUpdate();

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / Math.PI);
		}

		IBlockState blockState = this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));

		if (blockState.getMaterial() != Material.AIR)
		{

			if (blockState.getBlock() == Blocks.GLASS)
			{
				this.world.setBlockToAir(this.getPosition());
			}
			else
			{
				//blockState.getBlock().setBlockBoundsBasedOnState(this.world, this.getPosition());
				//AxisAlignedBB axisalignedbb = blockState.getSelectedBoundingBox(this.world, this.getPosition());
				AxisAlignedBB axisalignedbb = blockState.getBlock().getBoundingBox(blockState, this.world, this.getPosition());
				if (axisalignedbb != null && axisalignedbb.contains(new Vec3d(this.posX, this.posY, this.posZ)))
				{
					this.inGround = true;
				}
			}

		}

		if (this.arrowShake > 0) --this.arrowShake;

		if (this.inGround)
		{
			if (blockState == this.inBlock)
			{
				++this.ticksInGround;
				if (this.ticksInGround == 1200) this.setDead();
			}
			else
			{
				this.inGround = false;
				this.motionX *= this.rand.nextFloat() * 0.2F;
				this.motionY *= this.rand.nextFloat() * 0.2F;
				this.motionZ *= this.rand.nextFloat() * 0.2F;
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		}
		else
		{
			++this.ticksInAir;
			Vec3d vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			RayTraceResult rtr = this.world.rayTraceBlocks(vec31, vec3, false, true, false);
			vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (rtr != null) vec3 = new Vec3d(rtr.hitVec.x, rtr.hitVec.y, rtr.hitVec.z);

			Entity entity = null;
			List list = this.world.getEntitiesWithinAABBExcludingEntity(
				this, this.getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

			double d0 = 0.0D;
			int i;
			float f1;

			for (i = 0; i < list.size(); ++i)
			{
				Entity entity1 = (Entity)list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
				{
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
					RayTraceResult rtr1 = axisalignedbb1.calculateIntercept(vec31, vec3);

					if (rtr1 != null)
					{
						double d1 = vec31.distanceTo(rtr1.hitVec);

						if (d1 < d0 || d0 == 0.0D)
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null) rtr = new RayTraceResult(entity);
			if (rtr != null && rtr.entityHit != null && rtr.entityHit instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer)rtr.entityHit;

				if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer
					&& !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
				{
					rtr = null;
				}
			}

			float f2;
			float f4;

			if (rtr != null)
			{
				if (rtr.entityHit != null)
				{
					f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					int k = MathHelper.ceil(f2 * this.damage);
					if (this.getIsCritical()) k += this.rand.nextInt(k / 2 + 2);

					DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity != null ? this.shootingEntity : this);

					if (this.isBurning() && !(rtr.entityHit instanceof EntityEnderman)) rtr.entityHit.setFire(5);

					if (rtr.entityHit.attackEntityFrom(damagesource, k))
					{
						if (rtr.entityHit instanceof EntityLivingBase)
						{
							EntityLivingBase entitylivingbase = (EntityLivingBase)rtr.entityHit;

							if (!this.world.isRemote) entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);

							if (this.knockbackStrength > 0)
							{
								f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

								if (f4 > 0.0F)
								{
									rtr.entityHit.addVelocity(
										this.motionX * this.knockbackStrength * 0.6000000238418579D / f4,
										0.1D,
										this.motionZ * this.knockbackStrength * 0.6000000238418579D / f4);
								}
							}

							//TODO DarthXenon: Unsure what these do
							/*if (this.shootingEntity != null) if(this.shootingEntity instanceof EntityLivingBase) {
								EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
								EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity, entitylivingbase);
							}
							
							if (this.shootingEntity != null && rtr.entityHit != this.shootingEntity && rtr.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new SPacketChangeGameState(6, 0.0F));
							}*/
						}

						//this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));

						if (!(rtr.entityHit instanceof EntityEnderman))
						{
							this.setDead();
						}
					}
					else
					{
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				}
				else
				{
					//this.posX = rtr.func_178782_a().getX();
					//this.posY = rtr.func_178782_a().getY();
					//this.posZ = rtr.func_178782_a().getZ();
					this.posX = rtr.getBlockPos().getX();
					this.posY = rtr.getBlockPos().getY();
					this.posZ = rtr.getBlockPos().getZ();
					this.inBlock = this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock();
					this.motionX = ((float)(rtr.hitVec.x - this.posX));
					this.motionY = ((float)(rtr.hitVec.y - this.posY));
					this.motionZ = ((float)(rtr.hitVec.z - this.posZ));
					f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					this.posX -= this.motionX / f2 * 0.05000000074505806D;
					this.posY -= this.motionY / f2 * 0.05000000074505806D;
					this.posZ -= this.motionZ / f2 * 0.05000000074505806D;
					//this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
					this.setIsCritical(false);

					if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.AIR && this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.GLASS)
					{
						System.out.println("collided with block!");
						//this.inBlock.onEntityCollidedWithBlock(this.world, this.getPosition(), this);
						this.inBlock.onEntityCollidedWithBlock(this.world, this.getPosition(), this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)), this);
					}
				}
			}

			if (this.getIsCritical())
			{
				for (i = 0; i < 4; ++i)
				{
					this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * i / 4.0D,
						this.posY + this.motionY * i / 4.0D, this.posZ + this.motionZ * i / 4.0D,
						-this.motionX, -this.motionY + 0.2D, -this.motionZ);
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			for (this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0D / Math.PI); this.rotationPitch
				- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
			{
				;
			}

			while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
			{
				this.prevRotationPitch += 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw < -180.0F)
			{
				this.prevRotationYaw -= 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
			{
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch
				+ (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw
				+ (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f1 = 0.05F;

			if (this.isInWater())
			{
				for (int l = 0; l < 4; ++l)
				{
					f4 = 0.25F;
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX
						- this.motionX * f4, this.posY - this.motionY * f4,
						this.posZ - this.motionZ * f4, this.motionX,
						this.motionY, this.motionZ);
				}

				f3 = 0.8F;
			}

			if (this.isWet()) this.extinguish();

			this.motionX *= f3;
			this.motionY *= f3;
			this.motionZ *= f3;
			this.motionY -= f1;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.doBlockCollisions();
		}

	}

	/*@Override
	protected ItemStack getArrowStack() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
