package invasion.init;

import invasion.Invasion;
import invasion.entity.projectile.BlackArrowEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Invasion.MOD_ID);

    public static final RegistryObject<EntityType<BlackArrowEntity>> BLACK_ARROW = ENTITY_TYPES.register("black_arrow", () -> EntityType.Builder.<BlackArrowEntity>create(BlackArrowEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(new ResourceLocation(Invasion.MOD_ID, "black_arrow").toString()));
    public static final RegistryObject<EntityType<BlackArrowEntity>> BOULDER = ENTITY_TYPES.register("boulder", () -> EntityType.Builder.<BlackArrowEntity>create(BlackArrowEntity::new, EntityClassification.MISC).size(0.5f, 0.5f).build(new ResourceLocation(Invasion.MOD_ID, "boulder").toString()));

}
