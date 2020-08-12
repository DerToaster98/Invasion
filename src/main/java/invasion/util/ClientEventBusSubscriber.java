package invasion.util;

import invasion.Invasion;
import invasion.client.gui.NexusScreen;
import invasion.init.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Invasion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
 //TODO remove this file if not needed otherwise
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

    }
}
