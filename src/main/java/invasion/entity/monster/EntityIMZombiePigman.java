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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public class EntityIMZombiePigman extends InvadingEntity implements ICanDig
{

	private static final DataParameter<Boolean> META_CHANGED = EntityDataManager.createKey(EntityIMZombiePigman.class, DataSerializers.BOOLEAN); //29
	private static final DataParameter<Integer> FLAVOUR = EntityDataManager.createKey(EntityIMZombiePigman.class, DataSerializers.VARINT); //28
	private static final DataParameter<Boolean> IS_SWINGING = EntityDataManager.createKey(EntityIMZombiePigman.class, DataSerializers.BOOLEAN); //27
	private static final DataParameter<Byte> META_17 = EntityDataManager.createKey(EntityIMZombiePigman.class, DataSerializers.BYTE); //17
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    private TerrainModifier terrainModifier;
	private TerrainDigger terrainDigger;
	private boolean metaChanged;
	private int flavour;
	//private ItemStack defaultHeldItem;
	private Item itemDrop;
	private float dropChance;
	private int swingTimer;

	public EntityIMZombiePigman(World world)
	{
		this(world, null);
	}

	public EntityIMZombiePigman(World world, Nexus nexus)
	{
		super(world, nexus);
		this.terrainModifier = new TerrainModifier(this, 0.75F);
		this.terrainDigger = new TerrainDigger(this, this.terrainModifier, 1.0F);
		this.dropChance = 0.35F;
		this.isImmuneToFire = true;

		this.setAttributes(this.getTier(), this.flavour);
		this.floatsInWater = true;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(META_CHANGED, this.metaChanged = this.world == null ? true : this.world.isRemote);
		this.getDataManager().register(FLAVOUR, this.flavour = 0);
		this.getDataManager().register(IS_SWINGING, false);
		this.getDataManager().register(META_17, (byte)0);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.metaChanged != this.getDataManager().get(META_CHANGED))
		{
			this.metaChanged = this.getDataManager().get(META_CHANGED);
			//this.setTexture(this.getDataManager().get(TEXTURE));
			//if(this.tier != this.getDataManager().get(TIER)) this.setTier(this.getDataManager().get(TIER));
			if (this.flavour != this.getDataManager().get(FLAVOUR)) this.setFlavour(this.getDataManager().get(FLAVOUR));
		}

	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		this.updateAnimation(false);
		this.updateSound();
	}

	@Override
	public void onPathSet()
	{
		this.terrainModifier.cancelTask();
	}

	@Override
	protected void initEntityAI()
	{
		// added entityaiswimming and increased all other tasksordernumers with
		// 1
		this.tasksIM = new EntityAITasks(this.world.profiler);
		this.tasksIM.addTask(0, new EntityAISwimming(this));
		this.tasksIM.addTask(2, new EntityAIKillEntity(this, EntityPlayer.class, 40));
		this.tasksIM.addTask(2, new EntityAIKillEntity(this, EntityPlayerMP.class, 40));
		this.tasksIM.addTask(3, new EntityAIAttackNexus(this));
		this.tasksIM.addTask(4, new EntityAIWaitForEngy(this, 4.0F, true));
		this.tasksIM.addTask(5, new EntityAIKillEntity(this, EntityLiving.class, 40));
		this.tasksIM.addTask(6, new EntityAIGoToNexus(this));
		this.tasksIM.addTask(7, new EntityAIWanderIM(this));
		this.tasksIM.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasksIM.addTask(9, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));
		this.tasksIM.addTask(9, new EntityAILookIdle(this));

		this.targetTasksIM = new EntityAITasks(this.world.profiler);
		this.targetTasksIM.addTask(0, new EntityAITargetRetaliate(this, EntityLiving.class, Config.NIGHTSPAWNS_MOB_SIGHTRANGE));
		this.targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, Config.NIGHTSPAWNS_MOB_SIGHTRANGE, true));
		this.targetTasksIM.addTask(5, new EntityAIHurtByTarget(this, false));

		if (this.getTier() == 3)
		{
			// this.tasks.addTask(4, new EntityAIStoop(this));
			this.tasksIM.addTask(1, new EntityAICharge(this, EntityPlayer.class, 0.75F));
		}
		else
		{
			// track players from sensing them
			this.targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, Config.NIGHTSPAWNS_MOB_SENSERANGE, false));
			this.targetTasksIM.addTask(3, new EntityAITargetOnNoNexusPath(this, PigEngyEntity.class, 3.5F));
		}
	}

	@Override
	public void setTier(int tier)
	{
		super.setTier(tier);
		this.setAttributes(tier, this.flavour);
		if (this.getTextureId() == 0) this.setTexture(tier - 1);
	}

	public void setFlavour(int flavour)
	{
		this.getDataManager().set(FLAVOUR, flavour);
		this.flavour = flavour;
		this.setAttributes(this.getTier(), flavour);
	}

	@Override
	public String toString()
	{
		return "IMZombiePigman-T" + this.getTier();
	}

	@Override
	public IBlockAccess getTerrain()
	{
		return this.world;
	}

	//TODO: Removed Override annotation
	/*public ItemStack getHeldItem() {
		return this.defaultHeldItem;
	
	}*/

	@Override
	public boolean avoidsBlock(Block block)
	{
		if ((this.isImmuneToFire) && ((block == Blocks.FIRE) || (block == Blocks.FLOWING_LAVA) || (block == Blocks.LAVA)))
		{
			return false;
		}
		return super.avoidsBlock(block);
	}

	@Override
	public float getBlockRemovalCost(BlockPos pos)
	{
		return this.getBlockStrength(pos) * 20.0F;
	}

	@Override
	public boolean canClearBlock(BlockPos pos)
	{
		IBlockState state = this.world.getBlockState(pos);
		return (state.getBlock() == Blocks.AIR) || (this.isBlockDestructible(this.world, pos, state));

	}

	@Override
	public boolean onPathBlocked(Path path, INotifyTask notifee)
	{
		if ((!path.isFinished()) && ((this.isNexusBound()) || (this.getAttackTarget() != null)))
		{

			if ((path.getFinalPathPoint().distanceTo(path.getIntendedTarget()) > 2.2D) && (path.getCurrentPathIndex() + 2 >= path.getCurrentPathLength() / 2))
			{
				return false;
			}
			PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());

			if (this.terrainDigger.askClearPosition(node.pos, notifee, 1.0F))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isBigRenderTempHack()
	{
		return this.getTier() == 3;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		return (this.getTier() == 3) && (this.isSprinting()) ? this.chargeAttack(entity) : super.attackEntityAsMob(entity);
	}

	@Override
	public boolean canBePushed()
	{
		return this.getTier() != 3;
	}

	@Override
	public void knockBack(Entity par1Entity, float par2, double par3, double par5)
	{
		if (this.getTier() == 3) return;
		this.isAirBorne = true;
		float f = MathHelper.sqrt(par3 * par3 + par5 * par5);
		float f1 = 0.4F;
		this.motionX /= 2.0D;
		this.motionY /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= par3 / f * f1;
		this.motionY += f1;
		this.motionZ -= par5 / f * f1;

		if (this.motionY > 0.4000000059604645D) this.motionY = 0.4000000059604645D;
	}

	@Override
	public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap)
	{
		if ((this.getTier() == 2) && (this.flavour == 2) && (node.action == PathAction.SWIM))
		{
			float multiplier = 1.0F;
			if ((terrainMap instanceof IBlockAccessExtended))
			{
				int mobDensity = ((IBlockAccessExtended)terrainMap).getLayeredData(node.pos) & 0x7;
				multiplier += mobDensity * 3;
			}

			if ((node.pos.y > prevNode.pos.y) && (this.getCollide(terrainMap, node.pos) == 2))
			{
				multiplier += 2.0F;
			}

			return prevNode.distanceTo(node) * 1.2F * multiplier;
		}

		return super.getBlockPathCost(prevNode, node, terrainMap);
	}

	@Override
	public boolean canBreatheUnderwater()
	{
		return (this.getTier() == 2) && (this.flavour == 2);
	}

	@Override
	public boolean isBlockDestructible(IBlockAccess terrainMap, BlockPos pos, IBlockState state)
	{
		if (this.getDestructiveness() == 0) return false;

		BlockPos position = this.getCurrentTargetPos();
		int dY = position.getY() - pos.getY();
		boolean isTooSteep = false;
		if (dY > 0)
		{
			dY += 8;
			int dX = position.getX() - pos.getX();
			int dZ = position.getZ() - pos.getZ();
			double dXZ = Math.sqrt(dX * dX + dZ * dZ) + 1.E-005D;
			isTooSteep = dY / dXZ > 2.144D;
		}

		return (!isTooSteep) && (super.isBlockDestructible(terrainMap, pos, state));
	}

	@Override
	public void onFollowingEntity(Entity entity)
	{
		if (entity == null)
		{
			this.setDestructiveness(1);
		}
		else if (entity instanceof PigEngyEntity || entity instanceof MoulderingCreeperEntity)
		{
			this.setDestructiveness(0);
		}
		else
		{
			this.setDestructiveness(1);
		}
	}

	public float scaleAmount()
	{
		if (this.getTier() == 2) return 1.12F;
		if (this.getTier() == 3) return 1.21F;
		return 1.0F;
	}

	@Override
	public String getSpecies()
	{
		return "ZombiePigman";
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setInteger("flavour", this.flavour);
		super.writeEntityToNBT(nbttagcompound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		this.setTexture(nbttagcompound.getInteger("textureId"));
		this.flavour = nbttagcompound.getInteger("flavour");
		this.setFlavour(this.flavour);
	}

	public void updateAnimation(boolean override)
	{
		// System.out.println(this.getXCoord()+" "+this.getYCoord()+" "+this.getZCoord()+" charging:"+this.isCharging());
		if ((!this.world.isRemote) && ((this.terrainModifier.isBusy()) || override))
		{
			this.setSwinging(true);
		}
		int swingSpeed = this.getSwingSpeed();
		if (this.isSwinging())
		{
			this.swingTimer += 1;
			if (this.swingTimer >= swingSpeed)
			{
				this.swingTimer = 0;
				this.setSwinging(false);
			}
		}
		else
		{
			this.swingTimer = 0;
		}
		this.swingProgress = (float)this.swingTimer / (float)swingSpeed;

		if (this.isCharging())
		{
			boolean mobgriefing = this.world.getGameRules().getBoolean("mobGriefing");
			this.limbSwingAmount = ((float)(this.limbSwingAmount + 0.5D));
			int x = this.getPosition().getX();
			int y = this.getPosition().getY();
			int z = this.getPosition().getZ();
			float maxResistance = 400F;
			if (!this.world.isRemote)
			{
				for (int i = y; i <= y + 1; i++)
				{
					for (int j = x - 1; j <= x + 1; j++)
					{
						for (int k = z - 1; k <= z + 1; k++)
						{
							IBlockState blockState = this.world.getBlockState(new BlockPos(j, i, k));
							if (blockState.getMaterial() != Material.AIR)
							{
								if (this.isBlockDestructible(this.world, new BlockPos(j, i, k), blockState) && blockState.getBlock() != BlocksAndItems.blockNexus)
								{
									if (blockState.getBlock().getExplosionResistance(this) >= maxResistance)
									{
										maxResistance -= blockState.getBlock().getExplosionResistance(this);
										//this.playSound("random.explode", 0.2F, 0.5F);
										this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.2f, 0.5f);
										if (Config.DROP_DESTRUCTED_BLOCKS)
										{
											blockState.getBlock().dropBlockAsItem(this.world, new BlockPos(j, i, k), blockState, 0);
										}
										this.world.setBlockToAir(new BlockPos(j, i, k));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isSwinging()
	{
		//return getDataWatcher().getWatchableObjectByte(27) != 0;
		return this.getDataManager().get(IS_SWINGING);
	}

	protected void setSwinging(boolean flag)
	{
		//getDataWatcher().updateObject(27, Byte.valueOf((byte) (flag == true ? 1 : 0)));
		this.getDataManager().set(IS_SWINGING, flag);
	}

	protected void updateSound()
	{
		if (this.terrainModifier.isBusy())
		{
			if (--this.throttled2 <= 0)
			{
				//this.world.playSoundAtEntity(this, "invmod:scrape", 0.85F, 1.0F / (this.rand.nextFloat() * 0.5F + 1.0F));
				this.playSound(SoundHandler.scrape1, 0.85F, 1.0F / (this.rand.nextFloat() * 0.5F + 1.0F));
				this.throttled2 = (45 + this.rand.nextInt(20));
			}
		}
	}

	protected int getSwingSpeed()
	{
		return 10;
	}

	protected boolean chargeAttack(Entity entity)
	{
		int knockback = 4;
		entity.attackEntityFrom(DamageSource.causeMobDamage(this),
			this.attackStrength + 3);
		entity.addVelocity(
			-MathHelper.sin((float)((double)this.rotationYaw * Math.PI / 180d)) * knockback * 0.5F, 0.4D,
			MathHelper.cos((float)((double)this.rotationYaw * Math.PI / 180d)) * knockback * 0.5F);
		this.setSprinting(false);
		//this.world.playSoundAtEntity(entity, "damage.fallbig", 1.0F, 1.0F);
		this.playSound(SoundEvents.ENTITY_GENERIC_BIG_FALL, 1f, 1f);
		return true;
	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();
		this.terrainModifier.onUpdate();
	}

	protected ITerrainDig getTerrainDig()
	{
		return this.terrainDigger;
	}

	//TODO: Removed Override annotation
	/*protected String getLivingSound() {
		if (this.getTier() == 3) return this.rand.nextInt(3) == 0 ? "invmod:bigzombiePigman1" : null;
		return "mob.zombiepig.zpig";
	}*/

	@Override
	protected SoundEvent getAmbientSound()
	{
		if (this.getTier() == 3) return this.rand.nextInt(3) == 0 ? SoundHandler.bigzombie1 : null;
		return SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT;
	}

	//protected SoundEvent getHurtSound()
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ZOMBIE_PIG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_ZOMBIE_PIG_DEATH;
	}

	@Override
	protected Item getDropItem()
	{
		return Items.GOLD_NUGGET;
	}

	@Override
	protected void dropFewItems(boolean flag, int bonus)
	{
		super.dropFewItems(flag, bonus);
		if (this.rand.nextFloat() < 0.35F)
		{
			this.dropItem(Items.GOLD_NUGGET, 1);
		}

		if ((this.itemDrop != null)
			&& (this.rand.nextFloat() < this.dropChance))
		{
			this.entityDropItem(new ItemStack(this.itemDrop, 1), 0.0F);
		}
	}

	private void setAttributes(int tier, int flavour)
	{
		this.setMaxHealthAndHealth(Invasion.getMobHealth(this));
		this.setDestructiveness(2);
		this.isImmuneToFire = true;
		this.maxDestructiveness = 2;
		this.setGender(1);
		if (tier == 1)
		{
			this.setName("Zombie Pigman");
			this.setBaseMoveSpeedStat(0.25F);
			this.attackStrength = 8;
			//this.defaultHeldItem = new ItemStack(Items.GOLDEN_SWORD, 1);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
		}
		else if (tier == 2)
		{
			this.setName("Zombie Pigman");
			this.setBaseMoveSpeedStat(0.35F);
			this.attackStrength = 12;

			if (this.rand.nextInt(5) == 1)
			{
				this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
			}

			if (this.rand.nextInt(5) == 1)
			{
				this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
			}

			if (this.rand.nextInt(5) == 1)
			{
				this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
			}

			if (this.rand.nextInt(5) == 1)
			{
				this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
			}
		}
		else if (tier == 3)
		{
			this.setName("Zombie Pigman Brute");
			this.setBaseMoveSpeedStat(0.20F);
			this.attackStrength = 18;
		}
	}

	//TODO
	/*@Override
	protected void addRandomArmor() {
		super.addRandomArmor();
	
	}*/

	public boolean isCharging()
	{
		return this.getDataManager().get(META_17) != 0;
	}

	public void setCharging(boolean flag)
	{
		this.getDataManager().set(META_17, flag ? (byte)127 : (byte)0);
	}

	@Override
	public void onBlockRemoved(BlockPos pos, IBlockState state)
	{
		// TODO Auto-generated method stub

	}

}
