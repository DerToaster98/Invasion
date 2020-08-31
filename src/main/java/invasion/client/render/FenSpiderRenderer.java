package invasion.client.render;

import invasion.entity.monster.SpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FenSpiderRenderer<T extends SpiderEntity> extends MobRenderer<T, SpiderModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft","textures/entity/spider/spider.png");

    public FenSpiderRenderer(EntityRendererManager renderManagerIn) {
       super(renderManagerIn,new SpiderModel<>(),0.8f);
       addLayer(new SpiderEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TEXTURE;
    }
}
