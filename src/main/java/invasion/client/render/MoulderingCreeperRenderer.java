package invasion.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import invasion.Invasion;
import invasion.entity.monster.MoulderingCreeperEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class MoulderingCreeperRenderer extends MobRenderer<MoulderingCreeperEntity, CreeperModel<MoulderingCreeperEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID, "textures/entity/mouldering_creeper.png");

    public MoulderingCreeperRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new CreeperModel<>(), 0.5F);
    }

    @Override
    protected void preRenderCallback(MoulderingCreeperEntity creeper, MatrixStack matrixStack, float t) {
        float intensity = creeper.getCreeperFlashIntensity(t);
        float lvt_5_1_ = 1.0F + MathHelper.sin(intensity * 100.0F) * intensity * 0.01F;
        intensity = MathHelper.clamp(intensity, 0.0F, 1.0F);
        intensity *= intensity;
        intensity *= intensity;
        float lvt_6_1_ = (1.0F + intensity * 0.4F) * lvt_5_1_;
        float lvt_7_1_ = (1.0F + intensity * 0.1F) / lvt_5_1_;
        matrixStack.scale(lvt_6_1_, lvt_7_1_, lvt_6_1_);
    }

    @Override
    protected float getOverlayProgress(MoulderingCreeperEntity creeper, float t) {
        float intensity = creeper.getCreeperFlashIntensity(t);
        return (int) (intensity * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(intensity, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(MoulderingCreeperEntity entity) {
        return TEXTURE;
    }
}