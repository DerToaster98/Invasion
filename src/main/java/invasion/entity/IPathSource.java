package invasion.entity;

import invasion.IPathfindable;
import invasion.entity.ai.navigator.Path;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public interface IPathSource {

    Path createPath(IPathfindable paramIPathfindable, Vec3d pos0In, Vec3d pos1In, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

    Path createPath(EntityIMLiving paramEntityIMLiving, Entity paramEntity, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

    Path createPath(EntityIMLiving paramEntityIMLiving, Vec3d vec, float paramFloat1, float paramFloat2, IBlockAccess paramIBlockAccess);

    void createPath(IPathResult paramIPathResult, IPathfindable paramIPathfindable, BlockPos pos0, BlockPos pos1, float paramFloat, IBlockAccess paramIBlockAccess);

    void createPath(IPathResult paramIPathResult, EntityIMLiving paramEntityIMLiving, Entity paramEntity, float paramFloat, IBlockAccess paramIBlockAccess);

    void createPath(IPathResult paramIPathResult, EntityIMLiving paramEntityIMLiving, BlockPos pos, float paramFloat, IBlockAccess paramIBlockAccess);

    int getSearchDepth();

    void setSearchDepth(int paramInt);

    int getQuickFailDepth();

    void setQuickFailDepth(int paramInt);

    boolean canPathfindNice(PathPriority paramPathPriority, float paramFloat, int paramInt1, int paramInt2);

    enum PathPriority {
        LOW, MEDIUM, HIGH
    }

}