package invasion.item;

import invasion.init.ModItemGroups;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InfusedSwordItem extends SwordItem {
    // <3 minecraftforum ;)

    public InfusedSwordItem(Item.Properties properties) {
        //TODO set Attack damage
        super(ItemTier.DIAMOND, 1, 2, properties.maxDamage(40).maxStackSize(1));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    //TODO what does this?

    @Override
    public boolean hitEntity(ItemStack itemstack,
							 LivingEntity entity1, LivingEntity entity2) {
        if (this.isDamaged(itemstack)) {
            this.setDamage(itemstack, this.getDamage(itemstack) - 1);

        }
        return true;
    }

    //TODO fix this below

    /*
    @Override
    public float getStrVsBlock(ItemStack par1ItemStack, BlockState par2Block) {
        if (par2Block == Blocks.WEB) {
            return 15.0F;
        }

        Material material = par2Block.getMaterial();
        return (material != Material.PLANTS) && (material != Material.VINE) && (material != Material.CORAL)
                && (material != Material.LEAVES) && (material != Material.SPONGE) && (material != Material.CACTUS) ? 1.0F : 1.5F;
    }
    */

	/*
    @Override
    public ActionResult getItemUseAction(ItemStack par1ItemStack) {
        return ActionResult.Fa;
    }

	 */

	/*
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 0;
    }

	 */



	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote && itemstack.getDamage() == 0) {
            // if player isSneaking then refill hunger else refill health
            if (player.isSneaking()) {
                player.getFoodStats().addStats(6, 0.5f);
                //world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                player.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
            } else {
                player.heal(6.0F);
                // spawn heart particles around the player
				//TODO fix coordinates
                world.addParticle(ParticleTypes.HEART,
                        player.getPosX() + 1.5D, player.getPosY(),
                        player.getPosZ(), 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.HEART,
                        player.getPosX() - 1.5D, player.getPosY(),
                        player.getPosZ(), 0.0D, 0.0D, 0.0D);
                world.addOptionalParticle(ParticleTypes.HEART, player.getPosX(),
                        player.getPosY(), player.getPosZ() + 1.5D, 0.0D,
                        0.0D, 0.0D);
                world.addOptionalParticle(ParticleTypes.HEART, player.getPosX(),
                        player.getPosY(), player.getPosZ() - 1.5D, 0.0D,
                        0.0D, 0.0D);
            }

            itemstack.setDamage(this.getMaxDamage());
        }

        return new ActionResult<>(ActionResultType.PASS, itemstack);
    }

    @Override
    public boolean canHarvestBlock(BlockState state) {
        return state.getBlock() == Blocks.COBWEB;
    }


    /*@Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase playerIn) {
        return true;
    }

     */

}