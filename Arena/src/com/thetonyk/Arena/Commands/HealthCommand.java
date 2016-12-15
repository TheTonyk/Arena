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
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public class HealthCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = null;
		
		if (args.length < 1 && !(sender instanceof Player)) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /" + label + " <player>");
			return true;
			
		} else player = (Player) sender;
		
		if (args.length >= 1) {
			
			player = Bukkit.getPlayer(args[0]);
			
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		int health = (int) ((player.getHealth() / 2) * 10);
		int maxHealth = (int) ((player.getMaxHealth() / 2) * 10);
		int absorptionHealth = (int) ((nmsPlayer.getAbsorptionHearts() / 2) * 10);
		
		sender.sendMessage(Main.PREFIX + "Health of '§a" + player.getName() + "§7':");
		sender.sendMessage("§8⫸ §7Health: §6" + health + "§7%");
		sender.sendMessage("§8⫸ §7Max Health: §6" + maxHealth + "§7%");
		sender.sendMessage("§8⫸ §7Absorption: §6" + absorptionHealth + "§7%");
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
