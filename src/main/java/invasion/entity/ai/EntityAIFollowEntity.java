package invasion.entity.ai;

import invasion.entity.EntityIMLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;


public class EntityAIFollowEntity<T extends EntityLivingBase> extends EntityAIMoveToEntity<T> {
    private final float followDistanceSq;

    public EntityAIFollowEntity(EntityIMLiving entity, float followDistance) {
        this(entity, (Class<? extends T>) EntityLivingBase.class, followDistance);
    }

    public EntityAIFollowEntity(EntityIMLiving entity, Class<? extends T> target, float followDistance) {
        super(entity, target);
        this.followDistanceSq = (followDistance * followDistance);
    }

    @Override
    public void startExecuting() {
        this.getEntity().onFollowingEntity(this.getTarget());
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        this.getEntity().onFollowingEntity(null);
        super.resetTask();
    }

    @Override
    public void updateTask() {
        super.updateTask();
        Entity entity = this.getTarget();
        if (this.getEntity().getDistanceSq(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ) < this.followDistanceSq)
            this.getEntity().getNavigatorNew().haltForTick();
    }
}