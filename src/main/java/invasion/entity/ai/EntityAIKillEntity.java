package invasion.entity.ai;

import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;


public class EntityAIKillEntity<T extends EntityLivingBase> extends EntityAIMoveToEntity<T>
{
	private static final float ATTACK_RANGE = 1.0F;
	private int attackDelay;
	private int nextAttack;

	public EntityAIKillEntity(InvadingEntity entity, Class<? extends T> targetClass, int attackDelay)
	{
		super(entity, targetClass);
		this.attackDelay = attackDelay;
		this.nextAttack = 0;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();
		this.setAttackTime(this.getAttackTime() - 1);
		Entity target = this.getTarget();
		if (this.canAttackEntity(target))
		{
			this.attackEntity(target);
		}
	}

	protected void attackEntity(Entity target)
	{
		this.getEntity().attackEntityAsMob(this.getTarget());
		this.setAttackTime(this.getAttackDelay());
	}

	protected boolean canAttackEntity(Entity target)
	{
		if (this.getAttackTime() <= 0)
		{
			Entity entity = this.getEntity();
			double d = (entity.width + 1.0F) * (entity.width + 1.0F);

			return entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) < d;
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