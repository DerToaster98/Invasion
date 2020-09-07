package invasion.entity.monster;

import invasion.init.ModEntityTypes;
import invasion.nexus.Nexus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;


public class ImpEntity extends InvadingEntity {

    //private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24

    public ImpEntity(World world, Nexus nexus, int i) {
        super(ModEntityTypes.IMP.get(), world, nexus);
        setCanClimb(true);
    }

    public ImpEntity(EntityType<? extends ImpEntity> type, World world) {
        super(type, world, null);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));

    }

    /*
    @Override
    protected void registerGoals() {
        tasksIM = new EntityAITasks(world.profiler);
        tasksIM.addTask(0, new EntityAISwimming(this));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityPlayer.class, 40));
        tasksIM.addTask(1, new EntityAIKillEntity(this, EntityPlayerMP.class, 40));
        tasksIM.addTask(2, new EntityAIAttackNexus(this));
        tasksIM.addTask(3, new EntityAIWaitForEngy(this, 4.0F, true));
        tasksIM.addTask(4, new EntityAIKillEntity(this, EntityLiving.class, 40));
        tasksIM.addTask(5, new EntityAIGoToNexus(this));
        tasksIM.addTask(6, new EntityAIWanderIM(this));
        tasksIM.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasksIM.addTask(8, new EntityAIWatchClosest(this, MoulderingCreeperEntity.class, 12.0F));
        tasksIM.addTask(8, new EntityAILookIdle(this));

        targetTasksIM = new EntityAITasks(world.profiler);
        targetTasksIM.addTask(0, new EntityAITargetRetaliate(this, EntityLiving.class, getAggroRange()));
        targetTasksIM.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, getSenseRange(), false));
        targetTasksIM.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, getAggroRange(), true));
        targetTasksIM.addTask(5, new EntityAIHurtByTarget(this, false));
        targetTasksIM.addTask(3, new EntityAITargetOnNoNexusPath(this, PigEngyEntity.class, 3.5F));

    }

     */

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0f);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        entity.setFire(3);
        return super.attackEntityAsMob(entity);
    }

}