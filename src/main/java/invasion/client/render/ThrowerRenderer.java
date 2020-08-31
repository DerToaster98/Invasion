package invasion.client.render;


import invasion.Invasion;
import invasion.client.render.model.ThrowerModel;
import invasion.entity.monster.EntityIMThrower;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrowerRenderer<T extends EntityIMThrower> extends LivingRenderer<T, ThrowerModel<T>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(Invasion.MOD_ID + "textures/entity/throwerT1.png"),
            new ResourceLocation(Invasion.MOD_ID + "textures/entity/throwerT2.png")
    };

    public ThrowerRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new ThrowerModel<>(), 1.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityIMThrower entity) {
        return TEXTURES[entity.getTier()];
    }
}