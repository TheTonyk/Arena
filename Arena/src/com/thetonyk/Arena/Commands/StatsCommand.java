package com.thetonyk.Arena.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Inventories.StatsInventory;
import com.thetonyk.Arena.Managers.Settings;

public class StatsCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (args.length >= 1) {
			
			player = Bukkit.getPlayer(args[0]);
			
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player'§a" + args[0] + "§7' is not online.");
				return true;
				
			}
			
			try {
				
				Settings settings = Settings.getSettings(player.getUniqueId());
				
				if (!settings.getStats()) {
					
					sender.sendMessage(Main.PREFIX + "You are not allowed to see the stats of '§6" + player.getName() + "§7'.");
					return true;
					
				}
				
			} catch (SQLException exception) {
				
				sender.sendMessage(Main.PREFIX + "An error has occured while processing the command. Please try again later.");
				return true;
				
			}
			
		}
		
		StatsInventory inventory = StatsInventory.getInventory(player);
		player = (Player) sender;
		
		player.openInventory(inventory.getInventory());
		return true;
		
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
		
			case 1:
				Set<String> players = new HashSet<>();
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> players.add(p.getName()));
				suggestions.addAll(players);
				break;
			default:
				break;
			
		}
		
		if (!args[args.length - 1].isEmpty()) {
			
			suggestions = suggestions.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
			
		}
		
		return suggestions;
		
	}
	
}
