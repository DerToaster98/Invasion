package invasion.entity;

import invasion.INotifyTask;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathAction;
import net.minecraft.entity.Entity;


public interface INavigation extends INotifyTask
{

	public PathAction getCurrentWorkingAction();

	public void setSpeed(float paramFloat);

	public Path getPathToXYZ(double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat);

	public boolean tryMoveToXYZ(double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2);

	public Path getPathTowardsXZ(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3);

	public boolean tryMoveTowardsXZ(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3, float paramFloat);

	public Path getPathToEntity(Entity paramEntity, float paramFloat);

	public boolean tryMoveToEntity(Entity paramEntity, float paramFloat1, float paramFloat2);

	public void autoPathToEntity(Entity paramEntity);

	public boolean setPath(Path paramPath, float paramFloat);

	public boolean isWaitingForTask();

	public Path getPath();

	public void onUpdateNavigation();

	public int getLastActionResult();

	public boolean noPath();

	public int getStuckTime();

	public float getLastPathDistanceToTarget();

	public void clearPath();

	public void haltForTick();

	public Entity getTargetEntity();

	public String getStatus();

}