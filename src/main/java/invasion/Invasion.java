package invasion;

import invasion.client.gui.NexusScreen;
import invasion.init.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;


@Mod(value = Invasion.MOD_ID)
@EventBusSubscriber(modid = Invasion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Invasion {
    public static final String MOD_ID = "invasion";
    public static final Logger logger = LogManager.getLogger();
    public static final String[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS = {
            "zombie_t1_any", "zombie_t2_any_basic", "zombie_t2_plain", "zombie_t2_tar",
            "zombie_t2_pigman", "zombie_t3_any", "zombiePigman_t1_any", "zombiePigman_t2_any", "zombiePigman_t3_any", "spider_t1_any", "spider_t2_any", "pigengy_t1_any", "skeleton_t1_any", "thrower_t1", "thrower_t2", "creeper_t1_basic", "imp_t1"};
    public static final float[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS = {
            1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};

    public static HashMap<String, Long> deathList = new HashMap();
    public static HashMap<String, Integer> mobHealthNightspawn = new HashMap();
    public static HashMap<String, Integer> mobHealthInvasion = new HashMap();

    public static Invasion instance;

   //RM private static final MobBuilder defaultMobBuilder = new MobBuilder();
    // RM private static ISelect<IEntityIMPattern> nightSpawnPool1;

    public Invasion() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        ModSounds.SOUNDS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        instance = this;

    }
    

    public static void addToDeathList(String username, long timeStamp) {
        deathList.put(username, Long.valueOf(timeStamp));
    }

    //RM
     /*
    public static Entity[] getNightMobSpawns1(World world) {
        ISelect mobPool = getMobSpawnPool();
        int numberOfMobs = world.rand.nextInt(Config.NIGHTSPAWNS_MOB_MAX_GROUPSIZE) + 1;
        Entity[] entities = new Entity[numberOfMobs];
        for (int i = 0; i < numberOfMobs; i++) {
            InvadingEntity mob = getMobBuilder().createMobFromConstruct(((IEntityIMPattern) mobPool.selectNext()).generateEntityConstruct(), world, null);
            mob.setEntityIndependent();
            //also set in entityLiving constructor, is needed for ai to function properly, I believe
            // TODO
            //mob.setAggroRange(Config.NIGHTSPAWNS_MOB_SIGHTRANGE);
            //mob.setSenseRange(Config.NIGHTSPAWNS_MOB_SENSERANGE);
            mob.setBurnsInDay(Config.NIGHTSPAWNS_MOB_BURN_DURING_DAY);
            // TODO   entities[i] = mob;
        }
        return entities;
    }

    public static MobBuilder getMobBuilder() {
        return defaultMobBuilder;
    }
*/
    /*

    @EventHandler
    public void preInit(FML event) {
        this.nightSpawnConfig();
        this.loadHealthConfig();
        this.loadCreativeTabs();
        BlocksAndItems.loadBlocks();
        BlocksAndItems.loadItems();
        SoundHandler.init();
        proxy.registerEntityRenderers();
    }

     */

    /*
    @EventHandler
    public void load( event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

        // Register to receive subscribed events
        //FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        FMLInterModComms.sendMessage("Waila", "register", "invmod.common.util.IMWailaProvider.callbackRegister");
        BlocksAndItems.registerItems(event);
        this.loadEntities();
        CraftingAndSmelting.addRecipes();

        if (Config.NIGHTSPAWNS_ENABLED) {
            //BiomeGenBase[] allBiomes =BiomeGenBase.getBiomeGenArray();
            Set<ResourceLocation> biomeRegistryKeys = Biome.REGISTRY.getKeys();
            Biome[] allBiomes = new Biome[biomeRegistryKeys.size()];
            int i = 0;
            for (ResourceLocation key : biomeRegistryKeys) {
                allBiomes[i] = Biome.REGISTRY.getObject(key);
                i++;
            }
            EntityRegistry.addSpawn(EntityIMSpawnProxy.class, Config.NIGHTSPAWNS_MOB_SPAWNCHANCE, 1, 1, EnumCreatureType.MONSTER, allBiomes);
        }

        if (Config.MOB_LIMIT_OVERRIDE != 70) {
            try {
                Class c = EnumCreatureType.class;
                Object[] consts = c.getEnumConstants();
                Class sub = consts[0].getClass();
                Field field = sub.getDeclaredField("maxNumberOfCreature");
                field.setAccessible(true);
                field.set(EnumCreatureType.MONSTER, Integer.valueOf(Config.MOB_LIMIT_OVERRIDE));
            } catch (Exception e) {
                ModLogger.logFatal(e.getMessage());
            }
        }

    }

     */
    /*

    @SubscribeEvent
    public void postInitialise(FMLPostInitializationEvent evt) {

    }

     */

    //TODO
    /*
    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        if ((commandManager instanceof CommandHandler))
            ((CommandHandler) commandManager).registerCommand(new InvasionCommand());
    }

    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            if (Config.UPDATE_MESSAGES) VersionChecker.checkForUpdates((EntityPlayerMP) event.player);
        } catch (Exception e) {
        }
    }

     */

    //RM public static ISelect<IEntityIMPattern> getMobSpawnPool() {
    //RM    return nightSpawnPool1;
    //RM }

    /*
    //load Creativetab
    protected void loadCreativeTabs() {
        tabInvmod = new CreativeTabInvmod();
    }
    */

/*

    //Load Entities
    protected void loadEntities() {

        //Animations and rendering
        proxy.loadAnimations();
        //proxy.registerEntityRenderers();

        //Register Entities
        EntityRegistry.registerModEntity(EntityIMZombie.class, "IMZombie", 5, this, 128, 1, true, 0x6B753F, 0x281B0A);
        EntityRegistry.registerModEntity(EntityIMSkeleton.class, "IMSkeleton", 6, this, 128, 1, true, 0x9B9B9B, 0x797979);
        EntityRegistry.registerModEntity(EntityIMSpider.class, "IMSpider", 7, this, 128, 1, true, 0x504A3E, 0xA4121C);
        EntityRegistry.registerModEntity(EntityIMPigEngy.class, "IMPigEngy", 8, this, 128, 1, true, 0xEC9695, 0x420000);
        EntityRegistry.registerModEntity(EntityIMWolf.class, "IMWolf", 9, this, 128, 1, true, 0x99CCFF, 0xE6F2FF);
        EntityRegistry.registerModEntity(EntityIMEgg.class, "IMEgg", 10, this, 128, 1, true, 0xD9D9D9, 0x4D4D4D);
        EntityRegistry.registerModEntity(EntityIMCreeper.class, "IMCreeper", 11, this, 128, 1, true, 0x238F1F, 0xA5AAA6);
        EntityRegistry.registerModEntity(EntityIMImp.class, "IMImp", 12, this, 128, 1, true, 0xB40113, 0xFF0000);
        EntityRegistry.registerModEntity(EntityIMZombiePigman.class, "IMZombiePigman", 13, this, 128, 1, true, 0xEB8E91, 0x49652F);
        EntityRegistry.registerModEntity(EntityIMThrower.class, "IMThrower", 14, this, 128, 1, true, 0x5303814, 0x632808);

        EntityRegistry.registerModEntity(EntityIMBoulder.class, "IMBoulder", 1, this, 36, 4, true);
        EntityRegistry.registerModEntity(EntityIMBolt.class, "IMBolt", 2, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMTrap.class, "IMTrap", 3, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMPrimedTNT.class, "IMPrimedTNT", 4, this, 36, 4, true);

        if (Config.DEBUG) {
            EntityRegistry.registerModEntity(EntityIMBird.class, "IMBird", 15, this, 128, 1, true);
            EntityRegistry.registerModEntity(EntityIMGiantBird.class, "IMGiantBird", 16, this, 128, 1, true, 0x2B2B2B, 0xEA7EDC);
        }

        //spawneggs needed things and despensebehavior
        //BlockDispenser.dispenseBehaviorRegistry.putObject(itemSpawnEgg, new DispenserBehaviorSpawnEgg());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BlocksAndItems.itemSpawnEgg, new DispenserBehaviorSpawnEgg());

        //Add spawneggs
		/*SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 1, Reference.MODID+".IMZombie", "Zombie T1", CustomTags.IMZombie_T1(), 0x6B753F, 0x281B0A));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 2, Reference.MODID+".IMZombie", "Zombie T2", CustomTags.IMZombie_T2(), 0x497533, 0x7C7C7C));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 3, Reference.MODID+".IMZombie", "Tar Zombie T2", CustomTags.IMZombie_T2_tar(), 0x3A4225, 0x191C13));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 4, Reference.MODID+".IMZombie", "Zombie Brute T3", CustomTags.IMZombie_T3(), 0x586146, 0x1E4639));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 5, Reference.MODID+".IMSkeleton", "Skeleton T1", new NBTTagCompound(), 0x9B9B9B, 0x797979));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 6, Reference.MODID+".IMSpider", "Spider T1", new NBTTagCompound(), 0x504A3E, 0xA4121C));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 7, Reference.MODID+".IMSpider", "Spider T1 Baby", CustomTags.IMSpider_T1_baby(), 0x504A3E, 0xA4121C));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 8, Reference.MODID+".IMSpider", "Spider T2 Jumper", CustomTags.IMSpider_T2(), 0x444167, 0x0A0328));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 9, Reference.MODID+".IMSpider", "Spider T2 Mother", CustomTags.IMSpider_T2_mother(), 0x444167, 0x0A0328));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 10, Reference.MODID+".IMCreeper", "Creeper T1", new NBTTagCompound(), 0x238F1F, 0xA5AAA6));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 11, Reference.MODID+".IMPigEngy", "Pigman Engineer T1", new NBTTagCompound(), 0xEC9695, 0x420000));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 12, Reference.MODID+".IMThrower", "Thrower T1", new NBTTagCompound(), 0x545F37, 0x1D2D3E));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 13, Reference.MODID+".IMThrower", "Thrower T2", CustomTags.IMThrower_T2(), 0x5303814, 0x632808));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 14, Reference.MODID+".IMImp", "Imp T1", new NBTTagCompound(), 0xB40113, 0xFF0000));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 15, Reference.MODID+".IMZombiePigman", "Zombie Pigman T1", CustomTags.IMZombiePigman_T1(), 0xEB8E91, 0x49652F));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 16, Reference.MODID+".IMZombiePigman", "Zombie Pigman T2", CustomTags.IMZombiePigman_T2(), 0xEB8E91, 0x49652F));
		SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 17, Reference.MODID+".IMZombiePigman", "Zombie Pigman T3", CustomTags.IMZombiePigman_T3(), 0xEB8E91, 0x49652F));
		
		if (Config.DEBUG){   
		    SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 18, Reference.MODID+".IMGiantBird", "Vulture T1", new NBTTagCompound(), 0x2B2B2B, 0xEA7EDC));
		}*/

    // }

    public static ItemStack getRenderHammerItem() {
        return new ItemStack(ModItems.CATALYST.get(), 1);
    }

    public static Invasion instance() {
        return instance;
    }

    //RM
    /*
    public static int getMobHealth(InvadingEntity mob) {
        int health = 0;
        if (mob.isNexusBound()) {
            if (mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health") != null) {
                health = mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health");
            } else {
                return 20;
            }
        } else {
            if (mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health") != null) {
                health = mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health");
            } else {
                return 20;
            }
        }
        return health;
    }

     */

    private void setup(FMLCommonSetupEvent event) {
    }


    //load mobhealth config
    private void loadHealthConfig() {
        //Main spawns
        mobHealthInvasion.put("IMCreeper-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMVulture-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMImp-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMPigManEngineer-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMSkeleton-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMSpider-T1-Spider-invasionSpawn-health", 18);
        mobHealthInvasion.put("IMSpider-T1-Baby-Spider-invasionSpawn-health", 3);
        mobHealthInvasion.put("IMSpider-T2-Jumping-Spider-invasionSpawn-health", 18);
        mobHealthInvasion.put("IMSpider-T2-Mother-Spider-invasionSpawn-health", 23);
        mobHealthInvasion.put("IMThrower-T1-invasionSpawn-health", 50);
        mobHealthInvasion.put("IMThrower-T2-invasionSpawn-health", 70);
        mobHealthInvasion.put("IMZombie-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMZombie-T2-invasionSpawn-health", 30);
        mobHealthInvasion.put("IMZombie-T3-invasionSpawn-health", 65);
        mobHealthInvasion.put("IMZombiePigman-T1-invasionSpawn-health", 20);
        mobHealthInvasion.put("IMZombiePigman-T2-invasionSpawn-health", 30);
        mobHealthInvasion.put("IMZombiePigman-T3-invasionSpawn-health", 65);

        //Nightspawns
        mobHealthNightspawn.put("IMCreeper-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMVulture-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMImp-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMPigManEngineer-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMSkeleton-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMSpider-T1-Spider-nightSpawn-health", 18);
        mobHealthNightspawn.put("IMSpider-T1-Baby-Spider-nightSpawn-health", 3);
        mobHealthNightspawn.put("IMSpider-T2-Jumping-Spider-nightSpawn-health", 18);
        mobHealthNightspawn.put("IMSpider-T2-Mother-Spider-nightSpawn-health", 23);
        mobHealthNightspawn.put("IMThrower-T1-nightSpawn-health", 50);
        mobHealthNightspawn.put("IMThrower-T2-nightSpawn-health", 70);
        mobHealthNightspawn.put("IMZombie-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMZombie-T2-nightSpawn-health", 30);
        mobHealthNightspawn.put("IMZombie-T3-nightSpawn-health", 65);
        mobHealthNightspawn.put("IMZombiePigman-T1-nightSpawn-health", 20);
        mobHealthNightspawn.put("IMZombiePigman-T2-nightSpawn-health", 30);
        mobHealthNightspawn.put("IMZombiePigman-T3-nightSpawn-health", 65);

    }

    //RM
    /*
    protected void nightSpawnConfig() {
        String[] pool1Patterns = new String[DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length];
        float[] pool1Weights = new float[DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length];
        RandomSelectionPool mobPool = new RandomSelectionPool();
        nightSpawnPool1 = mobPool;
        if (DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length == DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length) {
            for (int i = 0; i < DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length; i++) {

                if (WaveBuilder.isPatternNameValid(pool1Patterns[i])) {
                    logger.debug("Added entry for pattern 1 slot {}", i + 1);
                    mobPool.addEntry(WaveBuilder.getPattern(pool1Patterns[i]), pool1Weights[i]);
                } else {
                    logger.warn("Pattern 1 slot {} in config not recognised. Proceeding as blank.", (i + 1));
                }
            }
        } else {
            logger.fatal("Mob pattern table element mismatch. Ensure each slot has a probability weight");
        }
    }

     */

    /*
    public static void broadcastToAll(String message) {
        //FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText(message));
        List<EntityPlayerMP> playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (int i = 0; i < playerList.size(); i++) {
            sendMessageToPlayer(playerList.get(i), message);
        }
    }

     */

    /*
    @Deprecated
    public static void sendMessageToPlayers(HashMap<String, Long> hashMap, String message) {
        sendMessageToPlayers(hashMap, message, null);
    }
    //TODO
    /*

    @Deprecated
    public static void sendMessageToPlayers(HashMap<String, Long> hashMap, String message, TextFormatting color) {
        if (hashMap != null) {
            for (Map.Entry entry : hashMap.entrySet()) {
                sendMessageToPlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername((String) entry.getKey()), message, color);
            }
        }
    }

    public static void sendMessageToPlayers(List<String> playerList, String msg) {
        sendMessageToPlayers(playerList, msg, null);
    }

    public static void sendMessageToPlayers(List<String> playerList, String msg, TextFormatting color) {
        if (playerList != null) {
            for (int i = 0; i < playerList.size(); i++) {
                sendMessageToPlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerList.get(i)), msg, color);
            }
        }
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, String message) {
        sendMessageToPlayer(player, message, null);
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, String message, TextFormatting color) {
        sendMessageToPlayer((EntityPlayer) player, message, color);
    }

    public static void sendMessageToPlayer(EntityPlayer player, String message) {
        sendMessageToPlayer(player, message, null);
    }

    public static void sendMessageToPlayer(EntityPlayer player, String message, TextFormatting color) {
        TextComponentTranslation s = new TextComponentTranslation(message);
        if (color != null) s.getStyle().setColor(color);
        if (player != null) player.sendMessage(s);
    }

     */

    @Override
    public String toString() {
        return "Main";
    }


}