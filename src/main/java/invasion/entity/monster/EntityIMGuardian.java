package invasion.entity.monster;

import com.google.common.base.Predicate;
import invasion.entity.ICanDig;
import invasion.entity.TerrainDigger;
import invasion.entity.TerrainModifier;
import invasion.nexus.INexusAccess;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


public class EntityIMGuardian extends InvadingEntity implements ICanDig {

    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24
    private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(EntityIMGuardian.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityIMGuardian.class, DataSerializers.VARINT);
    private final TerrainModifier terrainModifier;
    private final TerrainDigger terrainDigger;
    private float clientSideTailAnimation;
    private float clientSideTailAnimationO;
    private float clientSideTailAnimationSpeed;
    private float clientSideSpikesAnimation;


    //Copied from EntityGuardian
    private float clientSideSpikesAnimationO;
    private EntityLivingBase targetedEntity;
    private int clientSideAttackTime;
    private boolean clientSideTouchedGround;
    private EntityAIWander wander;
    public EntityIMGuardian(World worldIn, INexusAccess nexus) {
        super(worldIn);
        this.experienceValue = 30;
        this.setSize(0.85F, 0.85F);
        this.moveHelper = new EntityIMGuardian.GuardianMoveHelper(this);
        this.clientSideTailAnimation = this.rand.nextFloat();
        this.clientSideTailAnimationO = this.clientSideTailAnimation;

        this.terrainModifier = new TerrainModifier(this, 3f);
        this.terrainDigger = new TerrainDigger(this, this.terrainModifier, 5f);
    }
    public EntityIMGuardian(World worldIn) {
        this(worldIn, null);
    }

    @Override
    public String getSpecies() {
        return "Guardian";
    }

    @Override
    public float getBlockRemovalCost(BlockPos pos) {
        return this.getBlockStrength(pos) * 15.0F;
    }

    @Override
    public boolean canClearBlock(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return (state.getBlock() == Blocks.AIR) || (this.isBlockDestructible(this.world, pos, state));
    }

    @Override
    public void onBlockRemoved(BlockPos pos, IBlockState state) {

    }

    @Override
    public IBlockAccess getTerrain() {
        return this.world;
    }

    @Override
    protected void initEntityAI() {
        EntityAIMoveTowardsRestriction entityaimovetowardsrestriction = new EntityAIMoveTowardsRestriction(this, 1.0D);
        this.wander = new EntityAIWander(this, 1.0D, 80);
        this.tasksIM.addTask(4, new EntityIMGuardian.AIGuardianAttack(this));
        this.tasksIM.addTask(5, entityaimovetowardsrestriction);
        this.tasksIM.addTask(7, this.wander);
        this.tasksIM.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasksIM.addTask(8, new EntityAIWatchClosest(this, EntityIMGuardian.class, 12.0F, 0.01F));
        this.tasksIM.addTask(9, new EntityAILookIdle(this));
        this.wander.setMutexBits(3);
        entityaimovetowardsrestriction.setMutexBits(3);
        this.targetTasksIM.addTask(1, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 10, true, false, new EntityIMGuardian.GuardianTargetSelector(this)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

	/*public static void func_189766_b(DataFixer p_189766_0_){
		EntityLiving.func_189752_a(p_189766_0_, "Guardian");
	}*/

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setElder(compound.getBoolean("Elder"));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Elder", this.isElder());
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate getNewNavigator(World worldIn) {
        return new PathNavigateSwimmer(this, worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(STATUS, Byte.valueOf((byte) 0));
        this.dataManager.register(TARGET_ENTITY, Integer.valueOf(0));
    }

    /**
     * Returns true if given flag is set
     */
    private boolean isSyncedFlagSet(int flagId) {
        return (this.dataManager.get(STATUS).byteValue() & flagId) != 0;
    }

    /**
     * Sets a flag state "on/off" on both sides (client/server) by using DataWatcher
     */
    private void setSyncedFlag(int flagId, boolean state) {
        byte b0 = this.dataManager.get(STATUS).byteValue();

        if (state) {
            this.dataManager.set(STATUS, Byte.valueOf((byte) (b0 | flagId)));
        } else {
            this.dataManager.set(STATUS, Byte.valueOf((byte) (b0 & ~flagId)));
        }
    }

    public boolean isMoving() {
        return this.isSyncedFlagSet(2);
    }

    private void setMoving(boolean moving) {
        this.setSyncedFlag(2, moving);
    }

    public int getAttackDuration() {
        return this.isElder() ? 60 : 80;
    }

    public boolean isElder() {
        return this.isSyncedFlagSet(4);
    }

    /**
     * Sets this Guardian to be an elder or not.
     */
    public void setElder(boolean elder) {
        this.setSyncedFlag(4, elder);

        if (elder) {
            this.setSize(1.9975F, 1.9975F);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
            this.enablePersistence();

            if (this.wander != null) this.wander.setExecutionChance(400);
        }
    }

    @SideOnly(Side.CLIENT)
    public void setElder() {
        this.setElder(true);
        this.clientSideSpikesAnimation = 1.0F;
        this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
    }

    public boolean hasTargetedEntity() {
        return this.dataManager.get(TARGET_ENTITY).intValue() != 0;
    }

    public EntityLivingBase getTargetedEntity() {
        if (!this.hasTargetedEntity()) return null;
        if (this.world.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            } else {
                Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY).intValue());

                if (entity instanceof EntityLivingBase) {
                    this.targetedEntity = (EntityLivingBase) entity;
                    return this.targetedEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getAttackTarget();
        }
    }

    private void setTargetedEntity(int entityId) {
        this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (STATUS.equals(key)) {
            if (this.isElder() && this.width < 1.0F) this.setSize(1.9975F, 1.9975F);
        } else if (TARGET_ENTITY.equals(key)) {
            this.clientSideAttackTime = 0;
            this.targetedEntity = null;
        }
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isElder() ? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDERGUARDIAN_AMBIENTLAND) : (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isElder() ? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND) : (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isElder() ? (this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND) : (this.isInWater() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.5F;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return this.world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + this.world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate() {
        if (this.world.isRemote) {
            this.clientSideTailAnimationO = this.clientSideTailAnimation;

            if (!this.isInWater()) {
                this.clientSideTailAnimationSpeed = 2.0F;

                if (this.motionY > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                    this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, this.getSoundCategory(), 1.0F, 1.0F, false);
                }

                this.clientSideTouchedGround = this.motionY < 0.0D && this.world.isBlockNormalCube((new BlockPos(this)).down(), false);
            } else if (this.isMoving()) {
                if (this.clientSideTailAnimationSpeed < 0.5F) {
                    this.clientSideTailAnimationSpeed = 4.0F;
                } else {
                    this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
                }
            } else {
                this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
            }

            this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
            this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;

            if (!this.isInWater()) {
                this.clientSideSpikesAnimation = this.rand.nextFloat();
            } else if (this.isMoving()) {
                this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
            } else {
                this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
            }

            if (this.isMoving() && this.isInWater()) {
                Vec3d vec3d = this.getLook(0.0F);

                for (int i = 0; i < 2; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width - vec3d.x * 1.5D, this.posY + this.rand.nextDouble() * (double) this.height - vec3d.y * 1.5D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width - vec3d.z * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (this.hasTargetedEntity()) {
                if (this.clientSideAttackTime < this.getAttackDuration()) ++this.clientSideAttackTime;

                EntityLivingBase entitylivingbase = this.getTargetedEntity();

                if (entitylivingbase != null) {
                    this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
                    this.getLookHelper().onUpdateLook();
                    double d5 = this.getAttackAnimationScale(0.0F);
                    double d0 = entitylivingbase.posX - this.posX;
                    double d1 = entitylivingbase.posY + (double) (entitylivingbase.height * 0.5F) - (this.posY + (double) this.getEyeHeight());
                    double d2 = entitylivingbase.posZ - this.posZ;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 = d0 / d3;
                    d1 = d1 / d3;
                    d2 = d2 / d3;
                    double d4 = this.rand.nextDouble();

                    while (d4 < d3) {
                        d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
                        this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + d0 * d4, this.posY + d1 * d4 + (double) this.getEyeHeight(), this.posZ + d2 * d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }
        }

        if (this.inWater) {
            this.setAir(300);
        } else if (this.onGround) {
            this.motionY += 0.5D;
            this.motionX += (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.motionZ += (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.rotationYaw = this.rand.nextFloat() * 360.0F;
            this.onGround = false;
            this.isAirBorne = true;
        }

        if (this.hasTargetedEntity()) {
            this.rotationYaw = this.rotationYawHead;
        }

        super.onLivingUpdate();
    }

    @SideOnly(Side.CLIENT)
    public float getTailAnimation(float p_175471_1_) {
        return this.clientSideTailAnimationO + (this.clientSideTailAnimation - this.clientSideTailAnimationO) * p_175471_1_;
    }

    @SideOnly(Side.CLIENT)
    public float getSpikesAnimation(float p_175469_1_) {
        return this.clientSideSpikesAnimationO + (this.clientSideSpikesAnimation - this.clientSideSpikesAnimationO) * p_175469_1_;
    }

    public float getAttackAnimationScale(float p_175477_1_) {
        return ((float) this.clientSideAttackTime + p_175477_1_) / (float) this.getAttackDuration();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (this.isElder()) {
            int i = 1200;
            int j = 1200;
            int k = 6000;
            int l = 2;

            if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
                Potion potion = MobEffects.MINING_FATIGUE;

                for (EntityPlayerMP entityplayermp : this.world.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
                    @Override
                    public boolean apply(@Nullable EntityPlayerMP p_apply_1_) {
                        return EntityIMGuardian.this.getDistanceSq(p_apply_1_) < 2500.0D && p_apply_1_.interactionManager.survivalOrAdventure();
                    }
                })) {
                    if (!entityplayermp.isPotionActive(potion) || entityplayermp.getActivePotionEffect(potion).getAmplifier() < 2 || entityplayermp.getActivePotionEffect(potion).getDuration() < 1200) {
                        entityplayermp.connection.sendPacket(new SPacketChangeGameState(10, 0.0F));
                        entityplayermp.addPotionEffect(new PotionEffect(potion, 6000, 2));
                    }
                }
            }

            if (!this.hasHome()) {
                this.setHomePosAndDistance(new BlockPos(this), 16);
            }
        }
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return this.isElder() ? LootTableList.ENTITIES_ELDER_GUARDIAN : LootTableList.ENTITIES_GUARDIAN;
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel() {
        return true;
    }

    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    @Override
    public boolean isNotColliding() {
        return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere() {
        return (this.rand.nextInt(20) == 0 || !this.world.canBlockSeeSky(new BlockPos(this))) && super.getCanSpawnHere();
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.isMoving() && !source.isMagicDamage() && source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) source.getTrueSource();
            if (!source.isExplosion()) entitylivingbase.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
        }
        if (this.wander != null) this.wander.makeUpdate();
        return super.attackEntityFrom(source, amount);
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    @Override
    public int getVerticalFaceSpeed() {
        return 180;
    }

    /**
     * Moves the entity based on the specified heading.
     */
	/*@Override
	public void moveEntityWithHeading(float strafe, float forward)
	{*/
    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {
        if (this.isServerWorld()) {
            if (this.isInWater()) {
                this.moveRelative(strafe, 0.1F, forward, 0.1F);
                this.setVelocity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.8999999761581421D;
                this.motionY *= 0.8999999761581421D;
                this.motionZ *= 0.8999999761581421D;

                if (!this.isMoving() && this.getAttackTarget() == null) this.motionY -= 0.005D;
            } else {
                //super.moveEntityWithHeading(strafe, forward);
                super.moveRelative(strafe, up, forward, friction);
            }
        } else {
            //super.moveEntityWithHeading(strafe, forward);
            super.moveRelative(strafe, up, forward, friction);
        }
    }

    static class AIGuardianAttack extends EntityAIBase {
        private final EntityIMGuardian theEntity;
        private int tickCounter;

        public AIGuardianAttack(EntityIMGuardian guardian) {
            this.theEntity = guardian;
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && (this.theEntity.isElder() || this.theEntity.getDistanceSq(this.theEntity.getAttackTarget()) > 9.0D);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting() {
            this.tickCounter = -10;
            this.theEntity.getNavigator().clearPath();
            this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
            this.theEntity.isAirBorne = true;
        }

        /**
         * Resets the task
         */
        @Override
        public void resetTask() {
            this.theEntity.setTargetedEntity(0);
            this.theEntity.setAttackTarget(null);
            this.theEntity.wander.makeUpdate();
        }

        /**
         * Updates the task
         */
        @Override
        public void updateTask() {
            EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
            this.theEntity.getNavigator().clearPath();
            this.theEntity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);

            if (!this.theEntity.canEntityBeSeen(entitylivingbase)) {
                this.theEntity.setAttackTarget(null);
            } else {
                ++this.tickCounter;

                if (this.tickCounter == 0) {
                    this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
                    this.theEntity.world.setEntityState(this.theEntity, (byte) 21);
                } else if (this.tickCounter >= this.theEntity.getAttackDuration()) {
                    float f = 1.0F;

                    if (this.theEntity.world.getDifficulty() == EnumDifficulty.HARD) f += 2.0F;

                    if (this.theEntity.isElder()) f += 2.0F;

                    entitylivingbase.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), f);
                    entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.theEntity), (float) this.theEntity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                    this.theEntity.setAttackTarget(null);
                }

                super.updateTask();
            }
        }
    }

    static class GuardianMoveHelper extends EntityMoveHelper {
        private final EntityIMGuardian EntityIMGuardian;

        public GuardianMoveHelper(EntityIMGuardian guardian) {
            super(guardian);
            this.EntityIMGuardian = guardian;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.EntityIMGuardian.getNavigator().noPath()) {
                double d0 = this.posX - this.EntityIMGuardian.posX;
                double d1 = this.posY - this.EntityIMGuardian.posY;
                double d2 = this.posZ - this.EntityIMGuardian.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                this.EntityIMGuardian.rotationYaw = this.limitAngle(this.EntityIMGuardian.rotationYaw, f, 90.0F);
                this.EntityIMGuardian.renderYawOffset = this.EntityIMGuardian.rotationYaw;
                float f1 = (float) (this.speed * this.EntityIMGuardian.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                this.EntityIMGuardian.setAIMoveSpeed(this.EntityIMGuardian.getAIMoveSpeed() + (f1 - this.EntityIMGuardian.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double) (this.EntityIMGuardian.ticksExisted + this.EntityIMGuardian.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos(this.EntityIMGuardian.rotationYaw * 0.017453292F);
                double d6 = Math.sin(this.EntityIMGuardian.rotationYaw * 0.017453292F);
                this.EntityIMGuardian.motionX += d4 * d5;
                this.EntityIMGuardian.motionZ += d4 * d6;
                d4 = Math.sin((double) (this.EntityIMGuardian.ticksExisted + this.EntityIMGuardian.getEntityId()) * 0.75D) * 0.05D;
                this.EntityIMGuardian.motionY += d4 * (d6 + d5) * 0.25D;
                this.EntityIMGuardian.motionY += (double) this.EntityIMGuardian.getAIMoveSpeed() * d1 * 0.1D;
                EntityLookHelper entitylookhelper = this.EntityIMGuardian.getLookHelper();
                double d7 = this.EntityIMGuardian.posX + d0 / d3 * 2.0D;
                double d8 = (double) this.EntityIMGuardian.getEyeHeight() + this.EntityIMGuardian.posY + d1 / d3;
                double d9 = this.EntityIMGuardian.posZ + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                this.EntityIMGuardian.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
                this.EntityIMGuardian.setMoving(true);
            } else {
                this.EntityIMGuardian.setAIMoveSpeed(0.0F);
                this.EntityIMGuardian.setMoving(false);
            }
        }
    }

    static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
        private final EntityIMGuardian parentEntity;

        public GuardianTargetSelector(EntityIMGuardian guardian) {
            this.parentEntity = guardian;
        }

        @Override
        public boolean apply(@Nullable EntityLivingBase p_apply_1_) {
            return (p_apply_1_ instanceof EntityPlayer || p_apply_1_ instanceof EntitySquid) && p_apply_1_.getDistanceSq(this.parentEntity) > 9.0D;
        }
    }

}
