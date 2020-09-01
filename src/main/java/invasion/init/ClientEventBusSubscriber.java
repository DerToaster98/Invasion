package invasion.init;

import invasion.Invasion;
import invasion.client.gui.NexusScreen;
import invasion.client.render.FenSpiderRenderer;
import invasion.client.render.ImpRenderer;
import invasion.client.render.MoulderingCreeperRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Invasion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

        // register the Nexus screen
        ScreenManager.registerFactory(ModContainerTypes.NEXUS.get(), NexusScreen::new);

        // register the color of the trap
        event.getMinecraftSupplier().get().getBlockColors().register((IBlockColor) ModBlocks.TRAP.get());

        // register entity renderers
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FEN_SPIDER.get(), FenSpiderRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.IMP.get(), ImpRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.MOULDERING_CREEPER.get(), MoulderingCreeperRenderer::new);
    }
}
