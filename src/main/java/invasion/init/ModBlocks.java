package invasion.init;

import invasion.Invasion;
import invasion.block.NexusBlock;
import invasion.block.TrapBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Invasion.MOD_ID);

    public static final RegistryObject<Block> NEXUS = BLOCKS.register("nexus", NexusBlock::new);
    public static final RegistryObject<Block> TRAP = BLOCKS.register("trap", TrapBlock::new);
    public static final RegistryObject<Block> STONER = BLOCKS.register("stoner",()->new Block(Block.Properties.from(Blocks.STONE)));
}
