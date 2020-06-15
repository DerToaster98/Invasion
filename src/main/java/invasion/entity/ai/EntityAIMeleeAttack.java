package invasion.entity.ai;

import invasion.entity.Objective;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAIMeleeAttack<T extends EntityLivingBase> extends EntityAIBase
{
	private InvadingEntity theEntity;
	private Class<? extends T> targetClass;
	private float attackRange;
	private int attackDelay;
	private int nextAttack;

	public EntityAIMeleeAttack(InvadingEntity entity, Class<? extends T> targetClass, int attackDelay)
	{
		this.theEntity = entity;
		this.targetClass = targetClass;
		this.attackDelay = attackDelay;
		this.attackRange = 0.6F;
		this.nextAttack = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase target = this.theEntity.getAttackTarget();
		return (target != null) && (this.theEntity.getAIGoal() == Objective.MELEE_TARGET) && (this.theEntity.getDistance(target) < (this.attackRange + this.theEntity.width + target.width) * 4.0F) && (target.getClass().isAssignableFrom(this.targetClass));
	}

	@Override
	public void updateTask()
	{
		EntityLivingBase target = this.theEntity.getAttackTarget();
		if (this.canAttackEntity(target))
		{
			this.attackEntity(target);
		}
		this.setAttackTime(this.getAttackTime() - 1);
	}

	public Class<? extends T> getTargetClass()
	{
		return this.targetClass;
	}

	protected void attackEntity(EntityLivingBase target)
	{
		this.theEntity.attackEntityAsMob(target);
		this.setAttackTime(this.getAttackDelay());
	}

	protected boolean canAttackEntity(EntityLivingBase target)
	{
		if (this.getAttackTime() <= 0)
		{
			double d = this.theEntity.width + this.attackRange;
			return this.theEntity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) < d * d;
		}
		return false;
	}

	protected int getAttackTime()
	{
		return this.nextAttack;
	}

	protected void setAttackTime(int time)
	{
		this.nextAttack = time;
	}

	protected int getAttackDelay()
	{
		return this.attackDelay;
	}

	protected void setAttackDelay(int time)
	{
		this.attackDelay = time;
	}
}