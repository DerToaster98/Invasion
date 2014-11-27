package invmod.common.nexus;

import invmod.common.mod_Invasion;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNexus extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private Icon sideOn;

    @SideOnly(Side.CLIENT)
    private Icon sideOff;

    @SideOnly(Side.CLIENT)
    private Icon topOn;

    @SideOnly(Side.CLIENT)
    private Icon topOff;

    @SideOnly(Side.CLIENT)
    private Icon botTexture;

    public BlockNexus(int id)
    {
        super(id, Material.rock);
        this.setCreativeTab(mod_Invasion.tabInvmod);
        this.setResistance(6000000.0F);
        this.setHardness(3.0F);
        this.setStepSound(Block.soundGlassFootstep);
        this.setUnlocalizedName("blockNexus");
    }

    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.sideOn = iconRegister.registerIcon("invmod:nexusSideOn");
        this.sideOff = iconRegister.registerIcon("invmod:nexusSideOff");
        this.topOn = iconRegister.registerIcon("invmod:nexusTopOn");
        this.topOff = iconRegister.registerIcon("invmod:nexusTopOff");
        this.botTexture = iconRegister.registerIcon("obsidian");
    }

    @Override
    public Icon getIcon(int side, int meta)
    {
        if ((meta & 0x4) == 0)
        {
            if (side == 1)
            {
                return this.topOff;
            }

            return side != 0 ? this.sideOff : this.botTexture;
        }

        if (side == 1)
        {
            return this.topOn;
        }

        return side != 0 ? this.sideOn : this.botTexture;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        ItemStack item = entityPlayer.inventory.getCurrentItem();
        int itemId = item != null ? item.itemID : 0;

        if (world.isRemote)
        {
            return true;
        }

        if ((itemId != mod_Invasion.itemProbe.itemID) && ((!mod_Invasion.isDebug()) || (itemId != mod_Invasion.itemDebugWand.itemID)))
        {
            TileEntityNexus tileEntityNexus = (TileEntityNexus) world.getBlockTileEntity(x, y, z);

            if (tileEntityNexus != null)
            {
                mod_Invasion.setNexusClicked(tileEntityNexus);
                entityPlayer.openGui(mod_Invasion.getLoadedInstance(), mod_Invasion.getGuiIdNexus(), world, x, y, z);
            }

            return true;
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityNexus(world);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int numberOfParticles;

        if ((meta & 0x4) == 0)
        {
            numberOfParticles = 0;
        }
        else
        {
            numberOfParticles = 6;
        }

        for (int i = 0; i < numberOfParticles; i++)
        {
            double y1 = y + random.nextFloat();
            double y2 = (random.nextFloat() - 0.5D) * 0.5D;
            int direction = random.nextInt(2) * 2 - 1;
            double x2;
            double x1;
            double z1;
            double z2;

            if (random.nextInt(2) == 0)
            {
                z1 = z + 0.5D + 0.25D * direction;
                z2 = random.nextFloat() * 2.0F * direction;
                x1 = x + random.nextFloat();
                x2 = (random.nextFloat() - 0.5D) * 0.5D;
            }
            else
            {
                x1 = x + 0.5D + 0.25D * direction;
                x2 = random.nextFloat() * 2.0F * direction;
                z1 = z + random.nextFloat();
                z2 = (random.nextFloat() - 0.5D) * 0.5D;
            }

            world.spawnParticle("portal", x1, y1, z1, x2, y2, z2);
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z){
	
		TileEntityNexus tile=(TileEntityNexus)world.getBlockTileEntity(x, y, z);

		if(tile.isActive()){
			return -1.0F;
		}else{
			 return ForgeHooks.blockStrength(this, player, world, x, y, z);
		}
		
	}
 
}