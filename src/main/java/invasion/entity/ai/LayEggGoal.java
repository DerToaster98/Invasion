package invasion.entity.ai;

//NOOB HAUS: DONE

import invasion.entity.ISpawnsOffspring;
import invasion.entity.Objective;
import invasion.entity.block.EggEntity;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;


public class LayEggGoal extends Goal {

    private static final int EGG_LAY_TIME = 45;
    private static final int INITIAL_EGG_DELAY = 25;
    private static final int NEXT_EGG_DELAY = 230;
    private static final int EGG_HATCH_TIME = 125;
    protected final InvadingEntity entity;
    private int time;
    private boolean isLaying;
    private int eggCount;

    public LayEggGoal(InvadingEntity entity, int eggs) {
        this.entity = entity;
        eggCount = eggs;
    }

    @Override
    public boolean shouldExecute() {
        return (entity.getAIGoal() == Objective.TARGET_ENTITY) && (eggCount > 0) && (entity.getEntitySenses().canSee(this.entity.getAttackTarget()));
    }

    @Override
    public void startExecuting() {
        time = 25;
    }

    @Override
    public void tick() {
        time--;
        if (time <= 0) {
            if (!isLaying) {
                isLaying = true;
                time = 45;
                setMutexBits(1);
            } else {
                isLaying = false;
                eggCount -= 1;
                time = 230;
                this.setMutexBits(0);
                layEgg();
            }
        }
    }

    private void layEgg() {
        Entity[] contents;
        if ((this.entity instanceof ISpawnsOffspring))
            contents = ((ISpawnsOffspring) this.entity).getOffspring(null);
        else {
            contents = null;
        }
        this.entity.world.addEntity(new EggEntity(this.entity, contents, EGG_HATCH_TIME));
    }
}