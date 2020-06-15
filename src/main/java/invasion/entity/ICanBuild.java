package invasion.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;


/**
 * In order for an entity to use {@link TerrainBuilder}, implement this interface.
 *
 * @param <T> Must extend {@link EntityIMLiving}.
 * @author DarthXenon
 * @since IM 1.2.6
 */
public interface ICanBuild<T extends EntityIMLiving> {

    /**
     * Returns the entity, which must extend {@link Entity}.
     */
    default T getEntity() {
        return (this instanceof EntityIMLiving) ? (T) this : null;
    }

    boolean canPlaceLadderAt(BlockPos pos);

}
