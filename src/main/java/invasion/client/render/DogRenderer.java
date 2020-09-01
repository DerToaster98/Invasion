package invasion.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import invasion.Invasion;
import invasion.entity.ally.DogEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


//TODO maybe extend WolfRenderer directly?
@OnlyIn(Dist.CLIENT)
public class DogRenderer<T extends DogEntity> extends MobRenderer<T, WolfModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID + "textures/entity/wolf_tame_nexus.png");

    DogRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new WolfModel<>(), 0.8f);
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        //scale the wolf up a little
        matrixStackIn.push();
        matrixStackIn.scale(1.3f, 1.1f, 1.3f);

        // The following code is shamelessly stolen from WolfRenderer
        if (entityIn.isWolfWet()) {
            float f = entityIn.getBrightness() * entityIn.getShadingWhileWet(partialTicks);
            this.entityModel.setTint(f, f, f);
        }

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entityIn.isWolfWet()) {
            this.entityModel.setTint(1.0F, 1.0F, 1.0F);
        }

        matrixStackIn.pop();

    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TEXTURE;
    }
}