package invasion.entity.monster;

import invasion.IBlockAccessExtended;
import invasion.INotifyTask;
import invasion.Invasion;
import invasion.entity.ICanDig;
import invasion.entity.ITerrainDig;
import invasion.entity.TerrainDigger;
import invasion.entity.TerrainModifier;
import invasion.entity.ai.*;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathAction;
import invasion.entity.ai.navigator.PathNode;
import invasion.nexus.Nexus;
import invasion.util.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InvadingZombieEntity extends InvadingEntity implements ICanDig {

    private static final DataParameter<Boolean> META_CHANGED = EntityDataManager.createKey(InvadingZombieEntity.class, DataSerializers.BOOLEAN); //29
    private static final DataParameter<Integer> FLAVOUR = EntityDataManager.createKey(InvadingZombieEntity.class, DataSerializers.VARINT); //28
    private static final DataParameter<Boolean> IS_SWINGING = EntityDataManager.createKey(InvadingZombieEntity.class, DataSerializers.BOOLEAN); //27
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    private final TerrainModifier terrainModifier;
    private final TerrainDigger terrainDigger;
    private boolean metaChanged;
    //private int tier;
    private int flavour;
    //private ItemStack defaultHeldItem;
    private Item itemDrop;
    private float dropChance;
    private int swingTimer;

    public InvadingZombieEntity(World world) {
        this(world, null);
    }

    public InvadingZombieEntity(World world, Nexus nexus) {
        super(world, nexus);
        terrainModifier = new TerrainModifier(this, 2.0F);
        terrainDigger = new TerrainDigger(this, terrainModifier, 1.0F);
        dropChance = 0.0F;

        setAttributes(getTier(), flavour);
        floatsInWater = true;
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(META_CHANGED, metaChanged = world == null || world.isRemote);
        getDataManager().register(FLAVOUR, flavour = 0);
        getDataManager().register(IS_SWINGING, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (metaChanged != getDataManager().get(META_CHANGED)) {
            metaChanged = getDataManager().get(META_CHANGED);
            //setTexture(getDataManager().get(TEXTURE));
            //if(tier != getDataManager().get(TIER)) setTier(getDataManager().get(TIER));
            if (flavour != getDataManager().get(FLAVOUR)) setFlavour(getDataManager().get(FLAVOUR));
        }
        if ((!world.isRemote) && (flammability >= 20) && (isBurning())) doFireball();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        updateAnimation();
        updateSound();
    }

    @Override
    public void onPathSet() {
        terrainModifier.cancelTask();
    }

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

    @Override
    public String toString() {
        return "IMZombie-T" + getTier();
    }

    @Override
    public IBlockAccess getTerrain() {
        return world;
    }

    //TODO: Removed Override annotation
	/*public ItemStack getHeldItem() {
		return defaultHeldItem;
		
	}*/

    @Override
    public boolean avoidsBlock(Block block) {
        if ((isImmuneToFire) && ((block == Blocks.FIRE) || (block == Blocks.FLOWING_LAVA) || (block == Blocks.LAVA))) {
            return false;
        }
        return super.avoidsBlock(block);
    }

    @Override
    public float getBlockRemovalCost(BlockPos pos) {
        return getBlockStrength(pos) * 20.0F;
    }

    @Override
    public boolean canClearBlock(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return (state.getBlock() == Blocks.AIR) || (isBlockDestructible(world, pos, state));
    }

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

    public boolean isBigRenderTempHack() {
        return getTier() == 3;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return (getTier() == 3) && (isSprinting()) ? chargeAttack(entity) : super.attackEntityAsMob(entity);
    }

    @Override
    public boolean canBePushed() {
        return getTier() != 3;
    }

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

    @Override
    public boolean canBreatheUnderwater() {
        return (getTier() == 2) && (flavour == 2);
    }

    @Override
    public boolean isBlockDestructible(IBlockAccess terrainMap, BlockPos pos, IBlockState state) {
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

        return (!isTooSteep) && (super.isBlockDestructible(terrainMap, pos, state));
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
    public String getSpecies() {
        return "Zombie";
    }

	/*@Override
	public int getTier() {
		return tier < 3 ? 2 : 3;
	}*/

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putInt("flavour", flavour);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);

        setTexture(nbt.getInt("textureId"));
        flavour = nbttagcompound.getInteger("flavour");
        //tier = nbttagcompound.getInteger("tier");
        if (getTier() == 0) setTier(1);
        setFlavour(flavour);
        //setTier(tier);
        //super.readEntityFromNBT(nbttagcompound);
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

    public void setFlavour(int flavour) {
        getDataManager().set(FLAVOUR, flavour);
        this.flavour = flavour;
        setAttributes(getTier(), flavour);
    }

    @Override
    protected void sunlightDamageTick() {
        if ((getTier() == 2) && (flavour == 2)) {
            damageEntity(DamageSource.GENERIC, 3.0F);
        } else {
            super.sunlightDamageTick();
        }
    }

    protected void updateAnimation() {
        updateAnimation(false);
    }

    public void updateAnimation(boolean override) {
        if ((!world.isRemote) && ((terrainModifier.isBusy()) || override)) setSwinging(true);

        int swingSpeed = getSwingSpeed();
        if (isSwinging()) {
            swingTimer += 1;
            if (swingTimer >= swingSpeed) {
                swingTimer = 0;
                setSwinging(false);
            }
        } else {
            swingTimer = 0;
        }
        swingProgress = (float) swingTimer / (float) swingSpeed;
    }

    protected boolean isSwinging() {
        return getDataManager().get(IS_SWINGING);
    }

    protected void setSwinging(boolean flag) {
        //isSwingInProgress=flag;
        //getDataWatcher().updateObject(27, Byte.valueOf((byte) (flag == true ? 1 : 0)));
        getDataManager().set(IS_SWINGING, flag);
    }

    protected void updateSound() {
        if (terrainModifier.isBusy()) {
            if (--throttled2 <= 0) {
                //world.playSoundAtEntity(this, "invmod:scrape", 0.85F, 1.0F / (rand.nextFloat() * 0.5F + 1.0F));
                playSound(SoundHandler.scrape1, 0.85F, 1.0F / (rand.nextFloat() * 0.5F + 1.0F));
                throttled2 = (45 + rand.nextInt(20));
            }
        }
    }

    protected int getSwingSpeed() {
        return 10;
    }

    protected boolean chargeAttack(Entity entity) {
        int knockback = 4;
        entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength + 3);
        entity.addVelocity(-MathHelper.sin(rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F, 0.4D, MathHelper.cos(rotationYaw * 3.141593F / 180.0F) * knockback * 0.5F);
        setSprinting(false);
        playSound(SoundEvents.ENTITY_GENERIC_BIG_FALL, 1f, 1f);
        return true;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        terrainModifier.onUpdate();
    }

    protected ITerrainDig getTerrainDig() {
        return terrainDigger;
    }

    //TODO: Removed Override annotation
	/*protected String getLivingSound() {
		if (getTier() == 3) {
			return rand.nextInt(3) == 0 ? "invmod:bigzombie1" : null;
		}
	
		return "mob.zombie.say";
	}*/

    @Override
    protected SoundEvent getAmbientSound() {
        if (getTier() == 3) return rand.nextInt(3) == 0 ? SoundHandler.bigzombie1 : null;
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    //@Override
    //protected SoundEvent getHurtSound()
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    /*
    @Override
    protected Item getDropItem() {
        return Items.ROTTEN_FLESH;
    }

    @Override
    protected void dropFewItems(boolean flag, int bonus) {
        super.dropFewItems(flag, bonus);
        if (rand.nextFloat() < 0.35F) {
            dropItem(Items.ROTTEN_FLESH, 1);
        }

        if ((itemDrop != null) && (rand.nextFloat() < dropChance)) {
            entityDropItem(new ItemStack(itemDrop, 1, 0), 0.0F);
        }
    }

     */

    private void setAttributes(int tier, int flavour) {
        setMaxHealthAndHealth(Invasion.getMobHealth(this));
        setGender(1);
        if (tier == 1) {
            //tier = 1;
            setName("Zombie");
            setBaseMoveSpeedStat(0.19F);
            selfDamage = 3;
            maxSelfDamage = 6;
            flammability = 3;
            if (flavour == 0) {
                attackStrength = 4;
                maxDestructiveness = 2;
                setDestructiveness(2);
            } else if (flavour == 1) {
                attackStrength = 6;
                maxDestructiveness = 0;
                //defaultHeldItem = new ItemStack(Items.WOODEN_SWORD, 1);
                setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.WOODEN_SWORD));
                itemDrop = Items.WOODEN_SWORD;
                dropChance = 0.2F;
                setDestructiveness(0);
            }
        } else if (tier == 2) {
            //tier = 2;
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
        } else if (tier == 3) {
            //tier = 3;
            if (flavour == 0) {
                setName("Zombie Brute");
                setBaseMoveSpeedStat(0.17F);
                attackStrength = 18;
                selfDamage = 4;
                maxSelfDamage = 20;
                maxDestructiveness = 2;
                flammability = 4;
                dropChance = 0.0F;
                setDestructiveness(2);
            }
        }
    }

    private void doFireball() {
        BlockPos pos = new BlockPos(getPositionVector());
        for (int xOffset = -1; xOffset < 2; xOffset++)
            for (int yOffset = -1; yOffset < 2; yOffset++)
                for (int zOffset = -1; zOffset < 2; zOffset++) {
                    if ((world.isAirBlock(pos.add(xOffset, yOffset, zOffset)) || (world.getBlockState(pos.add(xOffset, yOffset, zOffset)).getMaterial().getCanBurn()))) {
                        world.setBlockState(pos.add(xOffset, yOffset, zOffset), Blocks.FIRE.getDefaultState());
                    }
                }

        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(1.5D, 1.5D, 1.5D));
        for (int el = entities.size() - 1; el >= 0; el--) {
            entities.get(el).setFire(8);
        }
        attackEntityFrom(DamageSource.IN_FIRE, 500.0F);
    }
}
