package invasion.entity.ai;

//NOOB HAUS: DONE - not certain if actually used ?

import invasion.entity.INavigationFlying;
import invasion.entity.MoveState;
import invasion.entity.Objective;
import invasion.entity.monster.EntityIMFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;


public class EntityAIBoP extends EntityAIBase {
    private static final int PATIENCE = 500;
    private final EntityIMFlying theEntity;
    private int timeWithGoal;
    private int timeWithTarget;
    private int patienceTime;
    private final float lastHealth;
    private Objective lastObjective;
    private EntityLivingBase lastTarget;

    public EntityAIBoP(EntityIMFlying entity) {
        this.theEntity = entity;
        this.timeWithGoal = 0;
        this.patienceTime = 0;
        this.lastHealth = entity.getHealth();
        this.lastObjective = entity.getAIGoal();
        this.lastTarget = entity.getAttackTarget();
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }

    @Override
    public void startExecuting() {
        this.timeWithGoal = 0;
        this.patienceTime = 0;
    }

    @Override
    public void updateTask() {
        this.timeWithGoal += 1;
        if (this.theEntity.getAIGoal() != this.lastObjective) {
            this.lastObjective = this.theEntity.getAIGoal();
            this.timeWithGoal = 0;
        }

        this.timeWithTarget += 1;
        if (this.theEntity.getAttackTarget() != this.lastTarget) {
            this.lastTarget = this.theEntity.getAttackTarget();
            this.timeWithTarget = 0;
        }

        if (this.theEntity.getAttackTarget() == null) {
            if (this.theEntity.getNexus() != null) {
                if (this.theEntity.getAIGoal() != Objective.BREAK_NEXUS) {
                    this.theEntity.transitionAIGoal(Objective.BREAK_NEXUS);
                }

            } else if (this.theEntity.getAIGoal() != Objective.CHILL) {
                this.theEntity.transitionAIGoal(Objective.CHILL);
                this.theEntity.getNavigatorNew().clearPath();
                this.theEntity.getNavigatorNew().setMovementType(INavigationFlying.MoveType.PREFER_WALKING);
                this.theEntity.getNavigatorNew().setLandingPath();
            }

        } else if ((this.theEntity.getAIGoal() == Objective.CHILL) || (this.theEntity.getAIGoal() == Objective.NONE)) {
            this.chooseTargetAction(this.theEntity.getAttackTarget());
        }

        if (this.theEntity.getAIGoal() != Objective.STAY_AT_RANGE) {
            if (this.theEntity.getAIGoal() == Objective.MELEE_TARGET) {
                if (this.timeWithGoal > 600) {
                    this.theEntity.transitionAIGoal(Objective.STAY_AT_RANGE);
                }
            }
        }
    }

    protected void chooseTargetAction(EntityLivingBase target) {
        if (this.theEntity.getMoveState() != MoveState.FLYING) {
            if ((this.theEntity.getDistance(target) < 10.0F) && (this.theEntity.world.rand.nextFloat() > 0.3F)) {
                this.theEntity.transitionAIGoal(Objective.MELEE_TARGET);
                return;
            }
        }
        this.theEntity.transitionAIGoal(Objective.STAY_AT_RANGE);
    }
}