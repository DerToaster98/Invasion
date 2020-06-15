package invasion.client.render;

import invasion.Reference;
import invasion.client.render.layer.LayerSkeletonCloak;
import invasion.client.render.model.ModelIMSkeleton;
import invasion.entity.monster.EntityIMSkeleton;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;


public class RenderIMSkeleton extends RenderBiped<EntityIMSkeleton>
{

	private static final ResourceLocation texture = new ResourceLocation(Reference.MODID + ":textures/skeleton.png");

	public RenderIMSkeleton(RenderManager renderManager)
	{
		this(renderManager, new ModelIMSkeleton(), 0.5f, 1f);
	}

	public RenderIMSkeleton(RenderManager renderManager, ModelBiped model, float shadowSize)
	{
		this(renderManager, model, shadowSize, 1f);
	}

	public RenderIMSkeleton(RenderManager renderManager, ModelBiped model, float shadowSize, float scale)
	{
		super(renderManager, model, shadowSize, scale);
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerBipedArmor(this)
		{
			@Override
			protected void initArmor()
			{
				this.modelLeggings = new ModelIMSkeleton(0.5f, true);
				this.modelArmor = new ModelIMSkeleton(1f, true);
			}
		});
		this.addLayer(new LayerSkeletonCloak(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityIMSkeleton entity)
	{
		return texture;
	}
}