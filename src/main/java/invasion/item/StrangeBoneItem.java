package invasion.item;

import invasion.BlocksAndItems;
import invasion.entity.ally.EntityIMWolf;
import invasion.nexus.Nexus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;


public class StrangeBoneItem extends ModItem
{

	public StrangeBoneItem()
	{
		super("strangeBone");
		this.setMaxStackSize(1);
	}

	@Override
	public int getDamage(ItemStack stack)
	{
		return 0;
	}


	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase targetEntity, EnumHand hand)
	{
		if ((!targetEntity.world.isRemote) && ((targetEntity instanceof EntityWolf)) && (!(targetEntity instanceof EntityIMWolf)))
		{
			EntityWolf wolf = (EntityWolf)targetEntity;

			if (wolf.isTamed())
			{
				Nexus nexus = null;
				int x = MathHelper.floor(wolf.posX);
				int y = MathHelper.floor(wolf.posY);
				int z = MathHelper.floor(wolf.posZ);

				for (int i = -7; i < 8; i++)
				{
					for (int j = -4; j < 5; j++)
					{
						for (int k = -7; k < 8; k++)
						{
							BlockPos newBlockPos = new BlockPos(x, y, z).add(i, j, k);
							if (wolf.world.getBlockState(newBlockPos).getBlock() == BlocksAndItems.blockNexus)
							{
								nexus = (Nexus)wolf.world.getTileEntity(newBlockPos);
								break;
							}
						}
					}
				}

				if (nexus != null)
				{
					EntityIMWolf newWolf = new EntityIMWolf(wolf, nexus);
					wolf.world.spawnEntity(newWolf);
					wolf.setDead();
					itemStack.shrink(1);
				}
				else
				{
					player.sendMessage(new TextComponentTranslation("The wolf doesn't like this strange bone."));
				}
			}

			return true;
		}

		return false;
	}

}