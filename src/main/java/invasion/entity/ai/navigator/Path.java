package invasion.entity.ai.navigator;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;


public class Path {
    public final PathNode[] points;
    private PathNode intendedTarget;
    private int pathLength;
    private int pathIndex;
    private float totalCost;

    public Path(PathNode[] apathpoint) {
        this.points = apathpoint;
        this.pathLength = apathpoint.length;
        if (apathpoint.length > 0) {
            this.intendedTarget = apathpoint[(apathpoint.length - 1)];
        }
    }

    public Path(PathNode[] apathpoint, PathNode intendedTarget) {
        this.points = apathpoint;
        this.pathLength = apathpoint.length;
        this.intendedTarget = intendedTarget;
    }

    public float getTotalPathCost() {
        return this.points[(this.pathLength - 1)].totalPathDistance;
    }

    public void incrementPathIndex() {
        this.pathIndex += 1;
    }

    public boolean isFinished() {
        return this.pathIndex >= this.points.length;
    }

    public PathNode getFinalPathPoint() {
        if (this.pathLength > 0) return this.points[(this.pathLength - 1)];
        return null;
    }

    public PathNode getPathPointFromIndex(int par1) {
        return this.points[par1];
    }

    public int getCurrentPathLength() {
        return this.pathLength;
    }

    public void setCurrentPathLength(int par1) {
        this.pathLength = par1;
    }

    public int getCurrentPathIndex() {
        return this.pathIndex;
    }

    public void setCurrentPathIndex(int par1) {
        this.pathIndex = par1;
    }

    public PathNode getIntendedTarget() {
        return this.intendedTarget;
    }

    /**
     * Gets the vector of the PathPoint associated with the given index.
     */
    public Vec3d getPositionAtIndex(Entity entity, int index) {
        double d = this.points[index].pos.x + (int) (entity.width + 1.0F) * 0.5D;
        double d1 = this.points[index].pos.y;
        double d2 = this.points[index].pos.z + (int) (entity.width + 1.0F) * 0.5D;
        return new Vec3d(d, d1, d2);
    }

    public Vec3d getCurrentNodeVec3d(Entity entity) {
        return this.getPositionAtIndex(entity, this.pathIndex);
    }

    public Vec3d destination() {
        return this.points[(this.points.length - 1)].pos;
    }

    public boolean equalsPath(Path par1PathEntity) {
        if (par1PathEntity == null) return false;

        if (par1PathEntity.points.length != this.points.length) return false;

        for (int i = 0; i < this.points.length; i++) {
            if (!this.points[i].pos.equals(par1PathEntity.points[i].pos)) {
                return false;
            }
        }

        return true;
    }

    public boolean isDestinationSame(Vec3d par1Vec3D) {
        PathNode pathpoint = this.getFinalPathPoint();
        if (pathpoint == null) return false;
        return (pathpoint.pos.x == par1Vec3D.x) && (pathpoint.pos.z == par1Vec3D.z);
    }

}