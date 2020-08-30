package invasion.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class BlackArrowEntity extends ArrowEntity {
    public BlackArrowEntity(EntityType<? extends ArrowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public BlackArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public BlackArrowEntity(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

}
