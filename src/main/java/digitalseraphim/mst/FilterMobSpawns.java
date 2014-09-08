package digitalseraphim.mst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FilterMobSpawns {
	private Map<Integer, Pair<Boolean, List<String>>> whiteBlackLists = new HashMap();

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent ejwe) {
		
		if(ejwe.entity instanceof EntityPlayer || !(ejwe.entity instanceof EntityLivingBase)){
			return;
		}

		int dimid = ejwe.world.provider.dimensionId;
		String name = EntityList.getEntityString(ejwe.entity);
		Pair<Boolean, List<String>> whiteBlackList = whiteBlackLists.get(dimid);

		if (whiteBlackList != null) {
			List<String> l = whiteBlackList.getRight();
			String className = ejwe.entity.getClass().getCanonicalName();
			
			boolean inList = l.contains(name) || l.contains(className);

			if (!inList) {
				for (String s : l) {
					if ((name != null && name.matches(s)) || className.matches(s)) {
						inList = true;
						break;
					}
				}
			}

			// if it is in the list, and list is blacklist, cancel
			// if it is not in the list, and list is whitelist, cancel
			if (inList != whiteBlackList.getLeft()) {
				if (MobSpawnTweaks.debug) {
					System.out.println("want to cancel "
							+ ejwe.entity.getClass().getCanonicalName() + " named: " + name) ;
				}
				ejwe.setCanceled(true);
				ejwe.entity.setDead();
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load loadEvent) {
		int dimid = loadEvent.world.provider.dimensionId;
		Configuration conf = MobSpawnTweaks.configFile;

		if (whiteBlackLists.containsKey(dimid)) {
			return;
		}

		if (conf.hasCategory("dimension." + dimid)) {
			ConfigCategory cat = conf.getCategory("dimension." + dimid);

			if (cat.containsKey("whitelist")) {
				Property p = cat.get("whitelist");
				String[] entityNames = p.getStringList();
				whiteBlackLists.put(dimid,
						Pair.of(true, Arrays.asList(entityNames)));
			} else if (cat.containsKey("blacklist")) {
				Property p = cat.get("blacklist");
				String[] entityNames = p.getStringList();
				whiteBlackLists.put(dimid,
						Pair.of(false, Arrays.asList(entityNames)));
			}
		}
	}
}
