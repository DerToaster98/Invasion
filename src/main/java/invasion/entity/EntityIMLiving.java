package invasion.entity;

import invasion.INotifyTask;
import invasion.IPathfindable;
import invasion.client.render.animation.util.IMMoveHelper;
import invasion.entity.ai.navigator.NavigatorIM;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathCreator;
import invasion.entity.ai.navigator.PathNavigateAdapter;
import invasion.nexus.Nexus;
import invasion.util.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;


public abstract class EntityIMLiving extends Entity implements IHasNexus, IPathfindable {

    private static final DataParameter<Boolean> IS_HOLDING_ONTO_LADDER = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.BOOLEAN); //20
    private static final DataParameter<Integer> MOVE_STATE = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.VARINT); //23
    private static final DataParameter<Integer> TIER = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.VARINT); //30
    private static final DataParameter<Integer> TEXTURE = EntityDataManager.createKey(EntityIMLiving.class, DataSerializers.VARINT); //31

    private PathCreator pathSource = new PathCreator(700, 50);
    private final NavigatorIM imNavigator = new NavigatorIM(this, this.pathSource);
    private final PathNavigateAdapter oldNavAdapter = new PathNavigateAdapter(this.imNavigator);
    private BlockPos collideSize;
    private BlockPos currentTargetPos = BlockPos.ORIGIN;
    private int rallyCooldown;
    private float turnRate = 30.0F;
    private float moveSpeedBase = 0.2f;
    private float moveSpeed = 0.2f;
    private MoveState moveState;
    private IMMoveHelper moveHelperIM = new IMMoveHelper(this);
    protected Nexus targetNexus;
    protected static List<Block> unDestructableBlocks = Arrays.asList(
            Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.END_PORTAL_FRAME,
            Blocks.LADDER, Blocks.CHEST);

    public EntityIMLiving(World worldIn) {
        this(worldIn, null);
    }

    public EntityIMLiving(World worldIn, Nexus nexus) {
        super(worldIn);
        this.targetNexus = nexus;
        this.collideSize = new BlockPos(this.width + 1.0F, this.height + 1.0F, this.width + 1.0F);
        this.setTier(1);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(IS_HOLDING_ONTO_LADDER, false);
        this.getDataManager().register(MOVE_STATE, (this.moveState = MoveState.STANDING).ordinal());
        this.getDataManager().register(TIER, 1);
        this.getDataManager().register(TEXTURE, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.moveState = MoveState.values()[this.getDataManager().get(MOVE_STATE)];
        switch (this.moveState) {
            case CLIMBING:
                //this.motionY = 0.1d;
                this.isJumping = true;
                break;
            case FLYING:
                this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
                break;
            case JUMPING:
                this.jump();
                //this.motionY = 0.5d;
                //this.isJumping = true;
                this.setMoveState(MoveState.RUNNING);
                break;
            case NONE:
                break;
            case RUNNING:
                //this.setVelocityToPosition(this.getMoveHelper().getX(), this.getMoveHelper().getY(), this.getMoveHelper().getZ(), this.getMoveHelper().getSpeed());
                //this.moveEntityWithHeading(this.getMoveHelper().getMoveStrafe(), this.getMoveHelper().getMoveForward());
                this.moveEntityWithHeading(this.moveStrafing, (float) this.getMoveHelper().getSpeed());
                //this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
                //this.moveEntityWithHeading(0f, (float)this.getMoveHelper().getSpeed());
                break;
            case STANDING:
                this.motionX = 0d;
                this.motionZ = 0d;
                break;
            default:
                break;
        }
    }

    public void onFollowingEntity(Entity entity) {
    }

    public void onPathSet() {
    }

    public boolean onPathBlocked(Path path, INotifyTask asker) {
        return false;
    }

    public void rally(Entity leader) {
        this.rallyCooldown = 300;
    }

    public boolean readyToRally() {
        return this.rallyCooldown == 0;
    }

    public void setTier(int tier) {
        this.getDataManager().set(TIER, tier);
        this.initEntityAI();
        this.setTexture(tier);
    }

    public int getTier() {
        return this.getDataManager().get(TIER);
    }

    public void setTexture(int textureId) {
        this.getDataManager().set(TEXTURE, textureId);
    }

    public int getTextureId() {
        return this.getDataManager().get(TEXTURE);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("tier", this.getDataManager().get(TIER));
        tag.setInteger("textureId", this.getDataManager().get(TEXTURE));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        if (!this.world.isRemote) {
            this.setTexture(tag.getInteger("textureId"));
            this.setTier(tag.getInteger("tier"));
        }
    }

    //TODO Removed Override annotation
    protected void updateAITick() {
        if (this.rallyCooldown > 0) {
            this.rallyCooldown--;
        }
    }

    public void setCurrentTargetPos(BlockPos pos) {
        this.currentTargetPos = pos;
    }

    public boolean canStandAt(IBlockAccess terrainMap, BlockPos pos) {
        boolean isSolidBlock = false;
        for (int xOffset = pos.getX(); xOffset < pos.getX() + this.collideSize.getX(); xOffset++) {
            for (int zOffset = pos.getZ(); zOffset < pos.getZ() + this.collideSize.getZ(); zOffset++) {
                BlockPos pos0 = new BlockPos(xOffset, pos.getY(), zOffset);
                Block block = terrainMap.getBlockState(pos0).getBlock();
                if (block != Blocks.AIR) {
                    if (!block.isPassable(terrainMap, pos0)) {
                        isSolidBlock = true;
                    } else if (this.avoidsBlock(block)) {
                        return false;
                    }
                }
            }
        }
        return isSolidBlock;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.collideSize = new BlockPos(width + 1f, height + 1f, width + 1f);
    }

    public void setIsHoldingIntoLadder(boolean flag) {
        if (!this.world.isRemote) this.getDataManager().set(IS_HOLDING_ONTO_LADDER, flag);
    }

    public boolean canStandAtAndIsValid(IBlockAccess terrainMap, BlockPos pos) {
        boolean flag0 = this.getCollide(terrainMap, pos) > 0;
        boolean flag1 = this.canStandAt(terrainMap, pos);
        return (flag0 && flag1);
    }

    protected boolean isAdjacentSolidBlock(IBlockAccess terrainMap, BlockPos pos) {
        if ((this.collideSize.getX() == 1) && (this.collideSize.getZ() == 1)) {
            for (int i = 0; i < 4; i++) {
                IBlockState blockState = terrainMap.getBlockState(pos.add(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i]));
                if ((blockState.getBlock() != Blocks.AIR) && (blockState.getMaterial().isSolid()))
                    return true;
            }
        } else if ((this.collideSize.getX() == 2) && (this.collideSize.getZ() == 2)) {
            for (int i = 0; i < 8; i++) {
                IBlockState blockState = terrainMap.getBlockState(pos.add(Coords.offsetAdj2X[i], 0, Coords.offsetAdj2Z[i]));
                if ((blockState.getBlock() != Blocks.AIR) && (blockState.getMaterial().isSolid()))
                    return true;
            }
        }
        return false;
    }

    protected int getCollide(IBlockAccess terrainMap, Vec3d vec) {
        return this.getCollide(terrainMap, new BlockPos(vec));
    }

    protected int getCollide(IBlockAccess terrainMap, BlockPos pos) {
        boolean destructibleFlag = false;
        boolean liquidFlag = false;
        for (int xOffset = pos.getX(); xOffset < pos.getX() + this.collideSize.getX(); xOffset++) {
            for (int yOffset = pos.getY(); yOffset < pos.getY() + this.collideSize.getY(); yOffset++) {
                for (int zOffset = pos.getZ(); zOffset < pos.getZ() + this.collideSize.getZ(); zOffset++) {
                    IBlockState blockState = terrainMap.getBlockState(new BlockPos(xOffset, yOffset, zOffset));
                    IBlockState blockState0 = terrainMap.getBlockState(new BlockPos(xOffset, yOffset - 1, zOffset));
                    if (blockState.getBlock() != Blocks.AIR) {
                        if ((blockState.getBlock() == Blocks.WATER) || (blockState.getBlock() == Blocks.LAVA)) {
                            liquidFlag = true;
                        } else if (!blockState.getBlock().isPassable(terrainMap, new BlockPos(xOffset, yOffset, zOffset))) {
                            if (this.isBlockDestructible(terrainMap, pos, blockState))
                                destructibleFlag = true;
                            else
                                return 0;
                        } else if (blockState0.getBlock() instanceof BlockFence) {
                            if (this.isBlockDestructible(terrainMap, pos, blockState0)) return 3;
                            return 0;
                        }

                        if (this.avoidsBlock(blockState.getBlock())) return -2;
                    }
                }
            }
        }
        if (destructibleFlag) return 2;
        if (liquidFlag) return -1;
        return 1;
    }

    public boolean avoidsBlock(Block block) {
        return ((block == Blocks.FIRE) || (block == Blocks.BEDROCK) || (block == Blocks.LAVA) || (block == Blocks.FLOWING_LAVA) || (block == Blocks.CACTUS));
    }

    public boolean ignoresBlock(Block block) {
        return block instanceof BlockTallGrass || block instanceof BlockDeadBush || block instanceof BlockFlower || block instanceof BlockBush
		/*return ((block == Blocks.TALLGRASS) || (block == Blocks.DEADBUSH) || (block == Blocks.RED_FLOWER)
				|| (block == Blocks.YELLOW_FLOWER) || (block == Blocks.BROWN_MUSHROOM)
				|| (block == Blocks.RED_MUSHROOM)*/ || (block == Blocks.WOODEN_PRESSURE_PLATE)
                || (block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) || (block == Blocks.STONE_PRESSURE_PLATE);
    }

    public boolean isBlockDestructible(IBlockAccess terrainMap, BlockPos pos, IBlockState state) {
        // check if mobgriefing is enabled
        boolean mobgriefing = this.world.getGameRules().getBoolean("mobGriefing");
        if (!mobgriefing) return false;
        if (unDestructableBlocks.contains(state.getBlock()) || this.blockHasLadder(terrainMap, pos)) return false;
        return state.getBlock() instanceof BlockDoor || state.getBlock() == Blocks.TRAPDOOR || state.getMaterial().isSolid();
    }

    protected boolean blockHasLadder(IBlockAccess terrainMap, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            if (terrainMap.getBlockState(pos.add(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])).getBlock() == Blocks.LADDER)
                return true;
        }
        return false;
    }

    protected void setBaseMoveSpeedStat(float speed) {
        this.moveSpeedBase = speed;
        this.moveSpeed = speed;
    }

    public void setMoveSpeedStat(float speed) {
        this.moveSpeed = speed;
        this.getNavigatorNew().setSpeed(speed);
        //this.getMoveHelper().setMoveSpeed(speed);
    }

    public void resetMoveSpeed() {
        this.setMoveSpeedStat(this.moveSpeedBase);
        this.getNavigatorNew().setSpeed(this.moveSpeedBase);
    }

    public void setMoveState(MoveState moveState) {
        this.moveState = moveState;
        if (!this.world.isRemote) this.getDataManager().set(MOVE_STATE, Integer.valueOf(moveState.ordinal()));
    }

    public void setTurnRate(float rate) {
        this.turnRate = rate;
    }

    public boolean isHoldingOntoLadder() {
        return this.getDataManager().get(IS_HOLDING_ONTO_LADDER);
    }

    public INavigation getNavigatorNew() {
        return this.imNavigator;
    }

    public IPathSource getPathSource() {
        return this.pathSource;
    }

    public BlockPos getCollideSize() {
        return this.collideSize;
    }

    public BlockPos[] getBlockRemovalOrder(BlockPos pos) {
        if (MathHelper.floor(this.posY) >= pos.getY()) {
            BlockPos[] blocks = new BlockPos[2];
            blocks[1] = pos.up();
            blocks[0] = new BlockPos(pos);
            return blocks;
        }

        BlockPos[] blocks = new BlockPos[3];
        blocks[2] = new BlockPos(pos);
        blocks[1] = new BlockPos(MathHelper.floor(this.posX),
                MathHelper.floor(this.posY) + this.collideSize.getY(),
                MathHelper.floor(this.posZ));
        blocks[0] = pos.up();
        return blocks;
    }

    public float getMoveSpeedStat() {
        return this.moveSpeed;
    }

    public float getBaseMoveSpeedStat() {
        return this.moveSpeedBase;
    }

    @Override
    public IMMoveHelper getMoveHelper() {
        return this.moveHelperIM;
    }

    public MoveState getMoveState() {
        return this.moveState;
    }

    public BlockPos getCurrentTargetPos() {
        return this.currentTargetPos;
    }

    public float getTurnRate() {
        return this.turnRate;
    }

    @Override
    public Nexus getNexus() {
        return this.targetNexus;
    }

}