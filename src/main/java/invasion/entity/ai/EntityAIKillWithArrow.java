package invasion.entity.ai;

import invasion.entity.monster.InvadingEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;


public class EntityAIKillWithArrow<T extends EntityLivingBase> extends EntityAIKillEntity<T>
{
	private float attackRangeSq;

	public EntityAIKillWithArrow(InvadingEntity entity, Class<? extends T> targetClass, int attackDelay, float attackRange)
	{
		super(entity, targetClass, attackDelay);
		this.attackRangeSq = (attackRange * attackRange);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();
		EntityLivingBase target = this.getTarget();
		if ((this.getEntity().getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) < 36.0D) && (this.getEntity().getEntitySenses().canSee(target)))
			this.getEntity().getNavigatorNew().haltForTick();
	}

	@Override
	protected void attackEntity(Entity target)
	{
		this.setAttackTime(this.getAttackDelay());
		//EntityArrow entityarrow = new EntityArrow(entity.world, entity, getTarget(), 1.1F, 12.0F);
		//this.getEntity().world.playSoundAtEntity(this.getEntity(), "random.bow", 1.0F, 1.0F / (this.getEntity().getRNG().nextFloat() * 0.4F + 0.8F));

		float bowCharge = ItemBow.getArrowVelocity(20);

		//Copied from EntitySkeleton.attackEntityWithRangedAttack()
		EntityTippedArrow arrow = new EntityTippedArrow(this.getEntity().world, this.getEntity());
		double d0 = this.getTarget().posX - this.getEntity().posX;
		double d1 = this.getTarget().getEntityBoundingBox().minY + (double)(this.getTarget().height / 3f) - arrow.posY;
		double d2 = this.getTarget().posZ - this.getEntity().posZ;
		double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
		arrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.getEntity().world.getDifficulty().getDifficultyId() * 4));

		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this.getEntity());
		int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this.getEntity());
		DifficultyInstance difficultyinstance = this.getEntity().world.getDifficultyForLocation(new BlockPos(this.getEntity()));
		arrow.setDamage((double)(bowCharge * 2.0F) + this.getEntity().getRNG().nextGaussian() * 0.25D + (double)((float)this.getEntity().world.getDifficulty().getDifficultyId() * 0.11F));

		if (i > 0) arrow.setDamage(arrow.getDamage() + (double)i * 0.5D + 0.5D);
		if (j > 0) arrow.setKnockbackStrength(j);

		boolean flag = this.getEntity().isBurning() && difficultyinstance.isHard() && this.getEntity().getRNG().nextBoolean();
		flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this.getEntity()) > 0;

		if (flag) arrow.setFire(100);

		ItemStack itemstack = this.getEntity().getHeldItem(EnumHand.OFF_HAND);

		if (itemstack != null && itemstack.getItem() == Items.TIPPED_ARROW) arrow.setPotionEffect(itemstack);

		this.getEntity().playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getEntity().getRNG().nextFloat() * 0.4F + 0.8F));
		this.getEntity().world.spawnEntity(arrow);
	}

	@Override
	protected boolean canAttackEntity(Entity target)
	{
		return (this.getAttackTime() <= 0) && (this.getEntity().getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) < this.attackRangeSq) && (this.getEntity().getEntitySenses().canSee(target));
	}
}