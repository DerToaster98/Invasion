package invasion.entity.ai;


import invasion.entity.INavigationFlying;
import invasion.entity.Objective;
import invasion.entity.monster.EntityIMFlying;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAICircleTarget extends EntityAIBase {
    private static final int ATTACK_SEARCH_TIME = 400;
    private final EntityIMFlying theEntity;
    private int time;
    private int patienceTime;
    private final int patience;
    private final float preferredHeight;
    private final float preferredRadius;

    public EntityAICircleTarget(EntityIMFlying entity, int patience, float preferredHeight, float preferredRadius) {
        this.theEntity = entity;
        this.time = 0;
        this.patienceTime = 0;
        this.patience = patience;
        this.preferredHeight = preferredHeight;
        this.preferredRadius = preferredRadius;
    }

    @Override
    public boolean shouldExecute() {
        return (this.theEntity.getAIGoal() == Objective.STAY_AT_RANGE) && (this.theEntity.getAttackTarget() != null);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return ((this.theEntity.getAIGoal() == Objective.STAY_AT_RANGE) || (this.isWaitingForTransition())) && (this.theEntity.getAttackTarget() != null);
    }

    @Override
    public void startExecuting() {
        INavigationFlying nav = this.theEntity.getNavigatorNew();
        nav.setMovementType(INavigationFlying.MoveType.PREFER_FLYING);
        nav.setCirclingPath(this.theEntity.getAttackTarget(), this.preferredHeight, this.preferredRadius);
        this.time = 0;
        int extraTime = (int) (4.0F * nav.getDistanceToCirclingRadius());
        if (extraTime < 0) {
            extraTime = 0;
        }
        this.patienceTime = (extraTime + this.theEntity.world.rand.nextInt(this.patience) + this.patience / 3);
    }

    @Override
    public void updateTask() {
        this.time += 1;
        if (this.theEntity.getAIGoal() == Objective.STAY_AT_RANGE) {
            this.patienceTime -= 1;
            if (this.patienceTime <= 0) {
                this.theEntity.transitionAIGoal(Objective.FIND_ATTACK_OPPORTUNITY);
                this.patienceTime = 400;
            }
        } else if (this.isWaitingForTransition()) {
            this.patienceTime -= 1;
            if (this.patienceTime > 0) ;
        }
    }

    protected boolean isWaitingForTransition() {
        return (this.theEntity.getPrevAIGoal() == Objective.STAY_AT_RANGE) && (this.theEntity.getAIGoal() == Objective.FIND_ATTACK_OPPORTUNITY);
    }
}