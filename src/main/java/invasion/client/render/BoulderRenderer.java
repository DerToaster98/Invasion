package invasion.client.render;

import invasion.Invasion;
import invasion.client.render.model.BoulderModel;
import invasion.entity.projectile.BoulderEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class BoulderRenderer extends EntityRenderer<BoulderEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID, "textures/entity/boulder.png");
    private final BoulderModel MODEL = new BoulderModel();

    public BoulderRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(BoulderEntity entityBoulder, double d, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        GL11.glEnable(32826);
        GL11.glScalef(2.2F, 2.2F, 2.2F);
        this.bindEntityTexture(entityBoulder);
        float spin = entityBoulder.getFlightTime() % 20 / 20.0F;
        this.boulderModel.render(entityBoulder, spin, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    @Override
    public ResourceLocation getEntityTexture(BoulderEntity entity) {
        return TEXTURE;
    }
}