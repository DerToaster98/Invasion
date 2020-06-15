package invasion.entity.ai;

import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.EntityLiving;


public class EntityAILeaderTarget extends EntityAISimpleTarget
{
	private final InvadingEntity theEntity;

	public EntityAILeaderTarget(InvadingEntity entity, Class<? extends EntityLiving> targetType, float distance)
	{
		this(entity, targetType, distance, true);
	}

	public EntityAILeaderTarget(InvadingEntity entity, Class<? extends EntityLiving> targetType, float distance, boolean needsLos)
	{
		super(entity, targetType, distance, needsLos);
		this.theEntity = entity;
	}

	@Override
	public boolean shouldExecute()
	{
		if (!this.theEntity.readyToRally())
		{
			return false;
		}
		return super.shouldExecute();
	}
}