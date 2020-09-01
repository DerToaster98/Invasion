package invasion.client.render;

import invasion.Invasion;
import invasion.client.render.model.ImpModel;
import invasion.entity.monster.ImpEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImpRenderer<T extends ImpEntity> extends LivingRenderer<T, ImpModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID + "textures/entity/imp.png");

    public ImpRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new ImpModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(ImpEntity entity) {
        return TEXTURE;
    }
}