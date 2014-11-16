package invmod.common;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;

public class TickHandlerServer
    implements ITickHandler
{
    private int elapsed;
    private long timer;

    public void tickStart(EnumSet<TickType> type, Object...tickData)
    {
    }

    public void tickEnd(EnumSet<TickType> type, Object...tickData)
    {
        if (type.contains(TickType.SERVER))
        {
            serverTick();
        }
    }

    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    public String getLabel()
    {
        return "IM Server Tick";
    }

    protected void serverTick()
    {
        mod_Invasion.onServerTick();
    }
}
