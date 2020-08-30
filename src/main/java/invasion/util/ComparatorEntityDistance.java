package invasion.util;

import net.minecraft.entity.Entity;

import java.util.Comparator;


public class ComparatorEntityDistance implements Comparator<Entity> {
    private final double x;
    private final double y;
    private final double z;

    public ComparatorEntityDistance(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public int compare(Entity entity1, Entity entity2) {
        double d1 = (this.x - entity1.getPosX()) * (this.x - entity1.getPosX()) + (this.y - entity1.getPosY()) * (this.y - entity1.getPosY()) + (this.z - entity1.getPosZ()) * (this.z - entity1.getPosZ());
        double d2 = (this.x - entity2.getPosX()) * (this.x - entity2.getPosX()) + (this.y - entity2.getPosY()) * (this.y - entity2.getPosY()) + (this.z - entity2.getPosZ()) * (this.z - entity2.getPosZ());
        return Double.compare(d2, d1);
    }
}