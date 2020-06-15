package invasion.entity;

import net.minecraft.entity.Entity;


public interface INavigationFlying extends INavigation {
    void setMovementType(MoveType paramMoveType);

    void setLandingPath();

    void setCirclingPath(Entity paramEntity, float paramFloat1, float paramFloat2);

    void setCirclingPath(double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2);

    float getDistanceToCirclingRadius();

    boolean isCircling();

    void setFlySpeed(float paramFloat);

    void setPitchBias(float paramFloat1, float paramFloat2);

    void enableDirectTarget(boolean paramBoolean);

    enum MoveType {
        PREFER_WALKING, MIXED, PREFER_FLYING
    }
}