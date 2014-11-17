package digitalseraphim.mst;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FilterMobSpawns {
	private Map<Integer, Pair<Boolean, HashList<String>>> whiteBlackLists = new HashMap();

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent ejwe) {

		if (ejwe.entity instanceof EntityPlayer
				|| !(ejwe.entity instanceof EntityLivingBase)) {
			return;
		}

		int dimid = ejwe.world.provider.dimensionId;
		Pair<Boolean, HashList<String>> whiteBlackList = whiteBlackLists
				.get(dimid);

		if (whiteBlackList != null) {
			String name = EntityList.getEntityString(ejwe.entity);
			HashList<String> l = whiteBlackList.getRight();
			String className = ejwe.entity.getClass().getCanonicalName();

			boolean inList = l.contains(name) || l.contains(className);

			// if it is in the list, and list is blacklist, cancel
			// if it is not in the list, and list is whitelist, cancel
			if (inList != whiteBlackList.getLeft()) {
				if (MobSpawnTweaks.debug) {
					System.out.println("want to cancel "
							+ ejwe.entity.getClass().getCanonicalName()
							+ " named: " + name);
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
				
				if (MobSpawnTweaks.debug) {
					LogHelper.info("processing whitelist");
				}
				
				HashList<String> wlEntities = processList(entityNames);
				whiteBlackLists.put(dimid, Pair.of(true, wlEntities));
			} else if (cat.containsKey("blacklist")) {
				Property p = cat.get("blacklist");
				String[] entityNames = p.getStringList();

				if (MobSpawnTweaks.debug) {
					LogHelper.info("processing blacklist");
				}
				
				HashList<String> wlEntities = processList(entityNames);
				whiteBlackLists.put(dimid, Pair.of(false, wlEntities));
			}
		}
	}

	private HashList<String> processList(String[] names){
		Map s2c = EntityList.stringToClassMapping;
		List<String> ll = Arrays.asList(names);
		HashList<String> ret = new HashList();
		
		for(Object o: s2c.keySet()){
			String name = (String)o;
			String clazz = ((Class<?>)s2c.get(name)).getCanonicalName();
			
			for(String n : ll){
				
				if((name != null && name.matches(n)) || (clazz != null && clazz.matches(n))){
					if(clazz != null){ //don't think this can be null, but just in case
						if (MobSpawnTweaks.debug) {
							LogHelper.info("adding " + clazz + " to list");
						}
						ret.add(clazz);
					}
				}
			}
		}
		
		return ret;
	}
	
	private static class HashList<T> implements Iterable<T> {
		LinkedHashMap<T, T> backing = new LinkedHashMap();

		public HashList() {
			// do nothing
		}

		public HashList(T[] elements) {
			for (T e : elements) {
				backing.put(e, e);
			}
		}

		public void add(T element) {
			backing.put(element, element);
		}

		public boolean contains(T element) {
			return backing.containsKey(element);
		}

		@Override
		public Iterator<T> iterator() {
			return backing.keySet().iterator();
		}
	}

}
