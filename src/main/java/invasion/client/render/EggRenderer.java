package invasion.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invasion.Invasion;
import invasion.client.render.model.EggModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class EggRenderer extends EntityRenderer<EggEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID + "textures/entity/spider_egg.png");
    private final EggModel model = new EggModel();

    protected EggRenderer(EntityRendererManager rendererManager) {
      super(rendererManager);
      shadowSize = 0.5f;
    }

    @Override
    public void render(EggEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        IVertexBuilder vertexBuilder = bufferIn.getBuffer(model.getRenderType(getEntityTexture(entityIn)));
        model.render(matrixStackIn,vertexBuilder,packedLightIn, OverlayTexture.NO_OVERLAY,1.0f,1.0f,1.0f,1.0f);

    }

    @Override
    public ResourceLocation getEntityTexture(EggEntity eggEntity) {
        return TEXTURE;
    }

}