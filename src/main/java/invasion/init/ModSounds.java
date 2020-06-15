package invasion.init;

import invasion.Invasion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Invasion.MOD_ID);

    public static final RegistryObject<SoundEvent> RUMBLE = SOUNDS.register("ambient.rumble", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "ambient.rumble")));
    public static final RegistryObject<SoundEvent> CHIME = SOUNDS.register("ambient.chime", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "ambient.chime")));
    public static final RegistryObject<SoundEvent> EGG_HATCH = SOUNDS.register("entity.egg.hatch", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "entity.egg.hatch")));
    public static final RegistryObject<SoundEvent> ZAP = SOUNDS.register("entity.bolt.zap", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "entity.bolt.zap")));
    public static final RegistryObject<SoundEvent> FIREBALL = SOUNDS.register("ambient.fireball", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "ambient.fireball")));
    public static final RegistryObject<SoundEvent> SCRAPE = SOUNDS.register("block.scrape", () -> new SoundEvent(new ResourceLocation(Invasion.MOD_ID, "block.scrape")));
}