package invasion.entity.ai.navigator;


public class NodeContainer {
    private PathNode[] pathPoints;
    private int count;

    public NodeContainer() {
        this.pathPoints = new PathNode[1024];
        this.count = 0;
    }

    public PathNode addPoint(PathNode pathpoint) {
        if (pathpoint.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathPoints.length) {
            PathNode[] apathpoint = new PathNode[this.count << 1];
            System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
            this.pathPoints = apathpoint;
        }
        this.pathPoints[this.count] = pathpoint;
        pathpoint.index = this.count;
        this.sortBack(this.count++);
        return pathpoint;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathNode dequeue() {
        PathNode pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[(--this.count)];
        this.pathPoints[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }
        pathpoint.index = -1;
        return pathpoint;
    }

    public void changeDistance(PathNode pathpoint, double d0) {
        double d1 = pathpoint.distanceToTarget;
        pathpoint.distanceToTarget = d0;
        if (d0 < d1) {
            this.sortBack(pathpoint.index);
        } else {
            this.sortForward(pathpoint.index);
        }
    }

    private void sortBack(int i) {
        PathNode pathpoint = this.pathPoints[i];
        double d = pathpoint.distanceToTarget;

        while (i > 0) {
            int j = i - 1 >> 1;
            PathNode pathpoint1 = this.pathPoints[j];
            if (d >= pathpoint1.distanceToTarget) {
                break;
            }
            this.pathPoints[i] = pathpoint1;
            pathpoint1.index = i;
            i = j;
        }

        this.pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    private void sortForward(int i) {
        PathNode pathpoint = this.pathPoints[i];
        double d0 = pathpoint.distanceToTarget;
        while (true) {
            int j = 1 + (i << 1);
            int k = j + 1;
            if (j >= this.count) {
                break;
            }
            PathNode pathpoint1 = this.pathPoints[j];
            double d1 = pathpoint1.distanceToTarget;
            double d2;
            PathNode pathpoint2;
            if (k >= this.count) {
                pathpoint2 = null;
                d2 = 1d;
            } else {
                pathpoint2 = this.pathPoints[k];
                d2 = pathpoint2.distanceToTarget;
            }
            if (d1 < d2) {
                if (d1 >= d0) {
                    break;
                }
                this.pathPoints[i] = pathpoint1;

                pathpoint1.index = i;
                i = j;
            } else {

                if (d2 >= d0) {
                    break;
                }
                //Unstoppable Custom Testcode
                //this seems to temp fix mobs not being able to spawn.
                if (pathpoint2 == null) {
                    break;
                }
                //end Unstoppable Custom Testcode
                this.pathPoints[i] = pathpoint2;
                pathpoint2.index = i;
                i = k;
            }
        }
        this.pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}