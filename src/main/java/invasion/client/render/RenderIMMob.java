/*
package invasion.client.render;


import invasion.entity.monster.InvadingEntity;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;


public abstract class InvasionEntityRenderer<E extends InvadingEntity, M extends EntityModel<E>> extends LivingRenderer<E, M>
{
	public RenderIMMob(RenderManager renderManager, ModelBase model, float shadowWidth)
	{
		super(renderManager, model, shadowWidth);
	}

	public void doRenderLiving(T entity, double renderX, double renderY, double renderZ, float interpYaw, float parTick)
	{
		super.doRender(entity, renderX, renderY, renderZ, interpYaw, parTick);
		if (entity.shouldRenderLabel())
		{
			String s = entity.getRenderLabel();
			// was this something important?
			// String[] labels = s.split("\n");
			// for (int i = 0; i < labels.length; i++)
			// {
			// renderLivingLabel(entity, labels[i], renderX, renderY +
			// (labels.length - 1 - i) * 0.22D, renderZ, 32);
			// }
		}
	}
}

 */