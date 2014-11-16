package invmod.common.util;

import invmod.common.mod_Invasion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.IPlayerTracker;

public class IMPlayerHandler implements IPlayerTracker
{
    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        try
        {
            if (mod_Invasion.getUpdateNotifications() && !mod_Invasion.getAlreadyNotified())
            {
                mod_Invasion.setAlreadyNotified(true);
                VersionChecker.checkForUpdates((EntityPlayerMP)player);
            }
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        // TODO Auto-generated method stub
    }
}
