package invasion.block;

import invasion.init.ModItems;
import invasion.init.ModTileEntityTypes;
import invasion.tileentity.NexusTileEntity;
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
public class NexusBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public NexusBlock() {
        super(Block.Properties.from(Blocks.OBSIDIAN).hardnessAndResistance(3.0F, 6000000.0F).sound(SoundType.GLASS));
        setDefaultState(getDefaultState().with(ACTIVATED, false));
    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    //TODO work out the correct ActionResultTypes
    @Override
    public ActionResultType onBlockActivated(BlockState blockstate, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) return ActionResultType.SUCCESS;

        Item item = player.getHeldItem(hand).getItem();
        //RM
        if (true || (item != ModItems.PROBE.get()) && ((/*!Config.DEBUG*/false) || (item != ModItems.DEBUG_WAND.get()))) {
            NexusTileEntity tileEntity = (NexusTileEntity) world.getTileEntity(pos);
            if (tileEntity != null) {
                //TODO which one of these two methods?
                NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, pos);
                //player.openContainer(tileEntity);
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