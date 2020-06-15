package invasion.init;

import invasion.Invasion;
import invasion.tileentity.NexusTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Invasion.MOD_ID);

    public static final RegistryObject<TileEntityType<NexusTileEntity>> NEXUS = TILE_ENTITY_TYPES.register("nexus", () -> TileEntityType.Builder.create(NexusTileEntity::new, ModBlocks.NEXUS.get()).build(null));
}