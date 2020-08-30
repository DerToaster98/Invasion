package invasion.entity.monster;

import invasion.BlocksAndItems;
import invasion.INotifyTask;
import invasion.Invasion;
import invasion.entity.ai.*;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathNode;
import invasion.entity.projectile.BoulderEntity;
import invasion.entity.projectile.EntityIMPrimedTNT;
import invasion.nexus.Nexus;
import invasion.util.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;


public class EntityIMThrower extends InvadingEntity {

    private int throwTime;
    private int punchTimer;
    private boolean clearingPoint;
    private BlockPos pointToClear;
    private INotifyTask clearPointNotifee;


    public EntityIMThrower(EntityType<? extends EntityIMThrower> type, World world
            , Nexus nexus) {
        super(type,world, nexus);
        this.setBaseMoveSpeedStat(0.13F);
        this.attackStrength = 10;
        this.selfDamage = 0;
        this.maxSelfDamage = 0;
        this.experienceValue = 20;
        this.clearingPoint = false;
        this.setMaxHealthAndHealth(Invasion.getMobHealth(this));
        this.setName("Thrower");
        this.setDestructiveness(2);
        this.setSize(1.8F, 1.95F);
    }
/*
    @Override
    protected void initEntityAI() {
        this.tasksIM = new EntityAITasks(this.world.profiler);
        this.tasksIM.addTask(0, new EntityAISwimming(this));
        if (this.getTier() == 1) {
            this.tasksIM.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayer.class, 55, 60.0F, 1.0F));
            this.tasksIM.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayerMP.class, 55, 60.0F, 1.0F));
        } else {
            this.tasksIM.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayer.class, 60, 90.0F, 1.5F));
            this.tasksIM.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayerMP.class, 60, 90.0F, 1.5F));
        }
        this.tasksIM.addTask(2, new EntityAIAttackNexus(this));
        this.tasksIM.addTask(3, new RandomBoulderGoal(this, 3));
        this.tasksIM.addTask(4, new EntityAIGoToNexus(this));
        this.tasksIM.addTask(7, new EntityAIWanderIM(this));
        this.tasksIM.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasksIM.addTask(9, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));
        this.tasksIM.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        this.tasksIM.addTask(10, new EntityAILookIdle(this));

        this.targetTasksIM = new EntityAITasks(this.world.profiler);
        this.targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, this.getSenseRange(), false));
        this.targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, this.getAggroRange(), true));
        this.targetTasksIM.addTask(3, new EntityAIHurtByTarget(this, false));
    }

 */

    @Override
    public void updateAITick() {
        super.updateAITick();
        this.throwTime -= 1;
        if (this.clearingPoint) {
            if (this.clearPoint()) {
                this.clearingPoint = false;
                if (this.clearPointNotifee != null) this.clearPointNotifee.notifyTask(0);
            }
        }
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
        if (this.getTier() == 2) return;
        this.isAirBorne = true;
        float f = MathHelper.sqrt(par3 * par3 + par5 * par5);
        float f1 = 0.2F;
        this.motionX /= 2.0D;
        this.motionY /= 2.0D;
        this.motionZ /= 2.0D;
        this.motionX -= par3 / f * f1;
        this.motionY += f1;
        this.motionZ -= par5 / f * f1;

        if (this.motionY > 0.4000000059604645D) this.motionY = 0.4000000059604645D;
    }

    public boolean canThrow() {
        return this.throwTime <= 0;
    }

    @Override
    public boolean onPathBlocked(Path path, INotifyTask notifee) {
        if (!path.isFinished()) {
            PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());
            this.clearingPoint = true;
            this.clearPointNotifee = notifee;
            this.pointToClear = new BlockPos(node.pos);
            return true;
        }
        return false;
    }

    @Override
    public void setTier(int tier) {
        super.setTier(tier);
        this.selfDamage = 0;
        this.maxSelfDamage = 0;
        this.clearingPoint = false;
        this.setMaxHealthAndHealth(Invasion.getMobHealth(this));

        if (tier == 1) {
            this.setBaseMoveSpeedStat(0.13F);
            this.attackStrength = 10;
            this.experienceValue = 20;
            this.setName("Thrower");
            this.setDestructiveness(2);
            this.setSize(1.8F, 1.95F);
        } else if (tier == 2) {
            this.setBaseMoveSpeedStat(0.23F);
            this.attackStrength = 15;
            this.experienceValue = 25;
            this.setName("Big Thrower");
            this.setDestructiveness(4);
            this.setSize(2F, 2F);
        }

        if (this.getTextureId() == 1) {
            this.setTexture(tier);
        }
    }

	/*@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("tier", this.tier);
		super.writeEntityToNBT(nbttagcompound);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setTexture(nbttagcompound.getInteger("tier"));
		this.tier = nbttagcompound.getInteger("tier");
		setTier(this.tier);
	}*/

    @Override
    public String getSpecies() {
        return "Zombie";
    }

    @Override
    public int getGender() {
        return 1;
    }

    //TODO: Removed Override annotation
	/*protected String getLivingSound() {
		return "mob.zombie.say";
	}*/

    @Override
    protected SoundEvent getAmbientSound() {
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

    protected boolean clearPoint() {
        if (--this.punchTimer <= 0) {
            //this is a cheat, I should fix it where it get's the point to clear
            int x = this.pointToClear.getX() + 1;
            int y = this.pointToClear.getY();
            int z = this.pointToClear.getZ();
            int mobX = MathHelper.floor(this.posX);
            int mobZ = MathHelper.floor(this.posZ);
            int xOffsetR = 0;
            int zOffsetR = 0;
            int axisX = 0;
            int axisZ = 0;

            float facing = this.rotationYaw % 360.0F;
            if (facing < 0.0F) {
                facing += 360.0F;
            }
            if ((facing >= 45.0F) && (facing < 135.0F)) {
                zOffsetR = -1;
                axisX = -1;
            } else if ((facing >= 135.0F) && (facing < 225.0F)) {
                xOffsetR = -1;
                axisZ = -1;
            } else if ((facing >= 225.0F) && (facing < 315.0F)) {
                zOffsetR = -1;
                axisX = 1;
            } else {
                xOffsetR = -1;
                axisZ = 1;
            }
            IBlockState blockState = this.world.getBlockState(new BlockPos(x, y, z));
            IBlockState blockState0 = this.world.getBlockState(new BlockPos(x, y + 1, z));
            IBlockState blockState1 = this.world.getBlockState(new BlockPos(x + xOffsetR, y, z + zOffsetR));
            IBlockState blockState2 = this.world.getBlockState(new BlockPos(x + xOffsetR, y + 1, z + zOffsetR));

            IBlockState blockState3 = this.world.getBlockState(new BlockPos(x - axisX, y + 1, z - axisZ));
            IBlockState blockState4 = this.world.getBlockState(new BlockPos(x - axisX + xOffsetR, y + 1, z - axisZ + zOffsetR));

            IBlockState blockState5 = this.world.getBlockState(new BlockPos(x - 2 * axisX, y + 1, z - 2 * axisZ));
            IBlockState blockState6 = this.world.getBlockState(new BlockPos(x - 2 * axisX + xOffsetR, y + 1, z - 2 * axisZ + zOffsetR));

            if (((blockState.getBlock() != null) && (blockState.getMaterial().isSolid()))
                    || ((blockState0.getBlock() != null) && (blockState0.getMaterial().isSolid()))
                    || ((blockState1.getBlock() != null) && (blockState1.getMaterial().isSolid()))
                    || ((blockState2.getBlock() != null) && (blockState2.getMaterial().isSolid()))) {
                this.tryDestroyBlock(x, y, z);
                this.tryDestroyBlock(x, y + 1, z);
                this.tryDestroyBlock(x + xOffsetR, y, z + zOffsetR);
                this.tryDestroyBlock(x + xOffsetR, y + 1, z + zOffsetR);
                this.punchTimer = 160;
            } else if (((blockState3.getBlock() != null) && (blockState3.getMaterial().isSolid()))
                    || ((blockState4.getBlock() != null) && (blockState4.getMaterial().isSolid()))) {
                this.tryDestroyBlock(x - axisX, y + 1, z - axisZ);
                this.tryDestroyBlock(x - axisX + xOffsetR, y + 1, z - axisZ + zOffsetR);
                this.punchTimer = 160;
            } else if (((blockState5.getBlock() != null) && (blockState5.getMaterial().isSolid()))
                    || ((blockState6.getBlock() != null) && (blockState6.getMaterial().isSolid()))) {
                this.tryDestroyBlock(x - 2 * axisX, y + 1, z - 2 * axisZ);
                this.tryDestroyBlock(x - 2 * axisX + xOffsetR, y + 1, z - 2 * axisZ + zOffsetR);
                this.punchTimer = 160;
            } else {
                return true;
            }
        }
        return false;
    }

    protected void tryDestroyBlock(int x, int y, int z) {
        this.tryDestroyBlock(new BlockPos(x, y, z));
    }

    protected void tryDestroyBlock(BlockPos pos) {
        Block block = this.world.getBlockState(pos).getBlock();
        //if ((block != null) && ((isNexusBound()) || (this.j != null))) {
        if ((block != null) || (this.entity != null)) {
            if ((block == BlocksAndItems.blockNexus) && (pos.equals(this.targetNexus.getPos()))) {
                this.targetNexus.attackNexus(5);
            } else if (block != BlocksAndItems.blockNexus) {
                IBlockState blockState = this.world.getBlockState(pos);
                this.world.setBlockToAir(pos);
                block.onBlockDestroyedByPlayer(this.world, pos, blockState);

                if (Config.DROP_DESTRUCTED_BLOCKS) {
                    block.dropBlockAsItem(this.world, pos, blockState, 0);
                }
                if (this.throttled == 0) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 4f);
                    this.throttled = 5;
                }
            }
        }
    }


    @Override
    public boolean attackEntityAsMob(Entity entity, int f) {
        if ((this.throwTime <= 0) && (f > 4.0F)) {
            this.throwTime = 120;
            //f is the throwdistance
            if (f < 50.0F) {
                this.throwBoulder(entity.posX, entity.posY + entity.getEyeHeight() - 0.7D, entity.posZ, false);
            }
            return true;
        } else {
            return super.attackEntityAsMob(entity, f);
        }

    }

    protected void throwBoulder(double entityX, double entityY, double entityZ, boolean forced) {
        float launchSpeed = 1.0F;
        double dX = entityX - this.posX;
        double dZ = entityZ - this.posZ;
        double dXY = MathHelper.sqrt(dX * dX + dZ * dZ);

        if ((0.025D * dXY / (launchSpeed * launchSpeed) <= 1.0D)) {
            BoulderEntity entityBoulder = new BoulderEntity(this.world, this, launchSpeed);
            double dY = entityY - entityBoulder.posY;
            double angle = 0.5D * Math.asin(0.025D * dXY / (launchSpeed * launchSpeed));
            dY += dXY * Math.tan(angle);
            entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
            this.world.spawnEntity(entityBoulder);
        } else if (forced) {
            BoulderEntity entityBoulder = new BoulderEntity(this.world, this, launchSpeed);
            double dY = entityY - entityBoulder.posY;
            dY += dXY * Math.tan(0.7853981633974483D);
            entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
            this.world.spawnEntity(entityBoulder);
        }

    }

    public void throwBoulder(double entityX, double entityY, double entityZ) {
        this.throwTime = 40;
        float launchSpeed = 1.0F;
        double dX = entityX - this.posX;
        double dZ = entityZ - this.posZ;
        double dXY = MathHelper.sqrt(dX * dX + dZ * dZ);
        double p = 0.025D * dXY / (launchSpeed * launchSpeed);
        double angle = p <= 1.0D ? 0.5D * p : 0.7853981633974483D;
        BoulderEntity entityBoulder = new BoulderEntity(this.world, this, launchSpeed);
        double dY = entityY - entityBoulder.posY;
        dY += dXY * Math.tan(angle);
        entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
        this.world.spawnEntity(entityBoulder);
    }

    public void throwTNT(double entityX, double entityY, double entityZ) {
        this.throwTime = 40;
        float launchSpeed = 1.0F;
        double dX = entityX - this.posX;
        double dZ = entityZ - this.posZ;
        double dXY = MathHelper.sqrt(dX * dX + dZ * dZ);
        double p = 0.025D * dXY / (launchSpeed * launchSpeed);
        double angle = p <= 1.0D ? 0.5D * p : 0.7853981633974483D;
        EntityIMPrimedTNT entityTNT = new EntityIMPrimedTNT(this.world, this, launchSpeed);
        double dY = entityY - entityTNT.posY;
        dY += dXY * Math.tan(angle);
        entityTNT.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
        this.world.spawnEntity(entityTNT);
    }

    @Override
    protected void dropFewItems(boolean flag, int bonus) {
        super.dropFewItems(flag, bonus);
        this.entityDropItem(new ItemStack(BlocksAndItems.itemSmallRemnants, 1), 0.0F);
    }

    @Override
    public String toString() {
        return "IMThrower-T" + this.getTier();
    }

}