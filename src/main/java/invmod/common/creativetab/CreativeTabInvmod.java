package invmod.common.creativetab;

import invmod.common.mod_Invasion;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabInvmod extends CreativeTabs
{
    public CreativeTabInvmod(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    public int getTabIconItemIndex()
    {
        return mod_Invasion.blockNexus.blockID;
    }

    public String getTranslatedTabLabel()
    {
        return "Invasion mod";
    }
}
