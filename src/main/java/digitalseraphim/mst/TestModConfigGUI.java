package digitalseraphim.mst;

import cpw.mods.fml.client.config.GuiConfig;  
import net.minecraft.client.gui.GuiScreen;  
import net.minecraftforge.common.config.ConfigElement;  
import net.minecraftforge.common.config.Configuration;

public class TestModConfigGUI extends GuiConfig {  
    public TestModConfigGUI(GuiScreen parent) {
        super(parent,
                new ConfigElement(MobSpawnTweaks.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "MobSpawnTweaks", false, false, GuiConfig.getAbridgedConfigPath(MobSpawnTweaks.configFile.toString()));
    }
}