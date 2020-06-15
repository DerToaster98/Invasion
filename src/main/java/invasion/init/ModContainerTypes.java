package invasion.init;

import invasion.Invasion;
import invasion.container.NexusContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Invasion.MOD_ID);

    public static final RegistryObject<ContainerType<NexusContainer>> NEXUS = CONTAINER_TYPES.register("nexus", () -> IForgeContainerType.create(NexusContainer::new));
}
