package invasion.entity.monster;

import invasion.IBlockAccessExtended;
import invasion.client.render.animation.util.FlyState;
import invasion.client.render.animation.util.IMBodyHelper;
import invasion.client.render.animation.util.IMLookHelper;
import invasion.client.render.animation.util.IMMoveHelperFlying;
import invasion.entity.INavigationFlying;
import invasion.entity.IPathSource;
import invasion.entity.ai.navigator.NavigatorFlying;
import invasion.entity.ai.navigator.PathAction;
import invasion.entity.ai.navigator.PathNode;
import invasion.entity.ai.navigator.PathfinderIM;
import invasion.nexus.Nexus;
import invasion.util.Coords;
import invasion.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public abstract class EntityIMFlying extends InvadingEntity
{

	//private static final int META_TARGET_X = 29;
	//private static final int META_TARGET_Y = 30;
	//private static final int META_TARGET_Z = 31;
	//private static final int META_THRUST_DATA = 28;
	//private static final int META_FLYSTATE = 27;

	private static final DataParameter<BlockPos> TARGET = EntityDataManager.createKey(EntityIMFlying.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Integer> FLY_STATE = EntityDataManager.createKey(EntityIMFlying.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> THRUST = EntityDataManager.createKey(EntityIMFlying.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    private final NavigatorFlying navigatorFlying;
	private final IMMoveHelperFlying i;
	private final IMLookHelper h;
	private final IMBodyHelper bn;
	private FlyState flyState;
	private float liftFactor;
	private float maxPoweredFlightSpeed;
	private float thrust;
	private float thrustComponentRatioMin;
	private float thrustComponentRatioMax;
	private float maxTurnForce;
	private float optimalPitch;
	private float landingSpeedThreshold;
	private float maxRunSpeed;
	private float flightAccelX;
	private float flightAccelY;
	private float flightAccelZ;
	private boolean thrustOn;
	private float thrustEffort;
	private boolean flyPathfind;
	private boolean debugFlying;

	public EntityIMFlying(World world)
	{
		this(world, null);
	}

	public EntityIMFlying(World world, Nexus nexus)
	{
		super(world, nexus);
		this.debugFlying = true;
		this.flyState = FlyState.GROUNDED;
		this.maxPoweredFlightSpeed = 0.28F;
		this.liftFactor = 0.4F;
		this.thrust = 0.08F;
		this.thrustComponentRatioMin = 0.0F;
		this.thrustComponentRatioMax = 0.1F;
		this.maxTurnForce = (this.getGravity() * 3.0F);
		this.optimalPitch = 52.0F;
		this.landingSpeedThreshold = (this.getMoveSpeedStat() * 1.2F);
		this.maxRunSpeed = 0.45F;
		this.thrustOn = false;
		this.thrustEffort = 1.0F;
		this.flyPathfind = true;

		this.i = new IMMoveHelperFlying(this);
		this.h = new IMLookHelper(this);
		this.bn = new IMBodyHelper(this);
		IPathSource pathSource = this.getPathSource();
		pathSource.setSearchDepth(800);
		pathSource.setQuickFailDepth(200);
		this.navigatorFlying = new NavigatorFlying(this, pathSource);

		//this.dataWatcher.addObject(29, Integer.valueOf(0));
		//this.dataWatcher.addObject(30, Integer.valueOf(0));
		//this.dataWatcher.addObject(31, Integer.valueOf(0));
		//this.dataWatcher.addObject(28, Byte.valueOf((byte) 0));
		//this.dataWatcher.addObject(27, Integer.valueOf(this.flyState.ordinal()));

		this.getDataManager().register(TARGET, new BlockPos(0, 0, 0));
		this.getDataManager().register(FLY_STATE, this.flyState.ordinal());
		this.getDataManager().register(THRUST, Byte.valueOf((byte)0));
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (!this.world.isRemote)
		{
			if (this.debugFlying)
			{
				Vec3d target = this.navigatorFlying.getTarget();
				//float oldTargetX = MathUtil.unpackFloat(this.dataWatcher.getWatchableObjectInt(29));
				//float oldTargetY = MathUtil.unpackFloat(this.dataWatcher.getWatchableObjectInt(30));
				//float oldTargetZ = MathUtil.unpackFloat(this.dataWatcher.getWatchableObjectInt(31));
				float oldTargetX = MathUtil.unpackFloat(this.getDataManager().get(TARGET).getX());
				float oldTargetY = MathUtil.unpackFloat(this.getDataManager().get(TARGET).getY());
				float oldTargetZ = MathUtil.unpackFloat(this.getDataManager().get(TARGET).getZ());
				float targX = (float)target.x;
				float targY = (float)target.y;
				float targZ = (float)target.z;

				if ((!MathUtil.floatEquals(oldTargetX, targX, 0.1F)) || (!MathUtil.floatEquals(oldTargetY, targY, 0.1F)) || (!MathUtil.floatEquals(oldTargetZ, targZ, 0.1F)))
				{
					//this.dataWatcher.updateObject(29, Integer.valueOf(MathUtil.packFloat((float) target.xCoord)));
					//this.dataWatcher.updateObject(30, Integer.valueOf(MathUtil.packFloat((float) target.yCoord)));
					//this.dataWatcher.updateObject(31, Integer.valueOf(MathUtil.packFloat((float) target.zCoord)));
					this.getDataManager().set(TARGET, new BlockPos(MathUtil.packFloat(targX), MathUtil.packFloat(targY), MathUtil.packFloat(targZ)));
				}
			}

			//byte thrustData = this.dataWatcher.getWatchableObjectByte(28);
			byte thrustData = this.getDataManager().get(THRUST);
			int oldThrustOn = thrustData & 0x1;
			int oldThrustEffortEncoded = thrustData >> 1 & 0xF;
			int thrustEffortEncoded = (int)(this.thrustEffort * 15.0F);
			if (this.thrustOn == oldThrustOn > 0)
			{
				if (thrustEffortEncoded == oldThrustEffortEncoded)
					;
			}
			else
			{
				//this.dataWatcher.updateObject(28, Byte.valueOf((byte) (thrustEffortEncoded << 1 | oldThrustOn)));
				this.getDataManager().set(THRUST, Byte.valueOf((byte)(thrustEffortEncoded << 1 | oldThrustOn)));
			}

		}
		else
		{
			if (this.debugFlying)
			{
				BlockPos targ = this.getDataManager().get(TARGET);
				this.navigatorFlying.setTarget(MathUtil.unpackFloat(targ.getX()), MathUtil.unpackFloat(targ.getY()), MathUtil.unpackFloat(targ.getZ()));
			}

			this.flyState = FlyState.values()[this.getDataManager().get(FLY_STATE)];

			//byte thrustData = this.dataWatcher.getWatchableObjectByte(28);
			byte thrustData = this.getDataManager().get(THRUST);
			this.thrustOn = ((thrustData & 0x1) > 0);
			this.thrustEffort = ((thrustData >> 1 & 0xF) / 15.0F);
		}
	}

	public FlyState getFlyState()
	{
		return this.flyState;
	}

	public boolean isThrustOn()
	{
		//return this.dataWatcher.getWatchableObjectByte(28) != 0;
		return this.getDataManager().get(THRUST) != 0;
	}

	public float getThrustEffort()
	{
		return this.thrustEffort;
	}

	public Vec3d getFlyTarget()
	{
		return this.navigatorFlying.getTarget();
	}

	@Override
	public INavigationFlying getNavigatorNew()
	{
		return this.navigatorFlying;
	}

	@Override
	public IMMoveHelperFlying getMoveHelper()
	{
		return this.i;
	}

	@Override
	public IMLookHelper getLookHelper()
	{
		return this.h;
	}

	public IMBodyHelper getBodyHelper()
	{
		return this.bn;
	}

	@Override
	public void moveEntityWithHeading(float x, float z)
	{
		if (this.isInWater())
		{
			double y = this.posY;
			this.moveFlying(x, z, 0.04F);
			this.setVelocity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.8D;
			this.motionY *= 0.8D;
			this.motionZ *= 0.8D;
			this.motionY -= 0.02D;
			if ((this.collidedHorizontally)
				&& (this.isOffsetPositionInLiquid(this.motionX, this.motionY
					+ 0.6D - this.posY + y, this.motionZ)))
				this.motionY = 0.3D;
		}
		else if (this.isInLava())
		{
			double y = this.posY;
			this.moveFlying(x, z, 0.04F);
			this.setVelocity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			this.motionY -= 0.02D;
			if ((this.collidedHorizontally)
				&& (this.isOffsetPositionInLiquid(this.motionX, this.motionY
					+ 0.6D - this.posY + y, this.motionZ)))
				this.motionY = 0.3D;
		}
		else
		{
			float groundFriction = 0.9995F;

			if (this.onGround)
			{
				groundFriction = this.getGroundFriction();

				float maxRunSpeed = this.getMaxRunSpeed();
				if (this.motionX * this.motionX + this.motionZ * this.motionZ < maxRunSpeed
					* maxRunSpeed)
				{
					float landMoveSpeed = this.getAIMoveSpeed();
					landMoveSpeed *= 0.162771F / (groundFriction
						* groundFriction * groundFriction);
					this.moveFlying(x, z, landMoveSpeed);
				}
			}
			else
			{
				this.moveFlying(x, z, 0.01F);
			}

			this.motionX += this.flightAccelX;
			this.motionY += this.flightAccelY;
			this.motionZ += this.flightAccelZ;

			this.setVelocity(this.motionX, this.motionY, this.motionZ);
			this.motionY -= this.getGravity();
			this.motionY *= this.getAirResistance();
			this.motionX *= groundFriction * this.getAirResistance();
			this.motionZ *= groundFriction * this.getAirResistance();
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dX = this.posX - this.prevPosX;
		double dZ = this.posZ - this.prevPosZ;
		float limbEnergy = MathHelper.sqrt(dX * dX + dZ * dZ) * 4.0F;

		if (limbEnergy > 1.0F)
		{
			limbEnergy = 1.0F;
		}

		this.limbSwingAmount += (limbEnergy - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public boolean isOnLadder()
	{
		return false;
	}

	public boolean hasFlyingDebug()
	{
		return this.debugFlying;
	}

	public void setPathfindFlying(boolean flag)
	{
		this.flyPathfind = flag;
	}

	public void setFlyState(FlyState flyState)
	{
		this.flyState = flyState;
		if (!this.world.isRemote) this.getDataManager().set(FLY_STATE, flyState.ordinal());
		//this.dataWatcher.updateObject(27, Integer.valueOf(flyState.ordinal()));
	}

	public float getMaxPoweredFlightSpeed()
	{
		return this.maxPoweredFlightSpeed;
	}

	public float getLiftFactor()
	{
		return this.liftFactor;
	}

	public float getThrust()
	{
		return this.thrust;
	}

	public float getThrustComponentRatioMin()
	{
		return this.thrustComponentRatioMin;
	}

	public float getThrustComponentRatioMax()
	{
		return this.thrustComponentRatioMax;
	}

	public float getMaxTurnForce()
	{
		return this.maxTurnForce;
	}

	public float getMaxPitch()
	{
		return this.optimalPitch;
	}

	public float getLandingSpeedThreshold()
	{
		return this.landingSpeedThreshold;
	}

	protected float getMaxRunSpeed()
	{
		return this.maxRunSpeed;
	}

	public void setFlightAccelerationVector(float xAccel, float yAccel,
		float zAccel)
	{
		this.flightAccelX = xAccel;
		this.flightAccelY = yAccel;
		this.flightAccelZ = zAccel;
	}

	public void setThrustOn(boolean flag)
	{
		this.thrustOn = flag;
	}

	public void setThrustEffort(float effortFactor)
	{
		this.thrustEffort = effortFactor;
	}

	protected void setMaxPoweredFlightSpeed(float speed)
	{
		this.maxPoweredFlightSpeed = speed;
		this.getNavigatorNew().setFlySpeed(speed);
	}

	protected void setThrust(float thrust)
	{
		this.thrust = thrust;
	}

	protected void setLiftFactor(float liftFactor)
	{
		this.liftFactor = liftFactor;
	}

	protected void setThrustComponentRatioMin(float ratio)
	{
		this.thrustComponentRatioMin = ratio;
	}

	protected void setThrustComponentRatioMax(float ratio)
	{
		this.thrustComponentRatioMax = ratio;
	}

	protected void setMaxTurnForce(float maxTurnForce)
	{
		this.maxTurnForce = maxTurnForce;
	}

	protected void setOptimalPitch(float pitch)
	{
		this.optimalPitch = pitch;
	}

	protected void setLandingSpeedThreshold(float speed)
	{
		this.landingSpeedThreshold = speed;
	}

	protected void setMaxRunSpeed(float speed)
	{
		this.maxRunSpeed = speed;
	}

	@Override
	public void fall(float par1, float multiplier)
	{
	}

	// @Override
	// protected void updateFallState(double par1, boolean par3) {
	// }

	@Override
	protected void calcPathOptions(IBlockAccess terrainMap,
		PathNode currentNode, PathfinderIM pathFinder)
	{
		if (!this.flyPathfind)
			super.calcPathOptions(terrainMap, currentNode, pathFinder);
		else
			this.calcPathOptionsFlying(terrainMap, currentNode, pathFinder);
	}

	protected void calcPathOptionsFlying(IBlockAccess terrainMap,
		PathNode currentNode, PathfinderIM pathFinder)
	{
		if ((currentNode.pos.y <= 0) || (currentNode.pos.y > 255))
		{
			return;
		}

		if (this.getCollide(terrainMap, currentNode.pos.addVector(0d, 1d, 0d)) > 0)
		{
			pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.NONE);
		}

		if (this.getCollide(terrainMap, currentNode.pos.addVector(0d, -1d, 0d)) > 0)
		{
			pathFinder.addNode(currentNode.pos.addVector(0d, -1d, 0d), PathAction.NONE);
		}

		for (int i = 0; i < 4; i++)
		{
			if (this.getCollide(terrainMap, currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])) > 0)
			{
				pathFinder.addNode(currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i]), PathAction.NONE);
			}
		}
		if (this.canSwimHorizontal())
		{
			for (int i = 0; i < 4; i++)
			{
				if (this.getCollide(terrainMap, currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])) == -1)
					pathFinder.addNode(currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i]), PathAction.SWIM);
			}
		}
	}

	@Override
	protected float calcBlockPathCost(PathNode prevNode, PathNode node,
		IBlockAccess terrainMap)
	{
		float multiplier = 1.0F;
		if ((terrainMap instanceof IBlockAccessExtended))
		{
			int mobDensity = ((IBlockAccessExtended)terrainMap)
				.getLayeredData(node.pos.x, node.pos.y, node.pos.z) & 0x7;
			multiplier += mobDensity * 3;
		}

		// for (int i = -1; i > -6; i--) {
		// Block block = terrainMap.getBlock(node.xCoord, node.yCoord + i,
		// node.zCoord);
		// if (block != Blocks.air) {
		// int blockType = getBlockType(block);
		// if (blockType != 1) {
		// multiplier += 1.0F - -i * 0.2F;
		// if ((blockType != 2) || (i < -2))
		// break;
		// multiplier = (float) (multiplier + (6.0D - -i * 2.0D));
		// break;
		// }
		//
		// }
		//
		// }
		//
		// for (int i = 0; i < 4; i++) {
		// for (int j = 1; j <= 2; j++) {
		// Block block = terrainMap.getBlock(node.xCoord +
		// invmod.util.CoordsInt.offsetAdjX[i] * j, node.yCoord, node.zCoord +
		// invmod.util.CoordsInt.offsetAdjZ[i] * j);
		// int blockType = getBlockType(block);
		// if (blockType != 1) {
		// multiplier += 1.5F - j * 0.5F;
		// if ((blockType != 2) || (i < -2))
		// break;
		// multiplier += 6.0F - j * 2.0F;
		// break;
		// }
		//
		// }
		//
		// }

		if (node.action == PathAction.SWIM)
		{
			multiplier *= ((node.pos.y <= prevNode.pos.y)
				&& (!terrainMap.isAirBlock(new BlockPos(node.pos.x,
					node.pos.y + 1, node.pos.z))) ? 3.0F : 1.0F);
			return prevNode.distanceTo(node) * 1.3F * multiplier;
		}

		Block block = terrainMap.getBlockState(
			new BlockPos(node.pos.x, node.pos.y, node.pos.z)).getBlock();
		return prevNode.distanceTo(node) * block.getExplosionResistance(null)
			* multiplier;
	}
}