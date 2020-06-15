package invasion.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAIWatchTarget extends EntityAIBase {
    private final EntityLiving theEntity;

    public EntityAIWatchTarget(EntityLiving entity) {
        this.theEntity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.theEntity.getAttackTarget() != null;
    }

    @Override
    public void updateTask() {
        this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 2.0F, 2.0F);
    }
}