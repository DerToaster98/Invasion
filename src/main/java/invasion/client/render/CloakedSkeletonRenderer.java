package invasion.client.render;

import invasion.Invasion;
import invasion.entity.monster.EntityIMSkeleton;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.util.ResourceLocation;


public class CloakedSkeletonRenderer<T extends EntityIMSkeleton> extends BipedRenderer<T, SkeletonModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Invasion.MOD_ID + "textures/entity/skeleton.png");

    public CloakedSkeletonRenderer(EntityRendererManager renderManager) {
        super(renderManager, new SkeletonModel<>(), 0.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityIMSkeleton entity) {
        return TEXTURE;
    }
}