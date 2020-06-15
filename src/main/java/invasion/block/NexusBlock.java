package invasion.block;

import invasion.init.ModItems;
import invasion.init.ModTileEntityTypes;
import invasion.tileentity.NexusTileEntity;
import invasion.util.config.Config;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NexusBlock extends ContainerBlock {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public NexusBlock() {
        super(Block.Properties.from(Blocks.OBSIDIAN).hardnessAndResistance(3.0F, 6000000.0F).sound(SoundType.GLASS));
        /*
        this.setUnlocalizedName(this.name);
        this.setRegistryName(this.name);
        this.setResistance(6000000.0F);
        this.setHardness(3.0F);
        //this.setStepSound(Blocks.glass.stepSound);
        this.setSoundType(Blocks.GLASS.getSoundType());
        this.itemBlock = new ItemBlock(this);
        this.itemBlock.setRegistryName(this.name);
*/
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    //TODO work out the correct ActionResultTypes
    @Override
    public ActionResultType onBlockActivated(BlockState blockstate, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return ActionResultType.FAIL;

        Item item = player.getHeldItem(hand).getItem();
        if ((item != ModItems.PROBE.get()) && ((!Config.DEBUG) || (item != ModItems.DEBUG_WAND.get()))) {
            NexusTileEntity tileEntity = (NexusTileEntity) world.getTileEntity(pos);
            if (tileEntity != null) {
                //TODO which one of these two methods?
                NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, pos);
                // player.openContainer(invasion);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (state.get(ACTIVATED)) {
            for (int i = 0; i < 6; ++i) {
                int j = rand.nextInt(2) * 2 - 1;
                int k = rand.nextInt(2) * 2 - 1;
                double d1 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
                double d2 = (float) pos.getY() + rand.nextFloat();
                double d3 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
                double d4 = rand.nextFloat() * (float) k;
                double d5 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
                double d6 = rand.nextFloat() * (float) k;
                world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, d4, d5, d6);
            }
        }

    }

    /*
    @OnlyIn(Dist.CLIENT)
    public void randomTick(BlockState blockState, World worldIn, BlockPos pos, Random rand) {

        int numberOfParticles = blockState.get(NexusBlock.ACTIVATED) ? 6 : 0;

        for (int i = 0; i < numberOfParticles; i++) {

            //Copied from BlockEnderChest
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
            double d1 = (float) pos.getY() + rand.nextFloat();
            double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            double d3 = rand.nextFloat() * (float) j;
            double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = rand.nextFloat() * (float) k;

            worldIn.addOptionalParticle(ParticleTypes.PORTAL,true, d0, d1, d2, d3, d4, d5);

			double y1 = blockPos.getY() + random.nextFloat();
			double y2 = (random.nextFloat() - 0.5D) * 0.5D;

			int direction = random.nextInt(2) * 2 - 1;
			double x2;
			double x1;
			double z1;
			double z2;
			if (random.nextInt(2) == 0) {
				z1 = blockPos.getZ() + 0.5D + 0.25D * direction;
				z2 = random.nextFloat() * 2.0F * direction;

				x1 = blockPos.getX() + random.nextFloat();
				x2 = (random.nextFloat() - 0.5D) * 0.5D;
			} else {
				x1 = blockPos.getX() + 0.5D + 0.25D * direction;
				x2 = random.nextFloat() * 2.0F * direction;
				z1 = blockPos.getZ() + random.nextFloat();
				z2 = (random.nextFloat() - 0.5D) * 0.5D;
			}

			world.spawnParticle(EnumParticleTypes.PORTAL, x1, y1, z1, x2, y2, z2);*
        }
    }*/


//TODO onReplaced

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.NEXUS.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return ModTileEntityTypes.NEXUS.get().create();
    }

    //TODO onlyIn?
    @Override
    public float getPlayerRelativeBlockHardness(BlockState blockstate, PlayerEntity player, IBlockReader blockReader, BlockPos pos) {
        if (blockstate.get(ACTIVATED)) {
            return -1.0F;
        } else {
            return super.getPlayerRelativeBlockHardness(blockstate, player, blockReader, pos);
        }
    }
}