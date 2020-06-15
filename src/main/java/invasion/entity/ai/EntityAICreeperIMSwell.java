package invasion.entity.ai;

//NOOB HAUS: Done 

import invasion.entity.ally.EntityIMWolf;
import invasion.entity.monster.MoulderingCreeperEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;


public class EntityAICreeperIMSwell extends Goal
{
	MoulderingCreeperEntity creeper;
	LivingEntity targetEntity;

	public EntityAICreeperIMSwell(MoulderingCreeperEntity par1EntityCreeper)
	{
		this.creeper = par1EntityCreeper;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase entityliving = this.creeper.getAttackTarget();

		return (this.creeper.getCreeperState() > 0) || ((entityliving != null) && (this.creeper.getDistanceSq(entityliving) < 9.0D) && ((entityliving.getClass() == EntityPlayer.class) || (entityliving.getClass() == EntityIMWolf.class) || (entityliving.getClass() == EntityPlayerMP.class)));
	}

	@Override
	public void startExecuting()
	{
		this.creeper.getNavigatorNew().clearPath();
		this.targetEntity = this.creeper.getAttackTarget();
	}

	@Override
	public void resetTask()
	{
		this.targetEntity = null;
	}

	@Override
	public void updateTask()
	{
		if (this.targetEntity == null)
		{
			this.creeper.setCreeperState(-1);
			return;
		}

		if (this.creeper.getDistanceSq(this.targetEntity) > 49.0D)
		{
			this.creeper.setCreeperState(-1);
			return;
		}

		if (!this.creeper.getEntitySenses().canSee(this.targetEntity))
		{
			this.creeper.setCreeperState(-1);
			return;
		}
		this.creeper.setCreeperState(1);
	}
}