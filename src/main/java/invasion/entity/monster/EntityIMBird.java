package invasion.entity.monster;

import invasion.Invasion;
import invasion.client.render.AnimationRegistry;
import invasion.client.render.animation.AnimationAction;
import invasion.client.render.animation.AnimationState;
import invasion.client.render.animation.util.FlyState;
import invasion.client.render.animation.util.LegController;
import invasion.client.render.animation.util.MouthController;
import invasion.client.render.animation.util.WingController;
import invasion.entity.MoveState;
import invasion.nexus.Nexus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;


public class EntityIMBird extends EntityIMFlying
{

	//private static final int META_ANIMATION_FLAGS = 26;
	private static final DataParameter<Integer> ANIMATION_FLAGS = EntityDataManager.<Integer>createKey(EntityIMBird.class, DataSerializers.VARINT);
	private AnimationState animationRun;
	private AnimationState animationFlap;
	private AnimationState animationBeak;
	private WingController wingController;
	private LegController legController;
	private MouthController beakController;
	private int animationFlags;
	private float carriedEntityYawOffset;
	private int tier;

	public EntityIMBird(World world)
	{
		this(world, null);
	}

	public EntityIMBird(World world, Nexus nexus)
	{
		super(world, nexus);
		this.animationRun = new AnimationState(AnimationRegistry.instance().getAnimation("bird_run"));
		this.animationFlap = new AnimationState(AnimationRegistry.instance().getAnimation("wing_flap_2_piece"));
		this.animationBeak = new AnimationState(AnimationRegistry.instance().getAnimation("bird_beak"));
		this.animationRun.setNewAction(AnimationAction.STAND);
		this.animationFlap.setNewAction(AnimationAction.WINGTUCK);
		this.animationBeak.setNewAction(AnimationAction.MOUTH_CLOSE);
		this.wingController = new WingController(this, this.animationFlap);
		this.legController = new LegController(this, this.animationRun);
		this.beakController = new MouthController(this, this.animationBeak);
		this.setName("Bird");
		this.setGender(2);
		this.setBaseMoveSpeedStat(1.0F);
		this.attackStrength = 1;
		this.setMaxHealthAndHealth(Invasion.getMobHealth(this));
		this.animationFlags = 0;
		this.carriedEntityYawOffset = 0.0F;
		this.setGravity(0.025F);
		this.setThrust(0.1F);
		this.setMaxPoweredFlightSpeed(0.5F);
		this.setLiftFactor(0.35F);
		this.setThrustComponentRatioMin(0.0F);
		this.setThrustComponentRatioMax(0.5F);
		this.setMaxTurnForce(this.getGravity() * 8.0F);
		this.setMoveState(MoveState.STANDING);
		this.setFlyState(FlyState.GROUNDED);
		this.tier = 1;

		//this.dataWatcher.addObject(26, Integer.valueOf(0));
		this.getDataManager().register(ANIMATION_FLAGS, Integer.valueOf(0));
	}

	public void doScreech()
	{
	}

	public void doMeleeSound()
	{
	}

	protected void doHurtSound()
	{
	}

	protected void doDeathSound()
	{
	}

	public AnimationState getWingAnimationState()
	{
		return this.animationFlap;
	}

	public float getLegSweepProgress()
	{
		return 1.0F;
	}

	public AnimationState getLegAnimationState()
	{
		return this.animationRun;
	}

	public AnimationState getBeakAnimationState()
	{
		return this.animationBeak;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.world.isRemote)
		{
			this.updateFlapAnimation();
			this.updateLegAnimation();
			this.updateBeakAnimation();
			//this.animationFlags = this.dataWatcher.getWatchableObjectInt(26);
			this.animationFlags = this.getDataManager().get(ANIMATION_FLAGS);
		}
		else
		{
			//this.dataWatcher.updateObject(26, Integer.valueOf(this.animationFlags));
			this.getDataManager().set(ANIMATION_FLAGS, this.animationFlags);
		}
	}

	@Override
	public String getSpecies()
	{
		return "Bird";
	}

	public boolean getClawsForward()
	{
		return (this.animationFlags & 0x1) > 0;
	}

	public boolean isAttackingWithWings()
	{
		return (this.animationFlags & 0x2) > 0;
	}

	public boolean isBeakOpen()
	{
		return (this.animationFlags & 0x4) > 0;
	}

	public float getCarriedEntityYawOffset()
	{
		return this.carriedEntityYawOffset;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		if (ForgeHooks.onLivingAttack(this, par1DamageSource, par2)) return false;
		if (this.isUnkillable()) return false;
		if (this.world.isRemote) return false;
		this.ticksExisted = 0;
		if (this.getHealth() <= 0.0F) return false;
		if ((par1DamageSource.isFireDamage()) && (this.isPotionActive(MobEffects.FIRE_RESISTANCE))) return false;


//		if (((par1DamageSource == DamageSource.anvil) || (par1DamageSource == DamageSource.fallingBlock)) && (getCurrentItemOrArmor(4) != null))
//		{
//			//getCurrentItemOrArmor(4).damageItem((int)(par2 * 4.0F + this.rand.nextFloat() * par2 * 2.0F), this);
//			par2 *= 0.75F;
//		}

		this.limbSwingAmount = 1.5F;
		boolean flag = true;

		if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F)
		{
			if (par2 <= this.lastDamage) return false;

			this.damageEntity(par1DamageSource, par2 - this.lastDamage);
			this.lastDamage = par2;
			flag = false;
		}
		else
		{
			this.lastDamage = par2;
//			this.prevHealth = getHealth();
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.damageEntity(par1DamageSource, par2);
			this.hurtTime = (this.maxHurtTime = 10);
		}

		this.attackedAtYaw = 0.0F;
		Entity entity = par1DamageSource.getTrueSource();

		if (entity != null)
		{
			if ((entity instanceof EntityLivingBase)) this.setRevengeTarget((EntityLivingBase)entity);

			if ((entity instanceof EntityPlayer))
			{
				this.recentlyHit = 100;
				this.attackingPlayer = ((EntityPlayer)entity);
			}
			else if ((entity instanceof EntityWolf))
			{
				EntityWolf entitywolf = (EntityWolf)entity;

				if (entitywolf.isTamed())
				{
					this.recentlyHit = 100;
					this.attackingPlayer = null;
				}
			}
		}

		if (flag)
		{
			this.world.setEntityState(this, (byte)2);
			if (par1DamageSource != DamageSource.DROWN) this.setBeenAttacked();
			if (entity != null)
			{
				double d0 = entity.posX - this.posX;
				double d1 = entity.posZ - this.posZ;
				for (d1 = entity.posZ - this.posZ; d0 * d0 + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D)
				{
					d0 = (Math.random() - Math.random()) * 0.01D;
				}
				this.attackedAtYaw = ((float)(Math.atan2(d1, d0) * 180.0D / 3.141592653589793D) - this.rotationYaw);
				this.knockBack(entity, par2, d0, d1);
			}
			else
			{
				this.attackedAtYaw = ((int)(Math.random() * 2.0D) * 180);
			}
		}

		if (this.getHealth() <= 0.0F)
		{
			if (flag) this.doDeathSound();
			this.onDeath(par1DamageSource);
		}
		else if (flag)
		{
			this.doHurtSound();
		}

		return true;
	}

	protected void setBeakState(int timeOpen)
	{
		this.beakController.setMouthState(timeOpen);
	}

	protected void onPickedUpEntity(Entity entity)
	{
		this.carriedEntityYawOffset = (entity.rotationYaw - entity.rotationYaw);
	}

	public void setClawsForward(boolean flag)
	{
		if ((flag ? 1 : 0) != (this.animationFlags & 0x1)) this.animationFlags ^= 1;
	}

	public void setAttackingWithWings(boolean flag)
	{
		if ((flag ? 1 : 0) != (this.animationFlags & 0x2)) this.animationFlags ^= 2;
	}

	protected void setBeakOpen(boolean flag)
	{
		if ((flag ? 1 : 0) != (this.animationFlags & 0x4)) this.animationFlags ^= 4;
	}

	@Override
	protected void updateAITick()
	{
	}

	protected void updateFlapAnimation()
	{
		this.wingController.update();
	}

	protected void updateLegAnimation()
	{
		this.legController.update();
	}

	protected void updateBeakAnimation()
	{
		this.beakController.update();
	}

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public String toString()
	{
		return "IMBird T" + this.getTier();
	}
}