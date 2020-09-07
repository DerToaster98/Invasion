package invasion.item;


import invasion.entity.ally.DogEntity;
import invasion.init.ModEntityTypes;
import invasion.nexus.Nexus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class StrangeBoneItem extends Item {
    public StrangeBoneItem(Properties properties) {
        super(properties);
    }


//TODO tame wolves

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        World world = playerIn.world;
        if (target instanceof WolfEntity && !(target instanceof DogEntity)) {
            if (world.isRemote) return true;
            DogEntity dog = new DogEntity(ModEntityTypes.DOG.get(), world);
            dog.from((WolfEntity) target);
            dog.acquiredByNexus(Nexus.get(world));
            world.addEntity(dog);
            target.remove();
            stack.shrink(1);
            return true;
        }
        return false;
    }


/*    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase targetEntity, EnumHand hand) {
        if ((!targetEntity.world.isRemote) && ((targetEntity instanceof EntityWolf)) && (!(targetEntity instanceof EntityIMWolf))) {
            EntityWolf wolf = (EntityWolf) targetEntity;

            if (wolf.isTamed()) {
                Nexus nexus = null;
                int x = MathHelper.floor(wolf.posX);
                int y = MathHelper.floor(wolf.posY);
                int z = MathHelper.floor(wolf.posZ);

                for (int i = -7; i < 8; i++) {
                    for (int j = -4; j < 5; j++) {
                        for (int k = -7; k < 8; k++) {
                            BlockPos newBlockPos = new BlockPos(x, y, z).add(i, j, k);
                            if (wolf.world.getBlockState(newBlockPos).getBlock() == BlocksAndItems.blockNexus) {
                                nexus = (Nexus) wolf.world.getTileEntity(newBlockPos);
                                break;
                            }
                        }
                    }
                }

                if (nexus != null) {
                    EntityIMWolf newWolf = new EntityIMWolf(wolf, nexus);
                    wolf.world.spawnEntity(newWolf);
                    wolf.setDead();
                    itemStack.shrink(1);
                } else {
                    player.sendMessage(new TextComponentTranslation("The wolf doesn't like this strange bone."));
                }
            }

            return true;
        }

        return false;
    }

 */

}