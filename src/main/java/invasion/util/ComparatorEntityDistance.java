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
        double d1 = (this.x - entity1.posX) * (this.x - entity1.posX) + (this.y - entity1.posY) * (this.y - entity1.posY) + (this.z - entity1.posZ) * (this.z - entity1.posZ);
        double d2 = (this.x - entity2.posX) * (this.x - entity2.posX) + (this.y - entity2.posY) * (this.y - entity2.posY) + (this.z - entity2.posZ) * (this.z - entity2.posZ);
        return Double.compare(d2, d1);
    }
}