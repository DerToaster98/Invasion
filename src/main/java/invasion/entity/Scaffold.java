package invasion.entity;

import invasion.IPathfindable;
import invasion.entity.ai.navigator.PathAction;
import invasion.entity.ai.navigator.PathNode;
import invasion.entity.ai.navigator.PathfinderIM;
import invasion.nexus.Nexus;
import invasion.util.Distance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;


public class Scaffold extends Structure implements IPathfindable, INBTSerializable<CompoundNBT> {

    private static final int MIN_SCAFFOLD_HEIGHT = 4;
    //private int xCoord;
    //private int yCoord;
    //private int zCoord;
    private BlockPos pos;
    private int targetHeight;
    private Direction orientation;
    private int[] platforms;
    private IPathfindable pathfindBase;
    private final Nexus nexus;
    private float latestPercentCompleted;
    private float latestPercentIntact;
    private float initialCompletion = 0.01f;

    public Scaffold(Nexus nexus) {
        this.nexus = nexus;
        calcPlatforms();
    }

    public Scaffold(double x, double y, double z, int height, Nexus nexus) {
        this(new BlockPos(x, y, z), height, nexus);
    }

    public Scaffold(BlockPos pos, int height, Nexus nexus) {
        this.pos = pos;
        targetHeight = height;
        this.nexus = nexus;
        calcPlatforms();
    }

    public void setPosition(BlockPos pos) {
        this.pos = pos;
    }

    public void setPosition(double x, double y, double z) {
        pos = new BlockPos(x, y, z);
    }

    public void setInitialIntegrity() {
        initialCompletion = evaluateIntegrity();
        if (initialCompletion == 0.0F) initialCompletion = 0.01F;
    }

    public Direction getOrientation() {
        return orientation;
    }

    public void setOrientation(Direction d) {
        orientation = d;
    }

    public void setHeight(int height) {
        targetHeight = height;
        calcPlatforms();
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void forceStatusUpdate() {
        latestPercentIntact = ((evaluateIntegrity() - initialCompletion) / (1.0F - initialCompletion));
        if (latestPercentIntact > latestPercentCompleted)
            latestPercentCompleted = latestPercentIntact;
    }

    public float getPercentIntactCached() {
        return latestPercentIntact;
    }

    public float getPercentCompletedCached() {
        return latestPercentCompleted;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Nexus getNexus() {
        return nexus;
    }

    public void setPathfindBase(IPathfindable base) {
        pathfindBase = base;
    }

    public boolean isLayerPlatform(int height) {
        if (height == targetHeight - 1) {
            return true;
        }
        if (platforms != null) {
            for (int i : platforms) {
                if (i == height) return true;
            }
        }
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putDouble("x", pos.getX());
        nbt.putDouble("y", pos.getY());
        nbt.putDouble("z", pos.getZ());
        nbt.putInt("targetHeight", targetHeight);
        nbt.putInt("orientation", orientation.getIndex());
        nbt.putFloat("initialCompletion", initialCompletion);
        nbt.putFloat("latestPercentCompleted", latestPercentCompleted);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
        targetHeight = nbt.getInt("targetHeight");
        orientation = Direction.byIndex(nbt.getInt("orientation"));
        initialCompletion = nbt.getFloat("initialCompletion");
        latestPercentCompleted = nbt.getFloat("latestPercentCompleted");
        calcPlatforms();
    }

    private void calcPlatforms() {
        int spanningPlatforms = targetHeight < 16 ? targetHeight / 4 - 1 : targetHeight / 5 - 1;
        if (spanningPlatforms > 0) {
            int avgSpace = targetHeight / (spanningPlatforms + 1);
            int remainder = targetHeight % (spanningPlatforms + 1) - 1;
            platforms = new int[spanningPlatforms];
            for (int i = 0; i < spanningPlatforms; i++) {
                platforms[i] = (avgSpace * (i + 1) - 1);
            }

            int i = spanningPlatforms - 1;
            while (remainder > 0) {
                platforms[i] += 1;
                if (i-- < 0) {
                    i = spanningPlatforms - 1;
                    remainder--;
                }
                remainder--;
            }
        } else {
            platforms = new int[0];
        }
    }

    private float evaluateIntegrity() {
        if (nexus != null) {
            int existingMainSectionBlocks = 0;
            int existingMainLadderBlocks = 0;
            int existingPlatformBlocks = 0;
            World world = nexus.getWorld();
            BlockPos.Mutable currentPos = new BlockPos.Mutable(pos);
            for (int i = 0; i < targetHeight; i++, currentPos.move(0, 1, 0)) {
                //set bool true, donno why

                if (world.getBlockState(currentPos.offset(orientation)).isSolid()) {
                    existingMainSectionBlocks++;
                }
                if (world.getBlockState(currentPos).getBlock() == Blocks.LADDER) {
                    existingMainLadderBlocks++;
                }

                if (isLayerPlatform(i)) {

                    //TODO

                }
                    
                /*IBlockState blockState0 = world.getBlockState(new BlockPos(vec.addVector(Coords.offsetAdjX[orientation], i, Coords.offsetAdjZ[orientation])));
                if (blockState0.isOpaqueCube()/*isSolidFullCube()) {
                    existingMainSectionBlocks++;
                }
                
                 
                
                if (world.getBlockState(new BlockPos(vec.addVector(0d, i, 0d))).getBlock() == Blocks.LADDER) {
                    existingMainLadderBlocks++;
                }
                
                if (isLayerPlatform(i)) {
                    for (int j = 0; j < 8; j++) {
                        BlockPos pos = new BlockPos(vec.addVector(Coords.offsetRing1X[j], i, Coords.offsetRing1Z[j]));
                        IBlockState blockState1 = world.getBlockState(pos);
                        if (blockState1.isSideSolid(world, pos, EnumFacing.UP)/*.isFully) {
                            existingPlatformBlocks++;
                        }
                    }
                }*/
            }

            float mainSectionPercent = targetHeight > 0 ? existingMainSectionBlocks / targetHeight : 0.0F;
            float ladderPercent = targetHeight > 0 ? existingMainLadderBlocks / targetHeight : 0.0F;

            return 0.7F * (0.7F * mainSectionPercent + 0.3F * ladderPercent) + 0.3F * (existingPlatformBlocks / ((platforms.length + 1) * 8));
        }
        return 0.0F;
    }

    @Override
    public float getBlockPathCost(PathNode prevNode, PathNode node, World world) {
        float materialMultiplier = world.getBlockState(node.pos).isSolid() ? 2.2F : 1.0F;
        if (node.action == PathAction.SCAFFOLD_UP) {
            if (prevNode.action != PathAction.SCAFFOLD_UP) {
                materialMultiplier *= 3.4F;
            }
            return prevNode.distanceTo(node) * 0.85F * materialMultiplier;
        }
        if (node.action == PathAction.BRIDGE) {
            if (prevNode.action == PathAction.SCAFFOLD_UP) {
                materialMultiplier = 0.0F;
            }
            return prevNode.distanceTo(node) * 1.1F * materialMultiplier;
        }
        if ((node.action == PathAction.LADDER_UP_NX) || (node.action == PathAction.LADDER_UP_NZ) || (node.action == PathAction.LADDER_UP_PX) || (node.action == PathAction.LADDER_UP_PZ)) {
            return prevNode.distanceTo(node) * 1.5F * materialMultiplier;
        }
        if (pathfindBase != null) {
            return pathfindBase.getBlockPathCost(prevNode, node, world);
        }
        return prevNode.distanceTo(node);
    }

    @Override
    public void getPathOptionsFromNode(World world, PathNode currentNode, PathfinderIM pathFinder) {
        if (pathfindBase != null) {
            pathfindBase.getPathOptionsFromNode(world, currentNode, pathFinder);
        }
        BlockState blockState = world.getBlockState(currentNode.pos.up());
        if ((currentNode.getPrevious() != null) && (currentNode.getPrevious().action == PathAction.SCAFFOLD_UP) && (!avoidsBlock(blockState.getBlock()))) {
            pathFinder.addNode(currentNode.pos.up(), PathAction.SCAFFOLD_UP);
            return;
        }
        int minDistance;
        if (nexus != null) {
            List<Scaffold> scaffolds = nexus.getAttackerAI().getScaffolds();
            minDistance = nexus.getAttackerAI().getMinDistanceBetweenScaffolds();
            for (int sl = scaffolds.size() - 1; sl >= 0; sl--) {
                Scaffold scaffold = scaffolds.get(sl);
                if (Distance.distanceBetween(scaffold.getPos(), currentNode.pos) < minDistance) {
                    return;
                }
            }
        }

        BlockState blockStateBelow = world.getBlockState(currentNode.pos.down(2));
        if ((blockState.isAir()) && (blockStateBelow.isSolid())) {
            boolean flag = false;
            for (int i = 1; i < 4; i++) {
                if (!world.getBlockState(currentNode.pos.up(i)).isAir()) {
                    flag = true;
                    break;
                }
            }

            if (!flag) pathFinder.addNode(currentNode.pos.up(), PathAction.SCAFFOLD_UP);
        }
    }

    private boolean avoidsBlock(Block block) {
        return (block == Blocks.FIRE) || (block == Blocks.BEDROCK) || (block == Blocks.WATER) || (block == Blocks.FLOWING_WATER) || (block == Blocks.LAVA) || (block == Blocks.FLOWING_LAVA);
    }


}
