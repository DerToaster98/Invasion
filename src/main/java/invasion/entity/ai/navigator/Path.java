package invasion.entity.ai.navigator;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;


public class Path {
    public final PathNode[] points;
    private PathNode intendedTarget;
    private int pathLength;
    private int pathIndex;
    private float totalCost;

    public Path(PathNode[] apathpoint) {
        points = apathpoint;
        pathLength = apathpoint.length;
        if (apathpoint.length > 0) {
            intendedTarget = apathpoint[(apathpoint.length - 1)];
        }
    }

    public Path(PathNode[] apathpoint, PathNode intendedTarget) {
        points = apathpoint;
        pathLength = apathpoint.length;
        this.intendedTarget = intendedTarget;
    }

    public float getTotalPathCost() {
        return points[(pathLength - 1)].totalPathDistance;
    }

    public void incrementPathIndex() {
        pathIndex += 1;
    }

    public boolean isFinished() {
        return pathIndex >= points.length;
    }

    public PathNode getFinalPathPoint() {
        if (pathLength > 0) return points[(pathLength - 1)];
        return null;
    }

    public PathNode getPathPointFromIndex(int par1) {
        return points[par1];
    }

    public int getCurrentPathLength() {
        return pathLength;
    }

    public void setCurrentPathLength(int par1) {
        pathLength = par1;
    }

    public int getCurrentPathIndex() {
        return pathIndex;
    }

    public void setCurrentPathIndex(int par1) {
        pathIndex = par1;
    }

    public PathNode getIntendedTarget() {
        return intendedTarget;
    }

    /**
     * Gets the vector of the PathPoint associated with the given index.
     */
    public Vec3d getPositionAtIndex(Entity entity, int index) {
        double d = points[index].pos.x + (int) (entity.getWidth() + 1.0F) * 0.5D;
        double d1 = points[index].pos.y;
        double d2 = points[index].pos.z + (int) (entity.getWidth() + 1.0F) * 0.5D;
        return new Vec3d(d, d1, d2);
    }

    public Vec3d getCurrentNodeVec3d(Entity entity) {
        return getPositionAtIndex(entity, pathIndex);
    }

    public Vec3d destination() {
        return points[(points.length - 1)].pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return pathLength == path.pathLength &&
                pathIndex == path.pathIndex &&
                Float.compare(path.totalCost, totalCost) == 0 &&
                Arrays.equals(points, path.points) &&
                Objects.equals(intendedTarget, path.intendedTarget);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(intendedTarget, pathLength, pathIndex, totalCost);
        result = 31 * result + Arrays.hashCode(points);
        return result;
    }
}