package com.thetonyk.Arena.Commands;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.ConnectionsAverager;
import com.thetonyk.Arena.Utils.DateUtils;

public class LagCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		int seconds = 5;
		
		if (args.length >= 1) {
			
			try {
				
				seconds = Integer.valueOf(args[0]);
				
			} catch (NumberFormatException exception) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /" + label + " [seconds]");
				return true;
				
			}
			
			if (seconds < 1) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /" + label + " [seconds]");
				return true;
				
			}
			
			if (seconds > 300) {
				
				sender.sendMessage(Main.PREFIX + "You can't see TPS average of more than 5 minutes.");
				return true;
				
			}
		
		}
		
		Format format = new DecimalFormat("00.00");
		String tps = format.format(ConnectionsAverager.getTps(seconds * 20));
		
		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		String start = DateUtils.toText(new Date().getTime() - time, true);
		
		long ram = ((Runtime.getRuntime().maxMemory() / 1024) / 1024) - ((Runtime.getRuntime().freeMemory() / 1024) / 1024);
		long maxRam = (Runtime.getRuntime().maxMemory() / 1024) / 1024;
		
		int entities = -Bukkit.getOnlinePlayers().size();
		int chunks = 0;
		
		for (World world : Bukkit.getWorlds()) {
			
			entities += world.getEntities().size();
			chunks += world.getLoadedChunks().length;
			
		}
		
		sender.sendMessage(Main.PREFIX + "About the server:");
		sender.sendMessage("§8⫸ §7Server TPS: §a" + tps + " §7(Press tab to see TPS)");
		sender.sendMessage("§8⫸ §7Uptime: §a" + start);
		sender.sendMessage("§8⫸ §7RAM: §a" + ram + "§7MB/§c" + maxRam + "§7MB");
		sender.sendMessage("§8⫸ §7Entities: §a" + entities);
		sender.sendMessage("§8⫸ §7Loaded chunks: §a" + chunks);
		return true;
		
	}

}
