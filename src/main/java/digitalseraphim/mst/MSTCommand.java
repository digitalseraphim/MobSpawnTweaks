package digitalseraphim.mst;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class MSTCommand extends CommandBase {

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandName() {
		return "mst";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/mst (whitelist|blacklist) [dimid] - set which type of filtering to use\n"
				+ "/mst add [dimid] <name|class> - add to the filtering list\n"
				+ "/mst remove [dimid] <name|class> - remove from the filtering list (must be exact)\n"
				+ "/mst list [dimid] - display current filtering list";
	}

	enum Commands {
		Whitelist, Blacklist, Add, Remove, List;

		public static Commands fromString(String s) {
			for (Commands c : values()) {
				if (c.name().equalsIgnoreCase(s)) {
					return c;
				}
			}
			return null;
		}
	}

	private void sendMessage(ICommandSender ics, String[] msg) {
		for (String s : msg) {
			ics.addChatMessage(new ChatComponentText(s));
		}
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		int dimId = ics.getEntityWorld().provider.dimensionId;
		Configuration cf = MobSpawnTweaks.configFile;
		if (args.length > 0) {
			Commands c = Commands.fromString(args[0]);

			switch (c) {
			case Whitelist: {
				String catname;
				String[] list = null;
				
				if (args.length > 1) {
					dimId = Integer.parseInt(args[1]);
				}

				catname = "dimension." + dimId;
				
				ConfigCategory dim = cf.getCategory(catname);
				
				if(dim.containsKey("whitelist")){
					list = dim.get("whitelist").getStringList();
				}else if(dim.containsKey("blacklist")){
					list = dim.get("blacklist").getStringList();
				}
			}
				break;

			case Blacklist:
				break;
			case Add:
				break;
			case Remove:
				break;
			case List:
				break;
			}
		} else {
			sendMessage(ics, new String[] { getCommandUsage(ics) });
		}
	}
}
