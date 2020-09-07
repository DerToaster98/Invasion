package invasion.entity.ally;

import invasion.nexus.Nexus;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public interface IConvertibleEntity<FROM extends Entity> {

    void acquiredByNexus(@Nullable Nexus nexus);
}
