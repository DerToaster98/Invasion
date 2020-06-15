package invasion.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;


public class ModifyBlockEntry {
    private final BlockPos pos;
    private final BlockState newBlock;
    private final int cost;
    private BlockState oldBlock;

    public ModifyBlockEntry(BlockPos pos, Block newBlock) {
        this(pos, newBlock.getDefaultState(), 0, null);
    }

    public ModifyBlockEntry(BlockPos pos, BlockState newBlock) {
        this(pos, newBlock, 0, null);
    }

    public ModifyBlockEntry(BlockPos pos, Block newBlock, int cost) {
        this(pos, newBlock != null ? newBlock.getDefaultState() : null, cost, null);
    }

    public ModifyBlockEntry(BlockPos pos, BlockState newBlock, int cost) {
        this(pos, newBlock, cost, null);
    }

    public ModifyBlockEntry(BlockPos pos, Block newBlock, int cost, Block oldBlock) {
        this(pos, newBlock.getDefaultState(), cost, oldBlock != null ? oldBlock.getDefaultState() : null);
    }

    public ModifyBlockEntry(BlockPos pos, BlockState newBlock, int cost, BlockState oldBlock) {
        this.pos = pos;
        this.newBlock = newBlock;
        this.cost = cost;
        this.oldBlock = oldBlock;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getNewBlock() {
        return newBlock;
    }

    public int getCost() {
        return cost;
    }

    public BlockState getOldBlock() {
        return oldBlock;
    }

    public void setOldBlock(BlockState state) {
        oldBlock = state;
    }
}