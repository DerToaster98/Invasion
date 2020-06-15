package invasion.entity.ai;

import invasion.entity.EntityIMLiving;
import invasion.entity.monster.PigEngyEntity;


public class EntityAIWaitForEngy extends EntityAIFollowEntity<PigEngyEntity>
{
	private final float PATH_DISTANCE_TRIGGER = 4.0F;
	private boolean canHelp;

	public EntityAIWaitForEngy(EntityIMLiving entity, float followDistance, boolean canHelp)
	{
		super(entity, PigEngyEntity.class, followDistance);
		this.canHelp = canHelp;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();
		if (this.canHelp)
		{
			this.getTarget().supportForTick(this.getEntity(), 1.0F);
		}
	}
}