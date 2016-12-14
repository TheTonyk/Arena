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
import com.thetonyk.Arena.Managers.PlayersManager;

public class ClearCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player;
		
		if (args.length < 1) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Only player can clear their inventory.");
				return true;
				
			}
			
			player = (Player) sender;
		
		} else {
			
			player = Bukkit.getPlayer(args[0]);
		
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		PlayersManager.clearPlayer(player);
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The inventory of player '§a" + player.getName() + "§7' has been cleared.");
		
		player.sendMessage(Main.PREFIX + "Your inventory has been cleared.");
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
