package digitalseraphim.mst;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobSpawnTweaks.MODID, version = MobSpawnTweaks.VERSION)
public class MobSpawnTweaks
{
    public static final String MODID = "MobSpawnTweaks";
    public static final String VERSION = "0.1";
    FilterMobSpawns fms = new FilterMobSpawns();
    public static Configuration configFile;
    public static boolean debug = false;
    
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
    }
}
