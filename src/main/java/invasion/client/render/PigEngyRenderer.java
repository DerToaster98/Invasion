package invasion.client.render;

import invasion.Invasion;
import invasion.entity.monster.PigEngyEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PigEngyRenderer<T extends PigEngyEntity> extends BipedRenderer<T, BipedModel<T>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(Invasion.MOD_ID, "textures/entity/pigengT1.png"),
            new ResourceLocation(Invasion.MOD_ID, "textures/entity/pigengT2.png")
    };

    public PigEngyRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new BipedModel<>(0), 0.5f); // Idk what the 0 parameter does
    }

    /* TODO I don't know what this does, it probably can be deleted
    public PigEngyRenderer(EntityRendererManager renderManager) {
        this(renderManager, new ModelBiped() {
            @Override
            public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
                super.setRotationAngles(limbSwing / 3f, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            }
        }, 0.5F, 1f);
        this.addLayer(new LayerHeldItem(this));
    }

   */
    @Override
    public ResourceLocation getEntityTexture(PigEngyEntity entity) {
        return TEXTURES[entity.getTier()];
    }
}