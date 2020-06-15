package invasion;

import invasion.entity.ai.navigator.PathAction;
import invasion.entity.ai.navigator.PathNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;


public class TerrainDataLayer implements IBlockAccessExtended {
    public static final int EXT_DATA_SCAFFOLD_METAPOSITION = 16384;
    private final World world;
    private Map<Integer, Integer> dataLayer = new HashMap<>();

    public TerrainDataLayer(World world) {
        this.world = world;
    }

    @Override
    public void setData(double x, double y, double z, Integer data) {
        this.dataLayer.put(PathNode.makeHash(x, y, z, PathAction.NONE), data);
    }

    @Override
    public int getLayeredData(int x, int y, int z) {
        int key = PathNode.makeHash(x, y, z, PathAction.NONE);
        if (this.dataLayer.containsKey(key)) {
            return this.dataLayer.get(key);
        }
        return 0;
    }

    public void setAllData(Map<Integer,Integer> data) {
        this.dataLayer = data;
    }

    public BlockState getBlockState(BlockPos blockPos) {
        return this.world.getBlockState(blockPos);
    }

}