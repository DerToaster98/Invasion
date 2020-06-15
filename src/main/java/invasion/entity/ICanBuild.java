package invasion.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;


/**
 * In order for an entity to use {@link TerrainBuilder}, implement this interface.
 * @author DarthXenon
 * @param <T> Must extend {@link EntityIMLiving}.
 * @since IM 1.2.6
 */
public interface ICanBuild<T extends EntityIMLiving>
{

	/**
	 * Returns the entity, which must extend {@link Entity}.
	 */
	public default T getEntity()
	{
		return (this instanceof EntityIMLiving) ? (T)this : null;
	}

	public boolean canPlaceLadderAt(BlockPos pos);

}
