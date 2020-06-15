package invasion.init;

import invasion.Invasion;
import invasion.block.NexusBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Invasion.MOD_ID);

    public static final RegistryObject<Block> NEXUS = BLOCKS.register("nexus", NexusBlock::new);
}
