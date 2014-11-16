package invmod.common.entity;

import invmod.common.IBlockAccessExtended;
import invmod.common.INotifyTask;
import invmod.common.IPathfindable;
import invmod.common.SparrowAPI;
import invmod.common.mod_Invasion;
import invmod.common.nexus.INexusAccess;
import invmod.common.util.CoordsInt;
import invmod.common.util.Distance;
import invmod.common.util.IPosition;
import invmod.common.util.MathUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class EntityIMLiving extends EntityCreature implements IMob, IPathfindable, IPosition, IHasNexus, SparrowAPI
{
    private final NavigatorIM bo;
    private final PathNavigateAdapter oldNavAdapter;
    private PathCreator pathSource;
    protected Goal currentGoal;
    protected Goal prevGoal;
    protected EntityAITasks tasks;
    protected EntityAITasks targetTasks;
    private IMMoveHelper i;
    private MoveState moveState;
    private float rotationRoll;
    private float rotationYawHeadIM;
    private float rotationPitchHead;
    private float prevRotationRoll;
    private float prevRotationYawHeadIM;
    private float prevRotationPitchHead;
    private int debugMode;
    private float airResistance;
    private float groundFriction;
    private float gravityAcel;
    private float moveSpeed;
    private float moveSpeedBase;
    private float turnRate;
    private float pitchRate;
    private int rallyCooldown;
    private IPosition currentTargetPos;
    private IPosition lastBreathExtendPos;
    private String simplyID;
    private String name;
    private String renderLabel;
    private boolean shouldRenderLabel;
    private int gender;
    private boolean isHostile;
    private boolean creatureRetaliates;
    protected INexusAccess targetNexus;
    protected int attackStrength;
    protected float attackRange;
    private float maxHealth;
    protected int selfDamage;
    protected int maxSelfDamage;
    protected int maxDestructiveness;
    protected float blockRemoveSpeed;
    protected boolean floatsInWater;
    private CoordsInt collideSize;
    private boolean canClimb;
    private boolean canDig;
    private boolean nexusBound;
    private boolean alwaysIndependent;
    private boolean burnsInDay;
    private int jumpHeight;
    private int aggroRange;
    private int senseRange;
    private int stunTimer;
    protected int throttled;
    protected int throttled2;
    protected int pathThrottle;
    protected int destructionTimer;
    protected int flammability;
    protected int destructiveness;
    protected Entity j;
    protected static final int META_CLIMB_STATE = 20;
    protected static final byte META_CLIMBABLE_BLOCK = 21;
    protected static final byte META_JUMPING = 22;
    protected static final byte META_MOVESTATE = 23;
    protected static final byte META_ROTATION = 24;
    protected static final byte META_RENDERLABEL = 25;
    protected static final float DEFAULT_SOFT_STRENGTH = 2.5F;
    protected static final float DEFAULT_HARD_STRENGTH = 5.5F;
    protected static final float DEFAULT_SOFT_COST = 2.0F;
    protected static final float DEFAULT_HARD_COST = 3.2F;
    protected static final float AIR_BASE_COST = 1.0F;
    protected static final Map<Integer, Float> blockCosts = new HashMap();
    private static final Map<Integer, Float> blockStrength = new HashMap();
    private static final Map<Integer, BlockSpecial> blockSpecials = new HashMap();
    private static final Map<Integer, Integer> blockType = new HashMap();
    protected static List<Integer> unDestructableBlocks = Arrays.asList(Block.bedrock.blockID, Block.commandBlock.blockID, Block.endPortalFrame.blockID, Block.ladder.blockID, Block.chest.blockID);
    public EntityIMLiving(World world)
    {
        this(world, null);
    }

    public EntityIMLiving(World world, INexusAccess nexus)
    {
        super(world);
        this.targetNexus = nexus;
        this.currentGoal = Goal.NONE;
        this.prevGoal = Goal.NONE;
        this.moveState = MoveState.STANDING;
        this.tasks = new EntityAITasks(world.theProfiler);
        this.targetTasks = new EntityAITasks(world.theProfiler);
        this.pathSource = new PathCreator(700, 50);
        this.bo = new NavigatorIM(this, this.pathSource);
        this.oldNavAdapter = new PathNavigateAdapter(this.bo);
        this.i = new IMMoveHelper(this);
        this.collideSize = new CoordsInt(MathHelper.floor_double(this.width + 1.0F), MathHelper.floor_double(this.height + 1.0F), MathHelper.floor_double(this.width + 1.0F));
        this.moveSpeedBase = 0.26F;
        this.moveSpeed = this.moveSpeedBase;
        this.turnRate = 30.0F;
        this.pitchRate = 2.0F;
        CoordsInt initCoords = new CoordsInt(0, 0, 0);
        this.currentTargetPos = initCoords;
        this.lastBreathExtendPos = initCoords;
        this.simplyID = "needID";
        this.renderLabel = "";
        this.shouldRenderLabel = false;
        this.gender = 0;
        this.isHostile = true;
        this.creatureRetaliates = true;
        this.debugMode = 0;
        this.airResistance = 0.9995F;
        this.groundFriction = 0.546F;
        this.gravityAcel = 0.08F;
        this.attackStrength = 2;
        this.attackRange = 0.0F;
        setMaxHealth(20.0F);
        setHealth(20.0F);
        this.selfDamage = 2;
        this.maxSelfDamage = 6;
        this.flammability = 2;
        this.isImmuneToFire = false;
        this.canClimb = false;
        this.canDig = true;
        this.floatsInWater = true;
        this.alwaysIndependent = false;
        this.jumpHeight = 1;
        this.experienceValue = 5;
        this.maxDestructiveness = 0;
        this.blockRemoveSpeed = 1.0F;

        if (nexus != null)
        {
            this.nexusBound = true;
            this.burnsInDay = false;
            this.aggroRange = 12;
            this.senseRange = 6;
        }
        else
        {
            this.nexusBound = false;
            this.burnsInDay = mod_Invasion.getNightMobsBurnInDay();
            this.aggroRange = mod_Invasion.getNightMobSightRange();
            this.senseRange = mod_Invasion.getNightMobSenseRange();
        }

        this.hasAttacked = false;
        this.destructionTimer = 0;
        this.destructiveness = 0;
        this.throttled = 0;
        this.throttled2 = 0;
        this.pathThrottle = 0;
        //debugTest
        this.setShouldRenderLabel(debugMode == 1);
        this.dataWatcher.addObject(20, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(21, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(22, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(23, Integer.valueOf(this.moveState.ordinal()));
        this.dataWatcher.addObject(24, Integer.valueOf(MathUtil.packAnglesDeg(this.rotationRoll, this.rotationYawHeadIM, this.rotationPitchHead, 0.0F)));
        this.dataWatcher.addObject(25, "");
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.prevRotationRoll = this.rotationRoll;
        this.prevRotationYawHeadIM = this.rotationYawHeadIM;
        this.prevRotationPitchHead = this.rotationPitchHead;

        if (this.worldObj.isRemote)
        {
            this.moveState = MoveState.values()[this.dataWatcher.getWatchableObjectInt(23)];
            int packedAngles = this.dataWatcher.getWatchableObjectInt(24);
            this.rotationRoll = MathUtil.unpackAnglesDeg_1(packedAngles);
            this.rotationYawHeadIM = MathUtil.unpackAnglesDeg_2(packedAngles);
            this.rotationPitchHead = MathUtil.unpackAnglesDeg_3(packedAngles);
            this.renderLabel = this.dataWatcher.getWatchableObjectString(25);
        }
        else
        {
            int packedAngles = MathUtil.packAnglesDeg(this.rotationRoll, this.rotationYawHeadIM, this.rotationPitchHead, 0.0F);

            if (packedAngles != this.dataWatcher.getWatchableObjectInt(24))
            {
                this.dataWatcher.updateObject(24, Integer.valueOf(packedAngles));
            }

            if (!this.renderLabel.equals(this.dataWatcher.getWatchableObjectString(25)))
            {
                this.dataWatcher.updateObject(25, this.renderLabel);
            }
        }
    }

    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        if (this.worldObj.isRemote)
        {
            if (this.dataWatcher.getWatchableObjectByte(22) == 1)
            {
                this.isJumping = true;
            }
            else
            {
                this.isJumping = false;
            }
        }
        else
        {
            setAdjacentClimbBlock(checkForAdjacentClimbBlock());
        }

        if (getAir() == 190)
        {
            this.lastBreathExtendPos = new CoordsInt(getXCoord(), getYCoord(), getZCoord());
        }
        else if (getAir() == 0)
        {
            IPosition pos = new CoordsInt(getXCoord(), getYCoord(), getZCoord());

            if (Distance.distanceBetween(this.lastBreathExtendPos, pos) > 4.0D)
            {
                this.lastBreathExtendPos = pos;
                setAir(180);
            }
        }

        if (this.simplyID == "needID")
            ;
    }

    public void onLivingUpdate()
    {
        if (!this.nexusBound)
        {
            float brightness = getBrightness(1.0F);

            if ((brightness > 0.5F) || (this.posY < 55.0D))
            {
                this.entityAge += 2;
            }

            if ((getBurnsInDay()) && (this.worldObj.isDaytime()) && (!this.worldObj.isRemote))
            {
                if ((brightness > 0.5F) && (this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) && (this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F))
                {
                    sunlightDamageTick();
                }
            }
        }

        super.onLivingUpdate();
    }

    public boolean attackEntityFrom(DamageSource damagesource, float damage)
    {
        if (super.attackEntityFrom(damagesource, damage))
        {
            Entity entity = damagesource.getEntity();

            if ((this.riddenByEntity == entity) || (this.ridingEntity == entity))
            {
                return true;
            }

            if (entity != this)
            {
                this.j = entity;
            }

            return true;
        }

        return false;
    }

    public boolean stunEntity(int ticks)
    {
        if (this.stunTimer < ticks)
        {
            this.stunTimer = ticks;
        }

        this.motionX = 0.0D;
        this.motionZ = 0.0D;
        return true;
    }

    public boolean attackEntityAsMob(Entity entity)
    {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), this.attackStrength);
    }

    public boolean attackEntityAsMob(Entity entity, int damageOverride)
    {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), damageOverride);
    }

    public void moveEntityWithHeading(float x, float z)
    {
        if (isInWater())
        {
            double y = this.posY;
            moveFlying(x, z, isAIEnabled() ? 0.04F : 0.02F);
            moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8D;
            this.motionY *= 0.8D;
            this.motionZ *= 0.8D;
            this.motionY -= 0.02D;

            if ((this.isCollidedHorizontally) && (isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6D - this.posY + y, this.motionZ)))
            {
                this.motionY = 0.3D;
            }
        }
        else if (handleLavaMovement())
        {
            double y = this.posY;
            moveFlying(x, z, isAIEnabled() ? 0.04F : 0.02F);
            moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;

            if ((this.isCollidedHorizontally) && (isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6D - this.posY + y, this.motionZ)))
            {
                this.motionY = 0.3D;
            }
        }
        else
        {
            float groundFriction = 0.91F;
            float landMoveSpeed;

            if (this.onGround)
            {
                groundFriction = getGroundFriction();
                int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

                if (i > 0)
                {
                    groundFriction = Block.blocksList[i].slipperiness * 0.91F;
                }

                landMoveSpeed = getAIMoveSpeed();
                landMoveSpeed *= 0.162771F / (groundFriction * groundFriction * groundFriction);
            }
            else
            {
                landMoveSpeed = this.jumpMovementFactor;
            }

            moveFlying(x, z, landMoveSpeed);

            if (isOnLadder())
            {
                float maxLadderXZSpeed = 0.15F;

                if (this.motionX < -maxLadderXZSpeed)
                {
                    this.motionX = (-maxLadderXZSpeed);
                }

                if (this.motionX > maxLadderXZSpeed)
                {
                    this.motionX = maxLadderXZSpeed;
                }

                if (this.motionZ < -maxLadderXZSpeed)
                {
                    this.motionZ = (-maxLadderXZSpeed);
                }

                if (this.motionZ > maxLadderXZSpeed)
                {
                    this.motionZ = maxLadderXZSpeed;
                }

                this.fallDistance = 0.0F;

                if (this.motionY < -0.15D)
                {
                    this.motionY = -0.15D;
                }

                if ((isHoldingOntoLadder()) || ((isSneaking()) && (this.motionY < 0.0D)))
                {
                    this.motionY = 0.0D;
                }
                else if ((this.worldObj.isRemote) && (this.isJumping))
                {
                    this.motionY += 0.04D;
                }
            }

            moveEntity(this.motionX, this.motionY, this.motionZ);

            if ((this.isCollidedHorizontally) && (isOnLadder()))
            {
                this.motionY = 0.2D;
            }

            this.motionY -= getGravity();
            this.motionY *= this.airResistance;
            this.motionX *= groundFriction * this.airResistance;
            this.motionZ *= groundFriction * this.airResistance;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double dX = this.posX - this.prevPosX;
        double dZ = this.posZ - this.prevPosZ;
        float limbEnergy = MathHelper.sqrt_double(dX * dX + dZ * dZ) * 4.0F;

        if (limbEnergy > 1.0F)
        {
            limbEnergy = 1.0F;
        }

        this.limbSwingAmount += (limbEnergy - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    public void moveFlying(float strafeAmount, float forwardAmount, float movementFactor)
    {
        float unit = MathHelper.sqrt_float(strafeAmount * strafeAmount + forwardAmount * forwardAmount);

        if (unit < 0.01F)
        {
            return;
        }

        if (unit < 20.0F)
        {
            unit = 1.0F;
        }

        unit = movementFactor / unit;
        strafeAmount *= unit;
        forwardAmount *= unit;
        float com1 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
        float com2 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
        this.motionX += strafeAmount * com2 - forwardAmount * com1;
        this.motionZ += forwardAmount * com2 + strafeAmount * com1;
    }

//not sure why, but this needed to be removed in order to let the mobs swim
//	public boolean handleWaterMovement() {
//		if (this.floatsInWater) {
//			return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
//		}
//
//		double vX = this.motionX;
//		double vY = this.motionY;
//		double vZ = this.motionZ;
//		boolean isInWater = this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
//		this.motionX = vX;
//		this.motionY = vY;
//		this.motionZ = vZ;
//		return isInWater;
//	}

    public void rally(Entity leader)
    {
        this.rallyCooldown = 300;
    }

    public void onFollowingEntity(Entity entity)
    {
    }

    public void onPathSet()
    {
    }

    public void onBlockRemoved(int x, int y, int z, int id)
    {
        if (getHealth() > this.maxHealth - this.maxSelfDamage)
        {
            attackEntityFrom(DamageSource.generic, this.selfDamage);
        }

        if ((this.throttled == 0) && ((id == 3) || (id == 2) || (id == 12) || (id == 13)))
        {
            this.worldObj.playSoundAtEntity(this, "step.gravel", 1.4F, 1.0F / (this.rand.nextFloat() * 0.6F + 1.0F));
            this.throttled = 5;
        }
        else
        {
            this.worldObj.playSoundAtEntity(this, "step.stone", 1.4F, 1.0F / (this.rand.nextFloat() * 0.6F + 1.0F));
            this.throttled = 5;
        }
    }

    public boolean avoidsBlock(int id)
    {
        if ((id == Block.fire.blockID) || (id == Block.bedrock.blockID) || (id == Block.lavaStill.blockID) || (id == Block.lavaMoving.blockID) || (id == Block.cactus.blockID))
        {
            return true;
        }

        return false;
    }

    public boolean ignoresBlock(int id)
    {
        if ((id == Block.tallGrass.blockID) || (id == Block.deadBush.blockID) || (id == Block.plantRed.blockID) || (id == Block.plantYellow.blockID) || (id == Block.mushroomBrown.blockID) || (id == Block.mushroomRed.blockID) || (id == Block.pressurePlatePlanks.blockID) || (id == Block.pressurePlateIron.blockID) || (id == Block.pressurePlateStone.blockID))
        {
            return true;
        }

        return false;
    }

    public boolean isBlockDestructible(IBlockAccess terrainMap, int x, int y, int z, int id)
    {
        //check if mobgriefing is enabled
        boolean mobgriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

        if (mobgriefing)
        {
            if (unDestructableBlocks.contains(id) || id == 0 || blockHasLadder(terrainMap, x, y, z))
            {
                return false;
            }

            if ((id == Block.doorIron.blockID) || (id == Block.doorWood.blockID) || (id == Block.trapdoor.blockID))
            {
                return true;
            }

            if (Block.blocksList[id].blockMaterial.isSolid())
            {
                return true;
            }
        }

        return false;
    }

    public boolean canEntityBeDetected(Entity entity)
    {
        float distance = getDistanceToEntity(entity);
        return (distance <= getSenseRange()) || ((canEntityBeSeen(entity)) && (distance <= getAggroRange()));
    }

    public double findDistanceToNexus()
    {
        if (this.targetNexus == null)
        {
            return Double.MAX_VALUE;
        }

        double x = this.targetNexus.getXCoord() + 0.5D - this.posX;
        double y = this.targetNexus.getYCoord() - this.posY + this.height * 0.5D;
        double z = this.targetNexus.getZCoord() + 0.5D - this.posZ;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Entity findPlayerToAttack()
    {
        EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, getSenseRange());

        if (entityPlayer != null)
        {
            return entityPlayer;
        }

        entityPlayer = this.worldObj.getClosestPlayerToEntity(this, getAggroRange());

        if ((entityPlayer != null) && (canEntityBeSeen(entityPlayer)))
        {
            return entityPlayer;
        }

        return null;
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setBoolean("alwaysIndependent", this.alwaysIndependent);
        super.writeEntityToNBT(nbttagcompound);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        this.alwaysIndependent = nbttagcompound.getBoolean("alwaysIndependent");

        if (this.alwaysIndependent)
        {
            setAggroRange(mod_Invasion.getNightMobSightRange());
            setSenseRange(mod_Invasion.getNightMobSenseRange());
            setBurnsInDay(mod_Invasion.getNightMobsBurnInDay());
        }

        super.readEntityFromNBT(nbttagcompound);
    }

    public float getPrevRotationRoll()
    {
        return this.prevRotationRoll;
    }

    public float getRotationRoll()
    {
        return this.rotationRoll;
    }

    public float getPrevRotationYawHeadIM()
    {
        return this.prevRotationYawHeadIM;
    }

    public float getRotationYawHeadIM()
    {
        return this.rotationYawHeadIM;
    }

    public float getPrevRotationPitchHead()
    {
        return this.prevRotationPitchHead;
    }

    public float getRotationPitchHead()
    {
        return this.rotationPitchHead;
    }

    public int getXCoord()
    {
        return MathHelper.floor_double(this.posX);
    }

    public int getYCoord()
    {
        return MathHelper.floor_double(this.posY);
    }

    public int getZCoord()
    {
        return MathHelper.floor_double(this.posZ);
    }

    public float getAttackRange()
    {
        return this.attackRange;
    }

    public void setMaxHealth(float health)
    {
        this.maxHealth = health;
    }

    public void setMaxHealthAndHealth(float health)
    {
        this.maxHealth = health;
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double)health);
        setHealth(health);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        boolean lightFlag = false;

        if ((this.nexusBound) || (getLightLevelBelow8()))
        {
            lightFlag = true;
        }

        return (super.getCanSpawnHere()) && (lightFlag) && (this.worldObj.isBlockNormalCube(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY + 0.5D) - 1, MathHelper.floor_double(this.posZ)));
    }

    public MoveState getMoveState()
    {
        return this.moveState;
    }

    public float getMoveSpeedStat()
    {
        return this.moveSpeed;
    }

    public float getBaseMoveSpeedStat()
    {
        return this.moveSpeedBase;
    }

    public int getJumpHeight()
    {
        return this.jumpHeight;
    }

    public float getBlockStrength(int x, int y, int z)
    {
        return getBlockStrength(x, y, z, this.worldObj.getBlockId(x, y, z));
    }

    public float getBlockStrength(int x, int y, int z, int id)
    {
        return getBlockStrength(x, y, z, id, this.worldObj);
    }

    public boolean getCanClimb()
    {
        return this.canClimb;
    }

    public boolean getCanDigDown()
    {
        return this.canDig;
    }

    public int getAggroRange()
    {
        return this.aggroRange;
    }

    public int getSenseRange()
    {
        return this.senseRange;
    }

    public float getBlockPathWeight(int i, int j, int k)
    {
        if (this.nexusBound)
        {
            return 0.0F;
        }

        return 0.5F - this.worldObj.getLightBrightness(i, j, k);
    }

    public boolean getBurnsInDay()
    {
        return this.burnsInDay;
    }

    public int getDestructiveness()
    {
        return this.destructiveness;
    }

    public float getTurnRate()
    {
        return this.turnRate;
    }

    public float getPitchRate()
    {
        return this.pitchRate;
    }

    public float getGravity()
    {
        return this.gravityAcel;
    }

    public float getAirResistance()
    {
        return this.airResistance;
    }

    public float getGroundFriction()
    {
        return this.groundFriction;
    }

    public CoordsInt getCollideSize()
    {
        return this.collideSize;
    }

    public static BlockSpecial getBlockSpecial(int id)
    {
        if (blockSpecials.containsKey(Integer.valueOf(id)))
        {
            return (BlockSpecial) blockSpecials.get(Integer.valueOf(id));
        }

        return BlockSpecial.NONE;
    }

    public Goal getAIGoal()
    {
        return this.currentGoal;
    }

    public Goal getPrevAIGoal()
    {
        return this.prevGoal;
    }

    public PathNavigateAdapter getNavigator()
    {
        return this.oldNavAdapter;
    }

    public INavigation getNavigatorNew()
    {
        return this.bo;
    }

    public IPathSource getPathSource()
    {
        return this.pathSource;
    }

    public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap)
    {
        return calcBlockPathCost(prevNode, node, terrainMap);
    }

    public void getPathOptionsFromNode(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder)
    {
        calcPathOptions(terrainMap, currentNode, pathFinder);
    }

    public IPosition getCurrentTargetPos()
    {
        return this.currentTargetPos;
    }

    public IPosition[] getBlockRemovalOrder(int x, int y, int z)
    {
        if (MathHelper.floor_double(this.posY) >= y)
        {
            IPosition[] blocks = new IPosition[2];
            blocks[1] = new CoordsInt(x, y + 1, z);
            blocks[0] = new CoordsInt(x, y, z);
            return blocks;
        }

        IPosition[] blocks = new IPosition[3];
        blocks[2] = new CoordsInt(x, y, z);
        blocks[1] = new CoordsInt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) + this.collideSize.getYCoord(), MathHelper.floor_double(this.posZ));
        blocks[0] = new CoordsInt(x, y + 1, z);
        return blocks;
    }

    public IMMoveHelper getMoveHelper()
    {
        return this.i;
    }

    public INexusAccess getNexus()
    {
        return this.targetNexus;
    }

    public String getRenderLabel()
    {
        return this.renderLabel;
    }

    public int getDebugMode()
    {
        return this.debugMode;
    }

    @Override
    public boolean isHostile()
    {
        return this.isHostile;
    }

    public boolean isNeutral()
    {
        return this.creatureRetaliates;
    }

    public boolean isThreatTo(Entity entity)
    {
        if ((this.isHostile) && ((entity instanceof EntityPlayer)))
        {
            return true;
        }

        return false;
    }

    public Entity getAttackingTarget()
    {
        return getAttackTarget();
    }

    public boolean isStupidToAttack()
    {
        return false;
    }

    public boolean doNotVaporize()
    {
        return false;
    }

    public boolean isPredator()
    {
        return false;
    }

    public boolean isPeaceful()
    {
        return false;
    }

    public boolean isPrey()
    {
        return false;
    }

    public boolean isUnkillable()
    {
        return false;
    }

    public boolean isFriendOf(Entity par1entity)
    {
        return false;
    }

    public boolean isNPC()
    {
        return false;
    }

    public int isPet()
    {
        return 0;
    }

    public String getName()
    {
        return this.name;
    }

    public int getGender()
    {
        return this.gender;
    }

    public Entity getPetOwner()
    {
        return null;
    }

    public float getSize()
    {
        return this.height * this.width;
    }

    public String customStringAndResponse(String s)
    {
        return null;
    }

    public int getTier()
    {
        return 0;
    }

    public String getSimplyID()
    {
        return this.simplyID;
    }

    public boolean isNexusBound()
    {
        return this.nexusBound;
    }

    public boolean isHoldingOntoLadder()
    {
        return this.dataWatcher.getWatchableObjectByte(20) == 1;
    }

    public boolean isOnLadder()
    {
        return isAdjacentClimbBlock();
    }

    public boolean isAdjacentClimbBlock()
    {
        return this.dataWatcher.getWatchableObjectByte(21) == 1;
    }

    public boolean checkForAdjacentClimbBlock()
    {
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.boundingBox.minY);
        int var3 = MathHelper.floor_double(this.posZ);
        int var4 = this.worldObj.getBlockId(var1, var2, var3);
        return (Block.blocksList[var4] != null) && (Block.blocksList[var4].isLadder(this.worldObj, var1, var2, var3, this));
    }

    public boolean readyToRally()
    {
        return this.rallyCooldown == 0;
    }

    public boolean canSwimHorizontal()
    {
        return true;
    }

    public boolean canSwimVertical()
    {
        return true;
    }

    public boolean shouldRenderLabel()
    {
        return this.shouldRenderLabel;
    }

    public void acquiredByNexus(INexusAccess nexus)
    {
        if ((this.targetNexus == null) && (!this.alwaysIndependent))
        {
            this.targetNexus = nexus;
            this.nexusBound = true;
        }
    }

    public void setDead()
    {
        super.setDead();

        if ((getHealth() <= 0.0F) && (this.targetNexus != null))
        {
            this.targetNexus.registerMobDied();
        }
    }

    public void setEntityIndependent()
    {
        this.targetNexus = null;
        this.nexusBound = false;
        this.alwaysIndependent = true;
    }

    public void setSize(float width, float height)
    {
        super.setSize(width, height);
        this.collideSize = new CoordsInt(MathHelper.floor_double(width + 1.0F), MathHelper.floor_double(height + 1.0F), MathHelper.floor_double(width + 1.0F));
    }

    public void setBurnsInDay(boolean flag)
    {
        this.burnsInDay = flag;
    }

    public void setAggroRange(int range)
    {
        this.aggroRange = range;
    }

    public void setSenseRange(int range)
    {
        this.senseRange = range;
    }

    public void setIsHoldingIntoLadder(boolean flag)
    {
        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(20, Byte.valueOf((byte)(flag ? 1 : 0)));
        }
    }

    public void setJumping(boolean flag)
    {
        super.setJumping(flag);

        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(22, Byte.valueOf((byte)(flag ? 1 : 0)));
        }
    }

    public void setAdjacentClimbBlock(boolean flag)
    {
        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(21, Byte.valueOf((byte)(flag ? 1 : 0)));
        }
    }

    public void setRenderLabel(String label)
    {
        this.renderLabel = label;
    }

    public void setShouldRenderLabel(boolean flag)
    {
        this.shouldRenderLabel = flag;
    }

    public void setDebugMode(int mode)
    {
        this.debugMode = mode;
        onDebugChange();
    }

    protected void updateAITasks()
    {
        this.worldObj.theProfiler.startSection("Entity IM");
        this.entityAge += 1;
        despawnEntity();
        getEntitySenses().clearSensingCache();
        this.targetTasks.onUpdateTasks();
        updateAITick();
        this.tasks.onUpdateTasks();
        getNavigatorNew().onUpdateNavigation();
        getLookHelper().onUpdateLook();
        getMoveHelper().onUpdateMoveHelper();
        getJumpHelper().doJump();
        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITick()
    {
        if (this.rallyCooldown > 0)
        {
            this.rallyCooldown -= 1;
        }

        if (getAttackTarget() != null)
        {
            this.currentGoal = Goal.TARGET_ENTITY;
        }
        else if (this.targetNexus != null)
        {
            this.currentGoal = Goal.BREAK_NEXUS;
        }
        else
        {
            this.currentGoal = Goal.CHILL;
        }
    }

    protected boolean isAIEnabled()
    {
        return true;
    }

    protected boolean canDespawn()
    {
        return !this.nexusBound;
    }

    protected void setRotationRoll(float roll)
    {
        this.rotationRoll = roll;
    }

    public void setRotationYawHeadIM(float yaw)
    {
        this.rotationYawHeadIM = yaw;
    }

    protected void setRotationPitchHead(float pitch)
    {
        this.rotationPitchHead = pitch;
    }

    protected void setAttackRange(float range)
    {
        this.attackRange = range;
    }

    protected void setCurrentTargetPos(IPosition pos)
    {
        this.currentTargetPos = pos;
    }

    protected void attackEntity(Entity entity, float f)
    {
        if ((this.attackTime <= 0) && (f < 2.0F) && (entity.boundingBox.maxY > this.boundingBox.minY) && (entity.boundingBox.minY < this.boundingBox.maxY))
        {
            this.attackTime = 38;
            attackEntityAsMob(entity);
        }
    }

    protected void sunlightDamageTick()
    {
        setFire(8);
    }

    protected boolean onPathBlocked(Path path, INotifyTask asker)
    {
        return false;
    }

    protected void dealFireDamage(int i)
    {
        super.dealFireDamage(i * this.flammability);
    }

    protected void dropFewItems(boolean flag, int amount)
    {
        if (this.rand.nextInt(4) == 0)
        {
            entityDropItem(new ItemStack(mod_Invasion.itemRemnants, 1), 0.0F);
        }
    }

    protected float calcBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap)
    {
        float multiplier = 1.0F;

        if ((terrainMap instanceof IBlockAccessExtended))
        {
            int mobDensity = ((IBlockAccessExtended) terrainMap).getLayeredData(node.xCoord, node.yCoord, node.zCoord) & 0x7;
            multiplier += mobDensity * 3;
        }

        if ((node.yCoord > prevNode.yCoord) && (getCollide(terrainMap, node.xCoord, node.yCoord, node.zCoord) == 2))
        {
            multiplier += 2.0F;
        }

        if (blockHasLadder(terrainMap, node.xCoord, node.yCoord, node.zCoord))
        {
            multiplier += 5.0F;
        }

        if (node.action == PathAction.SWIM)
        {
            multiplier *= ((node.yCoord <= prevNode.yCoord) && (terrainMap.getBlockId(node.xCoord, node.yCoord + 1, node.zCoord) != 0) ? 3.0F : 1.0F);
            return prevNode.distanceTo(node) * 1.3F * multiplier;
        }

        int id = terrainMap.getBlockId(node.xCoord, node.yCoord, node.zCoord);

        if (blockCosts.containsKey(Integer.valueOf(id)))
        {
            return prevNode.distanceTo(node) * ((Float) blockCosts.get(Integer.valueOf(id))).floatValue() * multiplier;
        }

        if (Block.blocksList[id].isCollidable())
        {
            return prevNode.distanceTo(node) * 3.2F * multiplier;
        }

        return prevNode.distanceTo(node) * 1.0F * multiplier;
    }

    protected void calcPathOptions(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder)
    {
        if ((currentNode.yCoord <= 0) || (currentNode.yCoord > 255))
        {
            return;
        }

        calcPathOptionsVertical(terrainMap, currentNode, pathFinder);

        if ((currentNode.action == PathAction.DIG) && (!canStandAt(terrainMap, currentNode.xCoord, currentNode.yCoord, currentNode.zCoord)))
        {
            return;
        }

        int height = getJumpHeight();

        for (int i = 1; i <= height; i++)
        {
            if (getCollide(terrainMap, currentNode.xCoord, currentNode.yCoord + i, currentNode.zCoord) == 0)
            {
                height = i - 1;
            }
        }

        int maxFall = 8;

        for (int i = 0; i < 4; i++)
        {
            if (currentNode.action != PathAction.NONE)
            {
                if ((i == 0) && (currentNode.action == PathAction.LADDER_UP_NX))
                {
                    height = 0;
                }

                if ((i == 1) && (currentNode.action == PathAction.LADDER_UP_PX))
                {
                    height = 0;
                }

                if ((i == 2) && (currentNode.action == PathAction.LADDER_UP_NZ))
                {
                    height = 0;
                }

                if ((i == 3) && (currentNode.action == PathAction.LADDER_UP_PZ))
                {
                    height = 0;
                }
            }

            int yOffset = 0;
            int currentY = currentNode.yCoord + height;
            boolean passedLevel = false;

            do
            {
                yOffset = getNextLowestSafeYOffset(terrainMap, currentNode.xCoord + CoordsInt.offsetAdjX[i], currentY, currentNode.zCoord + CoordsInt.offsetAdjZ[i], maxFall + currentY - currentNode.yCoord);

                if (yOffset > 0)
                {
                    break;
                }

                if (yOffset > -maxFall)
                {
                    pathFinder.addNode(currentNode.xCoord + CoordsInt.offsetAdjX[i], currentY + yOffset, currentNode.zCoord + CoordsInt.offsetAdjZ[i], PathAction.NONE);
                }

                currentY += yOffset - 1;

                if ((!passedLevel) && (currentY <= currentNode.yCoord))
                {
                    passedLevel = true;

                    if (currentY != currentNode.yCoord)
                    {
                        addAdjacent(terrainMap, currentNode.xCoord + CoordsInt.offsetAdjX[i], currentNode.yCoord, currentNode.zCoord + CoordsInt.offsetAdjZ[i], currentNode, pathFinder);
                    }
                }
            }
            while (currentY >= currentNode.yCoord);
        }

        if (canSwimHorizontal())
        {
            for (int i = 0; i < 4; i++)
            {
                if (getCollide(terrainMap, currentNode.xCoord + CoordsInt.offsetAdjX[i], currentNode.yCoord, currentNode.zCoord + CoordsInt.offsetAdjZ[i]) == -1)
                {
                    pathFinder.addNode(currentNode.xCoord + CoordsInt.offsetAdjX[i], currentNode.yCoord, currentNode.zCoord + CoordsInt.offsetAdjZ[i], PathAction.SWIM);
                }
            }
        }
    }

    protected void calcPathOptionsVertical(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder)
    {
        int collideUp = getCollide(terrainMap, currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord);

        if (collideUp > 0)
        {
            if (terrainMap.getBlockId(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord) == Block.ladder.blockID)
            {
                int meta = terrainMap.getBlockMetadata(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord);
                PathAction action = PathAction.NONE;

                if (meta == 4)
                {
                    action = PathAction.LADDER_UP_PX;
                }
                else if (meta == 5)
                {
                    action = PathAction.LADDER_UP_NX;
                }
                else if (meta == 2)
                {
                    action = PathAction.LADDER_UP_PZ;
                }
                else if (meta == 3)
                {
                    action = PathAction.LADDER_UP_NZ;
                }

                if (currentNode.action == PathAction.NONE)
                {
                    pathFinder.addNode(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord, action);
                }
                else if ((currentNode.action == PathAction.LADDER_UP_PX) || (currentNode.action == PathAction.LADDER_UP_NX) || (currentNode.action == PathAction.LADDER_UP_PZ) || (currentNode.action == PathAction.LADDER_UP_NZ))
                {
                    if (action == currentNode.action)
                    {
                        pathFinder.addNode(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord, action);
                    }
                }
                else
                {
                    pathFinder.addNode(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord, action);
                }
            }
            else if (getCanClimb())
            {
                if (isAdjacentSolidBlock(terrainMap, currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord))
                {
                    pathFinder.addNode(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord, PathAction.NONE);
                }
            }
        }

        int below = getCollide(terrainMap, currentNode.xCoord, currentNode.yCoord - 1, currentNode.zCoord);
        int above = getCollide(terrainMap, currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord);

        if (getCanDigDown())
        {
            if (below == 2)
            {
                pathFinder.addNode(currentNode.xCoord, currentNode.yCoord - 1, currentNode.zCoord, PathAction.DIG);
            }
            else if (below == 1)
            {
                int maxFall = 5;
                int yOffset = getNextLowestSafeYOffset(terrainMap, currentNode.xCoord, currentNode.yCoord - 1, currentNode.zCoord, maxFall);

                if (yOffset <= 0)
                {
                    pathFinder.addNode(currentNode.xCoord, currentNode.yCoord - 1 + yOffset, currentNode.zCoord, PathAction.NONE);
                }
            }
        }

        if (canSwimVertical())
        {
            if (below == -1)
            {
                pathFinder.addNode(currentNode.xCoord, currentNode.yCoord - 1, currentNode.zCoord, PathAction.SWIM);
            }

            if (above == -1)
            {
                pathFinder.addNode(currentNode.xCoord, currentNode.yCoord + 1, currentNode.zCoord, PathAction.SWIM);
            }
        }
    }

    protected void addAdjacent(IBlockAccess terrainMap, int x, int y, int z, PathNode currentNode, PathfinderIM pathFinder)
    {
        if (getCollide(terrainMap, x, y, z) <= 0)
        {
            return;
        }

        if (getCanClimb())
        {
            if (isAdjacentSolidBlock(terrainMap, x, y, z))
            {
                pathFinder.addNode(x, y, z, PathAction.NONE);
            }
        }
        else if (terrainMap.getBlockId(x, y, z) == Block.ladder.blockID)
        {
            pathFinder.addNode(x, y, z, PathAction.NONE);
        }
    }

    protected boolean isAdjacentSolidBlock(IBlockAccess terrainMap, int x, int y, int z)
    {
        if ((this.collideSize.getXCoord() == 1) && (this.collideSize.getZCoord() == 1))
        {
            for (int i = 0; i < 4; i++)
            {
                int id = terrainMap.getBlockId(x + CoordsInt.offsetAdjX[i], y, z + CoordsInt.offsetAdjZ[i]);

                if ((id > 0) && (Block.blocksList[id].blockMaterial.isSolid()))
                {
                    return true;
                }
            }
        }
        else if ((this.collideSize.getXCoord() == 2) && (this.collideSize.getZCoord() == 2))
        {
            for (int i = 0; i < 8; i++)
            {
                int id = terrainMap.getBlockId(x + CoordsInt.offsetAdj2X[i], y, z + CoordsInt.offsetAdj2Z[i]);

                if ((id > 0) && (Block.blocksList[id].blockMaterial.isSolid()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getNextLowestSafeYOffset(IBlockAccess terrainMap, int x, int y, int z, int maxOffsetMagnitude)
    {
        for (int i = 0; (i + y > 0) && (i < maxOffsetMagnitude); i--)
        {
            if ((canStandAtAndIsValid(terrainMap, x, y + i, z)) || ((canSwimHorizontal()) && (getCollide(terrainMap, x, y + i, z) == -1)))
            {
                return i;
            }
        }

        return 1;
    }

    protected boolean canStandAt(IBlockAccess terrainMap, int x, int y, int z)
    {
        boolean isSolidBlock = false;

        for (int xOffset = x; xOffset < x + this.collideSize.getXCoord(); xOffset++)
        {
            for (int zOffset = z; zOffset < z + this.collideSize.getZCoord(); zOffset++)
            {
                int id = terrainMap.getBlockId(xOffset, y - 1, zOffset);

                if (id != 0)
                {
                    if (!Block.blocksList[id].getBlocksMovement(terrainMap, xOffset, y - 1, zOffset))
                    {
                        isSolidBlock = true;
                    }
                    else if (avoidsBlock(id))
                    {
                        return false;
                    }
                }
            }
        }

        return isSolidBlock;
    }

    protected boolean canStandAtAndIsValid(IBlockAccess terrainMap, int x, int y, int z)
    {
        if ((getCollide(terrainMap, x, y, z) > 0) && (canStandAt(terrainMap, x, y, z)))
        {
            return true;
        }

        return false;
    }

    protected boolean canStandOnBlock(IBlockAccess terrainMap, int x, int y, int z)
    {
        int id = terrainMap.getBlockId(x, y, z);

        if ((id != 0) && (!Block.blocksList[id].getBlocksMovement(terrainMap, x, y, z)) && (!avoidsBlock(id)))
        {
            return true;
        }

        return false;
    }

    protected boolean blockHasLadder(IBlockAccess terrainMap, int x, int y, int z)
    {
        for (int i = 0; i < 4; i++)
        {
            if (terrainMap.getBlockId(x + CoordsInt.offsetAdjX[i], y, z + CoordsInt.offsetAdjZ[i]) == Block.ladder.blockID)
            {
                return true;
            }
        }

        return false;
    }

    protected int getCollide(IBlockAccess terrainMap, int x, int y, int z)
    {
        boolean destructibleFlag = false;
        boolean liquidFlag = false;

        for (int xOffset = x; xOffset < x + this.collideSize.getXCoord(); xOffset++)
        {
            for (int yOffset = y; yOffset < y + this.collideSize.getYCoord(); yOffset++)
            {
                for (int zOffset = z; zOffset < z + this.collideSize.getZCoord(); zOffset++)
                {
                    int id = terrainMap.getBlockId(xOffset, yOffset, zOffset);

                    if (id > 0)
                    {
                        if ((id == Block.waterStill.blockID) || (id == Block.waterMoving.blockID) || (id == Block.lavaStill.blockID) || (id == Block.lavaMoving.blockID))
                        {
                            liquidFlag = true;
                        }
                        else if (!Block.blocksList[id].getBlocksMovement(terrainMap, xOffset, yOffset, zOffset))
                        {
                            if (isBlockDestructible(terrainMap, x, y, z, id))
                            {
                                destructibleFlag = true;
                            }
                            else
                            {
                                return 0;
                            }
                        }
                        else if (terrainMap.getBlockId(xOffset, yOffset - 1, zOffset) == Block.fence.blockID)
                        {
                            if (isBlockDestructible(terrainMap, x, y, z, Block.fence.blockID))
                            {
                                return 3;
                            }

                            return 0;
                        }

                        if (avoidsBlock(id))
                        {
                            return -2;
                        }
                    }
                }
            }
        }

        if (destructibleFlag)
        {
            return 2;
        }

        if (liquidFlag)
        {
            return -1;
        }

        return 1;
    }

    protected boolean getLightLevelBelow8()
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32))
        {
            return false;
        }

        int l = this.worldObj.getBlockLightValue(i, j, k);

        if (this.worldObj.isThundering())
        {
            int i1 = this.worldObj.skylightSubtracted;
            this.worldObj.skylightSubtracted = 10;
            l = this.worldObj.getBlockLightValue(i, j, k);
            this.worldObj.skylightSubtracted = i1;
        }

        return l <= this.rand.nextInt(8);
    }

    protected void setAIGoal(Goal goal)
    {
        this.currentGoal = goal;
    }

    protected void setPrevAIGoal(Goal goal)
    {
        this.prevGoal = goal;
    }

    public void transitionAIGoal(Goal newGoal)
    {
        this.prevGoal = this.currentGoal;
        this.currentGoal = newGoal;
    }

    protected void setMoveState(MoveState moveState)
    {
        this.moveState = moveState;

        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(23, Integer.valueOf(moveState.ordinal()));
        }
    }

    protected void setDestructiveness(int x)
    {
        this.destructiveness = x;
    }

    protected void setGravity(float acceleration)
    {
        this.gravityAcel = acceleration;
    }

    protected void setGroundFriction(float frictionCoefficient)
    {
        this.groundFriction = frictionCoefficient;
    }

    protected void setCanClimb(boolean flag)
    {
        this.canClimb = flag;
    }

    protected void setJumpHeight(int height)
    {
        this.jumpHeight = height;
    }

    protected void setBaseMoveSpeedStat(float speed)
    {
        this.moveSpeedBase = speed;
        this.moveSpeed = speed;
    }

    public void setMoveSpeedStat(float speed)
    {
        this.moveSpeed = speed;
        getNavigatorNew().setSpeed(speed);
        getMoveHelper().setMoveSpeed(speed);
    }

    public void resetMoveSpeed()
    {
        setMoveSpeedStat(this.moveSpeedBase);
        getNavigatorNew().setSpeed(this.moveSpeedBase);
    }

    public void setTurnRate(float rate)
    {
        this.turnRate = rate;
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    protected void setGender(int gender)
    {
        this.gender = gender;
    }

    protected void onDebugChange()
    {
    }

    public static int getBlockType(int id)
    {
        if (blockType.containsKey(Integer.valueOf(id)))
        {
            return ((Integer) blockType.get(Integer.valueOf(id))).intValue();
        }

        return 0;
    }

    public static float getBlockStrength(int x, int y, int z, int id, World world)
    {
        if (blockSpecials.containsKey(Integer.valueOf(id)))
        {
            BlockSpecial special = (BlockSpecial) blockSpecials.get(Integer.valueOf(id));

            if (special == BlockSpecial.CONSTRUCTION_1)
            {
                int bonus = 0;

                if (world.getBlockId(x, y - 1, z) == id)
                {
                    bonus++;
                }

                if (world.getBlockId(x, y + 1, z) == id)
                {
                    bonus++;
                }

                if (world.getBlockId(x + 1, y, z) == id)
                {
                    bonus++;
                }

                if (world.getBlockId(x - 1, y, z) == id)
                {
                    bonus++;
                }

                if (world.getBlockId(x, y, z + 1) == id)
                {
                    bonus++;
                }

                if (world.getBlockId(x, y, z - 1) == id)
                {
                    bonus++;
                }

                return ((Float) blockStrength.get(Integer.valueOf(id))).floatValue() * (1.0F + bonus * 0.1F);
            }

            if (special == BlockSpecial.CONSTRUCTION_STONE)
            {
                int bonus = 0;
                int adjId = world.getBlockId(x, y - 1, z);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                adjId = world.getBlockId(x, y + 1, z);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                adjId = world.getBlockId(x - 1, y, z);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                adjId = world.getBlockId(x + 1, y, z);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                adjId = world.getBlockId(x, y, z - 1);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                adjId = world.getBlockId(x, y, z + 1);

                if ((adjId == 1) || (adjId == 4) || (adjId == 48) || (adjId == 98))
                {
                    bonus++;
                }

                return ((Float) blockStrength.get(Integer.valueOf(id))).floatValue() * (1.0F + bonus * 0.1F);
            }
        }

        if (blockStrength.containsKey(Integer.valueOf(id)))
        {
            return ((Float) blockStrength.get(Integer.valueOf(id))).floatValue();
        }

        return 2.5F;
    }

    public static void putBlockStrength(int id, float strength)
    {
        blockStrength.put(Integer.valueOf(id), Float.valueOf(strength));
    }

    public static void putBlockCost(int id, float cost)
    {
        blockCosts.put(Integer.valueOf(id), Float.valueOf(cost));
    }

    static
    {
        blockCosts.put(Integer.valueOf(0), Float.valueOf(1.0F));
        blockCosts.put(Integer.valueOf(Block.ladder.blockID), Float.valueOf(1.0F));
        blockCosts.put(Integer.valueOf(Block.stone.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.stoneBrick.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.cobblestone.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.cobblestoneMossy.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.brick.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.obsidian.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.blockIron.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.dirt.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.sand.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.gravel.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.glass.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.leaves.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.doorIron.blockID), Float.valueOf(2.24F));
        blockCosts.put(Integer.valueOf(Block.doorWood.blockID), Float.valueOf(1.4F));
        blockCosts.put(Integer.valueOf(Block.trapdoor.blockID), Float.valueOf(1.4F));
        blockCosts.put(Integer.valueOf(Block.sandStone.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.wood.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.planks.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.blockGold.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.blockDiamond.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.fence.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.netherrack.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.netherBrick.blockID), Float.valueOf(3.2F));
        blockCosts.put(Integer.valueOf(Block.slowSand.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.glowStone.blockID), Float.valueOf(2.0F));
        blockCosts.put(Integer.valueOf(Block.tallGrass.blockID), Float.valueOf(1.0F));
        blockStrength.put(Integer.valueOf(0), Float.valueOf(0.01F));
        blockStrength.put(Integer.valueOf(Block.stone.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.stoneBrick.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.cobblestone.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.cobblestoneMossy.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.brick.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.obsidian.blockID), Float.valueOf(7.7F));
        blockStrength.put(Integer.valueOf(Block.blockIron.blockID), Float.valueOf(7.7F));
        blockStrength.put(Integer.valueOf(Block.dirt.blockID), Float.valueOf(3.125F));
        blockStrength.put(Integer.valueOf(Block.grass.blockID), Float.valueOf(3.125F));
        blockStrength.put(Integer.valueOf(Block.sand.blockID), Float.valueOf(2.5F));
        blockStrength.put(Integer.valueOf(Block.gravel.blockID), Float.valueOf(2.5F));
        blockStrength.put(Integer.valueOf(Block.glass.blockID), Float.valueOf(2.5F));
        blockStrength.put(Integer.valueOf(Block.leaves.blockID), Float.valueOf(1.25F));
        blockStrength.put(Integer.valueOf(Block.vine.blockID), Float.valueOf(1.25F));
        blockStrength.put(Integer.valueOf(Block.doorIron.blockID), Float.valueOf(15.4F));
        blockStrength.put(Integer.valueOf(Block.doorWood.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.sandStone.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.wood.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.planks.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.blockGold.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.blockDiamond.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.fence.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.netherrack.blockID), Float.valueOf(3.85F));
        blockStrength.put(Integer.valueOf(Block.netherBrick.blockID), Float.valueOf(5.5F));
        blockStrength.put(Integer.valueOf(Block.slowSand.blockID), Float.valueOf(2.5F));
        blockStrength.put(Integer.valueOf(Block.glowStone.blockID), Float.valueOf(2.5F));
        blockStrength.put(Integer.valueOf(Block.tallGrass.blockID), Float.valueOf(0.3F));
        blockStrength.put(Integer.valueOf(Block.dragonEgg.blockID), Float.valueOf(15.0F));
        blockSpecials.put(Integer.valueOf(Block.stone.blockID), BlockSpecial.CONSTRUCTION_STONE);
        blockSpecials.put(Integer.valueOf(Block.stoneBrick.blockID), BlockSpecial.CONSTRUCTION_STONE);
        blockSpecials.put(Integer.valueOf(Block.cobblestone.blockID), BlockSpecial.CONSTRUCTION_STONE);
        blockSpecials.put(Integer.valueOf(Block.cobblestoneMossy.blockID), BlockSpecial.CONSTRUCTION_STONE);
        blockSpecials.put(Integer.valueOf(Block.brick.blockID), BlockSpecial.CONSTRUCTION_1);
        blockSpecials.put(Integer.valueOf(Block.sandStone.blockID), BlockSpecial.CONSTRUCTION_1);
        blockSpecials.put(Integer.valueOf(112), BlockSpecial.CONSTRUCTION_1);
        blockSpecials.put(Integer.valueOf(Block.obsidian.blockID), BlockSpecial.DEFLECTION_1);
        blockType.put(Integer.valueOf(0), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.tallGrass.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.deadBush.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.plantRed.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.plantYellow.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.pressurePlateStone.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.pressurePlateIron.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.pressurePlatePlanks.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.pressurePlateGold.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.stoneButton.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.woodenButton.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.torchRedstoneIdle.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.torchRedstoneActive.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.torchWood.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.lever.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.reed.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.crops.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.carrot.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.potato.blockID), Integer.valueOf(1));
        blockType.put(Integer.valueOf(Block.fire.blockID), Integer.valueOf(2));
        blockType.put(Integer.valueOf(Block.bedrock.blockID), Integer.valueOf(2));
        blockType.put(Integer.valueOf(Block.lavaStill.blockID), Integer.valueOf(2));
        blockType.put(Integer.valueOf(Block.lavaMoving.blockID), Integer.valueOf(2));
    }
}