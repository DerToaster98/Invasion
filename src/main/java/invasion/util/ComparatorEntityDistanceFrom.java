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
        double d1 = (this.posX - entity1.getPosX()) * (this.posX - entity1.getPosX()) + (this.posY - entity1.getPosY()) * (this.posY - entity1.getPosY()) + (this.posZ - entity1.getPosZ()) * (this.posZ - entity1.getPosZ());
        double d2 = (this.posX - entity2.getPosX()) * (this.posX - entity2.getPosX()) + (this.posY - entity2.getPosY()) * (this.posY - entity2.getPosY()) + (this.posZ - entity2.getPosZ()) * (this.posZ - entity2.getPosZ());
        if (d1 > d2)
            return -1;
        if (d1 < d2) {
            return 1;
        }
        return 0;
    }
}