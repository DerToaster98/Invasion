package invasion.entity.monster;

import invasion.init.ModEntityTypes;
import invasion.nexus.Nexus;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CloakedSkeletonEntity extends InvadingEntity implements IRangedAttackMob {
    public CloakedSkeletonEntity(EntityType<? extends CloakedSkeletonEntity> type, World world) {
        super(type, world, null);
    }

    public CloakedSkeletonEntity(World world, Nexus nexus) {
        super(ModEntityTypes.CLOAKED_SKELETON.get(), world, nexus);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //TODO
    }

    @Override //copied from AbstractSkeletonEntity
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(abstractarrowentity);
    }

    private AbstractArrowEntity fireArrow(ItemStack stack, float distanceFactor) {
        return ProjectileHelper.fireArrow(this, stack, distanceFactor);
    }

}
