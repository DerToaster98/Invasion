package invasion;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface IBlockAccessExtended {

    int getLayeredData(int paramInt1, int paramInt2, int paramInt3);

    default int getLayeredData(double x, double y, double z) {
        return this.getLayeredData(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    default int getLayeredData(Vec3d vec) {
        return this.getLayeredData(vec.x, vec.y, vec.z);
    }

    void setData(double x, double y, double z, Integer paramInteger);

}