package invasion.entity.monster;


import invasion.entity.IHasNexus;
import invasion.entity.Objective;
import invasion.nexus.Nexus;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class InvadingEntity extends MonsterEntity implements /*SparrowAPI,*/ IEntityAdditionalSpawnData, IHasNexus {

    //TODO reasonable defaults
    protected static final IAttribute JUMP_HEIGHT = new RangedAttribute(null, "invasion.jump_height", 1.0D, 0.0D, 3.0D).setDescription("Jump Height");
    protected static final IAttribute AGGRO_RANGE = new RangedAttribute(null, "invasion.aggro_range", 12.0D, 0.0D, 20.0D).setDescription("Aggression Range");
    protected static final IAttribute SENSE_RANGE = new RangedAttribute(null, "invasion.sense_range", 6.0D, 0.0D, 20.0D).setDescription("Sensory Range");
    private static final DataParameter<Boolean> IS_ADJACENT_CLIMB_BLOCK = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.BOOLEAN); //21
    private static final DataParameter<Boolean> IS_JUMPING = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.BOOLEAN); //22
    protected static Collection<Block> unbreakableBlocks = Arrays.asList(
            Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.END_PORTAL_FRAME,
            Blocks.LADDER, Blocks.CHEST);
    private final boolean canDig = true;
    @Nullable
    protected Nexus nexus; /// The nexus the entity is bound to. If null, the entity is independent
    protected Objective currentObjective = Objective.NONE;
    protected Objective prevObjective = Objective.NONE;
    protected int flammability = 2;      //TODO how flammable the entity is
    protected float attackRange = 0f;
    protected int selfDamage = 2;
    protected int maxSelfDamage = 6;
    protected int maxDestructiveness = 0;
    protected float blockRemoveSpeed = 1f;
    protected boolean floatsInWater = true;
    protected int destructiveness = 0;   //TODO maybe safe delete this
    protected boolean burnsInDay;
    private byte tier; /// The tier of the entity. if not needed, it should be left alone
    private boolean canClimb = false;
    private boolean nexusBound;
    private boolean alwaysIndependent = false;
    private int stunTimer;

    public InvadingEntity(EntityType<? extends InvadingEntity> type, World world, @Nullable Nexus nexus) {
        this(type, world, nexus, (byte) 0);
    }

    public InvadingEntity(EntityType<? extends InvadingEntity> type, World world, @Nullable Nexus nexus, byte tier) {
        super(type, world);

        this.nexus = nexus;
        setTier(tier);
        experienceValue = 5;
        nexusBound = nexus != null;
        burnsInDay = nexus == null; //RM && Config.NIGHTSPAWNS_MOB_BURN_DURING_DAY;
    }

    @SuppressWarnings("deprecation")
    public static float getBlockStrength(BlockPos pos, Block block, World world) {

        int bonus = 0;
        if (world.getBlockState(pos.down()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.up()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.west()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.east()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.north()).getBlock() == block) bonus++;
        if (world.getBlockState(pos.south()).getBlock() == block) bonus++;

        return block.getExplosionResistance() * (1.0F + bonus * 0.1F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(IS_ADJACENT_CLIMB_BLOCK, false);
        getDataManager().register(IS_JUMPING, false);
        //getDataManager().register(TIER, (byte) 1);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttributes().registerAttribute(JUMP_HEIGHT);
        getAttributes().registerAttribute(AGGRO_RANGE);
        getAttributes().registerAttribute(SENSE_RANGE);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeByte(getTier());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        setTier(additionalData.readByte());
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putBoolean("alwaysIndependent", alwaysIndependent);
        nbt.putShort("tier", tier);
        if (nexus != null) {
            CompoundNBT nexusTag = new CompoundNBT();
            nexusTag.putInt("x", nexus.getPos().getX());
            nexusTag.putInt("y", nexus.getPos().getY());
            nexusTag.putInt("z", nexus.getPos().getZ());
            nbt.put("nexus", nexusTag);
        }
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("tier")) {
            tier = nbt.getByte("tier");
        }
        if (nbt.contains("alwaysIndependent")) {
            alwaysIndependent = nbt.getBoolean("alwaysIndependent");
        }

        if (nbt.contains("nexus")) {
            //CompoundNBT nexusTag = nbt.getCompound("nexus");
            nexus = Nexus.get(world);// ((NexusTileEntity) world.getTileEntity(new BlockPos(nexusTag.getInt("x"),nexusTag.getInt("y"),nexusTag.getInt("z"))))
        }
    }

    @Override
    public void livingTick() {
        if (!nexusBound) {
            float brightness = getBrightness();
            if ((burnsInDay) && (world.isDaytime()) && (!world.isRemote)) {
                if ((brightness > 0.5F) && (world.canBlockSeeSky(getPosition()))
                        && (rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F)) {
                    sunlightDamageTick();
                }
            }
        }
        super.livingTick();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float damage) {
        if (super.attackEntityFrom(damagesource, damage)) {
            Entity entity = damagesource.getTrueSource();
            //if ((riddenByEntity == entity) || (ridingEntity == entity)) {
            if (getPassengers().contains(entity) || getRidingEntity() == entity) {
                return true;
            }
            if (entity != this) entity = entity;
            return true;
        }
        return false;
    }

    public void stunEntity(int ticks) {
        if (stunTimer < ticks) stunTimer = ticks;
        setMotion(0.0, getMotion().y, 0.0);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        //TODO
        return super.createNavigator(worldIn);
    }

    public double findDistanceToNexus() {
        if (nexus == null) return Double.MAX_VALUE;
        double x = nexus.getPos().getX() + 0.5D - getPosX();
        double y = nexus.getPos().getY() - getPosY() + getHeight() * 0.5D;
        double z = nexus.getPos().getZ() + 0.5D - getPosZ();
        return Math.sqrt(x * x + y * y + z * z);
    }

    public float getAttackRange() {
        return attackRange;
    }

    public float getBlockStrength(BlockPos pos) {
        return getBlockStrength(pos, world.getBlockState(pos).getBlock());
    }

    public float getBlockStrength(BlockPos pos, Block block) {
        return getBlockStrength(pos, block, world);
    }

    public boolean getCanClimb() {
        return canClimb;
    }

    protected void setCanClimb(boolean flag) {
        canClimb = flag;
    }

    public boolean getCanDigDown() {
        return canDig;
    }

    public int getDestructiveness() {
        return destructiveness;
    }

    protected void setDestructiveness(int x) {
        destructiveness = x;
    }

    public Objective getAIGoal() {
        return currentObjective;
    }

    protected void setAIGoal(Objective objective) {
        currentObjective = objective;
    }

    public Objective getPrevAIGoal() {
        return prevObjective;
    }

    protected void setPrevAIGoal(Objective objective) {
        prevObjective = objective;
    }

    public boolean isAdjacentClimbBlock() {
        return getDataManager().get(IS_ADJACENT_CLIMB_BLOCK);
    }

    public void setAdjacentClimbBlock(boolean flag) {
        if (!world.isRemote) getDataManager().set(IS_ADJACENT_CLIMB_BLOCK, flag);
    }

    public boolean canSwimHorizontal() {
        return true;
    }

    public boolean canSwimVertical() {
        return true;
    }

    @Override
    public void acquiredByNexus(Nexus nexus) {
        //TODO
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (nexus != null) {
            nexus.registerMobDied();
        }

        super.onDeath(cause);
    }

    @Override
    public void setJumping(boolean flag) {
        super.setJumping(flag);
        if (!world.isRemote) getDataManager().set(IS_JUMPING, flag);
    }

    @Override
    public boolean isNoDespawnRequired() {
        return nexusBound;
    }

    protected void sunlightDamageTick() {
        if (isImmuneToFire()) {
            damageEntity(DamageSource.GENERIC, 3.0F);
        } else {
            setFire(8);
        }
    }

    @Override
    protected void dealFireDamage(int i) {
        super.dealFireDamage(i * flammability);
    }
    /*

    protected float calcBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap) {
        float multiplier = 1.0F;
        if ((terrainMap instanceof IBlockAccessExtended)) {
            int mobDensity = ((IBlockAccessExtended) terrainMap).getLayeredData(node.pos) & 0x7;
            multiplier += mobDensity * 3;
        }

        if ((node.pos.y > prevNode.pos.y) && (getCollide(terrainMap, node.pos) == 2)) {
            multiplier += 2.0F;
        }

        if (blockHasLadder(terrainMap, new BlockPos(node.pos))) {
            multiplier += 5.0F;
        }

        if (node.action == PathAction.SWIM) {
            multiplier *= ((node.pos.y <= prevNode.pos.y)
                    && (!terrainMap.isAirBlock(new BlockPos(node.pos.addVector(0d, 1d, 0d)))) ? 3.0F : 1.0F);
            return prevNode.distanceTo(node) * 1.3F * multiplier;
        }

        Block block = terrainMap.getBlockState(new BlockPos(node.pos)).getBlock();
        return prevNode.distanceTo(node) * (block.getExplosionResistance(null)) * multiplier;
    }

    protected void calcPathOptions(PathNode currentNode, PathfinderIM pathFinder) {
        if ((currentNode.pos.y <= 0) || (currentNode.pos.y > 255)) return;

        calcPathOptionsVertical(world, currentNode, pathFinder);

        if ((currentNode.action == PathAction.DIG) && (!canStandAt(world, new BlockPos(currentNode.pos)))) {
            return;
        }

        int height = getJumpHeight();
        for (int i = 1; i <= height; i++) {
            if (getCollide(world, currentNode.pos.addVector(0d, i, 0d)) == 0) {
                height = i - 1;
            }
        }

        int maxFall = 8;
        for (int i = 0; i < 4; i++) {
            if (currentNode.action != PathAction.NONE) {
                if ((i == 0) && (currentNode.action == PathAction.LADDER_UP_NX)) height = 0;
                if ((i == 1) && (currentNode.action == PathAction.LADDER_UP_PX)) height = 0;
                if ((i == 2) && (currentNode.action == PathAction.LADDER_UP_NZ)) height = 0;
                if ((i == 3) && (currentNode.action == PathAction.LADDER_UP_PZ)) height = 0;
            }
            int yOffset = 0;
            int currentY = MathHelper.floor(currentNode.pos.y) + height;
            boolean passedLevel = false;
            do {
                yOffset = getNextLowestSafeYOffset(terrainMap,
                        new BlockPos(currentNode.pos.x + Coords.offsetAdjX[i], currentY, currentNode.pos.z + Coords.offsetAdjZ[i]),
                        maxFall + currentY - MathHelper.floor(currentNode.pos.y));
                if (yOffset > 0)
                    break;
                if (yOffset > -maxFall) {
                    pathFinder.addNode(new Vec3d(
                                    currentNode.pos.x + Coords.offsetAdjX[i], currentY + yOffset + 1, currentNode.pos.z + Coords.offsetAdjZ[i]),
                            PathAction.NONE);
                }

                currentY += yOffset - 1;

                if ((!passedLevel) && (currentY <= currentNode.pos.y)) {
                    passedLevel = true;
                    if (currentY != currentNode.pos.y) {
                        addAdjacent(terrainMap,
                                new BlockPos(currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i])),
                                currentNode, pathFinder);
                    }

                }

            }

            while (currentY >= currentNode.pos.y);
        }

        if (canSwimHorizontal()) {
            for (int i = 0; i < 4; i++) {
                Vec3d vec = currentNode.pos.addVector(Coords.offsetAdjX[i], 0, Coords.offsetAdjZ[i]);
                if (getCollide(terrainMap, vec) == -1) pathFinder.addNode(vec, PathAction.SWIM);
            }
        }
    }

    protected void calcPathOptionsVertical(IBlockAccess terrainMap, PathNode currentNode, PathfinderIM pathFinder) {
        Vec3d vecAbove = currentNode.pos.addVector(0d, 1d, 0d);
        Vec3d vecBelow = currentNode.pos.addVector(0d, -1d, 0d);
        BlockPos posAbove = new BlockPos(vecAbove);
        BlockPos posBelow = new BlockPos(vecBelow);
        int collideAbove = getCollide(terrainMap, posAbove);
        int collideBelow = getCollide(terrainMap, posBelow);

        if (collideAbove > 0) {
            if (world.getBlockState(posAbove).getBlock() == Blocks.LADDER) {
                switch (world.getBlockState(posAbove).get(LadderBlock.FACING)) {
                    case EAST:
                        action = PathAction.LADDER_UP_PX;
                        break;
                    case WEST:
                        action = PathAction.LADDER_UP_NX;
                        break;
                    case NORTH:
                        action = PathAction.LADDER_UP_PZ;
                        break;
                    case SOUTH:
                        action = PathAction.LADDER_UP_NZ;
                        break;
                    default:
                        action = PathAction.NONE;
                }

                switch (currentNode.action) {
                    case NONE:
                        pathFinder.addNode(currentNode.pos.addVector(0d, 1d, 0d), action);
                        break;
                    case LADDER_UP_PX:
                    case LADDER_UP_NX:
                    case LADDER_UP_PZ:
                    case LADDER_UP_NZ:
                        if (action == currentNode.action) {
                            pathFinder.addNode(vecAbove, action);
                        }
                        break;
                    default:
                        pathFinder.addNode(vecAbove, action);
                }
            } else if (getCanClimb()) {
                if (isAdjacentSolidBlock(terrainMap, posAbove)) pathFinder.addNode(vecAbove, PathAction.NONE);
            }
        }

        if (getCanDigDown()) {
            if (collideBelow == 2) {
                pathFinder.addNode(vecBelow, PathAction.DIG);
            } else if (collideBelow == 1) {
                int maxFall = 5;
                int yOffset = getNextLowestSafeYOffset(terrainMap, posBelow, maxFall);
                if (yOffset <= 0) pathFinder.addNode(vecBelow, PathAction.NONE);
            }
        }

        if (canSwimVertical()) {
            if (collideBelow == -1) pathFinder.addNode(currentNode.pos, PathAction.SWIM);
            if (collideAbove == -1) pathFinder.addNode(currentNode.pos, PathAction.SWIM);
        }
    }

    protected void addAdjacent(IBlockAccess terrainMap, BlockPos pos, PathNode currentNode, PathfinderIM pathFinder) {
        addAdjacent(terrainMap, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), currentNode, pathFinder);
    }

    protected void addAdjacent(IBlockAccess terrainMap, Vec3d pos, PathNode currentNode, PathfinderIM pathFinder) {
        if (getCollide(terrainMap, pos) <= 0) return;
        if (getCanClimb()) {
            if (isAdjacentSolidBlock(terrainMap, new BlockPos(pos))) pathFinder.addNode(pos, PathAction.NONE);
        } else if (terrainMap.getBlockState(new BlockPos(pos)).getBlock() == Blocks.LADDER) {
            pathFinder.addNode(pos, PathAction.NONE);
        }
    }

    protected int getNextLowestSafeYOffset(IBlockAccess terrainMap, BlockPos pos, int maxOffsetMagnitude) {
        for (int i = 0; (i + pos.getY() > 0) && (i < maxOffsetMagnitude); i--) {
            boolean flag0 = canStandAtAndIsValid(world != null ? world : terrainMap, pos.up(i)); //if the entity can stand on the block
            boolean flag1 = canSwimHorizontal(); //If the entity can swim
            boolean flag2 = getCollide(world != null ? world : terrainMap, pos.up(i)) == -1; //If the block is liquid
            if (flag0 || (flag1 && flag2)) return i;
        }
        return 1;
    }

    protected boolean canStandOnBlock(int x, int y, int z) {
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return (!block.isAir()) && (!block.isPassable(terrainMap, new BlockPos(x, y, z))) && (!avoidsBlock(block));
    }
    */

    public byte getTier() {
        //return getDataManager().get(TIER);
        return tier;
    }

    private void setTier(byte t) {
        tier = t;
    }

    public void transitionAIGoal(Objective newObjective) {
        prevObjective = currentObjective;
        currentObjective = newObjective;
    }

    /**
     * @return the texture index (0-based)
     */
    public int getTextureIndex() {
        return 0;
    }

    @Override
    @Nullable
    public Nexus getNexus() {
        return nexus;
    }

    public boolean isNexusBound() {
        return nexus != null;
    }
}