package invasion.item;

import invasion.BlocksAndItems;
import invasion.Invasion;
import invasion.Reference;
import invasion.entity.monster.*;
import invasion.nexus.Nexus;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class DebugWandItem extends Item {
    private final String name = "debugWand";
    private Nexus nexus;

    public DebugWandItem() {
        this.setRegistryName(this.name);
        GameRegistry.register(this);
        this.setMaxDamage(0);
        this.setUnlocalizedName(Reference.MODID + "_" + this.name);
        this.setMaxStackSize(1);
        this.setCreativeTab(Invasion.tabInvmod);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos blockPos,
                                           EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) return EnumActionResult.FAIL;
        Block block = world.getBlockState(blockPos).getBlock();
        if (block == BlocksAndItems.blockNexus) {
            this.nexus = ((Nexus) world.getTileEntity(blockPos));
            return EnumActionResult.SUCCESS;
        }
        BlockPos onePosAbove = new BlockPos(blockPos).add(0, 1, 0);
        EntityIMBird bird = new EntityIMGiantBird(world);
        bird.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

        EntityZombie zombie2 = new EntityZombie(world);
        zombie2.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

        EntityWolf wolf = new EntityWolf(world);
        wolf.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());
        world.spawnEntity(wolf);

        Entity pigEngy = new PigEngyEntity(world);
        pigEngy.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

        InvadingZombieEntity zombie = new InvadingZombieEntity(world, this.nexus);
        zombie.setTexture(0);
        zombie.setFlavour(0);
        zombie.setTier(1);
        zombie.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

        if (this.nexus != null) {
            Entity entity = new PigEngyEntity(world, this.nexus);
            entity.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

            zombie = new InvadingZombieEntity(world, this.nexus);
            zombie.setTexture(0);
            zombie.setFlavour(0);
            zombie.setTier(2);
            zombie.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

            Entity thrower = new EntityIMThrower(world, this.nexus);
            thrower.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

            MoulderingCreeperEntity creep = new MoulderingCreeperEntity(world, this.nexus);
            creep.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

            EntityIMSpider spider = new EntityIMSpider(world, this.nexus);

            spider.setTexture(0);
            spider.setFlavour(0);
            spider.setTier(2);

            spider.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

            EntityIMSkeleton skeleton = new EntityIMSkeleton(world, this.nexus);
            skeleton.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());
        }

        EntityIMSpider entity = new EntityIMSpider(world, this.nexus);

        entity.setTexture(0);
        entity.setFlavour(1);
        entity.setTier(2);

        entity.setPosition(onePosAbove.getX(), onePosAbove.getY(), onePosAbove.getZ());

        MoulderingCreeperEntity creep = new MoulderingCreeperEntity(world);
        creep.setPosition(150.5D, 64.0D, 271.5D);

        return EnumActionResult.SUCCESS;
    }

    //DarthXenon: Unused.
	/*public boolean hitEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase targetEntity) {
		if ((targetEntity instanceof EntityWolf)) {
			EntityWolf wolf = (EntityWolf) targetEntity;
	
			if (player != null) {
				wolf.func_152115_b(player.getDisplayName().toString());
				
			}
			return true;
		}
		return false;
	}*/

    public String getName() {
        return this.name;
    }
}