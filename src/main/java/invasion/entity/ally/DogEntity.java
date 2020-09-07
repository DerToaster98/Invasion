package invasion.entity.ally;

import invasion.entity.IHasNexus;
import invasion.entity.monster.InvadingEntity;
import invasion.nexus.Nexus;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DogEntity extends WolfEntity implements IHasNexus, IConvertibleEntity<WolfEntity> {

    @Nullable
    protected Nexus nexus;

    public DogEntity(EntityType<DogEntity> type, World world) {
        super(type, world);
    }

    /*
    public DogEntity(EntityType<? extends DogEntity> type, World worldIn, @Nullable Nexus nexus) {
        super(type, worldIn);
        this.nexus = nexus;
    }

     */

    public void from(WolfEntity wolf) {
        setPositionAndRotation(wolf.getPosX(), wolf.getPosY(), wolf.getPosZ(), wolf.getYaw(1.0f), wolf.getPitch(1.0f));
        setTamed(wolf.isTamed());
        setOwnerId(wolf.getOwnerId());
    }


    @Override
    protected void registerGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, this.sitGoal);
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        //this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new BegGoal(this, 8.0F));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, InvadingEntity.class, false));
    }

    @Override
    @Nullable
    public Nexus getNexus() {
        return nexus;
    }

    @Override
    public void acquiredByNexus(Nexus nexus) {
        this.nexus = nexus;
    }
}
