package invmod.common.entity;

import invmod.common.util.IPosition;

public class ModifyBlockEntry
    implements IPosition
{
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private int oldBlockId;
    private int newBlockId;
    private int newBlockMeta;
    private int cost;

    public ModifyBlockEntry(int x, int y, int z, int newBlockId)
    {
        this(x, y, z, newBlockId, 0, 0, -1);
    }

    public ModifyBlockEntry(int x, int y, int z, int newBlockId, int cost)
    {
        this(x, y, z, newBlockId, cost, 0, -1);
    }

    public ModifyBlockEntry(int x, int y, int z, int newBlockId, int cost, int newBlockMeta, int oldBlockId)
    {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.newBlockId = newBlockId;
        this.cost = cost;
        this.newBlockMeta = newBlockMeta;
        this.oldBlockId = oldBlockId;
    }

    public int getXCoord()
    {
        return this.xCoord;
    }

    public int getYCoord()
    {
        return this.yCoord;
    }

    public int getZCoord()
    {
        return this.zCoord;
    }

    public int getNewBlockId()
    {
        return this.newBlockId;
    }

    public int getNewBlockMeta()
    {
        return this.newBlockMeta;
    }

    public int getCost()
    {
        return this.cost;
    }

    public int getOldBlockId()
    {
        return this.oldBlockId;
    }

    public void setOldBlockId(int id)
    {
        this.oldBlockId = id;
    }
}