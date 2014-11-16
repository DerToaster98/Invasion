package invmod.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import invmod.common.entity.EntityIMLiving;
import invmod.common.mod_Invasion;
import invmod.common.nexus.TileEntityNexus;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemProbe extends ItemIM
{
    @SideOnly(Side.CLIENT)
    private Icon iconAdjuster;

    @SideOnly(Side.CLIENT)
    private Icon iconProbe;
    public static final String[] probeNames = { "Nexus Adjuster", "Material Probe" };

    public ItemProbe(int itemId)
    {
        super(itemId);
        this.maxStackSize = 1;
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(mod_Invasion.tabInvmod);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconAdjuster = par1IconRegister.registerIcon("invmod:adjuster");
        this.iconProbe = par1IconRegister.registerIcon("invmod:probe");
    }

    public boolean isFull3D()
    {
        return true;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        return itemstack;
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return false;
        }

        int id = world.getBlockId(x, y, z);

        if (id == mod_Invasion.blockNexus.blockID)
        {
            TileEntityNexus nexus = (TileEntityNexus)world.getBlockTileEntity(x, y, z);
            int newRange = nexus.getSpawnRadius();

            //check if the player wants to increase or decrease the range
            if (entityplayer.isSneaking())
            {
                newRange -= 8;

                if (newRange < 32)
                {
                    newRange = 128;
                }
            }
            else
            {
                newRange += 8;

                if (newRange > 128)
                {
                    newRange = 32;
                }
            }

            nexus.setSpawnRadius(newRange);
            mod_Invasion.sendMessageToPlayer(entityplayer.username, "Nexus range changed to: " + nexus.getSpawnRadius());
            return true;
        }

        if (itemstack.getItemDamage() == 1)
        {
            float blockStrength = EntityIMLiving.getBlockStrength(x, y, z, id, world);
            mod_Invasion.sendMessageToPlayer(entityplayer.username, "Block strength: " + (int)((blockStrength + 0.005D) * 100.0D) / 100.0D);
            return true;
        }

        return false;
    }

    public String getUnlocalizedName(ItemStack itemstack)
    {
        if (itemstack.getItemDamage() < probeNames.length)
        {
            return probeNames[itemstack.getItemDamage()];
        }

        return "";
    }

    public Icon getIconFromDamage(int i)
    {
        if (i == 1)
        {
            return this.iconProbe;
        }

        return this.iconAdjuster;
    }

    public int getItemEnchantability()
    {
        return 14;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List dest)
    {
        dest.add(new ItemStack(id, 1, 0));
        dest.add(new ItemStack(id, 1, 1));
    }
}