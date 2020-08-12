package invasion.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import invasion.Invasion;
import invasion.container.NexusContainer;
import invasion.nexus.NexusMode;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NexusScreen extends ContainerScreen<NexusContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID, "textures/gui/container/nexus.png");

    public NexusScreen(NexusContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        guiLeft=0;
        guiTop=0;
        xSize = 176;
        ySize = 166;
    }


    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //font.drawString(title.getFormattedText(),6.0f,6.0f,4210752);

        //TODO show translated versions

        font.drawString("Nexus - Level " + container.getLevel(), 46, 6, 4210752);
        font.drawString(container.getKills() + " mobs killed", 96, 60, 4210752);
        font.drawString("R: " + container.getRadius(), 142, 72, 4210752);

        if ((container.getMode() == NexusMode.MODE_1) || (container.getMode() == NexusMode.MODE_3)) {
            font.drawString("Activated!", 13, 62, 4210752);
            font.drawString("Wave " + container.getLevel(), 55, 37, 4210752);
        } else if (container.getMode() == NexusMode.MODE_2) {
            font.drawString("Power:", 56, 31, 4210752);
            font.drawString("" + container.getLevel(), 61, 44, 4210752);
        }

        if (container.isActivating() && (container.getMode() == NexusMode.MODE_0)) {
            font.drawString("Activating...", 13, 62, 4210752);
            if (container.getMode() != NexusMode.MODE_4)
                font.drawString("Are you sure?", 8, 72, 4210752);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);

        int l = container.getGenerationProgressScaled(26);
/*

        drawTexturedModalRect(j + 126, k + 28 + 26 - l, 185, 26 - l, 9, l);
        l = invasion.getCookProgressScaled(18);
        drawTexturedModalRect(j + 31, k + 51, 204, 0, l, 2);


        if ((container.getMode() == 1) || (invasion.getMode() == 3)) {
            drawTexturedModalRect(j + 19, k + 29, 176, 0, 9, 31);
            drawTexturedModalRect(j + 19, k + 19, 194, 0, 9, 9);
        } else if (container.getMode() == 2) {
            drawTexturedModalRect(j + 19, k + 29, 176, 31, 9, 31);
        }

        if (((container.getMode() == 0) || (invasion.getMode() == 2)) && (invasion.isActivating())) {
            l = container.getActivationProgressScaled(31);
            drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 31 - l, 9, l);
        } else if ((invasion.getMode() == 4) && (invasion.isActivating())) {
            l = invasion.getActivationProgressScaled(31);
            drawTexturedModalRect(j + 19, k + 29 + 31 - l, 176, 62 - l, 9, l);
        }

 */

    }
}
