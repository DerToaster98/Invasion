package invasion.entity.ally;

import invasion.nexus.Nexus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;

public interface IConvertibleEntity<FROM extends Entity> {
    void from(FROM fromEntity);

    default IConvertibleEntity<FROM> convert(FROM entity) {
        return convert(entity, null);
    }

    default IConvertibleEntity<FROM> convert(FROM entity, @Nullable Nexus nexus) {
        IConvertibleEntity<FROM> to = new FROM(EntityType.AREA_EFFECT_CLOUD);
        to.from(entity);
        return to;
    }
}
