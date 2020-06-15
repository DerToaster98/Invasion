package invasion.entity.ally;

import invasion.BlocksAndItems;
import invasion.entity.monster.InvadingEntity;
import invasion.nexus.Nexus;
import invasion.nexus.SpawnPoint;
import invasion.nexus.SpawnType;
import invasion.util.ComparatorDistanceFrom;
import invasion.util.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EntityIMWolf extends WolfEntity {

    //From EntityWolf
    //private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(EntityIMWolf.class, DataSerializers.FLOAT);
    //private static final DataParameter<Boolean> BEGGING = EntityDataManager.createKey(EntityIMWolf.class, DataSerializers.BOOLEAN);
    //private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityIMWolf.class, DataSerializers.VARINT);

    private static final DataParameter<Boolean> NEXUS_BOUND = EntityDataManager.createKey(EntityIMWolf.class, DataSerializers.BOOLEAN);

    private static final int META_BOUND = 30;
    private Nexus nexus;
    private BlockPos nexusPos;
    //private int nexusX;
    //private int nexusY;
    //private int nexusZ;
    private int updateTimer;
    private boolean loadedFromNBT;
    private float maxHealth;

    public EntityIMWolf(World world) {
        this(world, null);
    }

    public EntityIMWolf(EntityWolf wolf, Nexus nexus) {
        this(wolf.world, nexus);
        this.loadedFromNBT = false;
        this.setPositionAndRotation(wolf.posX, wolf.posY, wolf.posZ, wolf.rotationYaw, wolf.rotationPitch);
        //this.dataWatcher.updateObject(16, Byte.valueOf(wolf.getDataWatcher().getWatchableObjectByte(16)));
        //this.dataWatcher.updateObject(17, wolf.getDataWatcher().getWatchableObjectString(17));
        //this.dataWatcher.updateObject(18, Float.valueOf(wolf.getDataWatcher().getWatchableObjectFloat(18)));
        this.getDataManager().set(HEALTH, wolf.getHealth());
        this.getDataManager().set(BEGGING, wolf.isBegging());
        this.getDataManager().set(COLLAR_COLOR, wolf.getCollarColor().getDyeDamage());
        this.aiSit.setSitting(this.isSitting());
    }

    public EntityIMWolf(World world, Nexus nexus) {
        super(world);
        //this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, IMob.class, true)); //DarthXenon: No! classTarget must be a subclass of Entity!
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, InvadingEntity.class, true));
        this.setEntityHealth(this.getMaxHealth());
        //this.dataWatcher.addObject(30, Byte.valueOf((byte)0));
        this.nexus = nexus;
        if (nexus != null) {
            this.nexusPos = this.nexus.getPos();
            //this.nexusX = nexus.getXCoord();
            //this.nexusY = nexus.getYCoord();
            //this.nexusZ = nexus.getZCoord();
            this.getDataManager().set(NEXUS_BOUND, true);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(HEALTH, 8f);
        this.getDataManager().register(BEGGING, false);
        this.getDataManager().register(COLLAR_COLOR, 0);
        this.getDataManager().register(NEXUS_BOUND, false);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (this.loadedFromNBT) {
            this.loadedFromNBT = false;
            this.checkNexus();
        }

        if ((!this.world.isRemote) && (this.updateTimer++ > 40)) this.checkNexus();
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        int damage = this.isTamed() ? 4 : 2;
        if ((par1Entity instanceof IMob)) damage *= 2;
        boolean success = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (success) this.heal(4.0F);
        return success;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        if (this.isTamed()) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
        this.getDataManager().set(HEALTH, this.getMaxHealth());
    }

    //TODO
    //@Override
    //GetColarCollor
	/*public EnumDyeColor func_175546_cu()
	{
		//Not sure if this is correct after 1.8 update
		return EnumDyeColor.func_176766_a(this.dataWatcher.getWatchableObjectByte(30) == 1 ? 10 : 1);
	}*/

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.getAttackTarget() instanceof IMob ? SoundEvents.ENTITY_WOLF_GROWL : SoundEvents.ENTITY_WOLF_HURT;
		/*if ((getAttackTarget() instanceof IMob)) {
			return "mob.wolf.growl";
			
		}
		return "mob.wolf.hurt";*/
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime += 1;
        if (this.deathTime == 120) {
            int i;
            if ((!this.world.isRemote) && ((this.recentlyHit > 0) || (this.isPlayer())) && (!this.isChild())) {
                for (i = this.getExperiencePoints(this.attackingPlayer); i > 0; ) {
                    int k = EntityXPOrb.getXPSplit(i);
                    i -= k;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, k));
                }
            }

            this.setDead();
            for (int j = 0; j < 20; j++) {
                double d = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d, d1, d2);
            }
        }
    }


    @Override
    public void setDead() {
        this.isDead = true;
        if (this.nexus != null) {
            if (this.nexus.getMode() != 0) {
                this.respawnAtNexus();
            } else {
                super.setDead();
            }
        }

    }

    public void setEntityHealth(float par1) {
        //this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp(par1, 0.0F, getMaxHealth())));
        this.getDataManager().set(HEALTH, MathHelper.clamp(par1, 0f, this.getMaxHealth()));
    }

    public boolean respawnAtNexus() {
        if ((!this.world.isRemote) && (this.getDataManager().get(NEXUS_BOUND) /*this.dataWatcher.getWatchableObjectByte(30) == 1*/) && (this.nexus != null)) {
            EntityIMWolf wolfRecreation = new EntityIMWolf(this, this.nexus);

            int x = this.nexus.getPos().getX();
            int y = this.nexus.getPos().getY();
            int z = this.nexus.getPos().getZ();
            List<SpawnPoint> spawnPoints = new ArrayList<>();
            this.setRotation(0.0F, 0.0F);
            for (int vertical = 0; vertical < 3; vertical = vertical > 0 ? vertical * -1 : vertical * -1 + 1) {
                for (int i = -4; i < 5; i++) {
                    for (int j = -4; j < 5; j++) {
                        wolfRecreation.setPosition(x + i + 0.5F, y + vertical, z + j + 0.5F);
                        if (wolfRecreation.getCanSpawnHere())
                            spawnPoints.add(new SpawnPoint(x + i, y + vertical, z + i, 0, SpawnType.WOLF));
                    }
                }
            }
            Collections.sort(spawnPoints, new ComparatorDistanceFrom(x, y, z));

            if (spawnPoints.size() > 0) {
                SpawnPoint point = spawnPoints.get(spawnPoints.size() / 2);
                wolfRecreation.setPosition(point.getPos().getX() + 0.5D, point.getPos().getY(), point.getPos().getZ() + 0.5D);
                wolfRecreation.heal(60.0F);
                this.world.spawnEntity(wolfRecreation);
                return true;
            }
        }
        ModLogger.logWarn("No respawn spot for wolf");
        return false;
    }

    @Override
    public boolean getCanSpawnHere() {
        return (this.world.checkNoEntityCollision(this.getEntityBoundingBox())) && (this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).size() == 0) && (!this.world.containsAnyLiquid(this.getEntityBoundingBox()));
    }

//	@Override
//	public boolean interact(EntityPlayer player)
//	{
//		ItemStack itemstack = player.inventory.getCurrentItem();
//		if (itemstack != null)
//		{
////			if ((itemstack.getItem() == Items.bone) && (player.getDisplayName().equalsIgnoreCase(((EntityPlayerMP)getOwner()).getDisplayName())) && (this.dataWatcher.getWatchableObjectByte(30) == 1))
////			{
////				this.dataWatcher.updateObject(30, Byte.valueOf((byte)0));
////				this.nexus = null;
////
////				itemstack.stackSize -= 1;
////				if (itemstack.stackSize <= 0)
////					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
////				return false;
////			}
//			if ((itemstack.getItem() == Main.itemStrangeBone) && (player.getDisplayName().equalsIgnoreCase(((EntityPlayerMP)getOwner()).getDisplayName())))
//			{
//				Invasion newNexus = findNexus();
//				if ((newNexus != null) && (newNexus != this.nexus))
//				{
//					this.nexus = newNexus;
//					this.dataWatcher.updateObject(30, Byte.valueOf((byte)1));
//					this.nexusX = this.nexus.getXCoord();
//					this.nexusY = this.nexus.getYCoord();
//					this.nexusZ = this.nexus.getZCoord();
//
//					itemstack.stackSize -= 1;
//					if (itemstack.stackSize <= 0) {
//						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
//					}
//					this.maxHealth = 25.0F;
//					setEntityHealth(25.0F);
//				}
//				return true;
//			}
//		}
//		return super.interact(player);
//	}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        if (this.nexus != null) {
            nbttagcompound.setInteger("nexusX", this.nexus.getPos().getX());
            nbttagcompound.setInteger("nexusY", this.nexus.getPos().getY());
            nbttagcompound.setInteger("nexusZ", this.nexus.getPos().getZ());
        }
        //nbttagcompound.setByte("nexusBound", this.dataWatcher.getWatchableObjectByte(30));
        nbttagcompound.setBoolean("nexusBound", this.getDataManager().get(NEXUS_BOUND));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        //this.nexusX = ;
        this.nexusPos = new BlockPos(nbttagcompound.getInteger("nexusX"), nbttagcompound.getInteger("nexusY"), nbttagcompound.getInteger("nexusZ"));
        this.getDataManager().set(NEXUS_BOUND, nbttagcompound.getBoolean("nexusBound"));
        this.loadedFromNBT = true;
    }

    @Override
    public void setAngry(boolean par1) {
    }

    private void checkNexus() {
        if ((this.world != null) && (this.getDataManager().get(NEXUS_BOUND))) {
            if (this.world.getBlockState(this.nexusPos).getBlock() == BlocksAndItems.blockNexus) {
                this.nexus = ((Nexus) this.world.getTileEntity(this.nexusPos));
            }
            if (this.nexus == null) this.getDataManager().set(NEXUS_BOUND, false);
        }
    }

    private Nexus findNexus() {
        Nexus nexus = null;
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);
        for (int i = -7; i < 8; i++) {
            for (int j = -4; j < 5; j++) {
                for (int k = -7; k < 8; k++) {
                    if (this.world.getBlockState(new BlockPos(x + i, y + j, z + k)).getBlock() == BlocksAndItems.blockNexus) {
                        nexus = (Nexus) this.world.getTileEntity(new BlockPos(x + i, y + j, z + k));
                        break;
                    }
                }
            }
        }

        return nexus;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2float) {
        return super.attackEntityFrom(damageSource, par2float);
    }

}