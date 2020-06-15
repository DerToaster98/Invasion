package invasion.util;

import invasion.nexus.SpawnPoint;

import java.util.Comparator;


public class ComparatorDistanceFrom implements Comparator<SpawnPoint> {
    private final double x;
    private final double y;
    private final double z;

    public ComparatorDistanceFrom(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int compare(SpawnPoint pos1, SpawnPoint pos2) {
        double d1 = (this.x - pos1.getPos().getX()) * (this.x - pos1.getPos().getX()) + (this.y - pos1.getPos().getY()) * (this.y - pos1.getPos().getY()) + (this.z - pos1.getPos().getZ()) * (this.z - pos1.getPos().getZ());
        double d2 = (this.x - pos2.getPos().getX()) * (this.x - pos2.getPos().getX()) + (this.y - pos2.getPos().getY()) * (this.y - pos2.getPos().getY()) + (this.z - pos2.getPos().getZ()) * (this.z - pos2.getPos().getZ());
        return Double.compare(d2, d1);
    }
}