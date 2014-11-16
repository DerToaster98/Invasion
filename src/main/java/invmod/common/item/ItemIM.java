package invmod.common.item;

import invmod.common.mod_Invasion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;

public class ItemIM extends Item
{
    public ItemIM(int id)
    {
        super(id);
        this.setCreativeTab(mod_Invasion.tabInvmod);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("invmod:" + getUnlocalizedName().substring(5));
    }
}