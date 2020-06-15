package invasion.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;


public interface ICanDig {

    BlockPos[] getBlockRemovalOrder(BlockPos pos);

    float getBlockRemovalCost(BlockPos pos);

    boolean canClearBlock(BlockPos pos);

    void onBlockRemoved(BlockPos pos, IBlockState state);

    IBlockAccess getTerrain();

}