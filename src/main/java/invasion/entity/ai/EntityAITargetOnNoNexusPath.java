package invasion.entity.ai;

import invasion.entity.Objective;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.EntityLiving;


public class EntityAITargetOnNoNexusPath extends EntityAISimpleTarget {
    private final float PATH_DISTANCE_TRIGGER = 4.0F;

    public EntityAITargetOnNoNexusPath(InvadingEntity entity, Class<? extends EntityLiving> targetType, float distance) {
        super(entity, targetType, distance);
    }

    @Override
    public boolean shouldExecute() {
        if ((this.getEntity().getAIGoal() == Objective.BREAK_NEXUS) && (this.getEntity().getNavigatorNew().getLastPathDistanceToTarget() > 4.0F)) {
            return super.shouldExecute();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if ((this.getEntity().getAIGoal() == Objective.BREAK_NEXUS) && (this.getEntity().getNavigatorNew().getLastPathDistanceToTarget() > 4.0F)) {
            return super.shouldContinueExecuting();
        }
        return false;
    }
}