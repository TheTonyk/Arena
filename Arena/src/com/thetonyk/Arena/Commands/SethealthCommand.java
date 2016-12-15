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

public class SethealthCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player;
		int percents;
		boolean maxhealth = false;
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /sethealth <health> [player] [maxhealth]");
			return true;
			
		}
		
		try {
			
			percents = Integer.valueOf(args[0]);
			
		} catch (NumberFormatException exception) {
			
			sender.sendMessage(Main.PREFIX + "The number '§6" + args[0] + "§7' is not valid.");
			return true;
			
		}
		
		if (percents < 0) {
			
			sender.sendMessage(Main.PREFIX + "The number '§6" + args[0] + "§7' is not valid.");
			return true;
			
		}
		
		if (args.length >= 3) maxhealth = Boolean.valueOf(args[2]);
		
		if (args.length < 2) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /sethealth <health> [player] [maxhealth]");
				return true;
				
			}
			
			player = (Player) sender;
		
		} else {
			
			if (args[1].equalsIgnoreCase("*")) {
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					
					double health = (percents / 10) * 2;
					
					if (maxhealth) {
						
						online.setMaxHealth(health);
						continue;
						
					}
					
					if (health > online.getMaxHealth()) health = online.getMaxHealth();
					
					online.setHealth(health);
					
				}
				
				Bukkit.broadcastMessage(Main.PREFIX + "All players " + (maxhealth ? "max health" : "health") + " was set to §6" + percents + "§7%.");
				return true;
				
			}
			
			player = Bukkit.getPlayer(args[1]);
		
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		double health = (percents / 10) * 2;
		
		if (health > player.getMaxHealth() && !maxhealth) {
			
			health = player.getMaxHealth();
			percents = (int) (health / 2) * 10;
			
		}
		
		if (maxhealth) player.setMaxHealth(health);
		else player.setHealth(health);
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The " + (maxhealth ? "max health" : "health") + " of player '§a" + player.getName() + "§7' was set to §6" + percents + "§7%.");
		
		player.sendMessage(Main.PREFIX + "Your " + (maxhealth ? "max health" : "health") + " was set to §6" + percents + "§7%.");
		return true;
		
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
		
			case 2:
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
