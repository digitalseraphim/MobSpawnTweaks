package digitalseraphim.mst;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@Mod(modid = MobSpawnTweaks.MODID, version = MobSpawnTweaks.VERSION, acceptableRemoteVersions="*", guiFactory = "digitalseraphim.mst.MSTGuiFactory")
@MCVersion("@MC_VERSION@")
public class MobSpawnTweaks
{
    public static final String MODID = "MobSpawnTweaks";
    public static final String VERSION = "@VERSION@";
    FilterMobSpawns fms = new FilterMobSpawns();
    public static Configuration configFile;
    public static boolean debug = false;
      
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	LogHelper.info(MODID + " starting with version " + VERSION);
    	
        configFile = new Configuration(event.getSuggestedConfigurationFile());
 
        syncConfig();
    }
 
    public static void syncConfig() {
    	debug = configFile.getBoolean("debug", "general", false, "Whether to output debuging information");
    	
        if(configFile.hasChanged())
            configFile.save();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(fms);
    	FMLCommonHandler.instance().bus().register(fms);  
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if(eventArgs.modID.equals(MODID))
            syncConfig();
    }
    
	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new MSTCommand());
	}
}
