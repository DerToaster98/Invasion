package invasion.entity.ai;

import invasion.entity.INavigationFlying;
import invasion.entity.MoveState;
import invasion.entity.Objective;
import invasion.entity.monster.EntityIMBird;
import invasion.util.MathUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class EntityAISwoop extends EntityAIBase {
    private static final int INITIAL_LINEUP_TIME = 25;
    private final EntityIMBird theEntity;
    private final float minDiveClearanceY;
    private EntityLivingBase swoopTarget;
    private float diveAngle;
    private float diveHeight;
    private final float strikeDistance;
    private final float minHeight;
    private final float minXZDistance;
    private final float maxSteepness;
    private float finalRunLength;
    private final float finalRunArcLimit;
    private int time;
    private boolean isCommittedToFinalRun;
    private boolean endSwoop;
    private boolean usingClaws;

    public EntityAISwoop(EntityIMBird entity) {
        this.theEntity = entity;
        this.minDiveClearanceY = 0.0F;
        this.swoopTarget = null;
        this.diveAngle = 0.0F;
        this.diveHeight = 0.0F;
        this.maxSteepness = 40.0F;
        this.strikeDistance = (entity.width + 1.5F);
        this.minHeight = 6.0F;
        this.minXZDistance = 10.0F;
        this.finalRunLength = 4.0F;
        this.finalRunArcLimit = 15.0F;
        this.time = 0;
        this.isCommittedToFinalRun = false;
        this.endSwoop = false;
        this.usingClaws = false;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if ((this.theEntity.getAIGoal() == Objective.FIND_ATTACK_OPPORTUNITY) && (this.theEntity.getAttackTarget() != null)) {
            this.swoopTarget = this.theEntity.getAttackTarget();
            double dX = this.swoopTarget.posX - this.theEntity.posX;
            double dY = this.swoopTarget.posY - this.theEntity.posY;
            double dZ = this.swoopTarget.posZ - this.theEntity.posZ;
            double dXZ = Math.sqrt(dX * dX + dZ * dZ);
            if ((-dY < this.minHeight) || (dXZ < this.minXZDistance)) {
                return false;
            }
            double pitchToTarget = Math.atan(dY / dXZ) * 180.0D / 3.141592653589793D;
            if (pitchToTarget > this.maxSteepness) {
                return false;
            }
            this.finalRunLength = ((float) (dXZ * 0.42D));
            if (this.finalRunLength > 18.0F)
                this.finalRunLength = 18.0F;
            else if (this.finalRunLength < 4.0F) {
                this.finalRunLength = 4.0F;
            }
            this.diveAngle = ((float) (Math.atan((dXZ - this.finalRunLength) / dY) * 180.0D / 3.141592653589793D));
            if ((this.swoopTarget != null) && (this.isSwoopPathClear(this.swoopTarget, this.diveAngle))) {
                this.diveHeight = ((float) -dY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (this.theEntity.getAttackTarget() == this.swoopTarget) && (!this.endSwoop) && (this.theEntity.getMoveState() == MoveState.FLYING);
    }

    @Override
    public void startExecuting() {
        this.time = 0;
        this.theEntity.transitionAIGoal(Objective.SWOOP);
        this.theEntity.getNavigatorNew().setMovementType(INavigationFlying.MoveType.PREFER_FLYING);
        this.theEntity.getNavigatorNew().tryMoveToEntity(this.swoopTarget, 0.0F, this.theEntity.getMaxPoweredFlightSpeed());

        this.theEntity.doScreech();
    }

    @Override
    public void resetTask() {
        this.endSwoop = false;
        this.isCommittedToFinalRun = false;
        this.theEntity.getNavigatorNew().enableDirectTarget(false);
        if (this.theEntity.getAIGoal() == Objective.SWOOP) {
            this.theEntity.transitionAIGoal(Objective.NONE);
            this.theEntity.setClawsForward(false);
        }
    }

    @Override
    public void updateTask() {
        this.time += 1;
        if (!this.isCommittedToFinalRun) {
            if (this.theEntity.getDistance(this.swoopTarget) < this.finalRunLength) {
                this.theEntity.getNavigatorNew().setPitchBias(0.0F, 1.0F);
                if (this.isFinalRunLinedUp()) {
                    this.usingClaws = (this.theEntity.world.rand.nextFloat() > 0.6F);

                    this.theEntity.setClawsForward(true);

                    this.theEntity.getNavigatorNew().enableDirectTarget(true);
                    this.isCommittedToFinalRun = true;
                } else {
                    this.theEntity.transitionAIGoal(Objective.NONE);
                    this.endSwoop = true;
                }
            } else if (this.time > 25) {
                double dYp = -(this.swoopTarget.posY - this.theEntity.posY);
                if (dYp < 2.9D) {
                    dYp = 0.0D;
                }
                this.theEntity.getNavigatorNew().setPitchBias(this.diveAngle * (float) (dYp / this.diveHeight), (float) (0.6D * (dYp / this.diveHeight)));
            }

        } else if (this.theEntity.getDistance(this.swoopTarget) < this.strikeDistance) {
            this.theEntity.transitionAIGoal(Objective.FLYING_STRIKE);

            this.theEntity.getNavigatorNew().enableDirectTarget(false);
            this.endSwoop = true;
        } else {
            double dX = this.swoopTarget.posX - this.theEntity.posX;
            double dZ = this.swoopTarget.posZ - this.theEntity.posZ;
            double yawToTarget = Math.atan2(dZ, dX) * 180.0D / 3.141592653589793D - 90.0D;
            if (Math.abs(MathUtil.boundAngle180Deg(yawToTarget - this.theEntity.rotationYaw)) > 90.0D) {
                this.theEntity.transitionAIGoal(Objective.NONE);
                this.theEntity.getNavigatorNew().enableDirectTarget(false);
                this.theEntity.setClawsForward(false);
                this.endSwoop = true;
            }
        }
    }

    private boolean isSwoopPathClear(EntityLivingBase target, float diveAngle) {
        double dX = target.posX - this.theEntity.posX;
        double dY = target.posY - this.theEntity.posY;
        double dZ = target.posZ - this.theEntity.posZ;
        double dXZ = Math.sqrt(dX * dX + dZ * dZ);
        double dRayY = 2.0D;
        int hitCount = 0;
        double lowestCollide = this.theEntity.posY;
        for (double y = this.theEntity.posY - dRayY; y > target.posY; y -= dRayY) {
            double dist = Math.tan(90.0F + diveAngle) * (this.theEntity.posY - y);
            double x = -Math.sin(this.theEntity.rotationYaw / 180.0F * 3.141592653589793D) * dist;
            double z = Math.cos(this.theEntity.rotationYaw / 180.0F * 3.141592653589793D) * dist;
            Vec3d source = new Vec3d(x, y, z);
            //MovingObjectPosition collide = this.theEntity.world.rayTraceBlocks(source, target.getLook(1.0F));
            RayTraceResult collide = this.theEntity.world.rayTraceBlocks(source, target.getLook(1f), true, true, false);
            if (collide != null) {
                if (hitCount == 0) {
                    lowestCollide = y;
                }
                hitCount++;
            }
        }

        return this.isAcceptableDiveSpace(this.theEntity.posY, lowestCollide, hitCount);
    }

    private boolean isFinalRunLinedUp() {
        double dX = this.swoopTarget.posX - this.theEntity.posX;
        double dY = this.swoopTarget.posY - this.theEntity.posY;
        double dZ = this.swoopTarget.posZ - this.theEntity.posZ;
        double dXZ = Math.sqrt(dX * dX + dZ * dZ);
        double yawToTarget = Math.atan2(dZ, dX) * 180.0D / 3.141592653589793D - 90.0D;
        double dYaw = MathUtil.boundAngle180Deg(yawToTarget - this.theEntity.rotationYaw);
        if ((dYaw < -this.finalRunArcLimit) || (dYaw > this.finalRunArcLimit)) {
            return false;
        }
        double dPitch = Math.atan(dY / dXZ) * 180.0D / 3.141592653589793D - this.theEntity.rotationPitch;
        return (!(dPitch < -this.finalRunArcLimit)) && (!(dPitch > this.finalRunArcLimit));
    }

    protected boolean isAcceptableDiveSpace(double entityPosY, double lowestCollideY, int hitCount) {
        double clearanceY = entityPosY - lowestCollideY;
        return !(clearanceY < this.minDiveClearanceY);
    }
}