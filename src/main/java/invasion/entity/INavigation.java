package invasion.entity;

import invasion.INotifyTask;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathAction;
import net.minecraft.entity.Entity;


public interface INavigation extends INotifyTask {

    PathAction getCurrentWorkingAction();

    void setSpeed(float paramFloat);

    Path getPathToXYZ(double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat);

    boolean tryMoveToXYZ(double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2);

    Path getPathTowardsXZ(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3);

    boolean tryMoveTowardsXZ(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3, float paramFloat);

    Path getPathToEntity(Entity paramEntity, float paramFloat);

    boolean tryMoveToEntity(Entity paramEntity, float paramFloat1, float paramFloat2);

    void autoPathToEntity(Entity paramEntity);

    boolean setPath(Path paramPath, float paramFloat);

    boolean isWaitingForTask();

    Path getPath();

    void onUpdateNavigation();

    int getLastActionResult();

    boolean noPath();

    int getStuckTime();

    float getLastPathDistanceToTarget();

    void clearPath();

    void haltForTick();

    Entity getTargetEntity();

    String getStatus();

}