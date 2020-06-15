package invasion.entity.ai;

import invasion.entity.ai.navigator.Path;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;


public class EntityAIWanderIM extends EntityAIBase {
    private static final int MIN_HORIZONTAL_PATH = 1;
    private static final int MAX_HORIZONTAL_PATH = 6;
    private static final int MAX_VERTICAL_PATH = 4;
    private final InvadingEntity theEntity;
    private BlockPos movePosition;

    public EntityAIWanderIM(InvadingEntity entity) {
        this.theEntity = entity;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theEntity.getRNG().nextInt(120) == 0) {
            int x = this.theEntity.getPosition().getX() + this.theEntity.getRNG().nextInt(13) - 6;
            int z = this.theEntity.getPosition().getZ() + this.theEntity.getRNG().nextInt(13) - 6;
            Path path = this.theEntity.getNavigatorNew().getPathTowardsXZ(x, z, 1, 6, 4);
            if (path != null) {
                this.theEntity.getNavigatorNew().setPath(path, this.theEntity.getMoveSpeedStat());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (!this.theEntity.getNavigatorNew().noPath()) && (this.theEntity.getNavigatorNew().getStuckTime() < 40);
    }
}