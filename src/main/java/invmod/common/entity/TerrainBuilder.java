package invmod.common.entity;

import invmod.common.INotifyTask;
import invmod.common.nexus.INexusAccess;
import invmod.common.util.IPosition;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TerrainBuilder
    implements ITerrainBuild
{
    private static final float LADDER_COST = 25.0F;
    private static final float PLANKS_COST = 45.0F;
    private static final float COBBLE_COST = 65.0F;
    private EntityIMLiving theEntity;
    private ITerrainModify modifier;
    private float buildRate;

    public TerrainBuilder(EntityIMLiving entity, ITerrainModify modifier, float buildRate)
    {
        this.theEntity = entity;
        this.modifier = modifier;
        this.buildRate = buildRate;
    }

    public void setBuildRate(float buildRate)
    {
        this.buildRate = buildRate;
    }

    public float getBuildRate()
    {
        return this.buildRate;
    }

    public boolean askBuildScaffoldLayer(IPosition pos, INotifyTask asker)
    {
        if (this.modifier.isReadyForTask(asker))
        {
            Scaffold scaffold = this.theEntity.getNexus().getAttackerAI().getScaffoldAt(pos);

            if (scaffold != null)
            {
                int height = pos.getYCoord() - scaffold.getYCoord();
                int xOffset = invmod.common.util.CoordsInt.offsetAdjX[scaffold.getOrientation()];
                int zOffset = invmod.common.util.CoordsInt.offsetAdjZ[scaffold.getOrientation()];
                int id = this.theEntity.worldObj.getBlockId(pos.getXCoord() + xOffset, pos.getYCoord() - 1, pos.getZCoord() + zOffset);
                List modList = new ArrayList();

                if (height == 1)
                {
                    if (!Block.isNormalCube(id))
                    {
                        modList.add(new ModifyBlockEntry(pos.getXCoord() + xOffset, pos.getYCoord() - 1, pos.getZCoord() + zOffset, Block.planks.blockID, (int)(45.0F / this.buildRate)));
                    }

                    id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord());

                    if (id == 0)
                    {
                        modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
                    }
                }

                id = this.theEntity.worldObj.getBlockId(pos.getXCoord() + xOffset, pos.getYCoord(), pos.getZCoord() + zOffset);

                if (!Block.isNormalCube(id))
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord() + xOffset, pos.getYCoord(), pos.getZCoord() + zOffset, Block.planks.blockID, (int)(45.0F / this.buildRate)));
                }

                id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord(), pos.getZCoord());

                if (id != Block.ladder.blockID)
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord(), pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
                }

                if (scaffold.isLayerPlatform(height))
                {
                    for (int i = 0; i < 8; i++)
                    {
                        if ((invmod.common.util.CoordsInt.offsetRing1X[i] != xOffset) || (invmod.common.util.CoordsInt.offsetRing1Z[i] != zOffset))
                        {
                            id = this.theEntity.worldObj.getBlockId(pos.getXCoord() + invmod.common.util.CoordsInt.offsetRing1X[i], pos.getYCoord(), pos.getZCoord() + invmod.common.util.CoordsInt.offsetRing1Z[i]);

                            if (!Block.isNormalCube(id))
                            {
                                modList.add(new ModifyBlockEntry(pos.getXCoord() + invmod.common.util.CoordsInt.offsetRing1X[i], pos.getYCoord(), pos.getZCoord() + invmod.common.util.CoordsInt.offsetRing1Z[i], Block.planks.blockID, (int)(45.0F / this.buildRate)));
                            }
                        }
                    }
                }

                if (modList.size() > 0)
                {
                    return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
                }
            }
        }

        return false;
    }

    public boolean askBuildLadderTower(IPosition pos, int orientation, int layersToBuild, INotifyTask asker)
    {
        if (this.modifier.isReadyForTask(asker))
        {
            int xOffset = orientation == 1 ? -1 : orientation == 0 ? 1 : 0;
            int zOffset = orientation == 3 ? -1 : orientation == 2 ? 1 : 0;
            List modList = new ArrayList();
            int id = this.theEntity.worldObj.getBlockId(pos.getXCoord() + xOffset, pos.getYCoord() - 1, pos.getZCoord() + zOffset);

            if (!Block.isNormalCube(id))
            {
                modList.add(new ModifyBlockEntry(pos.getXCoord() + xOffset, pos.getYCoord() - 1, pos.getZCoord() + zOffset, Block.planks.blockID, (int)(45.0F / this.buildRate)));
            }

            id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord());

            if (id == 0)
            {
                modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
            }

            for (int i = 0; i < layersToBuild; i++)
            {
                id = this.theEntity.worldObj.getBlockId(pos.getXCoord() + xOffset, pos.getYCoord() + i, pos.getZCoord() + zOffset);

                if (!Block.isNormalCube(id))
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord() + xOffset, pos.getYCoord() + i, pos.getZCoord() + zOffset, Block.planks.blockID, (int)(45.0F / this.buildRate)));
                }

                id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() + i, pos.getZCoord());

                if (id != Block.ladder.blockID)
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() + i, pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
                }
            }

            if (modList.size() > 0)
            {
                return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
            }
        }

        return false;
    }

    public boolean askBuildLadder(IPosition pos, INotifyTask asker)
    {
        if (this.modifier.isReadyForTask(asker))
        {
            List modList = new ArrayList();
            int id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord(), pos.getZCoord());

            if (id != Block.ladder.blockID)
            {
                if (EntityIMPigEngy.canPlaceLadderAt(this.theEntity.worldObj, pos.getXCoord(), pos.getYCoord(), pos.getZCoord()))
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord(), pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
                }
                else
                {
                    return false;
                }
            }

            id = this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 2, pos.getZCoord());

            if ((id > 0) && (Block.blocksList[id].blockMaterial.isSolid()))
            {
                if (EntityIMPigEngy.canPlaceLadderAt(this.theEntity.worldObj, pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord()))
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord(), Block.ladder.blockID, (int)(25.0F / this.buildRate)));
                }
            }

            if (modList.size() > 0)
            {
                return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
            }
        }

        return false;
    }

    public boolean askBuildBridge(IPosition pos, INotifyTask asker)
    {
        if (this.modifier.isReadyForTask(asker))
        {
            List modList = new ArrayList();

            if (this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord()) == 0)
            {
                if ((this.theEntity.avoidsBlock(this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 2, pos.getZCoord()))) || (this.theEntity.avoidsBlock(this.theEntity.worldObj.getBlockId(pos.getXCoord(), pos.getYCoord() - 3, pos.getZCoord()))))
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord(), Block.cobblestone.blockID, (int)(65.0F / this.buildRate)));
                }
                else
                {
                    modList.add(new ModifyBlockEntry(pos.getXCoord(), pos.getYCoord() - 1, pos.getZCoord(), Block.planks.blockID, (int)(45.0F / this.buildRate)));
                }

                if (modList.size() > 0)
                {
                    return this.modifier.requestTask((ModifyBlockEntry[])modList.toArray(new ModifyBlockEntry[modList.size()]), asker, null);
                }
            }
        }

        return false;
    }
}