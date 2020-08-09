package invasion.entity.ai.navigator;

import invasion.IPathfindable;
import invasion.entity.EntityIMLiving;
import invasion.entity.IPathResult;
import invasion.entity.IPathSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class PathCreator implements IPathSource {

    private int searchDepth;
    private int quickFailDepth;
    private final int[] nanosUsed = new int[6];
    private int index;

    public PathCreator() {
        this(200, 50);
    }

    public PathCreator(int searchDepth, int quickFailDepth) {
        this.searchDepth = searchDepth;
        this.quickFailDepth = quickFailDepth;
    }

    @Override
    public int getSearchDepth() {
        return searchDepth;
    }

    @Override
    public void setSearchDepth(int depth) {
        searchDepth = depth;
    }

    @Override
    public int getQuickFailDepth() {
        return quickFailDepth;
    }

    @Override
    public void setQuickFailDepth(int depth) {
        quickFailDepth = depth;
    }

    @Override
    public Path createPath(IPathfindable entity, Vec3d pos0, Vec3d pos1, float targetRadius, float maxSearchRange, IBlockAccess terrainMap) {
        long time = System.nanoTime();
        Path path = PathfinderIM.createPath(entity, pos0, pos1,
                targetRadius, maxSearchRange, terrainMap, searchDepth,
                quickFailDepth);
        int elapsed = (int) (System.nanoTime() - time);
        nanosUsed[index] = elapsed;
        if (++index >= nanosUsed.length) {
            index = 0;
        }
        return path;
    }

    @Override
    public Path createPath(EntityIMLiving entity, Entity target, float targetRadius, float maxSearchRange, IBlockAccess terrainMap) {
        Vec3d vec = new Vec3d(target.posX + 0.5D - entity.width / 2.0F, target.posY, target.posZ + 0.5D - entity.width / 2.0F);
        return createPath(entity, vec, targetRadius, maxSearchRange, terrainMap);
    }

    @Override
    public Path createPath(EntityIMLiving entity, Vec3d vec, float targetRadius, float maxSearchRange, IBlockAccess terrainMap) {
        BlockPos size = entity.getCollideSize();
        double startZ;
        double startX;
        double startY = entity.getEntityBoundingBox().minY;
        if ((size.getX() <= 1) && (size.getZ() <= 1)) {
            startX = entity.getPosition().getX();
            startZ = entity.getPosition().getZ();
        } else {
            startX = entity.getEntityBoundingBox().minX;
            startZ = entity.getEntityBoundingBox().minZ;
        }
        return createPath(entity, new Vec3d(startX, startY, startZ),
                vec.addVector(0.5d - entity.width / 2.0F, 0d, 0.5d - entity.width / 2d),
                targetRadius, maxSearchRange, terrainMap);
    }

    @Override
    public void createPath(IPathResult observer, IPathfindable entity, BlockPos pos0, BlockPos pos1, float maxSearchRange,
                           IBlockAccess terrainMap) {
    }

    @Override
    public void createPath(IPathResult observer, EntityIMLiving entity,
                           Entity target, float maxSearchRange, IBlockAccess terrainMap) {
    }

    @Override
    public void createPath(IPathResult observer, EntityIMLiving entity, BlockPos pos, float maxSearchRange, IBlockAccess terrainMap) {
    }

    @Override
    public boolean canPathfindNice(IPathSource.PathPriority priority,
                                   float maxSearchRange, int searchDepth, int quickFailDepth) {
        return true;
    }
}