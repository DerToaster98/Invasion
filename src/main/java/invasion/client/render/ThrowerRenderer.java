package invasion.client.render;


import invasion.Invasion;
import invasion.client.render.model.ThrowerModel;
import invasion.entity.monster.EntityIMThrower;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;


public class ThrowerRenderer extends LivingRenderer<EntityIMThrower, ThrowerModel> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(Invasion.MOD_ID + "textures/entity/throwerT1.png"),
            new ResourceLocation(Invasion.MOD_ID + "textures/entity/throwerT2.png")
    };

    public ThrowerRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new ThrowerModel(), 1.5f);
    }

    /*
    @Override
    public void render(EntityIMThrower entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

     */

    @Override
    public ResourceLocation getEntityTexture(EntityIMThrower entity) {
        return TEXTURES[entity.getTier()];
    }
}