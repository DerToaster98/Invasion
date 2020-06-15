package invasion.entity.ai;

import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.EntityLivingBase;


public class EntityAITargetRetaliate extends EntityAISimpleTarget
{
	public EntityAITargetRetaliate(InvadingEntity entity, Class<? extends EntityLivingBase> targetType, float distance)
	{
		super(entity, targetType, distance);
	}

	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase attacker = this.getEntity().getAttackingEntity();
		if (attacker != null)
		{
			if ((this.getEntity().getDistance(attacker) <= this.getAggroRange()) && (this.getTargetType().isAssignableFrom(attacker.getClass())))
			{
				this.setTarget(attacker);
				return true;
			}
		}
		return false;
	}
}