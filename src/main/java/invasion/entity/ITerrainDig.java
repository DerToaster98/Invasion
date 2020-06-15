package invasion.entity;

import invasion.INotifyTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public interface ITerrainDig {

    boolean askRemoveBlock(BlockPos pos, INotifyTask onFinished, float costMultiplier);

    boolean askClearPosition(BlockPos pos, INotifyTask onFinished, float costMultiplier);

    default boolean askRemoveBlock(Vec3d vec, INotifyTask onFinished, float costMultiplier) {
        return this.askRemoveBlock(new BlockPos(vec), onFinished, costMultiplier);
    }

    default boolean askClearPosition(Vec3d vec, INotifyTask onFinished, float costMultiplier) {
        return this.askClearPosition(new BlockPos(vec), onFinished, costMultiplier);
    }

}