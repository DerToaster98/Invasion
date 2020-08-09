package invasion.entity.ai.navigator;


public class NodeContainer {
    private PathNode[] pathPoints = new PathNode[1024];
    private int count;

    public void addPoint(PathNode pathpoints) {
        if (pathpoints.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (count == pathPoints.length) {
            PathNode[] apathpoint = new PathNode[count << 1];
            System.arraycopy(pathPoints, 0, apathpoint, 0, count);
            pathPoints = apathpoint;
        }
        pathPoints[count] = pathpoints;
        pathpoints.index = count;
        sortBack(count++);
    }

    public void clearPath() {
        count = 0;
    }

    public PathNode dequeue() {
        PathNode pathpoint = pathPoints[0];
        pathPoints[0] = pathPoints[(--count)];
        pathPoints[count] = null;
        if (count > 0) {
            sortForward(0);
        }
        pathpoint.index = -1;
        return pathpoint;
    }

    public void changeDistance(PathNode pathpoint, double d0) {
        double d1 = pathpoint.distanceToTarget;
        pathpoint.distanceToTarget = d0;
        if (d0 < d1) {
            sortBack(pathpoint.index);
        } else {
            sortForward(pathpoint.index);
        }
    }

    private void sortBack(int i) {
        PathNode pathpoint = pathPoints[i];
        double d = pathpoint.distanceToTarget;

        while (i > 0) {
            int j = i - 1 >> 1;
            PathNode pathpoint1 = pathPoints[j];
            if (d >= pathpoint1.distanceToTarget) {
                break;
            }
            pathPoints[i] = pathpoint1;
            pathpoint1.index = i;
            i = j;
        }

        pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    private void sortForward(int i) {
        PathNode pathpoint = pathPoints[i];
        double d0 = pathpoint.distanceToTarget;
        while (true) {
            int j = 1 + (i << 1);
            int k = j + 1;
            if (j >= count) {
                break;
            }
            PathNode pathpoint1 = pathPoints[j];
            double d1 = pathpoint1.distanceToTarget;
            double d2;
            PathNode pathpoint2;
            if (k >= count) {
                pathpoint2 = null;
                d2 = 1d;
            } else {
                pathpoint2 = pathPoints[k];
                d2 = pathpoint2.distanceToTarget;
            }
            if (d1 < d2) {
                if (d1 >= d0) {
                    break;
                }
                pathPoints[i] = pathpoint1;

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
                pathPoints[i] = pathpoint2;
                pathpoint2.index = i;
                i = k;
            }
        }
        pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    public boolean isPathEmpty() {
        return count == 0;
    }
}