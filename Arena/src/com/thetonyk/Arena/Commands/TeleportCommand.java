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
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.SpecFeature;

public class TeleportCommand implements CommandExecutor, TabExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.teleportplayers") && (!(sender instanceof Player) || !SpecFeature.isSpectator((Player) sender))) {
			
			sender.sendMessage(Main.PREFIX + "You can only use this command while spectating.");
			return true;
			
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /" + label + " <player> " + (sender.hasPermission("global.teleportplayers") ? " [player]" : ""));
			return true;
			
		}
		
		Player player;
		Player destination;
		
		if (args.length < 2 || !sender.hasPermission("global.teleportplayers")) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Only player can teleport himself.");
				return true;
				
			}
			
			player = (Player) sender;
			destination = Bukkit.getPlayer(args[0]);
			
			if (destination == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
				return true;
				
			}

		} else {
			
			destination = Bukkit.getPlayer(args[1]);
			
			if (destination == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' is not online.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("*")) {
				
				Bukkit.getOnlinePlayers().stream().filter(p -> !SpecFeature.isSpectator(p)).forEach(p -> p.teleport(destination));
				
				Bukkit.broadcastMessage(Main.PREFIX + "All players were teleported to player '§6" + destination.getName() + "§7'.");
				return true;

			}
			
			player = Bukkit.getPlayer(args[0]);
		
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		player.teleport(destination);
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The player '§a" + player.getName() + "§7' has been teleported to the player '§6" + destination.getName() + "§7'.");
		
		player.sendMessage(Main.PREFIX + "You have been teleported to the player '§6" + destination.getName() + "§7'.");
		return true;
		
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		Set<String> players = new HashSet<>();
		
		switch (args.length) {
		
			case 1:
				players.clear();
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> players.add(p.getName()));
				suggestions.addAll(players);
				break;
			case 2:
				if (!sender.hasPermission("arena.teleportplayers")) break;
				
				players.clear();
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> players.add(p.getName()));
				suggestions.addAll(players);
			default:
				break;
			
		}
		
		if (!args[args.length - 1].isEmpty()) {
			
			suggestions = suggestions.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
			
		}
		
		return suggestions;
		
	}
	
}
