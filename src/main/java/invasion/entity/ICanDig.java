package invasion.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;


public interface ICanDig
{

	public BlockPos[] getBlockRemovalOrder(BlockPos pos);

	public float getBlockRemovalCost(BlockPos pos);

	public boolean canClearBlock(BlockPos pos);

	public void onBlockRemoved(BlockPos pos, IBlockState state);

	public IBlockAccess getTerrain();

}