package invasion.entity.monster;

import com.google.common.base.Predicate;
import invasion.INotifyTask;
import invasion.Invasion;
import invasion.entity.ai.*;
import invasion.entity.ai.navigator.Path;
import invasion.entity.ai.navigator.PathNode;
import invasion.nexus.Nexus;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MoulderingCreeperEntity extends InvadingEntity {

    private static final DataParameter<Integer> CREEPER_STATE = EntityDataManager.createKey(MoulderingCreeperEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    private int timeSinceIgnited;
    private int lastActiveTime;
    private boolean explosionDeath;
    private boolean commitToExplode;
    private int explodeDirection;

    private short fuseTime = 30;
    private final int explosionRadius = 3;

    // TODO rework constructors

    public MoulderingCreeperEntity(World world) {
        this(world, null);
    }

    public MoulderingCreeperEntity(World world, Nexus nexus) {
        super(world, nexus);
        setName("Creeper");
        setGender(0);
        setBaseMoveSpeedStat(0.21F);
        setMaxHealthAndHealth(Invasion.getMobHealth(this));
        initEntityAI();
    }


    @Override
    protected void registerGoals() {

        tasksIM = new EntityAITasks(world.profiler);
        tasksIM.addTask(0, new EntityAISwimming(this));
        tasksIM.addTask(1, new EntityAICreeperIMSwell(this));
        tasksIM.addTask(2, new EntityAIAvoidEntity(this, EntityOcelot.class, (Predicate) entity -> entity instanceof EntityOcelot, 6.0F, 0.25D, 0.300000011920929D));
        tasksIM.addTask(3, new EntityAIKillEntity(this, EntityPlayer.class, 40));
        tasksIM.addTask(3, new EntityAIKillEntity(this, EntityPlayerMP.class, 40));
        tasksIM.addTask(4, new EntityAIAttackNexus(this));
        tasksIM.addTask(5, new EntityAIWaitForEngy(this, 4.0F, true));
        tasksIM.addTask(6, new EntityAIKillEntity(this, EntityLiving.class, 40));
        tasksIM.addTask(7, new EntityAIGoToNexus(this));
        tasksIM.addTask(8, new EntityAIWanderIM(this));
        tasksIM.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 4.8F));
        tasksIM.addTask(9, new EntityAILookIdle(this));

        targetTasksIM = new EntityAITasks(world.profiler);
        targetTasksIM.addTask(0, new EntityAITargetRetaliate(this, EntityLiving.class, 12.0F));
        if (isNexusBound()) {
            targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, 20.0F, true));
        } else {
            targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, getSenseRange(), false));
            targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, getAggroRange(), true));
        }
        targetTasksIM.addTask(3, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(CREEPER_STATE, -1);
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putShort("Fuse", fuseTime);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("Fuse", Constants.NBT.TAG_ANY_NUMERIC)) {
            fuseTime = nbt.getShort("Fuse");
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getCreeperFlashIntensity(float f) {
        return MathHelper.lerp(f, (float) lastActiveTime, (float) timeSinceIgnited) / (float) (fuseTime - 2);
    }

    @Override
    public boolean onPathBlocked(Path path, INotifyTask notifee) {
        if (!path.isFinished()) {
            PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());
            double dX = node.pos.x + 0.5D - posX;
            double dZ = node.pos.z + 0.5D - posZ;
            float facing = (float) (Math.atan2(dZ, dX) * 180.0D / 3.141592653589793D) - 90.0F;
            if (facing < 0.0F) facing += 360.0F;
            facing %= 360.0F;

            if ((facing >= 45.0F) && (facing < 135.0F))
                explodeDirection = 1;
            else if ((facing >= 135.0F) && (facing < 225.0F))
                explodeDirection = 3;
            else if ((facing >= 225.0F) && (facing < 315.0F))
                explodeDirection = 0;
            else {
                explodeDirection = 2;
            }
            setCreeperState(1);
            commitToExplode = true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (explosionDeath) {
            explode();
            setDead();
        } else if (isAlive()) {
            lastActiveTime = timeSinceIgnited;
            int state = getCreeperState();

            if (state > 0) {
                if (commitToExplode) {
                    getMoveHelper().setMoveTo(posX + invasion.util.Coords.offsetAdjX[explodeDirection], posY, posZ + invasion.util.Coords.offsetAdjZ[explodeDirection], 0.0D);
                }
                if (timeSinceIgnited == 0) {
                    //world.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
                    playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1f, 0.5f);
                }
            }
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREEPER_DEATH;
    }

    @Override
    public String getSpecies() {
        return "Creeper";
    }

    /*
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);

        if ((par1DamageSource.getTrueSource() instanceof EntitySkeleton)) {
            dropItem(Item.getItemById(Item.getIdFromItem(Items.RECORD_13) + rand.nextInt(10)), 1);
        }
    }

     */

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        return true;
    }

    public float setCreeperFlashTime(float par1) {
        return (lastActiveTime + (timeSinceIgnited - lastActiveTime) * par1) / 28.0F;
    }

    @Override
    public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap) {
        Block block = terrainMap.getBlockState(new BlockPos(node.pos)).getBlock();
        if ((block != Blocks.AIR) && (!block.isPassable(terrainMap, new BlockPos(node.pos))) && (block != BlocksAndItems.blockNexus)) {
            return prevNode.distanceTo(node) * 12.0F;
        }

        return super.getBlockPathCost(prevNode, node, terrainMap);
    }

    @Override
    public String toString() {
        return "MoulderingCreeper-T" + getTier();
    }

    /* TODO drop Items
    @Override
    protected void dropFewItems(boolean flag, int amount) {
        entityDropItem(new ItemStack(Items.GUNPOWDER), 0.5F);
    }

     */

    protected void explode() {
        //   Explosion explosion = new Explosion(world, this, posX, posY, posZ, 2.1F, false, true);

        Explosion.Mode explosionMode = Explosion.Mode.DESTROY;
       /* if (!world.isRemote) explosion.doExplosionA();
        explosion.doExplosionB(true);
        //ExplosionUtil.doExplosionB(world,explosion,true);
*/

        dead = true;
        world.createExplosion(this, getPosX(), getPosY(), getPosZ(), (float) explosionRadius, explosionMode);
        remove();
    }

    public int getCreeperState() {
        return getDataManager().get(CREEPER_STATE);
    }

    public void setCreeperState(int state) {
        if ((commitToExplode) && (state != 1)) return;
        getDataManager().set(CREEPER_STATE, state);
    }
}