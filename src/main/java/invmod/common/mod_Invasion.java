package invmod.common;

import invmod.client.TickHandlerClient;
import invmod.common.creativetab.CreativeTabInvmod;
import invmod.common.entity.EntityIMArrowOld;
import invmod.common.entity.EntityIMBird;
import invmod.common.entity.EntityIMBolt;
import invmod.common.entity.EntityIMBoulder;
import invmod.common.entity.EntityIMCreeper;
import invmod.common.entity.EntityIMEgg;
import invmod.common.entity.EntityIMGiantBird;
import invmod.common.entity.EntityIMImp;
import invmod.common.entity.EntityIMLiving;
import invmod.common.entity.EntityIMPigEngy;
import invmod.common.entity.EntityIMSkeleton;
import invmod.common.entity.EntityIMSpawnProxy;
import invmod.common.entity.EntityIMSpider;
import invmod.common.entity.EntityIMThrower;
import invmod.common.entity.EntityIMTrap;
import invmod.common.entity.EntityIMWolf;
import invmod.common.entity.EntityIMZombie;
import invmod.common.entity.EntityIMZombiePigman;
import invmod.common.item.ItemDebugWand;
import invmod.common.item.ItemEngyHammer;
import invmod.common.item.ItemIM;
import invmod.common.item.ItemIMBow;
import invmod.common.item.ItemIMTrap;
import invmod.common.item.ItemInfusedSword;
import invmod.common.item.ItemProbe;
import invmod.common.item.ItemRemnants;
import invmod.common.item.ItemRiftFlux;
import invmod.common.item.ItemStrangeBone;
import invmod.common.nexus.BlockNexus;
import invmod.common.nexus.IEntityIMPattern;
import invmod.common.nexus.IMWaveBuilder;
import invmod.common.nexus.MobBuilder;
import invmod.common.nexus.TileEntityNexus;
import invmod.common.util.IMPlayerHandler;
import invmod.common.util.ISelect;
import invmod.common.util.RandomSelectionPool;
import invmod.common.util.ThreadGetData;
import invmod.common.util.Version;
import invmod.common.util.VersionChecker;
import invmod.common.util.spawneggs.CustomTags;
import invmod.common.util.spawneggs.DispenserBehaviorSpawnEgg;
import invmod.common.util.spawneggs.ItemSpawnEgg;
import invmod.common.util.spawneggs.SpawnEggInfo;
import invmod.common.util.spawneggs.SpawnEggRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "mod_Invasion", name = "Invasion", version = "1.1.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_Invasion
{
    //The client server Proxies, a packetHandler, soundHandler and then your basic render handler
    @SidedProxy(clientSide = "invmod.client.PacketHandlerClient", serverSide = "invmod.common.PacketHandlerCommon")
    public static PacketHandlerCommon packetHandler;

    @SidedProxy(clientSide = "invmod.client.SoundHandlerClient", serverSide = "invmod.common.SoundHandlerCommon")
    public static SoundHandlerCommon soundHandler = new SoundHandlerCommon();

    @SidedProxy(clientSide = "invmod.client.ProxyClient", serverSide = "invmod.common.ProxyCommon")
    public static ProxyCommon proxy;

    
    public static String recentNews;
    public static Version versionNumber = new Version(1, 1, 2);
    public static String latestVersionNumber;
    public static ResourceLoader resourceLoader;
    public static GuiHandler guiHandler;
    public static ConfigInvasion configInvasion;
    private static File configFile;
    private static boolean runFlag;
    private static long timer;
    private static long clientElapsed;
    private static long serverElapsed;
    private static boolean serverRunFlag;
    private static int killTimer;
    private static boolean loginFlag;
    private static HashMap<String, Long> deathList = new HashMap();
    private static MobBuilder defaultMobBuilder = new MobBuilder();
    private static BufferedWriter logOut;
    private static ISelect<IEntityIMPattern> nightSpawnPool1;
    private static TileEntityNexus focusNexus;
    private static TileEntityNexus activeNexus;
    private static boolean isInvasionActive = false;
    private static boolean soundInstalled = false;
    public static final byte PACKET_SFX = 0;
    public static final byte PACKET_INV_MOB_SPAWN = 2;
    public static int entityId = 250;
    /*NOOB HAUS: Default settings for blocks/items etc; used to write config (common.Config) on first run
     */

    private static final int DEFAULT_NEXUS_BLOCK_ID = 216;
    private static final int DEFAULT_GUI_ID_NEXUS = 76;
    private static final int DEFAULT_ITEM_ID_DEBUGWAND = 24399;
    private static final int DEFAULT_ITEM_ID_PHASECRYSTAL = 24400;
    private static final int DEFAULT_ITEM_ID_RIFTFLUX = 24401;
    private static final int DEFAULT_ITEM_ID_REMNANTS = 24402;
    private static final int DEFAULT_ITEM_ID_NEXUSCATALYST = 24403;
    private static final int DEFAULT_ITEM_ID_INFUSEDSWORD = 24404;
    private static final int DEFAULT_ITEM_ID_IMTRAP = 24405;
    private static final int DEFAULT_ITEM_ID_IMBOW = 24406;
    private static final int DEFAULT_ITEM_ID_CATAMIXTURE = 24407;
    private static final int DEFAULT_ITEM_ID_STABLECATAMIXTURE = 24408;
    private static final int DEFAULT_ITEM_ID_STABLENEXUSCATA = 24409;
    private static final int DEFAULT_ITEM_ID_DAMPINGAGENT = 24410;
    private static final int DEFAULT_ITEM_ID_STRONGDAMPINGAGENT = 24411;
    private static final int DEFAULT_ITEM_ID_STRANGEBONE = 24412;
    private static final int DEFAULT_ITEM_ID_PROBE = 24413;
    private static final int DEFAULT_ITEM_ID_STRONGCATALYST = 24414;
    private static final int DEFAULT_ITEM_ID_HAMMER = 24415;
    private static final int DEFAULT_ITEM_ID_EGG = 24416;
    private static final boolean DEFAULT_SOUNDS_ENABLED = true;
    private static final boolean DEFAULT_CRAFT_ITEMS_ENABLED = true;
    private static final boolean DEFAULT_NIGHT_SPAWNS_ENABLED = false;
    private static final int DEFAULT_MIN_CONT_MODE_DAYS = 2;
    private static final int DEFAULT_MAX_CONT_MODE_DAYS = 3;
    private static final int DEFAULT_NIGHT_MOB_SIGHT_RANGE = 20;
    private static final int DEFAULT_NIGHT_MOB_SENSE_RANGE = 8;
    private static final int DEFAULT_NIGHT_MOB_SPAWN_CHANCE = 30;
    private static final int DEFAULT_NIGHT_MOB_MAX_GROUP_SIZE = 3;
    private static final int DEFAULT_NIGHT_MOB_LIMIT_OVERRIDE = 70;
    private static final float DEFAULT_NIGHT_MOB_STATS_SCALING = 1.0F;
    private static final boolean DEFAULT_NIGHT_MOBS_BURN = true;
    public static final String[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS =
    {
        "zombie_t1_any", "zombie_t2_any_basic", "zombie_t2_plain", "zombie_t2_tar",
        "zombie_t2_pigman", "zombie_t3_any", "spider_t1_any", "spider_t2_any", "pigengy_t1_any", "skeleton_t1_any", "thrower_t1", "thrower_t2", "creeper_t1_basic", "imp_t1"
    };
    public static final float[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS =
    {
        1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
    };

    //NOOB HAUS: Declare them values.. Declare em good
    private static boolean alreadyNotified;
    private static boolean updateNotifications;
    private static boolean destructedBlocksDrop;
    private static boolean soundsEnabled;
    private static boolean craftItemsEnabled;
    private static boolean debugMode;
    private static int guiIdNexus;
    private static int minContinuousModeDays;
    private static int maxContinuousModeDays;
    private static boolean nightSpawnsEnabled;
    private static int nexusBlockId;
    private static int nightMobSightRange;
    private static int nightMobSenseRange;
    private static int nightMobSpawnChance;
    private static int nightMobMaxGroupSize;
    private static int maxNightMobs;
    private static float nightMobStatsScaling;
    private static boolean nightMobsBurnInDay;

    //mobhealth
    public static HashMap<String, Integer> mobHealthNightspawn = new HashMap();
    public static HashMap<String, Integer> mobHealthInvasion = new HashMap();

    //Creative tab declariation
    public static CreativeTabInvmod tabInvmod;

    //NOOB HAUS: The almighty Nexus Block Declaration
    public static BlockNexus blockNexus;

    //NOOB HAUS: Item Declarations
    public static Item itemPhaseCrystal;
    public static Item itemRiftFlux;
    public static Item itemRemnants;
    public static Item itemNexusCatalyst;
    public static Item itemInfusedSword;
    public static Item itemIMTrap;
    public static Item itemPenBow;
    public static Item itemCataMixture;
    public static Item itemStableCataMixture;
    public static Item itemStableNexusCatalyst;
    public static Item itemDampingAgent;
    public static Item itemStrongDampingAgent;
    public static Item itemStrangeBone;
    public static Item itemProbe;
    public static Item itemStrongCatalyst;
    public static Item itemEngyHammer;
    public static Item itemDebugWand;
    public static ItemSpawnEgg itemSpawnEgg;

    //NOOB HAUS: Wtf ?
    public static mod_Invasion instance;

    public static IMPlayerHandler playerhandler;

    public mod_Invasion()
    {
        instance = this;
        runFlag = true;
        serverRunFlag = true;
        loginFlag = false;
        timer = 0L;
        clientElapsed = 0L;
        guiHandler = new GuiHandler();
    }
    //NOOB HAUS: End wtf? I am not certain what this method is for..

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        /*NOOB HAUS: Let the real fun begin!
         *
         *First up, we check for the config file, write it if it don't exsit; or capture and return an error to the log
         */
        File logFile = proxy.getFile("/invasion_log.log");

        try
        {
            if (!logFile.exists())
            {
                logFile.createNewFile();
            }

            logOut = new BufferedWriter(new FileWriter(logFile));
        }
        catch (Exception e)
        {
            logOut = null;
            log("Couldn't write to logfile");
            log(e.getMessage());
        }

        //NOOB HAUS: Get the config file - store it into a variable; pass all that shiz thru common.configInvasion
        setAlreadyNotified(false);
        configFile = proxy.getFile("/config/invasion_config.cfg");
        configInvasion = new ConfigInvasion();
        configInvasion.loadConfig(configFile);
        destructedBlocksDrop = configInvasion.getPropertyValueBoolean("destructed-blocks-drop", true);
        updateNotifications = configInvasion.getPropertyValueBoolean("update-messages-enabled", true);;
        soundsEnabled = configInvasion.getPropertyValueBoolean("sounds-enabled", true);
        craftItemsEnabled = configInvasion.getPropertyValueBoolean("craft-items-enabled", true);
        debugMode = configInvasion.getPropertyValueBoolean("debug", false);
        guiIdNexus = configInvasion.getPropertyValueInt("guiID-Nexus", 76);
        minContinuousModeDays = configInvasion.getPropertyValueInt("min-days-to-attack", 2);
        maxContinuousModeDays = configInvasion.getPropertyValueInt("max-days-to-attack", 3);
        latestVersionNumber = VersionChecker.getLatestVersion();
        nightSpawnConfig();
        loadHealthConfig();
        //config options for strengtOverrides, how long it takes for mobs to dig through something.
        HashMap strengthOverrides = new HashMap();

        for (int i = 1; i < 4096; i++)
        {
            String property = configInvasion.getProperty("block" + i + "-strength", "null");

            if (property != "null")
            {
                float strength = Float.parseFloat(property);

                if (strength > 0.0F)
                {
                    strengthOverrides.put(Integer.valueOf(i), Float.valueOf(strength));
                    EntityIMLiving.putBlockStrength(i, strength);
                    float pathCost = 1.0F + strength * 0.4F;
                    EntityIMLiving.putBlockCost(i, pathCost);
                }
            }
        }

        configInvasion.saveConfig(configFile, strengthOverrides, debugMode);
        //Load the Things!!
        loadCreativeTabs();
        loadBlocks();
        loadItems();
        loadEntities();
        loadNames();
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        //Register sound handler
        MinecraftForge.EVENT_BUS.register(soundHandler);
        //Register moar all the handler
        NetworkRegistry.instance().registerGuiHandler(instance, guiHandler);
        NetworkRegistry.instance().registerChannel(packetHandler, "data");
        TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
        TickRegistry.registerTickHandler(new TickHandlerServer(), Side.SERVER);
        new ThreadGetData();
        // Register to receive subscribed events
        //FMLCommonHandler.instance()..findContainerFor(this);
        MinecraftForge.EVENT_BUS.register(this);
        playerhandler = new IMPlayerHandler();
        GameRegistry.registerPlayerTracker(playerhandler);
		FMLInterModComms.sendMessage("Waila", "register", "invmod.common.util.IMWailaProvider.callbackRegister");

        if (craftItemsEnabled)
        {
            addRecipes();
        }

        if (nightSpawnsEnabled)
        {
            BiomeGenBase[] biomes = { BiomeGenBase.plains, BiomeGenBase.extremeHills, BiomeGenBase.forest, BiomeGenBase.taiga, BiomeGenBase.swampland, BiomeGenBase.forestHills, BiomeGenBase.taigaHills, BiomeGenBase.extremeHillsEdge, BiomeGenBase.jungle, BiomeGenBase.jungleHills };
            EntityRegistry.addSpawn(EntityIMSpawnProxy.class, nightMobSpawnChance, 1, 1, EnumCreatureType.monster, biomes);
            EntityRegistry.addSpawn(EntityZombie.class, 1, 1, 1, EnumCreatureType.monster, biomes);
            EntityRegistry.addSpawn(EntitySpider.class, 1, 1, 1, EnumCreatureType.monster, biomes);
            EntityRegistry.addSpawn(EntitySkeleton.class, 1, 1, 1, EnumCreatureType.monster, biomes);
        }

        if (maxNightMobs != 70)
        {
            try
            {
                Class c = EnumCreatureType.class;
                Object[] consts = c.getEnumConstants();
                Class sub = consts[0].getClass();
                Field field = sub.getDeclaredField("maxNumberOfCreature");
                field.setAccessible(true);
                field.set(EnumCreatureType.monster, Integer.valueOf(maxNightMobs));
            }
            catch (Exception e)
            {
                log(e.getMessage());
            }
        }

        soundHandler.setSoundEnabled(soundsEnabled);
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();

        if ((commandManager instanceof CommandHandler))
        {
            ((CommandHandler) commandManager).registerCommand(new InvasionCommand());
        }
    }

    //load mobhealth config
    private void loadHealthConfig()
    {
        //Invasion spawns
        mobHealthInvasion.put("IMCreeper-T1-invasionSpawn-health", mod_Invasion.configInvasion.getPropertyValueInt("IMCreeper-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMVulture-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMVulture-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMImp-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMImp-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMPigManEngineer-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMPigManEngineer-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMSkeleton-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSkeleton-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMSpider-T1-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Spider-invasionSpawn-health", 18));
        mobHealthInvasion.put("IMSpider-T1-Baby-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Baby-Spider-invasionSpawn-health", 3));
        mobHealthInvasion.put("IMSpider-T2-Jumping-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Jumping-Spider-invasionSpawn-health", 18));
        mobHealthInvasion.put("IMSpider-T2-Mother-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Mother-Spider-invasionSpawn-health", 23));
        mobHealthInvasion.put("IMThrower-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T1-invasionSpawn-health", 50));
        mobHealthInvasion.put("IMThrower-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T2-invasionSpawn-health", 70));
        mobHealthInvasion.put("IMZombie-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T1-invasionSpawn-health", 20));
        mobHealthInvasion.put("IMZombie-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T2-invasionSpawn-health", 30));
        mobHealthInvasion.put("IMZombie-T3-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T3-invasionSpawn-health", 65));
    	mobHealthInvasion.put("IMZombiePigman-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMZombiePigman-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T2-invasionSpawn-health", 30));
		mobHealthInvasion.put("IMZombiePigman-T3-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T3-invasionSpawn-health", 65));
        //Nightspawns
        mobHealthNightspawn.put("IMCreeper-T1-nightSpawn-health", mod_Invasion.configInvasion.getPropertyValueInt("IMCreeper-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMVulture-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMVulture-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMImp-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMImp-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMPigManEngineer-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMPigManEngineer-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMSkeleton-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSkeleton-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMSpider-T1-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Spider-nightSpawn-health", 18));
        mobHealthNightspawn.put("IMSpider-T1-Baby-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Baby-Spider-nightSpawn-health", 3));
        mobHealthNightspawn.put("IMSpider-T2-Jumping-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Jumping-Spider-nightSpawn-health", 18));
        mobHealthNightspawn.put("IMSpider-T2-Mother-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Mother-Spider-nightSpawn-health", 23));
        mobHealthNightspawn.put("IMThrower-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T1-nightSpawn-health", 50));
        mobHealthNightspawn.put("IMThrower-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T2-nightSpawn-health", 70));
        mobHealthNightspawn.put("IMZombie-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T1-nightSpawn-health", 20));
        mobHealthNightspawn.put("IMZombie-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T2-nightSpawn-health", 30));
        mobHealthNightspawn.put("IMZombie-T3-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T3-nightSpawn-health", 65));
		mobHealthNightspawn.put("IMZombiePigman-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMZombiePigman-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T2-nightSpawn-health", 30));
		mobHealthNightspawn.put("IMZombiePigman-T3-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T3-nightSpawn-health", 65));
    }

    //load Creativetab
    protected void loadCreativeTabs()
    {
        tabInvmod = new CreativeTabInvmod(CreativeTabs.getNextID(), "InvmodTab");
    }

    //Load Blocks
    protected void loadBlocks()
    {
        blockNexus = new BlockNexus(configInvasion.getPropertyValueInt("blockID-Nexus", 216));
        GameRegistry.registerBlock(blockNexus, "Nexus");
        GameRegistry.registerTileEntity(TileEntityNexus.class, "Nexus");
    }

    //Load Items
    protected void loadItems()
    {
        itemPhaseCrystal = new ItemIM(configInvasion.getPropertyValueInt("itemID-PhaseCrystal", 24400)).setUnlocalizedName("phaseCrystal").setMaxStackSize(1);
        itemRiftFlux = new ItemRiftFlux(configInvasion.getPropertyValueInt("itemID-RiftFlux", 24401)).setUnlocalizedName("riftFlux");
        itemRemnants = new ItemRemnants(configInvasion.getPropertyValueInt("itemID-Remnants", 24402));
        itemNexusCatalyst = new ItemIM(configInvasion.getPropertyValueInt("itemID-NexusCatalyst", 24403)).setUnlocalizedName("nexusCatalyst").setMaxStackSize(1);
        itemInfusedSword = new ItemInfusedSword(configInvasion.getPropertyValueInt("itemID-InfusedSword", 24404)).setUnlocalizedName("infusedSword").setMaxStackSize(1);
        itemPenBow = new ItemIMBow(configInvasion.getPropertyValueInt("itemID-IMBow", 24406)).setUnlocalizedName("searingBow");
        itemCataMixture = new ItemIM(configInvasion.getPropertyValueInt("itemID-CataMixture", 24407)).setUnlocalizedName("catalystMixture").setMaxStackSize(1);
        itemStableCataMixture = new ItemIM(configInvasion.getPropertyValueInt("itemID-StableCataMixture", 24408)).setUnlocalizedName("stableCatalystMixture").setMaxStackSize(1);
        itemStableNexusCatalyst = new ItemIM(configInvasion.getPropertyValueInt("itemID-StableNexusCatalyst", 24409)).setUnlocalizedName("stableNexusCatalyst").setMaxStackSize(1);
        itemDampingAgent = new ItemIM(configInvasion.getPropertyValueInt("itemID-DampingAgent", 24410)).setUnlocalizedName("dampingAgent").setMaxStackSize(1);
        itemStrongDampingAgent = new ItemIM(configInvasion.getPropertyValueInt("itemID-StrongDampingAgent", 24411)).setUnlocalizedName("strongDampingAgent");
        itemStrangeBone = new ItemStrangeBone(configInvasion.getPropertyValueInt("itemID-StrangeBone", 24412)).setUnlocalizedName("strangeBone").setMaxStackSize(1);
        itemStrongCatalyst = new ItemIM(configInvasion.getPropertyValueInt("itemID-StrongCatalyst", 24414)).setUnlocalizedName("strongCatalyst").setMaxStackSize(1);
        itemEngyHammer = new ItemEngyHammer(configInvasion.getPropertyValueInt("itemID-EngyHammer", 24415)).setUnlocalizedName("engyHammer");
        itemProbe = new ItemProbe(configInvasion.getPropertyValueInt("itemID-Probe", 24413)).setUnlocalizedName("probe");
        itemIMTrap = new ItemIMTrap(configInvasion.getPropertyValueInt("itemID-IMTrap", 24405)).setUnlocalizedName("trap");
        itemSpawnEgg = new ItemSpawnEgg(configInvasion.getPropertyValueInt("itemID-SpawnEgg", 24416));

        if (debugMode)
        {
            itemDebugWand = new ItemDebugWand(configInvasion.getPropertyValueInt("itemID-DebugWand", 24399)).setUnlocalizedName("debugWand");
        }
        else
        {
            itemDebugWand = null;
        }
    }

    //Load Entities
    protected void loadEntities()
    {
        //Register Entities
        EntityRegistry.registerGlobalEntityID(EntityIMZombie.class, "IMZombie", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMZombiePigman.class, "IMZombiePigman", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMSkeleton.class, "IMSkeleton", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMSpider.class, "IMSpider", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMPigEngy.class, "IMPigEngy", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMThrower.class, "IMThrower", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMWolf.class, "IMWolf", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMEgg.class, "IMEgg", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMCreeper.class, "IMCreeper", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityIMImp.class, "IMImp", EntityRegistry.findGlobalUniqueEntityId());

        if (debugMode)
        {
            EntityRegistry.registerGlobalEntityID(EntityIMBird.class, "IMBird", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerGlobalEntityID(EntityIMGiantBird.class, "IMGiantBird", EntityRegistry.findGlobalUniqueEntityId());
        }

        EntityRegistry.registerModEntity(EntityIMBoulder.class, "IMBoulder", 1, this, 36, 4, true);
        EntityRegistry.registerModEntity(EntityIMBolt.class, "IMBolt", 2, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMTrap.class, "IMTrap", 3, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMArrowOld.class, "IMArrow", 4, this, 70, 1, true);
        //spawneggs needed things and despensebehavior
        GameRegistry.registerItem(itemSpawnEgg, itemSpawnEgg.getUnlocalizedName());
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemSpawnEgg, new DispenserBehaviorSpawnEgg());
        //Add spawneggs
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 1, "IMZombie", "Zombie T1", CustomTags.IMZombie_T1(), 0x6B753F, 0x281B0A));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 2, "IMZombie", "Zombie T2", CustomTags.IMZombie_T2(), 0x497533, 0x7C7C7C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 3, "IMZombie", "Tar Zombie T2", CustomTags.IMZombie_T2_tar(), 0x3A4225, 0x191C13));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 4, "IMZombie", "Zombie Brute T3", CustomTags.IMZombie_T3(), 0x586146, 0x1E4639));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 5, "IMSkeleton", "Skeleton T1", new NBTTagCompound(), 0x9B9B9B, 0x797979));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 6, "IMSpider", "Spider T1", new NBTTagCompound(), 0x504A3E, 0xA4121C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 7, "IMSpider", "Spider T1 Baby", CustomTags.IMSpider_T1_baby(), 0x504A3E, 0xA4121C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 8, "IMSpider", "Spider T2 Jumper", CustomTags.IMSpider_T2(), 0x444167, 0x0A0328));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 9, "IMSpider", "Spider T2 Mother", CustomTags.IMSpider_T2_mother(), 0x444167, 0x0A0328));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 10, "IMCreeper", "Creeper T1", new NBTTagCompound(), 0x238F1F, 0xA5AAA6));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 11, "IMPigEngy", "Pigman Engineer T1", new NBTTagCompound(), 0xEC9695, 0x420000));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 12, "IMThrower", "Thrower T1", new NBTTagCompound(), 0x545F37, 0x1D2D3E));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 13, "IMThrower", "Thrower T2", CustomTags.IMThrower_T2(), 0x5303814, 0x632808));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 14, "IMImp", "Imp T1", new NBTTagCompound(), 0xB40113, 0xFF0000));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 15, "IMZombiePigman", "Zombie Pigman T1", CustomTags.IMZombiePigman_T1(), 0xEB8E91, 0x49652F));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 16, "IMZombiePigman", "Zombie Pigman T2", CustomTags.IMZombiePigman_T2(), 0xEB8E91, 0x49652F));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 17, "IMZombiePigman", "Zombie Pigman T3", CustomTags.IMZombiePigman_T3(), 0xEB8E91, 0x49652F));
        if (debugMode)
        {
            SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 18, "IMGiantBird", "Vulture T1", new NBTTagCompound(), 0x2B2B2B, 0xEA7EDC));
        }

        //preload Textures
        proxy.preloadTexture("/mods/invmod/textures/zombie_old.png");
        proxy.preloadTexture("/mods/invmod/textures/zombieT2.png");
        proxy.preloadTexture("/mods/invmod/textures/zombieT2.png");
        proxy.preloadTexture("/mods/invmod/textures/zombieT2a.png");
        proxy.preloadTexture("/mods/invmod/textures/zombietar.png");
        proxy.preloadTexture("/mods/invmod/textures/zombieT1a.png");
        proxy.preloadTexture("/mods/invmod/textures/spiderT2.png");
        proxy.preloadTexture("/mods/invmod/textures/spiderT2b.png");
        proxy.preloadTexture("/mods/invmod/textures/throwerT1.png");
        proxy.preloadTexture("/mods/invmod/textures/throwerT2.png");
        proxy.preloadTexture("/mods/invmod/textures/pigengT1.png");
        proxy.preloadTexture("/mods/invmod/textures/nexusgui.png");
        proxy.preloadTexture("/mods/invmod/textures/boulder.png");
        proxy.preloadTexture("/mods/invmod/textures/trap.png");
        proxy.preloadTexture("/mods/invmod/textures/testmodel.png");
        proxy.preloadTexture("/mods/invmod/textures/burrower.png");
        proxy.preloadTexture("/mods/invmod/textures/spideregg.png");
        proxy.preloadTexture("/mods/invmod/textures/zombieT3.png");
        proxy.preloadTexture("/mods/invmod/textures/imp.png");
        proxy.preloadTexture("/mods/invmod/textures/vulture.png");
        //Animations and rendering
        proxy.loadAnimations();
        proxy.registerEntityRenderers();
    }
    //Register Names
    protected void loadNames()
    {
        //Block names
        LanguageRegistry.addName(blockNexus, "Nexus");
        //Item names
        LanguageRegistry.addName(itemPhaseCrystal, "Phase Crystal");
        LanguageRegistry.addName(itemNexusCatalyst, "Nexus Catalyst");
        LanguageRegistry.addName(itemInfusedSword, "Infused Sword");
        LanguageRegistry.addName(itemPenBow, "Searing Bow");
        LanguageRegistry.addName(itemCataMixture, "Catalyst Mixture");
        LanguageRegistry.addName(itemStableCataMixture, "Stable Catalyst Mixture");
        LanguageRegistry.addName(itemStableNexusCatalyst, "Stable Catalyst");
        LanguageRegistry.addName(itemDampingAgent, "Damping Agent");
        LanguageRegistry.addName(itemStrongDampingAgent, "Strong Damping Agent");
        LanguageRegistry.addName(itemStrangeBone, "Strange Bone");
        LanguageRegistry.addName(itemProbe, "Probe");
        LanguageRegistry.addName(itemStrongCatalyst, "Strong Nexus Catalyst");
        LanguageRegistry.addName(itemEngyHammer, "Engineers Hammer");
        LanguageRegistry.addName(itemRemnants, "Small Remnants");
        LanguageRegistry.addName(itemRiftFlux, "Rift Flux");
        LanguageRegistry.addName(new ItemStack(itemIMTrap, 1, 0), ItemIMTrap.trapNames[0]);
        LanguageRegistry.addName(new ItemStack(itemIMTrap, 1, 1), ItemIMTrap.trapNames[1]);
        LanguageRegistry.addName(new ItemStack(itemIMTrap, 1, 2), ItemIMTrap.trapNames[2]);
        LanguageRegistry.addName(new ItemStack(itemProbe, 1, 0), ItemProbe.probeNames[0]);
        LanguageRegistry.addName(new ItemStack(itemProbe, 1, 1), ItemProbe.probeNames[1]);
        //Mob names
        LanguageRegistry.instance().addStringLocalization("entity.IMZombie.name", "en_US", "IM Zombie");
        LanguageRegistry.instance().addStringLocalization("entity.IMSkeleton.name", "en_US", "IM Skeleton");
        LanguageRegistry.instance().addStringLocalization("entity.IMSpider.name", "en_US", "IM Spider");
        LanguageRegistry.instance().addStringLocalization("entity.IMPigEngy.name", "en_US", "IM Pigman Engineer");
        LanguageRegistry.instance().addStringLocalization("entity.IMBird.name", "en_US", "WIP-IM Bird");
        LanguageRegistry.instance().addStringLocalization("entity.IMThrower.name", "en_US", "IM Thrower");
        LanguageRegistry.instance().addStringLocalization("entity.IMWolf.name", "en_US", "IM Wolf");
        LanguageRegistry.instance().addStringLocalization("entity.IMCreeper.name", "en_US", "IM Creeper");
        LanguageRegistry.instance().addStringLocalization("entity.IMGiantBird.name", "en_US", "WIP-IM GiantBird");
        LanguageRegistry.instance().addStringLocalization("entity.IMImp.name", "en_US", "IM Imp");
        //Itemlike entities
        LanguageRegistry.instance().addStringLocalization("entity.IMBoulder.name", "en_US", "Boulder");
        LanguageRegistry.instance().addStringLocalization("entity.IMEgg.name", "en_US", "Spider Egg");
        LanguageRegistry.instance().addStringLocalization("entity.IMTrap.name", "en_US", "Trap");

        if (debugMode)
        {
            LanguageRegistry.addName(itemDebugWand, "Debug Wand");
        }
    }

    //Register Recipes
    protected void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(blockNexus, 1), new Object[] { " X ", "#D#", " # ", 'X', itemPhaseCrystal, '#', Item.redstone, 'D', Block.obsidian });
        GameRegistry.addRecipe(new ItemStack(itemPhaseCrystal, 1), new Object[] { " X ", "#D#", " X ", 'X', new ItemStack(Item.dyePowder, 1, 4), '#', Item.redstone, 'D', Item.diamond });
        GameRegistry.addRecipe(new ItemStack(itemPhaseCrystal, 1), new Object[] { " X ", "#D#", " X ", 'X', Item.redstone, '#', new ItemStack(Item.dyePowder, 1, 4), 'D', Item.diamond });
        GameRegistry.addRecipe(new ItemStack(itemRiftFlux, 1), new Object[] { "XXX", "XXX", "XXX", 'X', new ItemStack(itemRemnants, 1) });
        GameRegistry.addRecipe(new ItemStack(itemInfusedSword, 1), new Object[] { "X  ", "X# ", "X  ", 'X', new ItemStack(itemRiftFlux, 1), '#', new ItemStack(Item.swordDiamond, 1, OreDictionary.WILDCARD_VALUE)  });
        GameRegistry.addRecipe(new ItemStack(itemCataMixture, 1), new Object[] { "   ", "D#H", " X ", 'X', Item.bowlEmpty, '#', Item.redstone, 'D', Item.bone, 'H', Item.rottenFlesh });
        GameRegistry.addRecipe(new ItemStack(itemCataMixture, 1), new Object[] { "   ", "H#D", " X ", 'X', Item.bowlEmpty, '#', Item.redstone, 'D', Item.bone, 'H', Item.rottenFlesh });
        GameRegistry.addRecipe(new ItemStack(itemStableCataMixture, 1), new Object[] { "   ", "D#D", " X ", 'X', Item.bowlEmpty, '#', Item.coal, 'D', Item.bone, 'H', Item.rottenFlesh });
        GameRegistry.addRecipe(new ItemStack(itemDampingAgent, 1), new Object[] { "   ", "#X#", "   ", 'X', new ItemStack(itemRiftFlux, 1), '#', new ItemStack(Item.dyePowder, 1, 4) });
        GameRegistry.addRecipe(new ItemStack(itemStrongDampingAgent, 1), new Object[] { " X ", " X ", " X ", 'X', itemDampingAgent });
        GameRegistry.addRecipe(new ItemStack(itemStrongDampingAgent, 1), new Object[] { "   ", "XXX", "   ", 'X', itemDampingAgent });
        GameRegistry.addRecipe(new ItemStack(itemStrangeBone, 1), new Object[] { "   ", "X#X", "   ", 'X', new ItemStack(itemRiftFlux, 1), '#', Item.bone });
        GameRegistry.addRecipe(new ItemStack(itemPenBow, 1), new Object[] { "XXX", "X# ", "X  ", 'X', new ItemStack(itemRiftFlux, 1), '#', new ItemStack(Item.bow, 1, OreDictionary.WILDCARD_VALUE)  });
		GameRegistry.addRecipe(new ItemStack(Item.gunpowder, 16), new Object[] { " X ", " X ", " X ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });
		GameRegistry.addRecipe(new ItemStack(Item.gunpowder, 16), new Object[] { "   ", "XXX", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });
		GameRegistry.addRecipe(new ItemStack(Item.diamond, 1), new Object[] { " X ", "X X", " X ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });
        GameRegistry.addRecipe(new ItemStack(Item.ingotIron, 4), new Object[] { "   ", " X ", "   ", 'X', new ItemStack(itemRiftFlux, 1) });
        GameRegistry.addRecipe(new ItemStack(Item.redstone, 24), new Object[] { "   ", "X X", "   ", 'X', new ItemStack(itemRiftFlux, 1) });
        GameRegistry.addRecipe(new ItemStack(Item.dyePowder, 12, 4), new Object[] { " X ", "   ", " X ", 'X', new ItemStack(itemRiftFlux, 1, 1) });
        GameRegistry.addRecipe(new ItemStack(itemIMTrap, 1, 0), new Object[] { " X ", "X#X", " X ", 'X', Item.ingotIron, '#', new ItemStack(itemRiftFlux, 1) });
        GameRegistry.addRecipe(new ItemStack(itemIMTrap, 1, 2), new Object[] { "   ", " # ", " X ", 'X', new ItemStack(itemIMTrap, 1, 0), '#', Item.bucketLava });
        GameRegistry.addRecipe(new ItemStack(itemProbe, 1, 0), new Object[] { " X ", "XX ", "XX ", 'X', Item.ingotIron });
        GameRegistry.addRecipe(new ItemStack(itemProbe, 1, 1), new Object[] { " D ", " # ", " X ", 'X', Item.blazeRod, '#', itemPhaseCrystal, 'D', new ItemStack(itemProbe, 1, 0) });
        GameRegistry.addSmelting(itemCataMixture.itemID, new ItemStack(itemNexusCatalyst), 1.0F);
        GameRegistry.addSmelting(itemStableCataMixture.itemID, new ItemStack(itemStableNexusCatalyst), 1.0F);
    }

    protected void nightSpawnConfig()
    {
        nightSpawnsEnabled = configInvasion.getPropertyValueBoolean("night-spawns-enabled", false);
        nightMobSightRange = configInvasion.getPropertyValueInt("night-mob-sight-range", 20);
        nightMobSenseRange = configInvasion.getPropertyValueInt("night-mob-sense-range", 8);
        nightMobSpawnChance = configInvasion.getPropertyValueInt("night-mob-spawn-chance", 30);
        nightMobMaxGroupSize = configInvasion.getPropertyValueInt("night-mob-max-group-size", 3);
        maxNightMobs = configInvasion.getPropertyValueInt("mob-limit-override", 70);
        nightMobsBurnInDay = configInvasion.getPropertyValueBoolean("night-mobs-burn-in-day", true);
        String[] pool1Patterns = new String[DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length];
        float[] pool1Weights = new float[DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length];
        RandomSelectionPool mobPool = new RandomSelectionPool();
        nightSpawnPool1 = mobPool;

        if (DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length == DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length)
        {
            for (int i = 0; i < DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length; i++)
            {
                pool1Patterns[i] = configInvasion.getPropertyValueString("nm-spawnpool1-slot" + (1 + i), DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS[i]);
                pool1Weights[i] = configInvasion.getPropertyValueFloat("nm-spawnpool1-slot" + (1 + i) + "-weight", DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS[i]);

                if (IMWaveBuilder.isPatternNameValid(pool1Patterns[i]))
                {
                    log("Added entry for pattern 1 slot " + (i + 1));
                    mobPool.addEntry(IMWaveBuilder.getPattern(pool1Patterns[i]), pool1Weights[i]);
                }
                else
                {
                    log("Pattern 1 slot " + (i + 1) + " in config not recognised. Proceeding as blank.");
                    configInvasion.setProperty("nm-spawnpool1-slot" + (1 + i), "none");
                }
            }
        }
        else
        {
            log("Mob pattern table element mismatch. Ensure each slot has a probability weight");
        }
    }

    public static boolean onClientTick()
    {
        if (runFlag)
        {
            if ((soundsEnabled) && (!soundHandler.soundsInstalled()))
            {
                proxy.printGuiMessage("Invasion Mod Warning: Failed to auto-install sounds. You can disable this process in config or give a bug report");
            }

            runFlag = false;
        }

        return true;
    }

    public static boolean onServerTick()
    {
        if (serverRunFlag)
        {
            timer = System.currentTimeMillis();
            serverRunFlag = false;
        }

        serverElapsed -= timer;
        timer = System.currentTimeMillis();
        serverElapsed += timer;

        if (serverElapsed >= 100L)
        {
            serverElapsed -= 100L;

            if (loginFlag)
            {
                killTimer += 1;
            }

            if (killTimer > 35)
            {
                killTimer = 0;
                loginFlag = false;

                for (Map.Entry entry : deathList.entrySet())
                {
                    if (System.currentTimeMillis() - ((Long) entry.getValue()).longValue() > 300000L)
                    {
                        deathList.remove(entry.getKey());
                    }
                    else
                    {
                        for (World world : DimensionManager.getWorlds())
                        {
                            EntityPlayer player = world.getPlayerEntityByName((String) entry.getKey());

                            if (player != null)
                            {
                                player.attackEntityFrom(DamageSource.magic, 500.0F);
                                player.setDead();
                                deathList.remove(player.username);
                                broadcastToAll("Nexus energies caught up to " + player.username);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static void addToDeathList(String username, long timeStamp)
    {
        deathList.put(username, Long.valueOf(timeStamp));
    }

    @Override
    public String toString()
    {
        return "mod_Invasion";
    }

    @Override
    protected void finalize() throws Throwable
    {
        try
        {
            if (logOut != null)
            {
                logOut.close();
            }
        }
        catch (Exception e)
        {
            logOut = null;
            log("Error closing invasion log file");
        }
        finally
        {
            super.finalize();
        }
    }

    public static boolean isInvasionActive()
    {
        return isInvasionActive;
    }

    public static boolean tryGetInvasionPermission(TileEntityNexus nexus)
    {
        if (nexus == activeNexus)
        {
            return true;
        }

        if (nexus == null)
        {
            String s = "Nexus entity invalid";
            log(s);
        }
        else
        {
            activeNexus = nexus;
            isInvasionActive = true;
            return true;
        }

        return false;
    }

    public static void setInvasionEnded(TileEntityNexus nexus)
    {
        if (activeNexus == nexus)
        {
            isInvasionActive = false;
        }
    }

    public static void setNexusUnloaded(TileEntityNexus nexus)
    {
        if (activeNexus == nexus)
        {
            nexus = null;
            isInvasionActive = false;
        }
    }

    public static void setNexusClicked(TileEntityNexus nexus)
    {
        focusNexus = nexus;
    }

    public static TileEntityNexus getActiveNexus()
    {
        return activeNexus;
    }

    public static TileEntityNexus getFocusNexus()
    {
        return focusNexus;
    }

    public static Entity[] getNightMobSpawns1(World world)
    {
        ISelect mobPool = getMobSpawnPool();
        int numberOfMobs = world.rand.nextInt(nightMobMaxGroupSize) + 1;
        Entity[] entities = new Entity[numberOfMobs];

        for (int i = 0; i < numberOfMobs; i++)
        {
            EntityIMLiving mob = getMobBuilder().createMobFromConstruct(((IEntityIMPattern) mobPool.selectNext()).generateEntityConstruct(), world, null);
            mob.setEntityIndependent();
            mob.setAggroRange(getNightMobSightRange());
            mob.setSenseRange(getNightMobSenseRange());
            mob.setBurnsInDay(getNightMobsBurnInDay());
            entities[i] = mob;
        }

        return entities;
    }

    public static MobBuilder getMobBuilder()
    {
        return defaultMobBuilder;
    }

    public static ISelect<IEntityIMPattern> getMobSpawnPool()
    {
        return nightSpawnPool1;
    }

    public static int getMinContinuousModeDays()
    {
        return minContinuousModeDays;
    }

    public static int getMaxContinuousModeDays()
    {
        return maxContinuousModeDays;
    }

    public static int getNightMobSightRange()
    {
        return nightMobSightRange;
    }

    public static int getNightMobSenseRange()
    {
        return nightMobSenseRange;
    }

    public static boolean getNightMobsBurnInDay()
    {
        return nightMobsBurnInDay;
    }

    public static ItemStack getRenderHammerItem()
    {
        return new ItemStack(itemEngyHammer, 1);
    }

    public static int getGuiIdNexus()
    {
        return guiIdNexus;
    }

    public static mod_Invasion getLoadedInstance()
    {
        return instance;
    }

    public static void sendInvasionPacketToAll(byte[] data)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "data";
        packet.data = data;
        packet.length = packet.data.length;
        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendPacketToAllPlayers(packet);
    }

    public static void broadcastToAll(String message)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(message));
    }

    public static void sendMessageToPlayers(HashMap<String, Long> hashMap, String message)
    {
        if (hashMap != null)
        {
            for (Map.Entry entry : hashMap.entrySet())
            {
                sendMessageToPlayer((String) entry.getKey(), message);
            }
        }
    }

    public static void sendMessageToPlayer(String user, String message)
    {
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(user);

        if (player != null)
        {
            player.sendChatToPlayer(ChatMessageComponent.createFromText(message));
        }
    }

    public static void playGlobalSFX(String s)
    {
        soundHandler.playGlobalSFX(s);
    }

    public static void playSingleSFX(String s)
    {
        soundHandler.playSingleSFX(s);
    }

    public static void playSingleSFX(byte id)
    {
        soundHandler.playSingleSFX(id);
    }

    public static void log(String s)
    {
        if (s == null)
        {
            return;
        }

        try
        {
            if (logOut != null)
            {
                logOut.write(s);
                logOut.newLine();
                logOut.flush();
            }
            else
            {
                System.out.println(s);
            }
        }
        catch (IOException e)
        {
            System.out.println("Couldn't write to invasion log file");
            System.out.println(s);
        }
    }

    public static boolean isDebug()
    {
        return debugMode;
    }

    public static int getMobHealth(EntityIMLiving mob)
    {
        int health = 0;

        if (mob.isNexusBound())
        {
            if (mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health") != null)
            {
                health = mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health");
            }
            else
            {
                System.out.println(mob.toString() + " not found, using default health value");
                return 20;
            }
        }
        else
        {
            if (mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health") != null)
            {
                health = mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health");
            }
            else
            {
                System.out.println(mob.toString() + " not found, using default health value");
                return 20;
            }
        }

        return health;
    }

    public static boolean getUpdateNotifications()
    {
        return updateNotifications;
    }
    public static Version getVersionNumber()
    {
        return versionNumber;
    }

    public static String getLatestVersionNumber()
    {
        return latestVersionNumber;
    }
    public static String getRecentNews()
    {
        return recentNews;
    }

    public static boolean getAlreadyNotified()
    {
        return alreadyNotified;
    }

    public static void setAlreadyNotified(boolean alreadyNotified)
    {
        mod_Invasion.alreadyNotified = alreadyNotified;
    }

    public static boolean getDestructedBlocksDrop()
    {
        return destructedBlocksDrop;
    }
}