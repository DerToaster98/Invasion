package invasion.entity.ai;

//NOOB HAUS: Done

import invasion.entity.INavigationFlying;
import invasion.entity.Objective;
import invasion.entity.ai.navigator.Path;
import invasion.entity.monster.EntityIMFlying;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAIFlyingMoveToEntity extends EntityAIBase {
    private final EntityIMFlying theEntity;

    public EntityAIFlyingMoveToEntity(EntityIMFlying entity) {
        this.theEntity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return (this.theEntity.getAIGoal() == Objective.GOTO_ENTITY) && (this.theEntity.getAttackTarget() != null);
    }

    @Override
    public void startExecuting() {
        INavigationFlying nav = this.theEntity.getNavigatorNew();
        Entity target = this.theEntity.getAttackTarget();
        if (target != nav.getTargetEntity()) {
            nav.clearPath();
            nav.setMovementType(INavigationFlying.MoveType.PREFER_WALKING);
            Path path = nav.getPathToEntity(target, 0.0F);
            if (path.getCurrentPathLength() > 2.0D * this.theEntity.getDistance(target)) {
                nav.setMovementType(INavigationFlying.MoveType.MIXED);
            }
            nav.autoPathToEntity(target);
        }
    }

    @Override
    public void updateTask() {
    }
}