package invasion.util.spawneggs;

import java.util.Set;
import invasion.Invasion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class ItemSpawnEgg extends Item
{

	private final String name = "monsterplacer";

	public ItemSpawnEgg()
	{
		super();
		this.setHasSubtypes(true);
		this.setCreativeTab(Invasion.tabInvmod);
		this.setUnlocalizedName(this.name);
		this.setRegistryName(this.name);
		GameRegistry.register(this);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = ("" + I18n.format(this.getUnlocalizedName() + ".name")).trim();
		SpawnEggInfo info = SpawnEggRegistry.getEggInfo((short)stack.getItemDamage());

		if (info == null)
			return name;

		String mobID = info.mobID;
		String displayName = info.displayName;

		if (stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey("mobID"))
				mobID = compound.getString("mobID");
			if (compound.hasKey("displayName"))
				displayName = compound.getString("displayName");
		}

		if (displayName == null)
			name += ' ' + attemptToTranslate("entity." + mobID + ".name", mobID);
		else
			name += ' ' + attemptToTranslate("eggdisplay." + displayName, displayName);

		return name;
	}

	//TODO: Removed Override annotation
	public int getColorFromItemStack(ItemStack stack, int par2)
	{
		SpawnEggInfo info = SpawnEggRegistry.getEggInfo((short)stack.getItemDamage());

		if (info == null)
			return 16777215;

		int color = (par2 == 0) ? info.primaryColor : info.secondaryColor;

		if (stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (par2 == 0 && compound.hasKey("primaryColor"))
				color = compound.getInteger("primaryColor");
			if (par2 != 0 && compound.hasKey("secondaryColor"))
				color = compound.getInteger("secondaryColor");
		}

		return color;
	}

	//public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		/*
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}*/
		ItemStack stack = player.getHeldItem(hand);
		if (worldIn.isRemote) return EnumActionResult.PASS;

		Block block = worldIn.getBlockState(pos).getBlock();
		pos = pos.offset(facing);
		double d0 = 0.0D;

		if (facing == EnumFacing.UP && block != null)// && //TODO block.getRenderType() == 11)
			d0 = 0.5D;

		Entity entity = spawnCreature(worldIn, stack,
			new BlockPos((double)pos.getX() + 0.5D, (double)pos.getY() + d0,
				(double)pos.getZ() + 0.5D));

		if (entity != null)
		{
			if (entity instanceof EntityLiving && stack.hasDisplayName())
				((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
			if (!player.capabilities.isCreativeMode)
				//--stack.stackSize;
				stack.shrink(1);
		}
		return EnumActionResult.SUCCESS;

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote) return new ActionResult(EnumActionResult.PASS, stack);

		RayTraceResult trace = this.rayTrace(world, player, true); //getMovingObjectPositionFromPlayer(world, player, true);

		if (trace == null) return new ActionResult(EnumActionResult.FAIL, stack);

		if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			// GetblockPos()
			BlockPos blockpos = trace.getBlockPos();

			if (!world.isBlockModifiable(player, blockpos)) return new ActionResult(EnumActionResult.FAIL, stack);
			;

			if (world.getBlockState(blockpos).getBlock() instanceof BlockLiquid)
			{
				Entity entity = spawnCreature(world, stack, blockpos);

				if (entity != null)
				{
					if (entity instanceof EntityLiving && stack.hasDisplayName()) ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
					if (!player.capabilities.isCreativeMode) stack.shrink(1);;
				}
			}
		}

		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	public static Entity spawnCreature(World world, ItemStack stack, BlockPos blockpos)
	{
		SpawnEggInfo info = SpawnEggRegistry.getEggInfo((short)stack.getItemDamage());

		if (info == null)
			return null;

		String mobID = info.mobID;
		NBTTagCompound spawnData = info.spawnData;

		if (stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey("mobID"))
				mobID = compound.getString("mobID");
			if (compound.hasKey("spawnData"))
				spawnData = compound.getCompoundTag("spawnData");
		}

		Entity entity = null;

		entity = EntityList.createEntityByName(mobID, world);

		if (entity != null)
		{
			if (entity instanceof EntityLiving)
			{
				EntityLiving entityliving = (EntityLiving)entity;
				entity.setLocationAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
				entityliving.rotationYawHead = entityliving.rotationYaw;
				entityliving.renderYawOffset = entityliving.rotationYaw;
				//onSpawnWithEgg
				//entityliving.func_180482_a(world.getDifficultyForLocation(blockpos), null);
				if (!spawnData.hasNoTags())
					addNBTData(entity, spawnData);
				world.spawnEntity(entity);
				entityliving.playLivingSound();
				spawnRiddenCreatures(entity, world, spawnData);
			}
		}

		return entity;
	}

	private static void spawnRiddenCreatures(Entity entity, World world, NBTTagCompound cur)
	{
		while (cur.hasKey("Riding"))
		{
			cur = cur.getCompoundTag("Riding");
			Entity newEntity = EntityList.createEntityByName(cur.getString("id"), world);
			if (newEntity != null)
			{
				addNBTData(newEntity, cur);
				newEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
				world.spawnEntity(newEntity);
				//entity.mountEntity(newEntity);
				entity.startRiding(newEntity);
			}
			entity = newEntity;
		}
	}

	@SuppressWarnings("unchecked")
	private static void addNBTData(Entity entity, NBTTagCompound spawnData)
	{
		NBTTagCompound newTag = new NBTTagCompound();
		entity.writeToNBTOptional(newTag);

		for (String name : (Set<String>)spawnData.getKeySet())
			newTag.setTag(name, spawnData.getTag(name).copy());

		entity.readFromNBT(newTag);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (SpawnEggInfo info : SpawnEggRegistry.getEggInfoList())
			items.add(new ItemStack(this, 1, info.eggID));
	}
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		for (SpawnEggInfo info : SpawnEggRegistry.getEggInfoList())
			list.add(new ItemStack(item, 1, info.eggID));
	}*/

	public static String attemptToTranslate(String key, String _default)
	{
		//String result = StatCollector.translateToLocal(key);
		String result = I18n.format(key);
		return (result.equals(key)) ? _default : result;
	}

	public String getName()
	{
		return this.name;
	}
}