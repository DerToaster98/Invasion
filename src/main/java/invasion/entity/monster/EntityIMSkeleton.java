package invasion.entity.monster;

import invasion.Invasion;
import invasion.entity.ai.*;
import invasion.nexus.Nexus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


public class EntityIMSkeleton extends InvadingEntity implements IRangedAttackMob {

    protected static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityIMSkeleton.class, DataSerializers.BOOLEAN);

    private static final ItemStack defaultHeldItem = new ItemStack(Items.BOW);
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24
    //Copied from EntitySkeleton
    private final EntityAIAttackRangedBowIM aiArrowAttack = new EntityAIAttackRangedBowIM(this, 1.0D, 20, 15.0F);
    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false) {
        @Override
        public void resetTask() {
            super.resetTask();
            EntityIMSkeleton.this.setSwingingArms(false);
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            EntityIMSkeleton.this.setSwingingArms(true);
        }
    };
    private final int tier;

    public EntityIMSkeleton(World world) {
        this(world, null);
    }

    public EntityIMSkeleton(World world, Nexus nexus) {
        super(world, nexus);
        this.tier = 1;
        //setBurnsInDay(true);

        this.setMaxHealthAndHealth(Invasion.getMobHealth(this));
        this.setName("Skeleton");
        this.setGender(0);
        this.setBaseMoveSpeedStat(0.21F);
        //this.setAI();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(SWINGING_ARMS, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasksIM = new EntityAITasks(this.world.profiler);
        this.tasksIM.addTask(0, new EntityAISwimming(this));
        this.tasksIM.addTask(1, new EntityAIKillWithArrow(this, EntityPlayer.class, 65, 16.0F));
        this.tasksIM.addTask(1, new EntityAIKillWithArrow(this, EntityPlayerMP.class, 65, 16.0F));
        //this.tasks.addTask(1, new EntityAIRallyBehindEntity(this, EntityIMCreeper.class, 4.0F));
        this.tasksIM.addTask(2, new EntityAIKillWithArrow(this, EntityLiving.class, 65, 16.0F));
        this.tasksIM.addTask(3, new EntityAIAttackNexus(this));
        this.tasksIM.addTask(4, new EntityAIGoToNexus(this));
        this.tasksIM.addTask(5, new EntityAIWanderIM(this));
        this.tasksIM.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasksIM.addTask(6, new EntityAILookIdle(this));
        this.tasksIM.addTask(6, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));

        this.targetTasksIM = new EntityAITasks(this.world.profiler);
        this.targetTasksIM.addTask(0, new EntityAISimpleTarget(this, EntityPlayer.class, this.getSenseRange(), false));
        this.targetTasksIM.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    //TODO: Removed Override annotation
    protected String getLivingSound() {
        return "mob.skeleton.say";
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        //return "mob.skeleton.death";
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
    }

    @Override
    public String getSpecies() {
        return "Skeleton";
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public String toString() {
        return "IMSkeleton-T" + this.getTier();
    }

    @Override
    protected void dropFewItems(boolean flag, int bonus) {
        super.dropFewItems(flag, bonus);
        int i = this.rand.nextInt(3);
        for (int j = 0; j < i; j++) {
            this.dropItem(Items.ARROW, 1);
        }

        i = this.rand.nextInt(3);
        for (int k = 1; k < i; k++) {
            this.dropItem(Items.BONE, 1);
        }
    }

    //TODO: Removed Override annotation
    public ItemStack getHeldItem() {
        return defaultHeldItem;
    }

    //Copied from EntitySkeleton
    @SideOnly(Side.CLIENT)
    public boolean isSwingingArms() {
        return this.dataManager.get(SWINGING_ARMS);
    }

    //Copied from EntitySkeleton
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    //Copied from EntitySkeleton

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    //Copied from EntitySkeleton
    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
        if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) this.setCombatTask();
    }

    //Copied from EntitySkeleton
    public void setCombatTask() {
        if (this.world != null && !this.world.isRemote) {
            this.tasksIM.removeTask(this.aiAttackOnCollide);
            this.tasksIM.removeTask(this.aiArrowAttack);
            ItemStack itemstack = this.getHeldItemMainhand();

            if (itemstack != null && itemstack.getItem() == Items.BOW) {
                int i = 20;

                if (this.world.getDifficulty() != EnumDifficulty.HARD) {
                    i = 40;
                }

                this.aiArrowAttack.setAttackCooldown(i);
                this.tasksIM.addTask(4, this.aiArrowAttack);
            } else {
                this.tasksIM.addTask(4, this.aiAttackOnCollide);
            }
        }
    }

    //Copied from EntitySkeleton

    /**
     * Attack the specified entity using a ranged attack.
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entitytippedarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entitytippedarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getDifficultyId() * 4));
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
        DifficultyInstance difficultyinstance = this.world.getDifficultyForLocation(new BlockPos(this));
        entitytippedarrow.setDamage((double) (p_82196_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0) {
            entitytippedarrow.setDamage(entitytippedarrow.getDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            entitytippedarrow.setKnockbackStrength(j);
        }

        boolean flag = this.isBurning() && difficultyinstance.isHard() && this.rand.nextBoolean();
        flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0;

        if (flag) {
            entitytippedarrow.setFire(100);
        }

        ItemStack itemstack = this.getHeldItem(EnumHand.OFF_HAND);

        if (itemstack != null && itemstack.getItem() == Items.TIPPED_ARROW) {
            entitytippedarrow.setPotionEffect(itemstack);
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entitytippedarrow);
    }

}