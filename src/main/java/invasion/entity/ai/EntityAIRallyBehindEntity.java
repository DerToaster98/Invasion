package invasion.entity.ai;

import invasion.entity.EntityIMLiving;
import net.minecraft.entity.EntityLivingBase;


public class EntityAIRallyBehindEntity<T extends EntityLivingBase, ILeader> extends EntityAIFollowEntity<T> {

    private static final float DEFAULT_FOLLOW_DISTANCE = 5.0F;

    public EntityAIRallyBehindEntity(EntityIMLiving entity, Class<T> leader) {
        this(entity, leader, 5.0F);
    }

    public EntityAIRallyBehindEntity(EntityIMLiving entity, Class<T> leader, float followDistance) {
        super(entity, leader, followDistance);
    }

    @Override
    public boolean shouldExecute() {
        return (this.getEntity().readyToRally()) && (super.shouldExecute());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (this.getEntity().readyToRally()) && (super.shouldContinueExecuting());
    }

    @Override
    public void updateTask() {
        super.updateTask();
        if (this.getEntity().readyToRally()) {
            EntityLivingBase leader = this.getTarget();

            //if (((ILeader) leader).isMartyr())
            //rally(leader);
        }
    }

    protected void rally(T leader) {
        this.getEntity().rally(leader);
    }
}