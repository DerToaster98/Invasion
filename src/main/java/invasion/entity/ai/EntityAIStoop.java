package invasion.entity.ai;

import invasion.entity.EntityIMLiving;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAIStoop extends EntityAIBase {

    private final EntityIMLiving theEntity;
    private int updateTimer;
    private boolean stopStoop;

    public EntityAIStoop(EntityIMLiving entity) {
        this.theEntity = entity;
        this.stopStoop = true;
    }

    @Override
    public boolean shouldExecute() {
        if (--this.updateTimer <= 0) {
            this.updateTimer = 10;
            return this.theEntity.world.getBlockState(this.theEntity.getPosition().up(2)).getMaterial().blocksMovement();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.stopStoop;
    }

    @Override
    public void startExecuting() {
        this.theEntity.setSneaking(true);
        this.stopStoop = false;
    }

    @Override
    public void updateTask() {
        if (--this.updateTimer <= 0) {
            this.updateTimer = 10;
            if (!this.theEntity.world.getBlockState(this.theEntity.getPosition().up()).getMaterial().blocksMovement()) {
                this.theEntity.setSneaking(false);
                this.stopStoop = true;
            }
        }
    }
}