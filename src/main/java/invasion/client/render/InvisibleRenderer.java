package invasion.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InvisibleRenderer<E extends Entity> extends EntityRenderer<E> {
    public InvisibleRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(E entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

    }

    @Override
    public ResourceLocation getEntityTexture(E entity) {
        return null;
    }
}