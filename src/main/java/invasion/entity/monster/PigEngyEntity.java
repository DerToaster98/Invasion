package invasion.entity.monster;

import invasion.IBlockAccessExtended;
import invasion.INotifyTask;
import invasion.Invasion;
import invasion.entity.*;
import invasion.entity.ai.*;
import invasion.entity.ai.navigator.*;
import invasion.init.ModItems;
import invasion.nexus.Nexus;
import invasion.util.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class PigEngyEntity extends InvadingEntity implements ICanDig, ICanBuild {

    private static final DataParameter<Boolean> IS_SWINGING = EntityDataManager.createKey(PigEngyEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    private final NavigatorEngy navigatorEngy;
	private int swingTimer;
    private int planks;
    private int askForScaffoldTimer;
    private float supportThisTick;

    private TerrainModifier terrainModifier;
    private TerrainDigger terrainDigger;
    private TerrainBuilder terrainBuilder = null;

    public PigEngyEntity(World world, Nexus nexus) {
        super(world, nexus);
        IPathSource pathSource = getPathSource();
        pathSource.setSearchDepth(1500);
        pathSource.setQuickFailDepth(1500);
        navigatorEngy = new NavigatorEngy(this, pathSource);
		PathNavigateAdapter oldNavAdapter = new PathNavigateAdapter(navigatorEngy);
        pathSource.setSearchDepth(1200);

        if (terrainBuilder == null) {
            terrainModifier = new TerrainModifier(this, 2.8F);
            terrainDigger = new TerrainDigger(this, terrainModifier, 1.0F);
            terrainBuilder = new TerrainBuilder(this, terrainModifier, 1.0F);
        }

        setBaseMoveSpeedStat(0.23F);
        attackStrength = 2;
        selfDamage = 0;
        maxSelfDamage = 0;
        planks = 15;
        //tier = 1;
        maxDestructiveness = 2;
        askForScaffoldTimer = 0;
        isImmuneToFire = true;

        setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));

        setMaxHealthAndHealth(Invasion.getMobHealth(this));
        setDestructiveness(2);
        setJumpHeight(1);
        setCanClimb(false);

        int r = rand.nextInt(3);
        if (r == 0) setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.LADDER));
        else if (r == 1) setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        else setHeldItem(Hand.MAIN_HAND, new ItemStack(ModItems.ENGY_HAMMER.get()));
    }

    public PigEngyEntity(World world) {
        this(world, null);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(IS_SWINGING, false);
    }

    @Override
    protected void initEntityAI() {
        tasksIM = new EntityAITasks(world.profiler);
        tasksIM.addTask(0, new EntityAISwimming(this));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityPlayer.class, 60));
        tasksIM.addTask(2, new EntityAIAttackNexus(this));
        tasksIM.addTask(3, new EntityAIGoToNexus(this));
        tasksIM.addTask(7, new EntityAIWanderIM(this));
        tasksIM.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 7.0F));
        tasksIM.addTask(9, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));
        tasksIM.addTask(9, new EntityAILookIdle(this));

        targetTasksIM = new EntityAITasks(world.profiler);
        if (isNexusBound()) {
            targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, 3.0F, true));
        } else {
            targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, getSenseRange(), false));
            targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, getAggroRange(), true));
        }
        targetTasksIM.addTask(3, new EntityAIHurtByTarget(this, false));
    }

    @Override
    public void updateAITasks() {
        super.updateAITasks();
        terrainModifier.onUpdate();
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
        terrainBuilder.setBuildRate(1.0F + supportThisTick * 0.33F);

        supportThisTick = 0.0F;

        askForScaffoldTimer--;
        if (targetNexus != null) {
            int weight = 1;
            if (targetNexus.getPos().getY() - getPosition().getY() > 1) {
                weight = Math.max(6000 / targetNexus.getPos().getY() - getPosition().getY(), 1);
            }
            if ((currentObjective == Objective.BREAK_NEXUS)
                    && (((getNavigatorNew().getLastPathDistanceToTarget() > 2.0F) &&
                    (askForScaffoldTimer <= 0)) ||
                    (rand.nextInt(weight) == 0))) {
                if (targetNexus.getAttackerAI().askGenerateScaffolds(this)) {
                    getNavigatorNew().clearPath();
                    askForScaffoldTimer = 60;
                } else {
                    askForScaffoldTimer = 140;
                }
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        updateAnimation();
    }

    @Override
    public void onPathSet() {
        terrainModifier.cancelTask();
    }

    //TODO Prevents spawning with egg due to conflict with EntityAISwimming
	/*@Override
	public PathNavigateAdapter getNavigator() {
		return oldNavAdapter;
	}*/

    @Override
    public INavigation getNavigatorNew() {
        return navigatorEngy;
    }

    @Override
    public IBlockAccess getTerrain() {
        return world;
    }

    @Override
    public boolean onPathBlocked(Path path, INotifyTask notifee) {
        if (!path.isFinished()) {
            PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());
            return terrainDigger.askClearPosition(new BlockPos(node.pos), notifee, 1.0F);
        }
        return false;
    }

    public ITerrainBuild getTerrainBuildEngy() {
        return terrainBuilder;
    }

    protected ITerrainDig getTerrainDig() {
        return terrainDigger;
    }

    @Override
    public void setTier(int tier) {
        super.setTier(tier);
        terrainModifier = new TerrainModifier(this, 2.8f);
        terrainDigger = new TerrainDigger(this, terrainModifier, tier <= 1 ? 1f : ((float) tier) * 0.75f);
        terrainBuilder = null;
        if (tier == 2)
            terrainBuilder = new TerrainBuilder(this, terrainModifier, 1f, TerrainBuilder.LADDER_COST, TerrainBuilder.COBBLE_COST, Blocks.COBBLESTONE);
        if (terrainBuilder == null) terrainBuilder = new TerrainBuilder(this, terrainModifier, 1f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_PIGMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    public float getBlockRemovalCost(BlockPos pos) {
        return getBlockStrength(pos) * 20.0F;
    }

    @Override
    public boolean canClearBlock(BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || (isBlockDestructible(world, pos, blockState));
    }

    public boolean avoidsBlock(int id) {
        return ((id == 51) || (id == 7) || (id == 64) || (id == 8) || (id == 9) || (id == 10) || (id == 11));
    }

    public void supportForTick(EntityIMLiving entity, float amount) {
        supportThisTick += amount;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap) {
        if ((node.pos.x == -21) && (node.pos.z == 180)) planks = 10;
        IBlockState blockState = terrainMap.getBlockState(new BlockPos(node.pos));
        float materialMultiplier = (blockState.getBlock() != Blocks.AIR)
                && (isBlockDestructible(terrainMap, new BlockPos(node.pos), blockState)) ? 3.2F : 1.0F;

        switch (node.action) {
            case BRIDGE:
                return prevNode.distanceTo(node) * 1.7F * materialMultiplier;
            case SCAFFOLD_UP:
                return prevNode.distanceTo(node) * 0.5F;
            case LADDER_UP_NX:
            case LADDER_UP_NZ:
            case LADDER_UP_PX:
            case LADDER_UP_PZ:
                return prevNode.distanceTo(node) * 1.3F * materialMultiplier;
            case LADDER_TOWER_UP_PX:
            case LADDER_TOWER_UP_NX:
            case LADDER_TOWER_UP_PZ:
            case LADDER_TOWER_UP_NZ:
                return prevNode.distanceTo(node) * 1.4F;
            default:
                break;
        }

        float multiplier = 1.0F;
        if ((terrainMap instanceof IBlockAccessExtended)) {
            int mobDensity = ((IBlockAccessExtended) terrainMap).getLayeredData(node.pos.x, node.pos.y, node.pos.z) & 0x7;
            multiplier += mobDensity;
        }
        if (blockState.getBlock() == Blocks.AIR || blockState.getBlock() == Blocks.SNOW)
            return prevNode.distanceTo(node) * multiplier;
        if (blockState.getBlock() == Blocks.LADDER) return prevNode.distanceTo(node) * 0.7F * multiplier;
        if ((!blockState.getBlock().isPassable(terrainMap, new BlockPos(node.pos))) && (blockState != BlocksAndItems.blockNexus)) {
            return prevNode.distanceTo(node) * 3.2F;
        }

        return super.getBlockPathCost(prevNode, node, terrainMap);
    }

    @Override
    public void getPathOptionsFromNode(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        super.getPathOptionsFromNode(terrainMap, currentNode, pathFinder);
        if (planks <= 0) return;

        for (int i = 0; i < 4; i++) {
            if (getCollide(terrainMap, currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])) > 0) {
                for (int yOffset = 0; yOffset > -4; yOffset--) {
                    Vec3d vec = currentNode.pos.addVector(Coords.offsetAdjX[i], yOffset - 1, Coords.offsetAdjZ[i]);
                    if (!terrainMap.isAirBlock(new BlockPos(vec))) break;
                    pathFinder.addNode(currentNode.pos.addVector(Coords.offsetAdjX[i], yOffset, Coords.offsetAdjZ[i]), PathAction.BRIDGE);
                }
            }
        }
    }

    @Override
    protected void calcPathOptionsVertical(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        if ((currentNode.pos.x == -11) && (currentNode.pos.z == 177)) planks = 10;
        super.calcPathOptionsVertical(terrainMap, currentNode, pathFinder);
        if (planks <= 0) return;

        if (getCollide(terrainMap, currentNode.pos.addVector(0d, 1d, 0d)) > 0) {
            if (terrainMap.isAirBlock(new BlockPos(currentNode.pos.addVector(0d, 1d, 0d)))) {
                if (currentNode.action == PathAction.NONE) {
                    addAnyLadderPoint(terrainMap, currentNode, pathFinder);
                } else if (!continueLadder(terrainMap, currentNode, pathFinder)) {
                    addAnyLadderPoint(terrainMap, currentNode, pathFinder);
                }

            }

            if ((currentNode.action == PathAction.NONE) || (currentNode.action == PathAction.BRIDGE)) {
                int maxHeight = 4;
                for (int i = getCollideSize().getY(); i < 4; i++) {
                    Block block = terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(0d, i, 0d))).getBlock();
                    if ((block != Blocks.AIR)
                            && (!block.isPassable(terrainMap,
                            new BlockPos(currentNode.pos.addVector(0d, i, 0d))))) {
                        maxHeight = i - getCollideSize().getY();
                        break;
                    }

                }

                for (int i = 0; i < 4; i++) {
                    IBlockState blockState = terrainMap.getBlockState(new BlockPos(
                            currentNode.pos.addVector(Coords.offsetAdjX[i], 1, Coords.offsetAdjZ[i])));
                    if (blockState.isNormalCube()) {
                        for (int height = 0; height < maxHeight; height++) {
                            blockState = terrainMap.getBlockState(new BlockPos(
                                    currentNode.pos.addVector(Coords.offsetAdjX[i], height, Coords.offsetAdjZ[i])));
                            if (blockState.getBlock() != Blocks.AIR) {
                                if (!blockState.isNormalCube()) break;
                                pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.ladderTowerIndexOrient[i]);
                                break;
                            }
                        }
                    }
                }
            }

        }

        if ((terrainMap instanceof IBlockAccessExtended)) {
            int data = ((IBlockAccessExtended) terrainMap).getLayeredData(
                    currentNode.pos.x, currentNode.pos.y + 1,
                    currentNode.pos.z);
            if (data == 16384) {
                pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.SCAFFOLD_UP);
            }
        }
    }

    protected void addAnyLadderPoint(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        for (int i = 0; i < 4; i++) {
            if (terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(Coords.offsetAdjX[i], 1, Coords.offsetAdjZ[i]))).isNormalCube())
                pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.ladderIndexOrient[i]);
        }
    }

    // NOOB HAUS: possible cases? LADDER_UP_PX, LADDER_UP_NX, LADDER_UP_PZ,
    // LADDER_UP_NZ, LADDER_TOWER_UP_PX,
    // LADDER_TOWER_UP_NX, LADDER_TOWER_UP_PZ, LADDER_TOWER_UP_NZ, SCAFFOLD_UP
    protected boolean continueLadder(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        switch (currentNode.action) {
            case LADDER_TOWER_UP_PX:
                if (terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(1d, 1d, 0d))).isNormalCube()) {
                    pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.LADDER_UP_PX);
                }
                return true;
            case LADDER_TOWER_UP_NX:
                if (terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(-1d, 1d, 0d))).isNormalCube()) {
                    pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.LADDER_UP_NX);
                }
                return true;
            case LADDER_TOWER_UP_PZ:
                if (terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(0d, 1d, 1d))).isNormalCube()) {
                    pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.LADDER_UP_PZ);
                }
                return true;
            case LADDER_TOWER_UP_NZ:
                if (terrainMap.getBlockState(new BlockPos(currentNode.pos.addVector(0d, 1d, -1d))).isNormalCube()) {
                    pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), PathAction.LADDER_UP_NZ);
                }
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    protected void dropFewItems(boolean flag, int bonus) {
        super.dropFewItems(flag, bonus);
        if (rand.nextInt(2) == 0) {
            entityDropItem(new ItemStack(Items.LEATHER), 0f);
        } else if (isBurning()) {
            entityDropItem(new ItemStack(Items.COOKED_PORKCHOP), 0f);
        } else {
            entityDropItem(new ItemStack(Items.PORKCHOP), 0f);
        }
    }

    protected void updateAnimation() {
        if ((!world.isRemote) && (terrainModifier.isBusy())) {
            setSwinging(true);
            PathAction currentAction = getNavigatorNew().getCurrentWorkingAction();
            setHeldItem(EnumHand.MAIN_HAND, new ItemStack(currentAction == PathAction.NONE ? Items.IRON_PICKAXE : BlocksAndItems.itemEngyHammer));
        }
        int swingSpeed = getSwingSpeed();
        if (isSwinging()) {
            swingTimer += 1;
            if (swingTimer >= swingSpeed) {
                swingTimer = 0;
                setSwinging(false);
            }
        } else {
            swingTimer = 0;
        }

        swingProgress = (swingTimer / swingSpeed);
    }

    protected boolean isSwinging() {
        return getDataManager().get(IS_SWINGING);
    }

    protected void setSwinging(boolean flag) {
        getDataManager().set(IS_SWINGING, flag);
    }

    protected int getSwingSpeed() {
        return 10;
    }

    @Override
    public boolean canPlaceLadderAt(BlockPos pos) {
        if (EntityIMLiving.unDestructableBlocks.contains(world.getBlockState(pos).getBlock())) {
            return ((world.getBlockState(pos.west()).isNormalCube())
                    || (world.getBlockState(pos.east()).isNormalCube())
                    || (world.getBlockState(pos.north()).isNormalCube())
                    || (world.getBlockState(pos.south()).isNormalCube()));
        }
        return false;
    }

    @Override
    public void onBlockRemoved(BlockPos pos, IBlockState state) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return "IMPigManEngineer-T" + getTier();
    }

}