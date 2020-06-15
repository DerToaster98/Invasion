package invasion.entity.ai;

import invasion.entity.Objective;
import invasion.entity.INavigation;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;


public class EntityAIMeleeFight<T extends EntityLivingBase> extends EntityAIMeleeAttack<T>
{
	private InvadingEntity theEntity;
	private int time;
	private float startingHealth;
	private int damageDealt;
	private int invulnCount;
	private float retreatHealthLossPercent;

	public EntityAIMeleeFight(InvadingEntity entity, Class<? extends T> targetClass, int attackDelay, float retreatHealthLossPercent)
	{
		super(entity, targetClass, attackDelay);
		this.theEntity = entity;
		this.time = 0;
		this.startingHealth = 0.0F;
		this.damageDealt = 0;
		this.invulnCount = 0;
		this.retreatHealthLossPercent = retreatHealthLossPercent;
	}

	@Override
	public boolean shouldExecute()
	{
		Entity target = this.theEntity.getAttackTarget();
		return (this.theEntity.getAIGoal() == Objective.MELEE_TARGET) && (target != null) && (target.getClass().isAssignableFrom(this.getTargetClass()));
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return ((this.theEntity.getAIGoal() == Objective.MELEE_TARGET) || (this.isWaitingForTransition())) && (this.theEntity.getAttackTarget() != null);
	}

	@Override
	public void startExecuting()
	{
		this.time = 0;
		this.startingHealth = this.theEntity.getHealth();
		this.damageDealt = 0;
		this.invulnCount = 0;
	}

	@Override
	public void updateTask()
	{
		this.updateDisengage();
		this.updatePath();
		super.updateTask();
		if ((this.damageDealt > 0) || (this.startingHealth - this.theEntity.getHealth() > 0.0F))
			this.time += 1;
	}

	public void updatePath()
	{
		INavigation nav = this.theEntity.getNavigatorNew();
		if (this.theEntity.getAttackTarget() != nav.getTargetEntity())
		{
			nav.clearPath();
			nav.autoPathToEntity(this.theEntity.getAttackTarget());
		}
	}

	protected void updateDisengage()
	{
		if ((this.theEntity.getAIGoal() == Objective.MELEE_TARGET) && (this.shouldLeaveMelee()))
		{
			this.theEntity.transitionAIGoal(Objective.LEAVE_MELEE);
		}
	}

	protected boolean isWaitingForTransition()
	{
		return (this.theEntity.getAIGoal() == Objective.LEAVE_MELEE) && (this.theEntity.getPrevAIGoal() == Objective.MELEE_TARGET);
	}

	@Override
	protected void attackEntity(EntityLivingBase target)
	{
		float h = target.getHealth();
		super.attackEntity(target);
		h -= target.getHealth();
		if (h <= 0.0F)
		{
			this.invulnCount += 1;
		}
		this.damageDealt = ((int)(this.damageDealt + h));
	}

	protected boolean shouldLeaveMelee()
	{
		float damageReceived = this.startingHealth - this.theEntity.getHealth();
		if ((this.time > 40) && (damageReceived > this.theEntity.getMaxHealth() * this.retreatHealthLossPercent))
		{
			return true;
		}
		if ((this.time > 100) && (damageReceived - this.damageDealt > this.theEntity.getMaxHealth() * 0.66F * this.retreatHealthLossPercent))
		{
			return true;
		}
		if (this.invulnCount >= 2)
		{
			return true;
		}
		return false;
	}
}
