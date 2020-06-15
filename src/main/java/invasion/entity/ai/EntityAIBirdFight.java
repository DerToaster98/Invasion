package invasion.entity.ai;

import invasion.entity.Objective;
import invasion.entity.INavigationFlying;
import invasion.entity.ai.navigator.Path;
import invasion.entity.monster.EntityIMBird;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;


public class EntityAIBirdFight<T extends EntityLivingBase> extends EntityAIMeleeFight<T>
{
	private EntityIMBird theEntity;
	private boolean wantsToRetreat;
	private boolean buffetedTarget;

	public EntityAIBirdFight(EntityIMBird entity, Class<? extends T> targetClass, int attackDelay, float retreatHealthLossPercent)
	{
		super(entity, targetClass, attackDelay, retreatHealthLossPercent);
		this.theEntity = entity;
		this.wantsToRetreat = false;
		this.buffetedTarget = false;
	}

	@Override
	public void updateTask()
	{
		if (this.getAttackTime() == 0)
		{
			this.theEntity.setAttackingWithWings(this.isInStartMeleeRange());
		}
		super.updateTask();
	}

	@Override
	public void resetTask()
	{
		this.theEntity.setAttackingWithWings(false);
		super.resetTask();
	}

	@Override
	public void updatePath()
	{
		INavigationFlying nav = this.theEntity.getNavigatorNew();
		Entity target = this.theEntity.getAttackTarget();
		if (target != nav.getTargetEntity())
		{
			nav.clearPath();
			nav.setMovementType(INavigationFlying.MoveType.PREFER_WALKING);
			Path path = nav.getPathToEntity(target, 0.0F);
			if ((path != null) && (path.getCurrentPathLength() > 1.6D * this.theEntity.getDistance(target)))
			{
				nav.setMovementType(INavigationFlying.MoveType.MIXED);
			}
			nav.autoPathToEntity(target);
		}
	}

	@Override
	protected void updateDisengage()
	{
		if (!this.wantsToRetreat)
		{
			if (this.shouldLeaveMelee())
				this.wantsToRetreat = true;
		}
		else if ((this.buffetedTarget) && (this.theEntity.getAIGoal() == Objective.MELEE_TARGET))
		{
			this.theEntity.transitionAIGoal(Objective.LEAVE_MELEE);
		}
	}

	@Override
	protected void attackEntity(EntityLivingBase target)
	{
		this.theEntity.doMeleeSound();
		super.attackEntity(target);
		if (this.wantsToRetreat)
		{
			this.doWingBuffetAttack(target);
			this.buffetedTarget = true;
		}
	}

	protected boolean isInStartMeleeRange()
	{
		EntityLivingBase target = this.theEntity.getAttackTarget();
		if (target == null)
		{
			return false;
		}
		double d = this.theEntity.width + this.theEntity.getAttackRange() + 3.0D;
		return this.theEntity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) < d * d;
	}

	protected void doWingBuffetAttack(EntityLivingBase target)
	{
		int knockback = 2;
		target.addVelocity(-MathHelper.sin(this.theEntity.rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F, 0.4D, MathHelper.cos(this.theEntity.rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F);
		target.playSound(SoundEvents.ENTITY_GENERIC_BIG_FALL, 1f, 1f);
		//target.world.playSoundAtEntity(target, "damage.fallbig", 1.0F, 1.0F);
	}
}