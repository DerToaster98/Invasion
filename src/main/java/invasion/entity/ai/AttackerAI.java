package invasion.entity.ai;

import invasion.IBlockAccessExtended;
import invasion.IPathfindable;
import invasion.TerrainDataLayer;
import invasion.entity.EntityIMLiving;
import invasion.entity.IPathSource;
import invasion.entity.Scaffold;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathAction;
import invasion.entity.ai.navigator.PathCreator;
import invasion.entity.ai.navigator.PathNode;
import invasion.entity.monster.InvadingEntity;
import invasion.nexus.Nexus;
import invasion.util.Coords;
import invasion.util.Distance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//NOOB HAUS: Done


public class AttackerAI {
    private final Nexus nexus;
    private final IPathSource pathSource;
    private final Map<Integer,Integer> entityDensityData = new HashMap<>();
    private final List<Scaffold> scaffolds = new ArrayList<>();
    private int scaffoldLimit;
    private int minDistanceBetweenScaffolds;
    private int nextScaffoldCalcTimer;
    private int updateScaffoldTimer;
    private int nextEntityDensityUpdate;

    public AttackerAI(Nexus nexus) {
        this.nexus = nexus;
        pathSource = new PathCreator();
        pathSource.setSearchDepth(8500);
        pathSource.setQuickFailDepth(8500);
    }

    public void update() {
        nextScaffoldCalcTimer -= 1;
        if (--updateScaffoldTimer <= 0) {
            updateScaffoldTimer = 40;
            updateScaffolds();

            scaffoldLimit = (2 + nexus.getCurrentWave() / 2);
            minDistanceBetweenScaffolds = (90 / (nexus.getCurrentWave() + 10));
        }

        if (--nextEntityDensityUpdate <= 0) {
            nextEntityDensityUpdate = 20;
            updateDensityData();
        }
    }

    public IBlockAccessExtended wrapEntityData(TerrainDataLayer terrainMap) {
        //TODO wtf?
        TerrainDataLayer newTerrain = new TerrainDataLayer(terrainMap);
        newTerrain.setAllData(entityDensityData);
        return newTerrain;
    }

    public int getMinDistanceBetweenScaffolds() {
        return minDistanceBetweenScaffolds;
    }

    public List<Scaffold> getScaffolds() {
        return scaffolds;
    }

    public boolean askGenerateScaffolds(InvadingEntity entity) {
        if ((nextScaffoldCalcTimer > 0) || (scaffolds.size() > scaffoldLimit)) {
            return false;
        }
        nextScaffoldCalcTimer = 200;
        List<Scaffold> newScaffolds = findMinScaffolds(entity, new BlockPos(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ)));
        if ((newScaffolds != null) && (newScaffolds.size() > 0)) {
            addNewScaffolds(newScaffolds);
            return true;
        }

        return false;
    }

    public List<Scaffold> findMinScaffolds(IPathfindable pather, BlockPos pos) {
        Scaffold scaffold = new Scaffold(nexus);
        scaffold.setPathfindBase(pather);
        Path basePath = createPath(scaffold, pos, nexus.getPos(), 12.0F);
        if (basePath == null) {
            return new ArrayList<>();
        }
        List<Scaffold> scaffoldPositions = extractScaffolds(basePath);
        if (scaffoldPositions.size() > 1) {
            float lowestCost = (1.0F / 1.0F);
            int lowestCostIndex = -1;
            for (int i = 0; i < scaffoldPositions.size(); i++) {
                TerrainDataLayer terrainMap = new TerrainDataLayer(getChunkCache(pos.getX(), pos.getY(), pos.getZ(), nexus.getPos().getX(), nexus.getPos().getY(), nexus.getPos().getZ(), 12.0F));
                Scaffold s = scaffoldPositions.get(i);
                terrainMap.setData(s.getPos().x, s.getPos().y, s.getPos().z, Integer.valueOf(200000));
                Path path = createPath(pather, pos, nexus.getPos(), terrainMap);
                if ((path.getTotalPathCost() < lowestCost) && (path.getFinalPathPoint().equals(nexus.getPos()))) {
                    lowestCostIndex = i;
                }
            }

            if (lowestCostIndex >= 0) {
                List s = new ArrayList();
                s.add(scaffoldPositions.get(lowestCostIndex));
                return s;
            }

            List costDif = new ArrayList(scaffoldPositions.size());
            for (int i = 0; i < scaffoldPositions.size(); i++) {
                TerrainDataLayer terrainMap = new TerrainDataLayer(getChunkCache(pos.getX(), pos.getY(), pos.getZ(), nexus.getPos().getX(), nexus.getPos().getY(), nexus.getPos().getZ(), 12.0F));
                Scaffold s = (Scaffold) scaffoldPositions.get(i);
                for (int j = 0; j < scaffoldPositions.size(); j++) {
                    if (j != i) {
                        terrainMap.setData(s.getPos().x, s.getPos().y, s.getPos().z, Integer.valueOf(200000));
                    }
                }
                Path path = createPath(pather, pos, nexus.getPos(), terrainMap);

                if (!path.getFinalPathPoint().equals(nexus.getPos().getX(), nexus.getPos().getY(), nexus.getPos().getZ())) {
                    costDif.add(s);
                }

            }

            return costDif;
        }

        if (scaffoldPositions.size() == 1) {
            return scaffoldPositions;
        }

        return null;
    }

    public void addScaffoldDataTo(IBlockAccessExtended terrainMap) {
        for (Scaffold scaffold : scaffolds) {
            for (int i = 0; i < scaffold.getTargetHeight(); i++) {
                int data = terrainMap.getLayeredData(scaffold.getPos().x, scaffold.getPos().y + i, scaffold.getPos().z);
                terrainMap.setData(scaffold.getPos().x, scaffold.getPos().y + i, scaffold.getPos().z, data | 0x4000);
            }
        }
    }

    public Scaffold getScaffoldAt(BlockPos pos) {
        return getScaffoldAt(pos.getX(), pos.getY(), pos.getZ());
    }

    public Scaffold getScaffoldAt(int x, int y, int z) {
        for (Scaffold scaffold : scaffolds) {
            if ((scaffold.getPos().x == x) && (scaffold.getPos().z == z)) {
                if ((scaffold.getPos().y <= y) && (scaffold.getPos().y + scaffold.getTargetHeight() >= y))
                    return scaffold;
            }
        }
        return null;
    }

    public void onResume() {
        for (Scaffold scaffold : scaffolds) {
            scaffold.forceStatusUpdate();
        }
    }

    public void read(CompoundNBT nbt) {
        //Had to put extra int param in 1.7.2, used 0 not sure why
        ListNBT nbtScaffoldList = nbt.getList("scaffolds", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtScaffoldList.size(); i++) {
            Scaffold scaffold = new Scaffold(nexus);
            scaffold.readFromNBT(nbtScaffoldList.getCompound(i));
            scaffolds.add(scaffold);
        }
    }

    public void writeToNBT(CompoundNBT nbttagcompound) {
        NBTTagList nbttaglist = new NBTTagList();
        for (Scaffold scaffold : scaffolds) {
            NBTTagCompound nbtscaffold = new NBTTagCompound();
            scaffold.writeToNBT(nbtscaffold);
            nbttaglist.appendTag(nbtscaffold);
        }
        nbttagcompound.setTag("scaffolds", nbttaglist);
    }

    private Path createPath(IPathfindable pather, Vec3d pos0, Vec3d pos1, IBlockAccess terrainMap) {
        return pathSource.createPath(pather, pos0, pos1, 1.1F, 12.0F + (float) Distance.distanceBetween(pos0, pos1), terrainMap);
    }

    private Path createPath(IPathfindable pather, Vec3d pos0, Vec3d pos1, float axisExpand) {
        TerrainDataLayer terrainMap = new TerrainDataLayer(getChunkCache(new BlockPos(pos0), new BlockPos(pos1), axisExpand));
        addScaffoldDataTo(terrainMap);
        return createPath(pather, pos0, pos1, terrainMap);
    }

    private Path createPath(IPathfindable pather, BlockPos pos0, BlockPos pos1, IBlockAccess terrainMap) {
        return createPath(pather, new Vec3d(pos0.getX(), pos0.getY(), pos0.getZ()), new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ()), terrainMap);
    }

    private Path createPath(IPathfindable pather, BlockPos pos0, BlockPos pos1, float axisExpand) {
        return createPath(pather, new Vec3d(pos0.getX(), pos0.getY(), pos0.getZ()), new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ()), axisExpand);
    }

    private ChunkCache getChunkCache(BlockPos pos0, BlockPos pos1, float axisExpand) {
        return getChunkCache(pos0.getX(), pos0.getY(), pos0.getZ(), pos1.getX(), pos1.getY(), pos1.getZ(), axisExpand);
    }

    private ChunkCache getChunkCache(int x1, int y1, int z1, int x2, int y2, int z2, float axisExpand) {
        int d = (int) axisExpand;
        int cX2;
        int cX1;
        if (x1 < x2) {
            cX1 = x1 - d;
            cX2 = x2 + d;
        } else {
            cX2 = x1 + d;
            cX1 = x2 - d;
        }
        int cY2;
        int cY1;
        if (y1 < y2) {
            cY1 = y1 - d;
            cY2 = y2 + d;
        } else {
            cY2 = y1 + d;
            cY1 = y2 - d;
        }
        int cZ2;
        int cZ1;
        if (z1 < z2) {
            cZ1 = z1 - d;
            cZ2 = z2 + d;
        } else {
            cZ2 = z1 + d;
            cZ1 = z2 - d;
        }
        BlockPos blockPos = new BlockPos(cX1, cY1, cZ1);
        BlockPos blockPos2 = new BlockPos(cX2, cY2, cZ2);
        return new ChunkCache(nexus.getWorld(), blockPos, blockPos2, 0);
    }

    private List<Scaffold> extractScaffolds(Path path) {
        List scaffoldPositions = new ArrayList();
        boolean flag = false;
        double startHeight = 0d;
        for (int i = 0; i < path.getCurrentPathLength(); i++) {
            PathNode node = path.getPathPointFromIndex(i);
            if (!flag) {
                if (node.action == PathAction.SCAFFOLD_UP) {
                    flag = true;
                    startHeight = node.pos.y - 1;
                }

            } else if (node.action != PathAction.SCAFFOLD_UP) {
                Scaffold scaffold = new Scaffold(node.getPrevious().pos.x, startHeight, node.getPrevious().pos.z, MathHelper.floor(node.pos.y - startHeight), nexus);
                orientScaffold(scaffold, nexus.getWorld());
                scaffold.setInitialIntegrity();
                scaffoldPositions.add(scaffold);
                flag = false;
            }
        }

        return scaffoldPositions;
    }

    private void orientScaffold(Scaffold scaffold, IBlockAccess terrainMap) {
        int mostBlocks = 0;
        int highestDirectionIndex = 0;
        for (int i = 0; i < 4; i++) {
            int blockCount = 0;
            for (int height = 0; height < scaffold.getPos().y; height++) {
                if (terrainMap.getBlockState(new BlockPos(scaffold.getPos().addVector(Coords.offsetAdjX[i], height, Coords.offsetAdjZ[i]))).isNormalCube()) {
                    blockCount++;
                }
                if (terrainMap.getBlockState(new BlockPos(scaffold.getPos().addVector(Coords.offsetAdjX[i] * 2, height, Coords.offsetAdjZ[i] * 2))).isNormalCube()) {
                    blockCount++;
                }
            }
            if (blockCount > mostBlocks) {
                highestDirectionIndex = i;
            }
        }
        scaffold.setOrientation(highestDirectionIndex);
    }

    private void addNewScaffolds(List<Scaffold> newScaffolds) {
        for (Scaffold newScaffold : newScaffolds) {
            for (Scaffold existingScaffold : scaffolds) {
                if ((existingScaffold.getPos().x == newScaffold.getPos().x) && (existingScaffold.getPos().z == newScaffold.getPos().z)) {
                    if (newScaffold.getPos().y > existingScaffold.getPos().y) {
                        if (newScaffold.getPos().y < existingScaffold.getPos().y + existingScaffold.getTargetHeight()) {
                            existingScaffold.setHeight(MathHelper.floor(newScaffold.getPos().y + newScaffold.getTargetHeight() - existingScaffold.getPos().y));
                            break;
                        }

                    } else if (newScaffold.getPos().x + newScaffold.getTargetHeight() > existingScaffold.getPos().y) {
                        existingScaffold.setPosition(newScaffold.getPos());
                        existingScaffold.setHeight(MathHelper.floor(existingScaffold.getPos().y + existingScaffold.getTargetHeight() - newScaffold.getPos().y));
                        break;
                    }
                }

            }

            scaffolds.add(newScaffold);
        }
    }

    private void updateScaffolds() {
        for (int i = 0; i < scaffolds.size(); i++) {
            Scaffold lol = scaffolds.get(i);
            nexus.getWorld().spawnParticle(EnumParticleTypes.HEART, lol.getPos().x + 0.2D, lol.getPos().y + 0.2D, lol.getPos().z + 0.2D, lol.getPos().x + 0.5D, lol.getPos().y + 0.5D, lol.getPos().z + 0.5D);

            scaffolds.get(i).forceStatusUpdate();
            if (scaffolds.get(i).getPercentIntactCached() + 0.05F < 0.4F * scaffolds.get(i).getPercentCompletedCached())
                scaffolds.remove(i);
        }
    }

    private void updateDensityData() {
        entityDensityData.clearMap();
        List<EntityIMLiving> mobs = nexus.getMobList();
        for (EntityIMLiving mob : mobs) {
            int coordHash = PathNode.makeHash(mob.posX, mob.posY, mob.posZ, PathAction.NONE);
            if (entityDensityData.containsItem(coordHash)) {
                Integer value = (Integer) entityDensityData.lookup(coordHash);
                if (value < 7) {
                    entityDensityData.addKey(coordHash, value + 1);
                }
            } else {
                entityDensityData.addKey(coordHash, 1);
            }
        }
    }
}