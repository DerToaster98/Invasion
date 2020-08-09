package invasion.entity.monster;

import invasion.IBlockAccessExtended;
import invasion.IPathfindable;
import invasion.Invasion;
import invasion.client.render.animation.util.IMMoveHelper;
import invasion.entity.EntityIMLiving;
import invasion.entity.IHasNexus;
import invasion.entity.MoveState;
import invasion.entity.Objective;
import invasion.entity.ai.navigator.*;
import invasion.nexus.Nexus;
import invasion.util.Coords;
import invasion.util.Distance;
import invasion.util.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class InvadingEntity extends MonsterEntity implements /*SparrowAPI,*/ IHasNexus, IPathfindable {

    //TODO reasonable defaults
    protected static final IAttribute JUMP_HEIGHT = new RangedAttribute(null, "invasion.jump_height", 1.0D, 0.0D, 3.0D).setDescription("Jump Height");
    protected static final IAttribute AGGRO_RANGE = new RangedAttribute(null, "invasion.aggro_range", 12.0D, 0.0D, 20.0D).setDescription("Aggression Range");
    protected static final IAttribute SENSE_RANGE = new RangedAttribute(null, "invasion.sense_range", 6.0D, 0.0D, 20.0D).setDescription("Sensory Range");
    private static final DataParameter<Boolean> IS_ADJACENT_CLIMB_BLOCK = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.BOOLEAN); //21
    private static final DataParameter<Boolean> IS_JUMPING = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.BOOLEAN); //22
    private static final DataParameter<Boolean> IS_HOLDING_ONTO_LADDER = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.BOOLEAN); //20
    private static final DataParameter<Integer> MOVE_STATE = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.VARINT); //23
    private static final DataParameter<Byte> TIER = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.BYTE); //30
    private static final DataParameter<Integer> TEXTURE = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.VARINT); //31
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24
    protected static List<Block> unbreakableBlocks = Arrays.asList(
            Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.END_PORTAL_FRAME,
            Blocks.LADDER, Blocks.CHEST);

    protected final Nexus nexus;

    private final PathCreator pathSource = new PathCreator(700, 50);
    private final NavigatorIM imNavigator = new NavigatorIM(this, this.pathSource);
    private final PathNavigateAdapter oldNavAdapter = new PathNavigateAdapter(this.imNavigator);

    protected Objective currentObjective = Objective.NONE;
    protected Objective prevObjective = Objective.NONE;
    //protected EntityAITasks tasksIM;
    //protected EntityAITasks targetTasksIM;
    protected byte tier;
    protected float attackRange = 0f;
    protected int selfDamage = 2;
    protected int maxSelfDamage = 6;
    protected int maxDestructiveness = 0;
    protected float blockRemoveSpeed = 1f;
    protected boolean floatsInWater = true;
    protected int throttled = 0;
    protected int throttled2 = 0;
    protected int flammability = 2;
    protected int destructiveness = 0;
    private BlockPos collideSize;
    private final BlockPos currentTargetPos = BlockPos.ZERO;
    private int rallyCooldown;
    //private float turnRate = 30.0F;
    //private float moveSpeedBase = 0.2f;
    //private float moveSpeed = 0.2f;
    private MoveState moveState;
    private final IMMoveHelper moveHelperIM = new IMMoveHelper(this);
    //private float rotationRoll = 0f;

    //DarthXenon: Not sure what these should be initialized as
    //private float rotationYawHeadIM = 0f;
    //private float rotationPitchHead = 0f;
    //private float prevRotationRoll = 0f;
    //private float prevRotationYawHeadIM = 0f;
    //private float prevRotationPitchHead = 0f;
    private int debugMode;
    private final float airResistance = 0.9995F;
    private float groundFriction = 0.546F;
    private float gravityAcel = 0.08F;
    private final float pitchRate = 2f;
    private BlockPos lastBreathExtendPos = BlockPos.ZERO;
    private float maxHealth;
    private boolean canClimb = false;
    private final boolean canDig = true;
    private boolean nexusBound;
    private boolean alwaysIndependent = false;
    private boolean burnsInDay;
    private int stunTimer;

   /*
    private int jumpHeight = 1;
    private int aggroRange;
    private int senseRange;

     */

    public InvadingEntity(EntityType<? extends InvadingEntity> type, World world, Nexus nexus) {
        super(type, world);

        this.nexus = nexus;
        debugMode = Config.DEBUG ? 1 : 0;
        setMaxHealthAndHealth(Invasion.getMobHealth(this));
        isImmuneToFire = false;
        experienceValue = 5;
        nexusBound = nexus != null;
        burnsInDay = nexus == null && Config.NIGHTSPAWNS_MOB_BURN_DURING_DAY;
        //   aggroRange = nexus != null ? 12 : Config.NIGHTSPAWNS_MOB_SIGHTRANGE;
        //   senseRange = nexus != null ? 6 : Config.NIGHTSPAWNS_MOB_SENSERANGE;
        // debugTest
    }

    public static float getBlockStrength(BlockPos pos, Block block, World world) {

        int bonus = 0;
        if (world.getBlockState(pos.down()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.up()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.west()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.east()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.north()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.south()).getBlock() == block) bonus++;

        return block.getExplosionResistance() * (1.0F + bonus * 0.1F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(IS_ADJACENT_CLIMB_BLOCK, false);
        getDataManager().register(IS_JUMPING, false);
        getDataManager().register(TIER, (byte) 1);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(JUMP_HEIGHT);
        getAttributes().registerAttribute(AGGRO_RANGE);
        getAttributes().registerAttribute(SENSE_RANGE);
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            isJumping = getDataManager().get(IS_JUMPING);
        } else {
            setAdjacentClimbBlock(checkForAdjacentClimbBlock());
        }

        if (getAir() == 190) {
            lastBreathExtendPos = getPosition();
        } else if (getAir() == 0) {
            if (Distance.distanceBetween(lastBreathExtendPos, getPosition()) > 4.0D) {
                lastBreathExtendPos = getPosition();
                setAir(180);
            }
        }
        
    }

    @Override
    public void livingTick() {
        if (!nexusBound) {
            float brightness = getBrightness();
            if ((brightness > 0.5F) || (getPosY() < 55.0D)) {
                ticksExisted += 2;
            }
            if ((getBurnsInDay()) && (world.isDaytime()) && (!world.isRemote)) {
                if ((brightness > 0.5F) && (world.canBlockSeeSky(getPosition()))
                        && (rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F)) {
                    sunlightDamageTick();
                }
            }
        }
        super.livingTick();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float damage) {
        if (super.attackEntityFrom(damagesource, damage)) {
            Entity entity = damagesource.getTrueSource();
            //if ((riddenByEntity == entity) || (ridingEntity == entity)) {
            if (getPassengers().contains(entity) || getRidingEntity() == entity) {
                return true;
            }
            if (entity != this) entity = entity;
            return true;
        }
        return false;
    }

    public void stunEntity(int ticks) {
        if (stunTimer < ticks) stunTimer = ticks;
        setMotion(0.0, getMotion().y, 0.0);
    }

    //TODO maybe switch back to a custom implementation of attackEntityAsMob
/*
    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this),(float) getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
    }

    public boolean attackEntityAsMob(Entity entity, int damageOverride) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), damageOverride);
    }
    */

    @Override
    public void moveRelative(float p_213309_1_, Vec3d relative) {
        super.moveRelative(p_213309_1_, relative);
    }

    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {
	/*	// TODO Auto-generated method stub
		super.moveRelative(strafe, up, forward, friction);
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward)
	{*/
        //super.moveEntityWithHeading(strafe, forward);
        super.moveRelative(strafe, up, forward, friction);
        if (isInWater()) {
            double y = getPosY();
            moveFlying(strafe, forward, 0.04F);
            setVelocity(motionX, motionY, motionZ);
            motionX *= 0.8D;
            motionY *= 0.8D;
            motionZ *= 0.8D;
            motionY -= 0.02D;
            if ((collidedHorizontally)
                    && (isOffsetPositionInLiquid(motionX, motionY + 0.6D - posY + y, motionZ)))
                motionY = 0.3D;
        } else if (isInLava()) {
            double y = posY;
            moveFlying(strafe, forward, 0.04F);
            setVelocity(motionX, motionY, motionZ);
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
            motionY -= 0.02D;//"get paid pupper"


            if ((collidedHorizontally)
                    && (isOffsetPositionInLiquid(motionX, motionY + 0.6D - posY + y, motionZ)))
                motionY = 0.3D;
        } else {
            float groundFriction = 0.91F;
            float landMoveSpeed;
            if (onGround) {
                groundFriction = getGroundFriction();
                Block block = world.getBlockState(new BlockPos(posX, getEntityBoundingBox().minY - 1d, posZ)).getBlock();
                if (block != Blocks.AIR) groundFriction = block.slipperiness * 0.91F;
                landMoveSpeed = getAIMoveSpeed();

                landMoveSpeed *= 0.162771F / (groundFriction * groundFriction * groundFriction);
            } else {
                landMoveSpeed = jumpMovementFactor;
            }

            moveFlying(strafe, forward, landMoveSpeed);
//"get paid pupper"


            if (isOnLadder()) {
                float maxLadderXZSpeed = 0.15F;
                if (motionX < -maxLadderXZSpeed) motionX = (-maxLadderXZSpeed);
                if (motionX > maxLadderXZSpeed) motionX = maxLadderXZSpeed;
                if (motionZ < -maxLadderXZSpeed) motionZ = (-maxLadderXZSpeed);
                if (motionZ > maxLadderXZSpeed) motionZ = maxLadderXZSpeed;

                fallDistance = 0.0F;
                if (motionY < -0.15D) motionY = -0.15D;

                if ((isHoldingOntoLadder()) || ((isSneaking()) && (motionY < 0.0D))) {
                    motionY = 0.0D;
                } else if ((world.isRemote) && (isJumping)) {
                    motionY += 0.04D;
                }
            }
            setVelocity(motionX, motionY, motionZ);

            if ((collidedHorizontally) && (isOnLadder())) motionY = 0.2D;
            motionY -= getGravity();
            motionY *= airResistance;
            motionX *= groundFriction * airResistance;
            motionZ *= groundFriction * airResistance;
        }

        prevLimbSwingAmount = limbSwingAmount;
        double dX = posX - prevPosX;
        double dZ = posZ - prevPosZ;
        float limbEnergy = MathHelper.sqrt(dX * dX + dZ * dZ) * 4.0F;

        if (limbEnergy > 1.0F) {
            limbEnergy = 1.0F;
        }

        limbSwingAmount += (limbEnergy - limbSwingAmount) * 0.4F;
        limbSwing += limbSwingAmount;
    }

	/*
	// not sure why, but this needed to be removed in order to let the mobs swim
	// public boolean handleWaterMovement() {
	// if (floatsInWater) {
	// return
	// world.handleMaterialAcceleration(getEntityBoundingBox().expand(0.0D,
	// -0.4D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
	// }
	//
	// double vX = motionX;
	// double vY = motionY;
	// double vZ = motionZ;
	// boolean isInWater =
	// world.handleMaterialAcceleration(getEntityBoundingBox().expand(0.0D,
	// -0.4D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
	// motionX = vX;
	// motionY = vY;
	// motionZ = vZ;
	// return isInWater;
	// }
	*/

    //TODO: Removed Override annotation
    public void moveFlying(float strafeAmount, float forwardAmount, float movementFactor) {
        float unit = MathHelper.sqrt(strafeAmount * strafeAmount + forwardAmount * forwardAmount);

        if (unit < 0.01F) return;
        if (unit < 20.0F) unit = 1.0F;

        unit = movementFactor / unit;
        strafeAmount *= unit;
        forwardAmount *= unit;

        float com1 = MathHelper.sin(rotationYaw * 3.141593F / 180.0F);
        float com2 = MathHelper.cos(rotationYaw * 3.141593F / 180.0F);
        motionX += strafeAmount * com2 - forwardAmount * com1;
        motionZ += forwardAmount * com2 + strafeAmount * com1;
    }

    public void onBlockRemoved(int x, int y, int z, int id) {
        if (getHealth() > maxHealth - maxSelfDamage) {
            attackEntityFrom(DamageSource.GENERIC, selfDamage);
        }

        if ((throttled == 0) && ((id == 3) || (id == 2) || (id == 12) || (id == 13))) {
            playSound(SoundEvents.BLOCK_GRAVEL_STEP, 1.4f, 1f / (rand.nextFloat() * 0.6f + 1f));
        } else {
            playSound(SoundEvents.BLOCK_STONE_STEP, 1.4f, 1f / (rand.nextFloat() * 0.6f + 1f));
        }
        throttled = 5;
    }

    public boolean canEntityBeDetected(Entity entity) {
        float distance = getDistance(entity);
        return (distance <= getSenseRange())
                || ((canEntityBeSeen(entity)) && (distance <= getAggroRange()));
    }

	/*
	// TODO: Fix This
	// @Override
	// public Entity findPlayerToAttack() {
	// EntityPlayer entityPlayer = world.getClosestPlayerToEntity(
	// this, getSenseRange());
	// if (entityPlayer != null) {
	// return entityPlayer;
	// }
	// entityPlayer = world.getClosestPlayerToEntity(this,
	// getAggroRange());
	// if ((entityPlayer != null) && (canEntityBeSeen(entityPlayer))) {
	// return entityPlayer;
	// }
	// return null;
	// }
	*/

    public double findDistanceToNexus() {
        if (nexus == null) return Double.MAX_VALUE;
        double x = nexus.getPos().getX() + 0.5D - getPosX();
        double y = nexus.getPos().getY() - getPosY() + getHeight() * 0.5D;
        double z = nexus.getPos().getZ() + 0.5D - getPosZ();
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putBoolean("alwaysIndependent", alwaysIndependent);
        nbt.putShort("tier", getTier());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);

        if (nbt.contains("tier")) {
            getDataManager().set(TIER, nbt.getByte("tier"));
        }
        if (nbt.contains("alwaysIndependent")) {
            alwaysIndependent = nbt.getBoolean("alwaysIndependent");
        }
        if (alwaysIndependent) {
            setBurnsInDay(Config.NIGHTSPAWNS_MOB_BURN_DURING_DAY);
            setAggroRange(Config.NIGHTSPAWNS_MOB_SIGHTRANGE);
            setSenseRange(Config.NIGHTSPAWNS_MOB_SENSERANGE);
        }
    }
/*
    public float getPrevRotationRoll() {
        return prevRotationRoll;
    }

    public float getRotationRoll() {
        return rotationRoll;
    }

    public void setRotationRoll(float roll) {
        rotationRoll = roll;
    }

    public float getPrevRotationYawHeadIM() {
        return prevRotationYawHeadIM;
    }

    public float getRotationYawHeadIM() {
        return rotationYawHeadIM;
    }

    public void setRotationYawHeadIM(float yaw) {
        rotationYawHeadIM = yaw;
    }

    public float getPrevRotationPitchHead() {
        return prevRotationPitchHead;
    }

    public float getRotationPitchHead() {
        return rotationPitchHead;
    }

    public void setRotationPitchHead(float pitch) {
        rotationPitchHead = pitch;
    }*/

    public float getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(float range) {
        attackRange = range;
    }

    public void setMaxHealthAndHealth(float health) {
        maxHealth = health;
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        setHealth(health);
    }

    //DarthXenon: Boolean flags are kept separate for debug breakpoint purposes
    @Override
    public boolean getCanSpawnHere() {
        boolean lightFlag = ((nexusBound) || (getLightLevelBelow8()));
        BlockPos pos = new BlockPos(posX, getEntityBoundingBox().minY + 0.5D, posZ);
        //boolean onGround = WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, pos);
        boolean onGround = world.isTopSolid(pos.down(), EnumFacing.UP, false);
        boolean inWall = isEntityInOpaqueBlockBeforeSpawn();
        return (super.getCanSpawnHere()) && (lightFlag) && (onGround && !inWall);
    }

    public boolean isEntityInOpaqueBlockBeforeSpawn() {
        AxisAlignedBB box = getEntityBoundingBox();
        BlockPos min = new BlockPos(box.minX, box.minY, box.minZ);
        BlockPos max = new BlockPos(MathHelper.ceil(box.maxX), MathHelper.ceil(box.maxY), MathHelper.ceil(box.maxZ));
        for (int x = min.getX(); x < max.getX(); x++) {
            for (int y = min.getY(); y < max.getY(); y++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    if (world.isBlockNormalCube(new BlockPos(x, y, z), false)) return true;
                }
            }
        }
        return false;
    }

    /*
    public int getJumpHeight() {
        return jumpHeight;
    }

    protected void setJumpHeight(int height) {
        jumpHeight = height;
    }

     */

    public float getBlockStrength(BlockPos pos) {
        return getBlockStrength(pos, world.getBlockState(pos).getBlock());
    }

    public float getBlockStrength(BlockPos pos, Block block) {
        return getBlockStrength(pos, block, world);
    }

    public boolean getCanClimb() {
        return canClimb;
    }

    protected void setCanClimb(boolean flag) {
        canClimb = flag;
    }

    public boolean getCanDigDown() {
        return canDig;
    }
/*
    public int getAggroRange() {
        return aggroRange;
    }

    public void setAggroRange(int range) {
        aggroRange = range;
    }

    public int getSenseRange() {
        return senseRange;
    }

    public void setSenseRange(int range) {
        senseRange = range;
    }

 */

    //TODO Prevents the entity from spawning with egg; conflict with EntityAISwimming
	/*@Override
	public PathNavigate getNavigator() {
		return oldNavAdapter;
	}*/

    // TODO: Used to have override annotation
    /*
    public float getBlockPathWeight(int i, int j, int k) {
        if (nexusBound) return 0.0F;
        return 0.5F - world.getLightBrightness(new BlockPos(i, j, k));
    }

     */

    public boolean getBurnsInDay() {
        return burnsInDay;
    }

    public void setBurnsInDay(boolean flag) {
        burnsInDay = flag;
    }
/*
    @Override
    public boolean isHostile() {
        return isHostile;
    }

    @Override
    public boolean isNeutral() {
        return creatureRetaliates;
    }

    @Override
    public boolean isThreatTo(Entity entity) {
        return isHostile() && entity instanceof PlayerEntity;
    }

    @Override
    public Entity getAttackingTarget() {
        return getAttackTarget();
    }

    @Override
    public boolean isStupidToAttack() {
        return false;
    }

    @Override
    public boolean doNotVaporize() {
        return false;
    }

    @Override
    public boolean isPredator() {
        return false;
    }

    @Override
    public boolean isPeaceful() {
        return false;
    }

    @Override
    public boolean isPrey() {
        return false;
    }

    @Override
    public boolean isUnkillable() {
        return false;
    }

    @Override
    public boolean isFriendOf(Entity par1entity) {
        return false;
    }

    @Override
    public boolean isNPC() {
        return false;
    }

    @Override
    public int isPet() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getGender() {
        return gender;
    }

    @Override
    @Nullable
    public Entity getPetOwner() {
        return null;
    }

    @Override
    public float getSize() {
        return height * width;
    }

    @Override
    @Nullable
    public String customStringAndResponse(String s) {
        return null;
    }

    @Override
    public String getSimplyID() {
        return "needID";
    }

    public boolean isNexusBound() {
        return nexusBound;
    }

    @Override
    public boolean isOnLadder() {
        return isAdjacentClimbBlock();
    }

 */

    public int getDestructiveness() {
        return destructiveness;
    }

    protected void setDestructiveness(int x) {
        destructiveness = x;
    }

    public float getPitchRate() {
        return pitchRate;
    }

    public float getGravity() {
        return gravityAcel;
    }

    protected void setGravity(float acceleration) {
        gravityAcel = acceleration;
    }

    public float getAirResistance() {
        return airResistance;
    }

    public float getGroundFriction() {
        return groundFriction;
    }

    public void setGroundFriction(float frictionCoefficient) {
        groundFriction = frictionCoefficient;
    }

    public Objective getAIGoal() {
        return currentObjective;
    }

    protected void setAIGoal(Objective objective) {
        currentObjective = objective;
    }

    public Objective getPrevAIGoal() {
        return prevObjective;
    }

    protected void setPrevAIGoal(Objective objective) {
        prevObjective = objective;
    }

    @Override
    public float getBlockPathCost(PathNode prevNode, PathNode node) {
        return calcBlockPathCost(prevNode, node, world);
    }

    @Override
    public void getPathOptionsFromNode(PathNode currentNode, PathfinderIM pathFinder) {
        calcPathOptions(world != null ? world : terrainMap, currentNode, pathFinder);
    }

    public int getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(int mode) {
        debugMode = mode;
        onDebugChange();
    }

    public boolean isAdjacentClimbBlock() {
        return getDataManager().get(IS_ADJACENT_CLIMB_BLOCK);
    }

    public void setAdjacentClimbBlock(boolean flag) {
        if (!world.isRemote) getDataManager().set(IS_ADJACENT_CLIMB_BLOCK, flag);
    }

    public boolean checkForAdjacentClimbBlock() {
        BlockPos pos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);
        IBlockState blockState = world.getBlockState(pos);
        if (blockState == null) return false;
        return (blockState.getBlock().isLadder(blockState, world, pos, this));
    }

    public boolean canSwimHorizontal() {
        return true;
    }

    // TODO: Fix this
    // @Override
    // protected void attackEntity(Entity entity, float f) {
    // if ((attackTime <= 0) && (f < 2.0F)
    // && (entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY)
    // && (entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY)) {
    // attackTime = 38;
    // attackEntityAsMob(entity);
    // }
    // }

    public boolean canSwimVertical() {
        return true;
    }

    @Override
    public void acquiredByNexus(Nexus nexus) {
        if ((nexus == null) && (!alwaysIndependent)) {
            this.nexus = nexus;
            nexusBound = true;
        }
    }

    /*
    @Override
    protected void dropFewItems(boolean flag, int amount) {
        if (rand.nextInt(4) == 0) {
            entityDropItem(new ItemStack(BlocksAndItems.itemSmallRemnants), 0f);
        }
    }

     */

    @Override
    public void setDead() {
        super.setDead();
        if ((getHealth() <= 0.0F) && (nexus != null)) nexus.registerMobDied();
    }

    public void setEntityIndependent() {
        nexus = null;
        nexusBound = false;
        alwaysIndependent = true;
    }

    @Override
    public void setJumping(boolean flag) {
        super.setJumping(flag);
        if (!world.isRemote) getDataManager().set(IS_JUMPING, flag);
    }

    @Override
    protected void updateAITasks() {
        world.getProfiler().startSection("Invasion Entity AI");
        ticksExisted++;
        despawnEntity();
        getEntitySenses().clearSensingCache();
        targetTasksIM.onUpdateTasks();
        updateAITick();
        tasksIM.onUpdateTasks();
        getNavigatorNew().onUpdateNavigation();
        getLookHelper().onUpdateLook();
        getMoveHelper().onUpdateMoveHelper();
        getJumpHelper().doJump();
        world.getProfiler().endSection();
    }

    @Override
    protected void updateAITick() {
        super.updateAITick();
        if (getAttackTarget() != null) {
            currentObjective = Objective.TARGET_ENTITY;
        } else if (nexus != null) {
            currentObjective = Objective.BREAK_NEXUS;
        } else {
            currentObjective = Objective.CHILL;
        }
    }

    @Override
    public boolean canDespawn() {
        return !nexusBound;
    }

    protected void sunlightDamageTick() {
        if (isImmuneToFire) {
            damageEntity(DamageSource.GENERIC, 3.0F);
        } else {
            setFire(8);
        }
    }

    @Override
    protected void dealFireDamage(int i) {
        super.dealFireDamage(i * flammability);
    }

    protected float calcBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap) {
        float multiplier = 1.0F;
        if ((terrainMap instanceof IBlockAccessExtended)) {
            int mobDensity = ((IBlockAccessExtended) terrainMap).getLayeredData(node.pos) & 0x7;
            multiplier += mobDensity * 3;
        }

        if ((node.pos.y > prevNode.pos.y) && (getCollide(terrainMap, node.pos) == 2)) {
            multiplier += 2.0F;
        }

        if (blockHasLadder(terrainMap, new BlockPos(node.pos))) {
            multiplier += 5.0F;
        }

        if (node.action == PathAction.SWIM) {
            multiplier *= ((node.pos.y <= prevNode.pos.y)
                    && (!terrainMap.isAirBlock(new BlockPos(node.pos.addVector(0d, 1d, 0d)))) ? 3.0F : 1.0F);
            return prevNode.distanceTo(node) * 1.3F * multiplier;
        }

        Block block = terrainMap.getBlockState(new BlockPos(node.pos)).getBlock();
        return prevNode.distanceTo(node) * (block.getExplosionResistance(null)) * multiplier;
    }

    protected void calcPathOptions(PathNode currentNode, PathfinderIM pathFinder) {
        if ((currentNode.pos.y <= 0) || (currentNode.pos.y > 255)) return;

        calcPathOptionsVertical(world, currentNode, pathFinder);

        if ((currentNode.action == PathAction.DIG) && (!canStandAt(world, new BlockPos(currentNode.pos)))) {
            return;
        }

        int height = getJumpHeight();
        for (int i = 1; i <= height; i++) {
            if (getCollide(world, currentNode.pos.addVector(0d, i, 0d)) == 0) {
                height = i - 1;
            }
        }

        int maxFall = 8;
        for (int i = 0; i < 4; i++) {
            if (currentNode.action != PathAction.NONE) {
                if ((i == 0) && (currentNode.action == PathAction.LADDER_UP_NX)) height = 0;
                if ((i == 1) && (currentNode.action == PathAction.LADDER_UP_PX)) height = 0;
                if ((i == 2) && (currentNode.action == PathAction.LADDER_UP_NZ)) height = 0;
                if ((i == 3) && (currentNode.action == PathAction.LADDER_UP_PZ)) height = 0;
            }
            int yOffset = 0;
            int currentY = MathHelper.floor(currentNode.pos.y) + height;
            boolean passedLevel = false;
            do {
                yOffset = getNextLowestSafeYOffset(terrainMap,
                        new BlockPos(currentNode.pos.x + Coords.offsetAdjX[i], currentY, currentNode.pos.z + Coords.offsetAdjZ[i]),
                        maxFall + currentY - MathHelper.floor(currentNode.pos.y));
                if (yOffset > 0)
                    break;
                if (yOffset > -maxFall) {
                    pathFinder.addNode(new Vec3d(
                                    currentNode.pos.x + Coords.offsetAdjX[i], currentY + yOffset + 1, currentNode.pos.z + Coords.offsetAdjZ[i]),
                            PathAction.NONE);
                }

                currentY += yOffset - 1;

                if ((!passedLevel) && (currentY <= currentNode.pos.y)) {
                    passedLevel = true;
                    if (currentY != currentNode.pos.y) {
                        addAdjacent(terrainMap,
                                new BlockPos(currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])),
                                currentNode, pathFinder);
                    }

                }

            }

            while (currentY >= currentNode.pos.y);
        }

        if (canSwimHorizontal()) {
            for (int i = 0; i < 4; i++) {
                Vec3d vec = currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i]);
                if (getCollide(terrainMap, vec) == -1) pathFinder.addNode(vec, PathAction.SWIM);
            }
        }
    }

    protected void calcPathOptionsVertical(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        Vec3d vecAbove = currentNode.pos.addVector(0d, 1d, 0d);
        Vec3d vecBelow = currentNode.pos.addVector(0d, -1d, 0d);
        BlockPos posAbove = new BlockPos(vecAbove);
        BlockPos posBelow = new BlockPos(vecBelow);
        int collideAbove = getCollide(terrainMap, posAbove);
        int collideBelow = getCollide(terrainMap, posBelow);

        if (collideAbove > 0) {
            if (world.getBlockState(posAbove).getBlock() == Blocks.LADDER) {
                switch (world.getBlockState(posAbove).get(LadderBlock.FACING)) {
                    case EAST:
                        action = PathAction.LADDER_UP_PX;
                        break;
                    case WEST:
                        action = PathAction.LADDER_UP_NX;
                        break;
                    case NORTH:
                        action = PathAction.LADDER_UP_PZ;
                        break;
                    case SOUTH:
                        action = PathAction.LADDER_UP_NZ;
                        break;
                    default:
                        action = PathAction.NONE;
                }

                switch (currentNode.action) {
                    case NONE:
                        pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), action);
                        break;
                    case LADDER_UP_PX:
                    case LADDER_UP_NX:
                    case LADDER_UP_PZ:
                    case LADDER_UP_NZ:
                        if (action == currentNode.action) {
                            pathFinder.addNode(vecAbove, action);
                        }
                        break;
                    default:
                        pathFinder.addNode(vecAbove, action);
                }
            } else if (getCanClimb()) {
                if (isAdjacentSolidBlock(terrainMap, posAbove)) pathFinder.addNode(vecAbove, PathAction.NONE);
            }
        }

        if (getCanDigDown()) {
            if (collideBelow == 2) {
                pathFinder.addNode(vecBelow, PathAction.DIG);
            } else if (collideBelow == 1) {
                int maxFall = 5;
                int yOffset = getNextLowestSafeYOffset(terrainMap, posBelow, maxFall);
                if (yOffset <= 0) pathFinder.addNode(vecBelow, PathAction.NONE);
            }
        }

        if (canSwimVertical()) {
            if (collideBelow == -1) pathFinder.addNode(currentNode.pos, PathAction.SWIM);
            if (collideAbove == -1) pathFinder.addNode(currentNode.pos, PathAction.SWIM);
        }
    }

    protected void addAdjacent(IBlockAccess terrainMap, BlockPos pos, PathNode currentNode, PathfinderIM pathFinder) {
        addAdjacent(terrainMap, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), currentNode, pathFinder);
    }

    protected void addAdjacent(IBlockAccess terrainMap, Vec3d pos, PathNode currentNode, PathfinderIM pathFinder) {
        if (getCollide(terrainMap, pos) <= 0) return;
        if (getCanClimb()) {
            if (isAdjacentSolidBlock(terrainMap, new BlockPos(pos))) pathFinder.addNode(pos, PathAction.NONE);
        } else if (terrainMap.getBlockState(new BlockPos(pos)).getBlock() == Blocks.LADDER) {
            pathFinder.addNode(pos, PathAction.NONE);
        }
    }

    protected int getNextLowestSafeYOffset(IBlockAccess terrainMap, BlockPos pos, int maxOffsetMagnitude) {
        for (int i = 0; (i + pos.getY() > 0) && (i < maxOffsetMagnitude); i--) {
            boolean flag0 = canStandAtAndIsValid(world != null ? world : terrainMap, pos.up(i)); //if the entity can stand on the block
            boolean flag1 = canSwimHorizontal(); //If the entity can swim
            boolean flag2 = getCollide(world != null ? world : terrainMap, pos.up(i)) == -1; //If the block is liquid
            if (flag0 || (flag1 && flag2)) return i;
        }
        return 1;
    }

    protected boolean canStandOnBlock(int x, int y, int z) {
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return (!block.isAir()) && (!block.isPassable(terrainMap, new BlockPos(x, y, z))) && (!avoidsBlock(block));
    }

    public byte getTier() {
        return getDataManager().get(TIER);
    }

    protected boolean getLightLevelBelow8() {
        BlockPos blockPos = new BlockPos(getPosX(), getBoundingBox().minY, getPosZ());

        if (world.getLightFor(EnumSkyBlock.SKY, blockPos) > rand.nextInt(32)) return false;
        int l = world.getBlockLightOpacity(blockPos);

        if (world.isThundering()) {
            int i1 = world.getSkylightSubtracted();
            world.setSkylightSubtracted(10);
            l = world.getBlockLightOpacity(blockPos);
            world.setSkylightSubtracted(i1);
        }
        return l <= rand.nextInt(8);
    }

    public void transitionAIGoal(Objective newObjective) {
        prevObjective = currentObjective;
        currentObjective = newObjective;

    }

    abstract protected void onDebugChange();

    @Override
    public Nexus getNexus() {
        return nexus;
    }

    public boolean isNexusBound() {
        return nexusBound;
    }
}