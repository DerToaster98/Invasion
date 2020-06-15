package invasion.client.render;

import invasion.Reference;
import invasion.client.render.model.ModelVulture;
import invasion.entity.monster.EntityIMBird;
import invasion.util.MathUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;


public class RenderGiantBird extends RenderIMMob<EntityIMBird> {
    private static final ResourceLocation texture = new ResourceLocation(Reference.MODID + ":textures/vulture.png");
    private final ModelVulture modelBird;

    public RenderGiantBird(RenderManager renderManager) {
        super(renderManager, new ModelVulture(), 0.4F);
        this.modelBird = ((ModelVulture) this.mainModel);
    }

    @Override
    public void doRenderLiving(EntityIMBird entityBird, double renderX, double renderY, double renderZ, float interpYaw, float partialTick) {
        if (entityBird.hasFlyingDebug()) {
            this.renderNavigationVector(entityBird, renderX, renderY, renderZ);
        }

        float roll = MathUtil.interpRotationDeg(entityBird.getPrevRotationRoll(), entityBird.getRotationRoll(), partialTick);
        float headYaw = MathUtil.interpRotationDeg(entityBird.getPrevRotationYawHeadIM(), entityBird.getRotationYawHeadIM(), partialTick);
        float headPitch = MathUtil.interpRotationDeg(entityBird.getPrevRotationPitchHead(), entityBird.getRotationPitchHead(), partialTick);

        this.modelBird.resetSkeleton();
        this.modelBird.setFlyingAnimations(entityBird.getWingAnimationState(), entityBird.getLegAnimationState(), entityBird.getBeakAnimationState(), roll, headYaw, headPitch, partialTick);

        super.doRenderLiving(entityBird, renderX, renderY, renderZ, interpYaw, partialTick);
    }

    protected void renderModel(EntityLiving par1EntityLiving, float par2, float par3, float par4, float par5, float par6, float par7) {
        this.modelBird.setRotationAngles(par2, par3, par4, par5, par6, par7, par1EntityLiving);
        //super.renderModel(par1EntityLiving, par2, par3, par4, par5, par6, par7);
    }

    private void renderNavigationVector(EntityIMBird entityBird, double entityRenderOffsetX, double entityRenderOffsetY, double entityRenderOffsetZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glPushMatrix();

        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);

        Vec3d target = entityBird.getFlyTarget();
        double drawWidth = 0.1D;

        //tessellator.getWorldRenderer().startDrawing(5);
        //tessellator.getWorldRenderer()/*setColorRGBA_F*/.func_178960_a(1.0F, 0.0F, 0.0F, 1.0F);
        buffer.begin(5, new VertexFormat());
        buffer.color(1f, 0f, 0f, 1f);
        for (int j = 0; j < 5; j++) {
            double xOffset = drawWidth;
            double zOffset = drawWidth;
            if ((j == 1) || (j == 2)) {
                xOffset += drawWidth * 2.0D;
            }
            if ((j == 2) || (j == 3)) {
                zOffset += drawWidth * 2.0D;
            }
            //tessellator.getWorldRenderer().addVertex(entityRenderOffsetX - entityBird.width / 2.0F + xOffset, entityRenderOffsetY + entityBird.height / 2.0F, entityRenderOffsetZ - entityBird.width / 2.0F + zOffset);
            //tessellator.getWorldRenderer().addVertex(target.xCoord + xOffset - renderManager.viewerPosX, target.yCoord - renderManager.viewerPosY, target.zCoord + zOffset - renderManager.viewerPosZ);
            buffer.addVertexData(new int[]{
                    (int) (entityRenderOffsetX - entityBird.width / 2.0F + xOffset),
                    (int) (entityRenderOffsetY + entityBird.height / 2.0F),
                    (int) (entityRenderOffsetZ - entityBird.width / 2.0F + zOffset)
            });
            buffer.addVertexData(new int[]{
                    (int) (target.x + xOffset - this.renderManager.viewerPosX),
                    (int) (target.y - this.renderManager.viewerPosY),
                    (int) (target.z + zOffset - this.renderManager.viewerPosZ)
            });
        }
        tessellator.draw();

        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityIMBird entity) {
        return texture;
    }
}