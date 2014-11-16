package invmod.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import invmod.common.mod_Invasion;
import invmod.common.entity.EntityIMTrap;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemIMTrap extends ItemIM
{
    @SideOnly(Side.CLIENT)
    private Icon emptyIcon;

    @SideOnly(Side.CLIENT)
    private Icon riftIcon;

    @SideOnly(Side.CLIENT)
    private Icon flameIcon;
    public static final String[] trapNames = { "Empty Trap",
                                 "Rift Trap (folded)", "Flame Trap (folded)", "XYZ Trap"
                                             };

    public ItemIMTrap(int itemId)
    {
        super(itemId);
        this.maxStackSize = 64;
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(mod_Invasion.tabInvmod);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.emptyIcon = par1IconRegister.registerIcon("invmod:trapEmpty");
        this.riftIcon = par1IconRegister.registerIcon("invmod:trapPurple");
        this.flameIcon = par1IconRegister.registerIcon("invmod:trapRed");
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world,
                                      EntityPlayer entityplayer)
    {
        return itemstack;
    }

    public boolean onItemUseFirst(ItemStack itemstack,
                                  EntityPlayer entityplayer, World world, int x, int y, int z,
                                  int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return false;
        }

        if (side == 1)
        {
            EntityIMTrap trap;

            if (itemstack.getItemDamage() == 1)
            {
                trap = new EntityIMTrap(world, x + 0.5D, y + 1.0D, z + 0.5D, 1);
            }
            else
            {
                if (itemstack.getItemDamage() == 2)
                {
                    trap = new EntityIMTrap(world, x + 0.5D, y + 1.0D,
                                            z + 0.5D, 2);
                }
                else
                {
                    return false;
                }
            }

            if ((trap.isValidPlacement())
                    && (world.getEntitiesWithinAABB(EntityIMTrap.class,
                                                    trap.boundingBox).size() == 0))
            {
                world.spawnEntityInWorld(trap);

                // players in creative mode won't lose the item
                if (!entityplayer.capabilities.isCreativeMode)
                {
                    itemstack.stackSize -= 1;
                }
            }

            return true;
        }

        return false;
    }

    public String getUnlocalizedName(ItemStack itemstack)
    {
        if (itemstack.getItemDamage() < trapNames.length)
        {
            return trapNames[itemstack.getItemDamage()];
        }

        return "";
    }

    public Icon getIconFromDamage(int i)
    {
        if (i == 1)
        {
            return this.riftIcon;
        }

        if (i == 2)
        {
            return this.flameIcon;
        }

        return this.emptyIcon;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List dest)
    {
        dest.add(new ItemStack(id, 1, 0));
        dest.add(new ItemStack(id, 1, 1));
        dest.add(new ItemStack(id, 1, 2));
    }
}