package invasion.entity.monster;

import invasion.Invasion;
import invasion.SoundHandler;
import invasion.entity.ai.EntityAIBirdFight;
import invasion.entity.ai.EntityAIBoP;
import invasion.entity.ai.EntityAICircleTarget;
import invasion.entity.ai.EntityAIFlyingStrike;
import invasion.entity.ai.EntityAIFlyingTackle;
import invasion.entity.ai.EntityAIPickUpEntity;
import invasion.entity.ai.EntityAISimpleTarget;
import invasion.entity.ai.EntityAIStabiliseFlying;
import invasion.entity.ai.EntityAISwoop;
import invasion.entity.ai.EntityAIWatchTarget;
import invasion.nexus.Nexus;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;


public class EntityIMGiantBird extends EntityIMBird
{

	private static final float PICKUP_OFFSET_X = 0.0F;
	private static final float PICKUP_OFFSET_Y = 0.2F;
	private static final float PICKUP_OFFSET_Z = -0.92F;
	private static final float MODEL_ROTATION_OFFSET_Y = 1.9F;
	private static final byte TRIGGER_SQUAWK = 10;
	private static final byte TRIGGER_SCREECH = 10;
	private static final byte TRIGGER_DEATHSOUND = 10;
	private int tier;

	public EntityIMGiantBird(World world)
	{
		this(world, null);
	}

	public EntityIMGiantBird(World world, Nexus nexus)
	{
		super(world, nexus);
		this.setName("Bird");
		this.setGender(2);
		this.attackStrength = 5;

		this.tier = 1;
		this.setSize(1.9F, 2.8F);
		this.setGravity(0.03F);
		this.setThrust(0.028F);
		this.setMaxPoweredFlightSpeed(0.9F);
		this.setLiftFactor(0.35F);
		this.setThrustComponentRatioMin(0.0F);
		this.setThrustComponentRatioMax(0.5F);
		this.setMaxTurnForce(this.getGravity() * 8.0F);
		this.setMaxHealthAndHealth(Invasion.getMobHealth(this));
		this.setBaseMoveSpeedStat(0.4F);
		this.setAI();
		this.setDebugMode(1);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if ((this.getDebugMode() == 1) && (!this.world.isRemote))
		{
			this.setRenderLabel(this.getAIGoal() + "\n" + this.getNavString());
		}
	}

	@Override
	public boolean canDespawn()
	{
		return false;
	}

	//TODO Removed Override annotation
	public void updateRiderPosition()
	{
		if (!this.getPassengers().isEmpty())
		{
			double x = 0.0D;
			double y = this.getMountedYOffset() - 1.899999976158142D;
			double z = -0.9200000166893005D;

			double dAngle = this.rotationPitch / 180.0F * 3.141592653589793D;
			double sinF = Math.sin(dAngle);
			double cosF = Math.cos(dAngle);
			double tmp = z * cosF - y * sinF;
			y = y * cosF + z * sinF;
			z = tmp;

			dAngle = this.rotationYaw / 180.0F * 3.141592653589793D;
			sinF = Math.sin(dAngle);
			cosF = Math.cos(dAngle);
			tmp = x * cosF - z * sinF;
			z = z * cosF + x * sinF;
			x = tmp;

			y += 1.899999976158142D + this.getPassengers().get(0).getYOffset();

			this.getPassengers().get(0).lastTickPosX = (this.lastTickPosX + x);
			this.getPassengers().get(0).lastTickPosY = (this.lastTickPosY + y);
			this.getPassengers().get(0).lastTickPosZ = (this.lastTickPosZ + z);
			this.getPassengers().get(0).setPosition(this.posX + x, this.posY + y, this.posZ + z);
			this.getPassengers().get(0).rotationYaw = (this.getCarriedEntityYawOffset() + this.rotationYaw);
		}
	}

	@Override
	public boolean shouldRiderSit()
	{
		return false;
	}

	@Override
	public double getMountedYOffset()
	{
		return -0.2000000029802322D;
	}

	@Override
	public void doScreech()
	{
		if (!this.world.isRemote)
		{
			//this.world.playSoundAtEntity(this, "invmod:v_screech"+(rand.nextInt(2)+1), 6.0F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.playSound(this.rand.nextBoolean() ? SoundHandler.v_screech1 : SoundHandler.v_screech2, 6.0F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.world.setEntityState(this, (byte)10);
		}
		else
		{
			this.setBeakState(35);
		}
	}

	@Override
	public void doMeleeSound()
	{
		this.doSquawk();
	}

	@Override
	protected void doHurtSound()
	{
		this.doSquawk();
	}

	@Override
	protected void doDeathSound()
	{
		if (!this.world.isRemote)
		{
			//this.world.playSoundAtEntity(this, "invmod:v_death1", 1.9F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.playSound(SoundHandler.v_death1, 1.9F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.world.setEntityState(this, (byte)10);
		}
		else
		{
			this.setBeakState(25);
		}
	}

	@Override
	protected void onDebugChange()
	{
		this.setShouldRenderLabel(this.getDebugMode() == 1);
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte b){
		super.handleHealthUpdate(b);
		if (b == 10){
			this.doSquawk();
		} else if (b == 10){
			this.doScreech();
		} else if (b == 10){
			this.doDeathSound();
		}
	}*/

	private void doSquawk()
	{
		if (!this.world.isRemote)
		{
			//this.world.playSoundAtEntity(this, "invmod:v_squawk", 1.9F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.playSound(SoundHandler.v_squawk1, 1.9F, 1.0F + (this.rand.nextFloat() * 0.2F - 0.1F));
			this.world.setEntityState(this, (byte)10);
		}
		else
		{
			this.setBeakState(10);
		}
	}

	private String getNavString()
	{
		return this.getNavigatorNew().getStatus();
	}

	private void setAI()
	{
		this.tasksIM = new EntityAITasks(this.world.profiler);

		this.tasksIM.addTask(0, new EntityAISwoop(this));

		this.tasksIM.addTask(3, new EntityAIBoP(this));
		this.tasksIM.addTask(4, new EntityAIFlyingStrike(this));
		this.tasksIM.addTask(4, new EntityAIFlyingTackle(this));
		this.tasksIM.addTask(4, new EntityAIPickUpEntity(this, 0.0F, 0.2F, 0.0F, 1.5F, 1.5F, 20, 45.0F, 45.0F));
		this.tasksIM.addTask(4, new EntityAIStabiliseFlying(this, 35));
		this.tasksIM.addTask(4, new EntityAICircleTarget(this, 300, 16.0F, 45.0F));
		this.tasksIM.addTask(4, new EntityAIBirdFight(this, EntityZombie.class, 25, 0.4F));
		this.tasksIM.addTask(4, new EntityAIWatchTarget(this));

		this.targetTasksIM = new EntityAITasks(this.world.profiler);

		this.targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityZombie.class, 58.0F, true));
	}

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public String toString()
	{
		return "IMVulture-T" + this.getTier();
	}
}