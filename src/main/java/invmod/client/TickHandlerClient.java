package invmod.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import invmod.common.mod_Invasion;
import java.util.EnumSet;

public class TickHandlerClient
    implements ITickHandler
{
    public void tickStart(EnumSet<TickType> type, Object...tickData)
    {
    }

    public void tickEnd(EnumSet<TickType> type, Object...tickData)
    {
        if (type.contains(TickType.CLIENT))
        {
            clientTick();
        }
    }

    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    public String getLabel()
    {
        return "IM Client Tick";
    }

    protected void clientTick()
    {
        mod_Invasion.onClientTick();
    }
}
