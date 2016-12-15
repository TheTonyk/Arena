package com.thetonyk.Arena.Commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.Arena.Main;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MinecraftKey;

public class GiveCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length < 2) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /give <player> <item> [number] [damage]");
			return true;
			
		}
		
		MinecraftKey key = new MinecraftKey(args[1]);
		
		Player player = Bukkit.getPlayer(args[0]);
		Material type = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(key)).getType();
		int number = 1;
		int damage = 0;
		
		if (player == null && !args[0].equalsIgnoreCase("*")) {
			
			sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
			return true;
			
		}
		
		if (type == Material.AIR) {
			
			sender.sendMessage(Main.PREFIX + "The item '§6" + args[1] + "§7' is not valid.");
			return true;
			
		}
		
		if (args.length >= 3) {
			
			try {
				
				number = Integer.valueOf(args[2]);
				
			} catch (NumberFormatException exception) {
				
				sender.sendMessage(Main.PREFIX + "The number '§6" + args[2] + "§7' is not valid.");
				return true;
				
			}
			
			if (number < 0) {
				
				sender.sendMessage(Main.PREFIX + "The number '§6" + args[2] + "§7' is not valid.");
				return true;
				
			}
			
		}
		
		if (args.length >= 4) {
			
			try {
				
				damage = Integer.valueOf(args[3]);
				
			} catch (NumberFormatException exception) {
				
				sender.sendMessage(Main.PREFIX + "The number '§6" + args[3] + "§7' is not valid.");
				return true;
				
			}
			
			if (damage < 0) {
				
				sender.sendMessage(Main.PREFIX + "The number '§6" + args[3] + "§7' is not valid.");
				return true;
				
			}
			
		}
		
		ItemStack item = new ItemStack(type, number, (short) damage);
		
		if (args[0].equalsIgnoreCase("*")) {
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				Map<Integer, ItemStack> items = online.getInventory().addItem(item);
				
				items.values().stream().forEach(i -> online.getWorld().dropItem(online.getLocation(), i));
				
			}
			
			Bukkit.broadcastMessage(Main.PREFIX + "All players receive §6" + number + " " + type.toString().toLowerCase().replaceAll("_", " ") + "§7.");
			
		}
		
		Map<Integer, ItemStack> items = player.getInventory().addItem(item);
		
		items.values().stream().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The player '§a" + player.getName() + "§7' receive §6" + number + " " + type.toString().toLowerCase().replaceAll("_", " ") + "§7.");
		
		player.sendMessage(Main.PREFIX + "Your receive §6" + number + " " + type.toString().toLowerCase().replaceAll("_", " ") + "§7.");
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
