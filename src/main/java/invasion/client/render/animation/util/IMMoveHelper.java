package invasion.client.render.animation.util;

import invasion.entity.EntityIMLiving;
import invasion.entity.MoveState;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;


public class IMMoveHelper extends EntityMoveHelper
{

	protected EntityIMLiving entity;
	protected double targetSpeed;
	protected boolean needsUpdate;
	protected boolean isRunning;

	public IMMoveHelper(InvadingEntity par1EntityLiving)
	{
		super(par1EntityLiving);
		this.needsUpdate = false;
		this.entity = par1EntityLiving;
		this.posX = par1EntityLiving.posX;
		this.posY = par1EntityLiving.posY;
		this.posZ = par1EntityLiving.posZ;
	}

	@Override
	public boolean isUpdating()
	{
		return this.needsUpdate;
	}

	public void setMoveSpeed(float speed)
	{
		this.speed = speed;
	}

	public void setMoveTo(BlockPos pos, float speed)
	{
		this.setMoveTo(pos.getX(), pos.getY(), pos.getZ(), speed);
	}

	@Override
	public void setMoveTo(double x, double y, double z, double speedIn)
	{
		super.setMoveTo(x, y, z, speedIn);
		this.needsUpdate = true;
	}

	@Override
	public void onUpdateMoveHelper()
	{
		//super.onUpdateMoveHelper();
		if (!this.needsUpdate)
		{
			this.entity.setMoveForward(0.0F);
			this.entity.setMoveState(MoveState.STANDING);
			return;
		}

		MoveState result = this.doGroundMovement();
		this.entity.setMoveState(result);

		super.onUpdateMoveHelper();
	}

	public float getMoveForward()
	{
		return this.moveForward;
	}

	public float getMoveStrafe()
	{
		return this.moveStrafe;
	}

	protected MoveState doGroundMovement()
	{
		this.needsUpdate = false;
		this.targetSpeed = this.speed;
		boolean isInLiquid = (this.entity.isInWater()) || (this.entity.isInLava());
		double dX = this.posX - this.entity.posX;
		double dZ = this.posZ - this.entity.posZ;
		//This value is positive is the target pos is greater than the entity's current pos.
		double dY = this.posY - (!isInLiquid ? MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D) : this.entity.posY);

		float newYaw = (float)(Math.atan2(dZ, dX) * 180.0D / Math.PI) - 90.0F;
		EnumFacing ladderPos = null;
		if ((Math.abs(dX) < 0.8D) && (Math.abs(dZ) < 0.8D) && ((dY > 0.0D) || (this.entity.isHoldingOntoLadder())))
		{
			ladderPos = this.getClimbFace(this.entity.getPosition());
			if (ladderPos == null) ladderPos = this.getClimbFace(this.entity.getPosition().up());
			// TODO: Fix this, just made the case values random.
			if (ladderPos != null)
			{
				switch (ladderPos)
				{
					case NORTH:
						newYaw = (float)(Math.atan2(dZ, dX + 1.0D) * 180.0D / Math.PI) - 90.0F;
						break;
					case SOUTH:
						newYaw = (float)(Math.atan2(dZ, dX - 1.0D) * 180.0D / Math.PI) - 90.0F;
						break;
					case EAST:
						newYaw = (float)(Math.atan2(dZ + 1.0D, dX) * 180.0D / Math.PI) - 90.0F;
						break;
					case WEST:
						newYaw = (float)(Math.atan2(dZ - 1.0D, dX) * 180.0D / Math.PI) - 90.0F;
					default:
						break;
				}
			}
		}

		double dXZSq = dX * dX + dZ * dZ;
		double distanceSquared = dXZSq + dY * dY;
		if ((distanceSquared < 0.01D) && (ladderPos == null)) return MoveState.STANDING;

		if ((dXZSq > 0.04D) || (ladderPos != null))
		{
			this.entity.rotationYaw = this.correctRotation(this.entity.rotationYaw, newYaw, this.entity.getTurnRate());
			double moveSpeed;
			if ((distanceSquared >= 0.064D) || (this.entity.isSprinting()))
			{
				moveSpeed = this.targetSpeed;
			}
			else
			{
				moveSpeed = this.targetSpeed * 0.5D;
			}
			if ((this.entity.isInWater()) && (moveSpeed < 0.6D))
			{
				moveSpeed = 0.6000000238418579D;
			}
			this.entity.setAIMoveSpeed((float)moveSpeed);
		}

		double w = Math.max(this.entity.width * 0.5F + 1.0F, 1.0D);
		//double w = this.entity.width * 0.5F + 1.0F;
		boolean isBlockCloseEnough = dXZSq <= w * w;
		if ((dY > 0.0D) && (isBlockCloseEnough || isInLiquid))
		{
			//this.entity.getJumpHelper().setJumping();
			//this.entity.setJumping(true);
			if (ladderPos != null) return MoveState.CLIMBING;
			return MoveState.JUMPING;
		}
		return MoveState.RUNNING;
	}

	protected float correctRotation(float currentYaw, float newYaw,
		float turnSpeed)
	{
		float dYaw = newYaw - currentYaw;
		while (dYaw < -180.0F)
			dYaw += 360.0F;
		while (dYaw >= 180.0F)
			dYaw -= 360.0F;
		if (dYaw > turnSpeed)
			dYaw = turnSpeed;
		if (dYaw < -turnSpeed)
		{
			dYaw = -turnSpeed;
		}
		return currentYaw + dYaw;
	}

	protected EnumFacing getClimbFace(BlockPos blockPos)
	{

		Block block = this.entity.world.getBlockState(blockPos).getBlock();
		if (block instanceof BlockLadder)
		{
			IBlockState blockState = this.entity.world.getBlockState(blockPos);
			return blockState.getValue(BlockLadder.FACING);/*.getProperties().get(BlockLadder.field_176382_a);*/
		}
		// } else if (block instanceof BlockVine) {
		// IBlockState blockState =
		// this.entity.world.getBlockState(blockPos);
		// return
		// (EnumFacing)blockState.getProperties().get(BlockVine.field_176382_a);
		//
		// if (meta == 1)
		// return 2;
		// if (meta == 4)
		// return 3;
		// if (meta == 2)
		// return 1;
		// if (meta == 8)
		// return 0;
		// }

		// TODO: Allow vines to be climbed.
		return null;
	}
}