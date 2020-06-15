package invasion.entity.monster;

import invasion.INotifyTask;
import invasion.entity.*;
import invasion.entity.ai.navigator.NavigatorBurrower;
import invasion.entity.ai.navigator.PathNavigateAdapter;
import invasion.nexus.Nexus;
import invasion.util.PosRotate3D;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


//NOOB HAUS: This one is done I think...
public class EntityIMBurrower extends InvadingEntity implements ICanDig {

    public static final int NUMBER_OF_SEGMENTS = 16;
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24
    private final NavigatorBurrower bo;
    private final PathNavigateAdapter oldNavAdapter;
    protected float prevRotX;
    protected float prevRotY;
    protected float prevRotZ;
    private final TerrainModifier terrainModifier;
    private final TerrainDigger terrainDigger;
    private EntityAITasks goals;
    private final PosRotate3D[] segments3D;
    private final PosRotate3D[] segments3DLastTick;
    private float rotX;
    private float rotY;
    private float rotZ;

    public EntityIMBurrower(World world) {
        this(world, null);
    }

    public EntityIMBurrower(World world, Nexus nexus) {
        super(world, nexus);

        IPathSource pathSource = this.getPathSource();
        pathSource.setSearchDepth(800);
        pathSource.setQuickFailDepth(400);
        this.bo = new NavigatorBurrower(this, pathSource, 16, -4);
        this.oldNavAdapter = new PathNavigateAdapter(this.bo);

        this.terrainModifier = new TerrainModifier(this, 2.0F);
        this.terrainDigger = new TerrainDigger(this, this.terrainModifier, 1.0F);

        this.setName("Burrower");
        this.setGender(0);
        this.setSize(0.5F, 0.5F);
        this.setJumpHeight(0);
        this.setCanClimb(true);
        this.setDestructiveness(2);
        this.maxDestructiveness = 2;
        this.blockRemoveSpeed = 0.5F;

        this.segments3D = new PosRotate3D[16];
        this.segments3DLastTick = new PosRotate3D[16];

        PosRotate3D zero = new PosRotate3D();
        for (int i = 0; i < 16; i++) {
            this.segments3D[i] = zero;
            this.segments3DLastTick[i] = zero;
        }
    }

    @Override
    public String toString() {
        return "EntityIMBurrower#u-u-u";
    }

    @Override
    public IBlockAccess getTerrain() {
        return this.world;
    }

    public float getBlockPathCost(PathPoint prevNode, PathPoint node, IBlockAccess worldMap) {
        Block block = worldMap.getBlockState(new BlockPos(node.x, node.y, node.z)).getBlock();

        float penalty = 0.0F;
        int enclosedLevelSide = 0;
        if (!this.world.getBlockState(new BlockPos(node.x, node.y - 1, node.z)).isNormalCube()) penalty += 0.3F;
        if (!this.world.getBlockState(new BlockPos(node.x, node.y + 1, node.z)).isNormalCube()) penalty += 2.0F;
        if (!this.world.getBlockState(new BlockPos(node.x + 1, node.y, node.z)).isNormalCube()) enclosedLevelSide++;
        if (!this.world.getBlockState(new BlockPos(node.x - 1, node.y, node.z)).isNormalCube()) enclosedLevelSide++;
        if (!this.world.getBlockState(new BlockPos(node.x, node.y, node.z + 1)).isNormalCube()) enclosedLevelSide++;
        if (!this.world.getBlockState(new BlockPos(node.x, node.y, node.z - 1)).isNormalCube()) enclosedLevelSide++;

        if (enclosedLevelSide > 2) enclosedLevelSide = 2;
        penalty += enclosedLevelSide * 0.5F;

        if (block == Blocks.AIR) return prevNode.distanceTo(node) * 1.0F * penalty;
        return prevNode.distanceTo(node) * 1.0F * block.getExplosionResistance(null) * penalty;
    }

    @Override
    public float getBlockRemovalCost(BlockPos pos) {
        return this.getBlockStrength(pos) * 20.0F;
    }

    @Override
    public boolean canClearBlock(BlockPos pos) {
        IBlockState blockState = this.world.getBlockState(pos);
        return (blockState.getBlock() == Blocks.AIR) || (this.isBlockDestructible(this.world, pos, blockState));
    }

    @Override
    public String getSpecies() {
        return "";
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public PathNavigateAdapter getNavigator() {
        return this.oldNavAdapter;
    }

    @Override
    public INavigation getNavigatorNew() {
        return this.bo;
    }

    protected boolean onPathBlocked(BlockPos pos, INotifyTask notifee) {
        return this.terrainDigger.askClearPosition(pos, notifee, 1.0F);
    }

    public float getRotX() {
        return this.rotX;
    }

    public float getRotY() {
        return this.rotY;
    }

    public float getRotZ() {
        return this.rotZ;
    }

    public float getPrevRotX() {
        return this.prevRotX;
    }

    public float getPrevRotY() {
        return this.prevRotY;
    }

    public float getPrevRotZ() {
        return this.prevRotZ;
    }

    public PosRotate3D[] getSegments3D() {
        return this.segments3D;
    }

    public PosRotate3D[] getSegments3DLastTick() {
        return this.segments3DLastTick;
    }

    public void setSegment(int index, PosRotate3D pos) {
        if (index < this.segments3D.length) {
            this.segments3DLastTick[index] = this.segments3D[index];
            this.segments3D[index] = pos;
        }
    }

    public void setHeadRotation(PosRotate3D pos) {
        this.prevRotX = this.rotX;
        this.prevRotY = this.rotY;
        this.prevRotZ = this.rotZ;
        this.rotX = pos.getRotX();
        this.rotY = pos.getRotY();
        this.rotZ = pos.getRotZ();
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (this.isInWater()) {
            double y = this.posY;
            this.moveFlying(x, z, 0.02F);
            this.setVelocity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8D;
            this.motionY *= 0.8D;
            this.motionZ *= 0.8D;
            this.motionY -= 0.02D;
            if ((this.collidedHorizontally) && (this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6D - this.posY + y, this.motionZ)))
                this.motionY = 0.3D;
        } else if (this.isInLava()) {
            double y = this.posY;
            this.moveFlying(x, z, 0.02F);
            this.setVelocity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;
            if ((this.collidedHorizontally) && (this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6D - this.posY + y, this.motionZ)))
                this.motionY = 0.3D;
        } else {
            float groundFriction = 1.0F;
            if (this.onGround) {
                groundFriction = 0.546F;
                Block block = this.world
                        .getBlockState(
                                new BlockPos(
                                        MathHelper.floor(this.posX),
                                        MathHelper.floor(this
                                                .getEntityBoundingBox().minY) - 1,
                                        MathHelper.floor(this.posZ)))
                        .getBlock();
                if (block != Blocks.AIR) {
                    groundFriction = block.slipperiness * 0.91F;
                }
            }
            if (this.isOnLadder()) {
                float maxLadderXZSpeed = 0.15F;
                if (this.motionX < -maxLadderXZSpeed)
                    this.motionX = (-maxLadderXZSpeed);
                if (this.motionX > maxLadderXZSpeed)
                    this.motionX = maxLadderXZSpeed;
                if (this.motionZ < -maxLadderXZSpeed)
                    this.motionZ = (-maxLadderXZSpeed);
                if (this.motionZ > maxLadderXZSpeed) {
                    this.motionZ = maxLadderXZSpeed;
                }
                this.fallDistance = 0.0F;
                if (this.motionY < -0.15D) {
                    this.motionY = -0.15D;
                }
                if ((this.isSneaking()) && (this.motionY < 0.0D)) {
                    this.motionY = 0.0D;
                }
            }
            this.setVelocity(this.motionX, this.motionY, this.motionZ);
            if ((this.collidedHorizontally) && (this.isOnLadder())) {
                this.motionY = 0.2D;
            }
            float airResistance = 0.98F;
            float gravityAcel = 0.0F;
            this.motionY -= gravityAcel;
            this.motionY *= airResistance;
            this.motionX *= airResistance;
            this.motionZ *= airResistance;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double dX = this.posX - this.prevPosX;
        double dZ = this.posZ - this.prevPosZ;
        float f4 = MathHelper.sqrt(dX * dX + dZ * dZ) * 4.0F;

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.terrainModifier.onUpdate();
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
    }

    @Override
    public void onBlockRemoved(BlockPos pos, IBlockState state) {
        // TODO Auto-generated method stub

    }
}