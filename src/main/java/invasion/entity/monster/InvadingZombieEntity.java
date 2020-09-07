package invasion.entity.monster;

import invasion.entity.ICanDig;
import invasion.init.ModEntityTypes;
import invasion.init.ModSounds;
import invasion.nexus.Nexus;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InvadingZombieEntity extends InvadingEntity implements ICanDig {

    private static final DataParameter<Integer> FLAVOUR = EntityDataManager.createKey(InvadingZombieEntity.class, DataSerializers.VARINT); //28
    private static final DataParameter<Boolean> COVERED_IN_TAR = EntityDataManager.createKey(InvadingZombieEntity.class, DataSerializers.BOOLEAN);

    /*
    private final TerrainModifier terrainModifier;
    private final TerrainDigger terrainDigger;
    private boolean metaChanged;

     */

    private int textureIndex;
    private int swingTimer;

    public InvadingZombieEntity(EntityType<? extends InvadingZombieEntity> type, World world) {
        super(type, world, null);
    }

    public InvadingZombieEntity(World world, Nexus nexus, byte tier) {
        super(ModEntityTypes.INVADING_ZOMBIE.get(), world, nexus, tier);
        if (tier < 1 || tier > 3) throw new IllegalArgumentException("Tier must be between 1 and 3");
        getDataManager().set(FLAVOUR, 0);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(FLAVOUR, 0);
        getDataManager().register(COVERED_IN_TAR, false);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        int flavour = getDataManager().get(FLAVOUR);
        switch (getTier()) {
            case 1:
                getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.19f);
                selfDamage = 6;
                maxSelfDamage = 6;
                flammability = 3;
                if (flavour == 0) {
                    getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4);
                    setDestructiveness(2);
                    maxDestructiveness = 2;
                } else {
                    getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6);
                    setDestructiveness(0);
                    maxDestructiveness = 0;
                    setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
                }
            case 2:
                if (flavour == 0) {
                    setName("Zombie");
                    setBaseMoveSpeedStat(0.19F);
                    attackStrength = 7;
                    selfDamage = 4;
                    maxSelfDamage = 12;
                    maxDestructiveness = 2;
                    flammability = 4;
                    itemDrop = Items.IRON_CHESTPLATE;
                    setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                    dropChance = 0.25F;
                    setDestructiveness(2);
                } else if (flavour == 1) {
                    setName("Zombie Marauder");
                    setBaseMoveSpeedStat(0.19F);
                    attackStrength = 10;
                    selfDamage = 3;
                    maxSelfDamage = 9;
                    maxDestructiveness = 0;
                    //itemDrop = Items.IRON_SWORD;
                    dropChance = 0.25F;
                    //defaultHeldItem = new ItemStack(Items.IRON_SWORD, 1);
                    setHeldItem(EnumHand.MAIN_HAND, new ItemStack(rand.nextBoolean() ? Items.IRON_SWORD : Items.IRON_AXE));
                    setHeldItem(EnumHand.OFF_HAND, new ItemStack(rand.nextBoolean() ? Items.IRON_SWORD : Items.IRON_AXE));
                    itemDrop = getHeldItem(rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND).getItem();
                    setDestructiveness(0);
                } else if (flavour == 2) {
                    setName("Tar Zombie");
                    setBaseMoveSpeedStat(0.19F);
                    attackStrength = 5;
                    selfDamage = 3;
                    maxSelfDamage = 9;
                    maxDestructiveness = 2;
                    flammability = 30;
                    floatsInWater = false;
                    setDestructiveness(2);
                } else if (flavour == 3) {
                    setName("Zombie Pigman");
                    setBaseMoveSpeedStat(0.25F);
                    attackStrength = 8;
                    maxDestructiveness = 2;
                    isImmuneToFire = true;
                    //defaultHeldItem = new ItemStack(Items.GOLDEN_SWORD, 1);
                    setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
                    setDestructiveness(2);
                }
            case 3:
                getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17f);
                getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(18);
                selfDamage = 4;
                maxSelfDamage = 20;
                maxDestructiveness = 2;
                flammability = 4;
                setDestructiveness(2);

        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(0, new SwimGoal(this));
        // goalSelector.addGoal(1, new MeleeAttackGoal(PlayerEntity.class,  );
        goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 15));
        goalSelector.addGoal(8, new LookAtGoal(this, MoulderingCreeperEntity.class, 15));
        goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if ((!world.isRemote) && (flammability >= 20) && (isBurning())) doFireball();
    }

    /*
    @Override
    public void onPathSet() {
        terrainModifier.cancelTask();
    }

     */
/*
    @Override
    protected void initEntityAI() {
        //added entityaiswimming and increased all other tasksordernumers with 1
        tasksIM = new EntityAITasks(world.profiler);
        tasksIM.addTask(0, new EntityAISwimming(this));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityPlayer.class, 40));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityPlayerMP.class, 40));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityGolem.class, 30));
        tasksIM.addTask(2, new EntityAIAttackNexus(this));
        tasksIM.addTask(3, new EntityAIWaitForEngy(this, 4.0F, true));
        tasksIM.addTask(4, new EntityAIKillEntity(this, EntityLiving.class, 40));
        tasksIM.addTask(5, new EntityAIGoToNexus(this));
        //tasks.addTask(5, new EntityAIMoveTowardsNexus(this));
        tasksIM.addTask(6, new EntityAIWanderIM(this));
        tasksIM.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasksIM.addTask(8, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));
        tasksIM.addTask(8, new EntityAILookIdle(this));


        targetTasksIM = new EntityAITasks(world.profiler);
        targetTasksIM.addTask(0, new EntityAITargetRetaliate(this, EntityLiving.class, Config.NIGHTSPAWNS_MOB_SENSERANGE));
        targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, Config.NIGHTSPAWNS_MOB_SIGHTRANGE, true));
        targetTasksIM.addTask(5, new EntityAIHurtByTarget(this, false));

        if (getTier() == 3) {
            tasksIM.addTask(4, new EntityAIStoop(this));
            tasksIM.addTask(3, new EntityAISprint(this));
        } else {
            //track players from sensing them
            targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, Config.NIGHTSPAWNS_MOB_SENSERANGE, false));
            targetTasksIM.addTask(3, new EntityAITargetOnNoNexusPath(this, PigEngyEntity.class, 3.5F));
        }
    }

 */

    @Override
    public int getTextureIndex() {
        return textureIndex;
    }

    @Override
    public float getBlockRemovalCost(BlockPos pos) {
        return getBlockStrength(pos) * 20.0F;
    }

    @Override
    public boolean canClearBlock(BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return (blockState.getBlock() == Blocks.AIR) || (isBlockDestructible(world, pos, blockState));
    }
/*
    @Override
    public boolean onPathBlocked(Path path, INotifyTask notifee) {
        if ((!path.isFinished()) && ((isNexusBound()) || (getAttackTarget() != null))) {

            if ((path.getFinalPathPoint().distanceTo(path.getIntendedTarget()) > 2.2D) && (path.getCurrentPathIndex() + 2 >= path.getCurrentPathLength() / 2)) {
                return false;
            }
            PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());

            return terrainDigger.askClearPosition(new BlockPos(node.pos), notifee, 1.0F);
        }
        return false;
    }

 */

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return (getTier() == 3) && (isSprinting()) ? chargeAttack(entity) : super.attackEntityAsMob(entity);
    }

    @Override
    public boolean canBePushed() {
        return getTier() != 3;
    }
/* todo did this change anything?

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
        if (getTier() == 3) return;
        isAirBorne = true;
        float f = MathHelper.sqrt(par3 * par3 + par5 * par5);
        float f1 = 0.4F;
        motionX /= 2.0D;
        motionY /= 2.0D;
        motionZ /= 2.0D;
        motionX -= par3 / f * f1;
        motionY += f1;
        motionZ -= par5 / f * f1;

        if (motionY > 0.4000000059604645D) motionY = 0.4000000059604645D;
    }

 */

    /*
    @Override
    public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap) {
        if ((getTier() == 2) && (flavour == 2) && (node.action == PathAction.SWIM)) {
            float multiplier = 1.0F;
            if ((terrainMap instanceof IBlockAccessExtended)) {
                int mobDensity = ((IBlockAccessExtended) terrainMap).getLayeredData(node.pos) & 0x7;
                multiplier += mobDensity * 3;
            }

            if ((node.pos.y > prevNode.pos.y) && (getCollide(terrainMap, node.pos) == 2)) {
                multiplier += 2.0F;
            }

            return prevNode.distanceTo(node) * 1.2F * multiplier;
        }

        return super.getBlockPathCost(prevNode, node, terrainMap);
    }

     */

    @Override
    public boolean canBreatheUnderwater() {
        return (getTier() == 2) && (flavour == 2);
    }

    // @Override
    public boolean isBlockDestructible(IBlockReader world, BlockPos pos, BlockState state) {
        if (getDestructiveness() == 0) return false;

        BlockPos position = getCurrentTargetPos();
        int dY = position.getY() - pos.getY();
        boolean isTooSteep = false;
        if (dY > 0) {
            dY += 8;
            int dX = position.getX() - pos.getX();
            int dZ = position.getZ() - pos.getZ();
            double dXZ = Math.sqrt(dX * dX + dZ * dZ) + 1.E-005D;
            isTooSteep = dY / dXZ > 2.144D;
        }

        return (!isTooSteep) && (super.isBlockDestructible(world, pos, state));
    }

    @Override
    public void onFollowingEntity(Entity entity) {
        if (entity == null) {
            setDestructiveness(1);
        } else if (((entity instanceof PigEngyEntity)) || ((entity instanceof MoulderingCreeperEntity))) {
            setDestructiveness(0);
        } else {
            setDestructiveness(1);
        }
    }

    public float scaleAmount() {
        if (getTier() == 2) return 1.12F;
        if (getTier() == 3) return 1.21F;
        return 1.0F;
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("flavour", flavour);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        setFlavour(nbt.getByte("flavour"));
    }

    @Override
    public void setTier(int tier) {
        super.setTier(tier);
        setAttributes(tier, flavour);
        if (getTextureId() == 0) {
            if (tier == 1) {
                setTexture(rand.nextBoolean() ? 0 : 1);
            } else if (tier == 2) {
                if (flavour == 2) {
                    setTexture(5);
                } else if (flavour == 3) {
                    setTexture(3);
                } else {
                    setTexture(rand.nextBoolean() ? 2 : 4);
                }
            } else if (tier == 3) {
                setTexture(6);
            }
        }
    }


    @Override
    protected void sunlightDamageTick() {
        if ((getTier() == 2) && (flavour == 2)) {
            damageEntity(DamageSource.ON_FIRE, 3.0F);
        } else {
            super.sunlightDamageTick();
        }
    }
    /*
    protected void updateSound() {
        if (terrainModifier.isBusy()) {
            if (--throttled2 <= 0) {
                //world.playSoundAtEntity(this, "invmod:scrape", 0.85F, 1.0F / (rand.nextFloat() * 0.5F + 1.0F));
                playSound(SoundHandler.scrape1, 0.85F, 1.0F / (rand.nextFloat() * 0.5F + 1.0F));
                throttled2 = (45 + rand.nextInt(20));
            }
        }
    }

     */

    protected int getSwingSpeed() {
        return 10;
    }

    protected boolean chargeAttack(Entity entity) {
        int knockback = 4;
        entity.attackEntityFrom(DamageSource.causeMobDamage(this), (int) getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue() + 3);
        entity.addVelocity(-MathHelper.sin(rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F, 0.4D, MathHelper.cos(rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F);
        setSprinting(false);
        playSound(SoundEvents.ENTITY_GENERIC_BIG_FALL, 1f, 1f);
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (getTier() == 3) return rand.nextInt(3) == 0 ? ModSounds.ENTITY_BIG_ZOMBIE_AMBIENT.get() : null;
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    private void doFireball() {
        for (int xOffset = -1; xOffset < 2; xOffset++)
            for (int yOffset = -1; yOffset < 2; yOffset++)
                for (int zOffset = -1; zOffset < 2; zOffset++) {
                    BlockPos pos = getPosition().add(xOffset, yOffset, zOffset);
                    if ((world.isAirBlock(pos) && world.isTopSolid(pos, this))) { //TODO another way to check if top is solid
                        world.setBlockState(pos.add(xOffset, yOffset, zOffset), Blocks.FIRE.getDefaultState());
                    }
                }

        world.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(1.5d, 1.5d, 1.5d)).forEach(entity -> entity.setFire(8));
        attackEntityFrom(DamageSource.IN_FIRE, 500.0F);
    }
}
