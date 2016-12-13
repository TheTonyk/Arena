package com.thetonyk.Arena.Commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.thetonyk.Arena.Main;

public class WhitelistCommand implements CommandExecutor, TabExecutor {
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length > 0) {
				
			if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "/" + label + " " + args[0] + " <player>");
					return true;
					
				}
				
				OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
				
				if (player == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' doesn't exist.");
					return true;
					
				}
				
				boolean action = args[0].equalsIgnoreCase("add") ? true : false;
				
				if (action == player.isWhitelisted()) {
					
					sender.sendMessage(Main.PREFIX + "The player '§a" + player.getName() + "§7' is " + (action ? "already" : "not") + " whitelisted.");
					return true;
					
				}
				
				player.setWhitelisted(action);
				sender.sendMessage(Main.PREFIX + "The player '§a" + player.getName() + "§7' has been " + (action ? "whitelisted" : "unwhitelisted") + ".");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
				
				Boolean whitelist = args[0].equalsIgnoreCase("on");
				
				if (whitelist == Bukkit.hasWhitelist()) {
					
					sender.sendMessage(Main.PREFIX + "The whitelist is already §a" + args[0] + "§7.");
					return true;
					
				}
				
				Bukkit.setWhitelist(whitelist);
				sender.sendMessage(Main.PREFIX + "The whitelist is now §a" + args[0] + "§7.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("all")) {
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> p.setWhitelisted(true));
				
				Bukkit.broadcastMessage(Main.PREFIX + "All players are been whitelisted.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("clear")) {
				
				if (Bukkit.getWhitelistedPlayers().size() <= 0) {
					
					sender.sendMessage(Main.PREFIX + "There is not whitelisted players.");
					return true;
					
				}
				
				Bukkit.getWhitelistedPlayers().stream().forEach(p -> p.setWhitelisted(false));
				
				Bukkit.broadcastMessage(Main.PREFIX + "The whitelist has been cleared");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("status")) {
				
				String status = Bukkit.hasWhitelist() ? "on" : "off";
				
				sender.sendMessage(Main.PREFIX + "The whitelist is §a" + status + "§7.");
				sender.sendMessage(Main.PREFIX + "There are §a" + Bukkit.getWhitelistedPlayers().size() + "§7 whitelisted players.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				
				if (Bukkit.getWhitelistedPlayers().size() <= 0) {
					
					sender.sendMessage(Main.PREFIX + "There is not whitelisted players.");
					return true;
					
				}
				
				sender.sendMessage(Main.PREFIX + "List of whitelisted players:");
				Bukkit.getWhitelistedPlayers().stream().forEach(p -> sender.sendMessage("§8⫸ §7'" + (p.isOnline() ? "§a" : "§c") + p.getName() + "§7'"));
				sender.sendMessage(Main.PREFIX + "§6" + Bukkit.getWhitelistedPlayers().size() + "§7 whitelisted players listed.");
				return true;
				
			}
			
		}
		
		sender.sendMessage(Main.PREFIX + "Usage of /" + label+ ":");
		sender.sendMessage("§8⫸ §6/" + label+ " add <player> §8- §7Add a player.");
		sender.sendMessage("§8⫸ §6/" + label+ " remove <player> §8- §7Remove a player.");
		sender.sendMessage("§8⫸ §6/" + label+ " on|off §8- §7Enable/Disable the whitelist.");
		sender.sendMessage("§8⫸ §6/" + label+ " all §8- §7Whitelist all players.");
		sender.sendMessage("§8⫸ §6/" + label+ " clear §8- §7Clear the whitelist.");
		sender.sendMessage("§8⫸ §6/" + label+ " status §8- §7See status of whitelist.");
		sender.sendMessage("§8⫸ §6/" + label+ " list §8- §7List whitelisted players.");
		return true;
		
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
		
			case 1:
				suggestions.add("add");
				suggestions.add("remove");
				suggestions.add("on");
				suggestions.add("off");
				suggestions.add("all");
				suggestions.add("clear");
				suggestions.add("status");
				suggestions.add("list");
				break;
			case 2:
				
				Set<String> players = new HashSet<>();
				
				if (args[0].equalsIgnoreCase("add")) Bukkit.getOnlinePlayers().stream().forEach(p -> players.add(p.getName()));
				else if (args[0].equalsIgnoreCase("remove")) Bukkit.getWhitelistedPlayers().stream().forEach(p -> players.add(p.getName()));
				
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
