package invasion.util;

import net.minecraft.entity.Entity;

import java.util.Comparator;


public class ComparatorEntityDistanceFrom implements Comparator<Entity> {
    private final double posX;
    private final double posY;
    private final double posZ;

    public ComparatorEntityDistanceFrom(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Override
    public int compare(Entity entity1, Entity entity2) {
        double d1 = (this.posX - entity1.posX) * (this.posX - entity1.posX) + (this.posY - entity1.posY) * (this.posY - entity1.posY) + (this.posZ - entity1.posZ) * (this.posZ - entity1.posZ);
        double d2 = (this.posX - entity2.posX) * (this.posX - entity2.posX) + (this.posY - entity2.posY) * (this.posY - entity2.posY) + (this.posZ - entity2.posZ) * (this.posZ - entity2.posZ);
        if (d1 > d2)
            return -1;
        if (d1 < d2) {
            return 1;
        }
        return 0;
    }
}