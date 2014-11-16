package invmod.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import invmod.common.mod_Invasion;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRemnants extends ItemIM
{
    public ItemRemnants(int id)
    {
        super(id);
        setMaxDamage(0);
        this.setUnlocalizedName("smallRemnants");
        this.setMaxStackSize(64);
    }
}