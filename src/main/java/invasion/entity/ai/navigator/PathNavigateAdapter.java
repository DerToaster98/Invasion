package invasion.entity.ai.navigator;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.Vec3d;


public class PathNavigateAdapter extends PathNavigateGround
{

	private NavigatorIM navigator;
	protected WalkNodeProcessor walkNodeProcessor;

	public PathNavigateAdapter(NavigatorIM navigator)
	{
		super(navigator.getEntity(), navigator.getEntity().world);
		this.navigator = navigator;
		if (this.nodeProcessor == null) this.nodeProcessor = this.walkNodeProcessor;
	}

	@Override
	public void onUpdateNavigation()
	{
		this.navigator.onUpdateNavigation();
	}

	@Override
	public boolean noPath()
	{
		return this.navigator.noPath();
	}

	@Override
	public void clearPath()
	{
		this.navigator.clearPath();
	}

	@Override
	public void setSpeed(double speed)
	{
		this.navigator.setSpeed((float)speed);
	}

	@Override
	public boolean tryMoveToXYZ(double x, double y, double z, double movespeed)
	{
		return this.navigator.tryMoveToXYZ(x, y, z, 0.0F, (float)movespeed);
	}

	@Override
	public boolean tryMoveToEntityLiving(Entity entity, double movespeed)
	{
		return this.navigator.tryMoveToEntity(entity, 0.0F, (float)movespeed);
	}

	public boolean setPath(invasion.entity.ai.navigator.Path entity, float movespeed)
	{
		return this.navigator.setPath(entity, movespeed);
	}

	@Override
	public boolean setPath(net.minecraft.pathfinding.Path entity, double movespeed)
	{
		return false;
	}

	// @Override
	// public PathEntity getPathToXYZ(double x, double y, double z) {
	// return null;
	// }
	//
	// @Override
	// public void setAvoidsWater(boolean avoidsWater) {
	// }
	//
	// @Override
	// public boolean getAvoidsWater() {
	// return false;
	// }
	//
	// @Override
	// public void setBreakDoors(boolean breakDoors) {
	// }
	//
	// @Override
	// public void setEnterDoors(boolean enterDoors) {
	// }
	//
	// @Override
	// public boolean getCanBreakDoors() {
	// return false;
	// }
	//
	// @Override
	// public void setAvoidSun(boolean shouldAvoidSun) {
	// }
	//
	// @Override
	// public void setCanSwim(boolean shouldBeAbleToSwim) {
	// }

	@Override
	public net.minecraft.pathfinding.Path getPathToEntityLiving(Entity entity)
	{
		return null;
	}

	@Override
	public net.minecraft.pathfinding.Path getPath()
	{
		return null;
	}

	@Override
	protected PathFinder getPathFinder()
	{
		this.walkNodeProcessor = new WalkNodeProcessor();
		//was canBreakDoors
		this.walkNodeProcessor.setCanOpenDoors(true);
		this.walkNodeProcessor.setCanEnterDoors(true); //.func_176175_a(true); //TODO: Unsure.
		return new PathFinder(this.walkNodeProcessor);
	}

	@Override
	protected Vec3d getEntityPosition()
	{
		return null;
	}

	@Override
	protected boolean canNavigate()
	{
		return false;
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_)
	{
		return false;
	}

	/*@Override
	public void func_179693_d(boolean thing) {
		this.walkNodeProcessor.func_176178_d(thing);
	}*/

	//TODO: Unsure.
	@Override
	public void setBreakDoors(boolean thing)
	{
		//was canBreakDoors
		this.walkNodeProcessor.setCanOpenDoors(true);
		this.walkNodeProcessor.setCanEnterDoors(thing);
	}
}