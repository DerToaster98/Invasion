package invasion.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import invasion.Invasion;
import invasion.container.NexusContainer;
import invasion.tileentity.NexusTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NexusScreen extends ContainerScreen<NexusContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID, "textures/gui/container/nexus.png");
    private NexusTileEntity tileEntity;

    public NexusScreen(NexusContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //TODO show translated versions

        this.font.drawString("Nexus - Level " + this.tileEntity.getNexusLevel(), 46, 6, 4210752);
        this.font.drawString(this.tileEntity.getNexusKills() + " mobs killed", 96, 60, 4210752);
        this.font.drawString("R: " + this.tileEntity.getSpawnRadius(), 142, 72, 4210752);

        if ((this.tileEntity.getMode() == 1) || (this.tileEntity.getMode() == 3)) {
            this.font.drawString("Activated!", 13, 62, 4210752);
            this.font.drawString("Wave " + this.tileEntity.getCurrentWave(), 55, 37, 4210752);
        } else if (this.tileEntity.getMode() == 2) {
            this.font.drawString("Power:", 56, 31, 4210752);
            this.font.drawString("" + this.tileEntity.getNexusPowerLevel(), 61, 44, 4210752);
        }

        if ((this.tileEntity.isActivating()) && (this.tileEntity.getMode() == 0)) {
            this.font.drawString("Activating...", 13, 62, 4210752);
            if (this.tileEntity.getMode() != 4)
                this.font.drawString("Are you sure?", 8, 72, 4210752);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(x, y, 0, 0, this.xSize, this.ySize);

        int l = this.container.getGenerationProgressScaled(26);
        this.drawTexturedModalRect(j + 126, k + 28 + 26 - l, 185, 26 - l, 9, l);
        l = this.invasion.getCookProgressScaled(18);
        this.drawTexturedModalRect(j + 31, k + 51, 204, 0, l, 2);


        if ((this.container.getMode() == 1) || (this.invasion.getMode() == 3)) {
            this.drawTexturedModalRect(j + 19, k + 29, 176, 0, 9, 31);
            this.drawTexturedModalRect(j + 19, k + 19, 194, 0, 9, 9);
        } else if (this.container.getMode() == 2) {
            this.drawTexturedModalRect(j + 19, k + 29, 176, 31, 9, 31);
        }

        if (((this.container.getMode() == 0) || (this.invasion.getMode() == 2)) && (this.invasion.isActivating())) {
            l = this.container.getActivationProgressScaled(31);
            this.drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 31 - l, 9, l);
        } else if ((this.invasion.getMode() == 4) && (this.invasion.isActivating())) {
            l = this.invasion.getActivationProgressScaled(31);
            this.drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 62 - l, 9, l);
        }

    }
}
