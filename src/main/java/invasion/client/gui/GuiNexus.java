/*
package invasion.client.gui;


import invasion.nexus.Nexus;
import org.lwjgl.opengl.GL11;
import invasion.Reference;
import invasion.inventory.container.ContainerNexus;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;


public class GuiNexus extends GuiContainer
{
	private static final ResourceLocation background = new ResourceLocation(Reference.MODID + ":textures/nexus.png");
	private Nexus nexus;

	public GuiNexus(InventoryPlayer inventoryplayer, Nexus tileentityInvasion)
	{
		super(new ContainerNexus(inventoryplayer, tileentityInvasion));
		this.nexus = tileentityInvasion;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		this.fontRenderer.drawString("Nexus - Level " + this.nexus.getNexusLevel(), 46, 6, 4210752);
		this.fontRenderer.drawString(this.nexus.getNexusKills() + " mobs killed", 96, 60, 4210752);
		this.fontRenderer.drawString("R: " + this.nexus.getSpawnRadius(), 142, 72, 4210752);

		if ((this.nexus.getMode() == 1) || (this.nexus.getMode() == 3))
		{
			this.fontRenderer.drawString("Activated!", 13, 62, 4210752);
			this.fontRenderer.drawString("Wave " + this.nexus.getCurrentWave(), 55, 37, 4210752);
		}
		else if (this.nexus.getMode() == 2)
		{
			this.fontRenderer.drawString("Power:", 56, 31, 4210752);
			this.fontRenderer.drawString("" + this.nexus.getNexusPowerLevel(), 61, 44, 4210752);
		}

		if ((this.nexus.isActivating()) && (this.nexus.getMode() == 0))
		{
			this.fontRenderer.drawString("Activating...", 13, 62, 4210752);
			if (this.nexus.getMode() != 4)
				this.fontRenderer.drawString("Are you sure?", 8, 72, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int un1, int un2)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);

		int l = this.nexus.getGenerationProgressScaled(26);
		this.drawTexturedModalRect(j + 126, k + 28 + 26 - l, 185, 26 - l, 9, l);
		l = this.nexus.getCookProgressScaled(18);
		this.drawTexturedModalRect(j + 31, k + 51, 204, 0, l, 2);


		if ((this.nexus.getMode() == 1) || (this.nexus.getMode() == 3))
		{
			this.drawTexturedModalRect(j + 19, k + 29, 176, 0, 9, 31);
			this.drawTexturedModalRect(j + 19, k + 19, 194, 0, 9, 9);
		}
		else if (this.nexus.getMode() == 2)
		{
			this.drawTexturedModalRect(j + 19, k + 29, 176, 31, 9, 31);
		}

		if (((this.nexus.getMode() == 0) || (this.nexus.getMode() == 2)) && (this.nexus.isActivating()))
		{
			l = this.nexus.getActivationProgressScaled(31);
			this.drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 31 - l, 9, l);
		}
		else if ((this.nexus.getMode() == 4) && (this.nexus.isActivating()))
		{
			l = this.nexus.getActivationProgressScaled(31);
			this.drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 62 - l, 9, l);
		}
	}
}

 */