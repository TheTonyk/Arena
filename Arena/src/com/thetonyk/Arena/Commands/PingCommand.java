package com.thetonyk.Arena.Commands;

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
import com.thetonyk.Arena.Managers.ConnectionsAverager;

public class PingCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player;
		int seconds = 1;
		
		if (args.length < 1) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Only player can see their ping.");
				return true;
				
			}
			
			player = (Player) sender;
			
		} else {
			
			player = Bukkit.getPlayer(args[0]);
			
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player'§a" + args[0] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		if (args.length >= 2) {
		
			try {
				
				seconds = Integer.valueOf(args[1]);
				
			} catch (NumberFormatException exception) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /" + label + " [player] [seconds]");
				return true;
				
			}
			
			if (seconds < 1) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /" + label + " [player] [seconds]");
				return true;
				
			}
			
			if (seconds > 300) {
				
				sender.sendMessage(Main.PREFIX + "You can't see ping average of more than 5 minutes.");
				return true;
				
			}
		
		}
		
		int ping = (int) ConnectionsAverager.getPing(player.getUniqueId(), seconds * 20);
		
		if (ping < 1) {
			
			sender.sendMessage(Main.PREFIX + (sender.getName().equalsIgnoreCase(player.getName()) ? "Your ping " : "The ping of '§a" + player.getName() + "§7' ") + "isn't yet calculated.");
			return true;
			
		}
		
		sender.sendMessage(Main.PREFIX + (sender.getName().equalsIgnoreCase(player.getName()) ? "Your ping: §6" : "Ping of '§a" + player.getName() + "§7': §6") + ping + "§7ms");
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
			case 2:
				suggestions.add("1");
				suggestions.add("5");
				suggestions.add("10");
				suggestions.add("60");
				suggestions.add("120");
				suggestions.add("300");
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
