package invasion.client.render;

import invasion.Reference;
import invasion.client.render.layer.LayerIMSpiderEyes;
import invasion.entity.monster.EntityIMSpider;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class RenderSpiderIM extends RenderLiving<EntityIMSpider> {
    private static final ResourceLocation t_eyes = new ResourceLocation("textures/entity/spider_eyes.png");
    private static final ResourceLocation t_spider = new ResourceLocation("textures/entity/spider/spider.png");
    private static final ResourceLocation t_jumping = new ResourceLocation(Reference.MODID + ":textures/spiderT2.png");
    private static final ResourceLocation t_mother = new ResourceLocation(Reference.MODID + ":textures/spiderT2b.png");

    public RenderSpiderIM(RenderManager renderManager) {
        super(renderManager, new ModelSpider(), 1.0F);
        this.addLayer(new LayerIMSpiderEyes(this));
        // setRenderPassModel(new ModelSpider());
    }

    @Override
    protected float getDeathMaxRotation(EntityIMSpider entityspider) {
        return 180.0F;
    }

	/*protected int setSpiderEyeBrightness(EntityIMSpider entityspider, int i, float f) {
		if (i != 0) {
			return -1;
		}
	
		bindTexture(t_eyes);
		float f1 = 1.0F;
		GL11.glEnable(3042);
		GL11.glDisable(3008);
		GL11.glBlendFunc(1, 1);
	
		if (entityspider.isInvisible()) {
			GL11.glDepthMask(false);
		} else {
			GL11.glDepthMask(true);
		}
	
		char c0 = 61680;
		int j = c0 % 65536;
		int k = c0 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, f1);
		return 1;
	}*/

    @Override
    protected void preRenderCallback(EntityIMSpider entityliving, float f) {
        float f1 = entityliving.spiderScaleAmount();
        this.shadowSize = f1;
        GL11.glScalef(f1, f1, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityIMSpider entity) {
        switch (entity.getTextureId()) {
            case 0:
                return t_spider;
            case 1:
                return t_jumping;
            case 2:
                return t_mother;
        }
        return t_spider;
    }
}