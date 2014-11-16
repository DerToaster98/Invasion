package invmod.common;

import invmod.common.entity.PathAction;
import invmod.common.entity.PathNode;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;

public class TerrainDataLayer
    implements IBlockAccessExtended
{
    public static final int EXT_DATA_SCAFFOLD_METAPOSITION = 16384;
    private IBlockAccess world;
    private IntHashMap dataLayer;

    public TerrainDataLayer(IBlockAccess world)
    {
        this.world = world;
        this.dataLayer = new IntHashMap();
    }

    public void setData(int x, int y, int z, Integer data)
    {
        this.dataLayer.addKey(PathNode.makeHash(x, y, z, PathAction.NONE), data);
    }

    public int getLayeredData(int x, int y, int z)
    {
        int key = PathNode.makeHash(x, y, z, PathAction.NONE);

        if (this.dataLayer.containsItem(key))
        {
            return ((Integer)this.dataLayer.lookup(key)).intValue();
        }

        return 0;
    }

    public void setAllData(IntHashMap data)
    {
        this.dataLayer = data;
    }

    public int getBlockId(int x, int y, int z)
    {
        return this.world.getBlockId(x, y, z);
    }

    public TileEntity getBlockTileEntity(int x, int y, int z)
    {
        return this.world.getBlockTileEntity(x, y, z);
    }

    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int meta)
    {
        return this.world.getLightBrightnessForSkyBlocks(x, y, z, meta);
    }

    public float getBrightness(int x, int y, int z, int meta)
    {
        return this.world.getBrightness(x, y, z, meta);
    }

    public float getLightBrightness(int x, int y, int z)
    {
        return this.world.getLightBrightness(x, y, z);
    }

    public int getBlockMetadata(int x, int y, int z)
    {
        return this.world.getBlockMetadata(x, y, z);
    }

    public Material getBlockMaterial(int x, int y, int z)
    {
        return this.world.getBlockMaterial(x, y, z);
    }

    public boolean isBlockOpaqueCube(int x, int y, int z)
    {
        return this.world.isBlockOpaqueCube(x, y, z);
    }

    public boolean isBlockNormalCube(int x, int y, int z)
    {
        return this.world.isBlockNormalCube(x, y, z);
    }

    public boolean isAirBlock(int x, int y, int z)
    {
        return this.world.isAirBlock(x, y, z);
    }

    public BiomeGenBase getBiomeGenForCoords(int i, int j)
    {
        return this.world.getBiomeGenForCoords(i, j);
    }

    public int getHeight()
    {
        return this.world.getHeight();
    }

    public boolean extendedLevelsInChunkCache()
    {
        return this.world.extendedLevelsInChunkCache();
    }

    public boolean doesBlockHaveSolidTopSurface(int x, int y, int z)
    {
        return this.world.doesBlockHaveSolidTopSurface(x, y, z);
    }

    public Vec3Pool getWorldVec3Pool()
    {
        return this.world.getWorldVec3Pool();
    }

    public int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4)
    {
        return this.world.isBlockProvidingPowerTo(var1, var2, var3, var4);
    }

    public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        return this.world.isBlockSolidOnSide(x, y, z, side, _default);
    }
}