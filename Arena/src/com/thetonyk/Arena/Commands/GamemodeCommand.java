package com.thetonyk.Arena.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player;
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /gamemode <gamemode> [player]");
			return true;
			
		}
		
		GameMode gamemode;
		
		try {
			
			int number = Integer.valueOf(args[0]);
			
			if (number == 0) number = 1;
			else if (number == 1) number = 0;
			
			gamemode = GameMode.values()[number];
			
		} catch (NumberFormatException exception) {
			
			try {
				
				gamemode = GameMode.valueOf(args[0].toUpperCase());
				
			} catch (IllegalArgumentException exception2) {
				
				sender.sendMessage(Main.PREFIX + "Incorrect gamemode. Please try again.");
				return true;
				
			}
			
		}
		
		if (gamemode == null) {
			
			sender.sendMessage(Main.PREFIX + "Incorrect gamemode. Please try again.");
			return true;
			
		}
		
		if (args.length < 2) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Only player can toggle their gamemode.");
				return true;
				
			}
			
			player = (Player) sender;
		
		} else {
			
			player = Bukkit.getPlayer(args[1]);
		
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		player.setGameMode(gamemode);
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The gamemode of player '§a" + player.getName() + "§7' has been set to §6" + gamemode.name().toLowerCase() + "§7.");
		
		player.sendMessage(Main.PREFIX + "Your gamemode has been set to §6" + gamemode.name().toLowerCase() + "§7.");
		return true;
		
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
		
			case 1:
				Set<String> gamemodes = new HashSet<>();
				
				Arrays.stream(GameMode.values()).forEach(g -> gamemodes.add(g.name().toLowerCase()));
				suggestions.addAll(gamemodes);
				break;
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
