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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Utils.NamesUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class EffectCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage of /" + label+ ":");
			sender.sendMessage("§8⫸ §6/" + label+ " <effect> <duration> <level> [player] §8- §7Add an effect to a player.");
			sender.sendMessage("§8⫸ §6/" + label+ " remove [player] §8- §7Remove an effect from a player.");
			sender.sendMessage("§8⫸ §6/" + label+ " clear [player] §8- §7Remove all the effects from a player.");
			return true;
			
		}
		
		Player player;
		
		if (args[0].equalsIgnoreCase("clear")) {
			
			if (args.length < 2) {
				
				if (!(sender instanceof Player)) {
					
					sender.sendMessage(Main.PREFIX + "Only player can clear his effects.");
					return true;
					
				}
				
				player = (Player) sender;
			
			} else {
				
				if (args[1].equalsIgnoreCase("*")) {
					
					Bukkit.getOnlinePlayers().stream().forEach(p -> p.getActivePotionEffects().stream().forEach(e -> p.removePotionEffect(e.getType())));
					Bukkit.broadcastMessage(Main.PREFIX + "All players effects were cleared.");
					return true;
					
				}
				
				player = Bukkit.getPlayer(args[1]);
			
				if (player == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' is not online.");
					return true;
					
				}
				
			}
			
			for (PotionEffect effect : player.getActivePotionEffects()) {
				
				player.removePotionEffect(effect.getType());
				
			}
			
			if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The effects of player were been cleared.");
			
			player.sendMessage(Main.PREFIX + "Your effects were been cleared.");
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("remove")) {
			
			if (args.length < 2) {
				
				if (!(sender instanceof Player)) {
					
					sender.sendMessage(Main.PREFIX + "Only player can remove his effects.");
					return true;
					
				}
				
				player = (Player) sender;
			
			} else {
				
				player = Bukkit.getPlayer(args[1]);
			
				if (player == null && !args[1].equalsIgnoreCase("*")) {
					
					sender.sendMessage(Main.PREFIX + "The player '§a" + args[1] + "§7' is not online.");
					return true;
					
				}
				
			}
			
			if (args.length < 3) {
				
				if (!(sender instanceof Player)) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " remove <player> <effect>");
					return true;
					
				}
			
				List<PotionEffectType> types = new ArrayList<>();
				
				if (args.length >= 2 && args[1].equalsIgnoreCase("*")) {
					
					Bukkit.getOnlinePlayers().stream().forEach(p -> p.getActivePotionEffects().stream().filter(e -> !types.contains(e.getType())).forEach(e -> types.add(e.getType())));
					
				} else {
					
					player.getActivePotionEffects().stream().forEach(e -> types.add(e.getType()));
					
				}
				
				sender.sendMessage(Main.PREFIX + "Choose an effect to remove: §8(§7Click on it§8)");
				
				for (PotionEffectType type : types) {
					
					ComponentBuilder message = new ComponentBuilder("§8⫸ ");
					message.append(NamesUtils.getPotionName(type)).color(ChatColor.GOLD);
					message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " remove " + (args[1].equalsIgnoreCase("*") ? "*" : player.getName()) + " " + NamesUtils.getPotionName(type).toLowerCase().replaceAll(" ", "_")));
					message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to remove.").color(ChatColor.GRAY).create()));
					((Player) sender).spigot().sendMessage(message.create());
					
				}
				
				return true;
			
			}
			
			PotionEffectType type = NamesUtils.getPotionEffect(args[2]);
			
			if (type == null) {
				
				sender.sendMessage(Main.PREFIX + "The effect '§6" + args[0] + "§7' is not valid.");
				return true;
				
			}
			
			if (args[1].equalsIgnoreCase("*")) {
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> p.removePotionEffect(type));
				Bukkit.broadcastMessage(Main.PREFIX + "The effect §6" + NamesUtils.getPotionName(type) + " §7has been removed from all players.");
				return true;
				
			}
			
			player.removePotionEffect(type);
			
			if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The effects §6" + NamesUtils.getPotionName(type) + " §7has been removed from '§6" + player.getName() + "§7.");
			
			player.sendMessage(Main.PREFIX + "Your effect §6" + NamesUtils.getPotionName(type) + " §7has been removed.");
			return true;
			
		}
		
		if (args.length < 3) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /" + label + " <effect> <duration> <level> [player]");
			return true;
			
		}
		
		PotionEffectType type = NamesUtils.getPotionEffect(args[0]);
		int duration;
		int level;
		
		if (type == null) {
			
			sender.sendMessage(Main.PREFIX + "The effect '§6" + args[0] + "§7' is not valid.");
			return true;
			
		}
		
		try {
			
			duration = Integer.valueOf(args[1]);
			
		} catch (NumberFormatException exception) {
			
			sender.sendMessage(Main.PREFIX + "The duration '§6" + args[1] + "§7' is not correct.");
			return true;
			
		}
		
		if (duration < 1) {
			
			sender.sendMessage(Main.PREFIX + "The duration '§6" + args[1] + "§7' is not correct.");
			return true;
			
		}
		
		try {
			
			level = Integer.valueOf(args[2]);
			
		} catch (NumberFormatException exception) {
			
			sender.sendMessage(Main.PREFIX + "The level '§6" + args[2] + "§7' is not correct.");
			return true;
			
		}
		
		if (duration < 1) {
			
			sender.sendMessage(Main.PREFIX + "The level '§6" + args[2] + "§7' is not correct.");
			return true;
			
		}
		
		PotionEffect effect = new PotionEffect(type, duration * 20, level - 1, false, false);
		
		if (args.length < 4) {
			
			if (!(sender instanceof Player)) {
				
				sender.sendMessage(Main.PREFIX + "Only player can change his effects.");
				return true;
				
			}
			
			player = (Player) sender;
		
		} else {
			
			if (args[3].equalsIgnoreCase("*")) {
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> p.addPotionEffect(effect, true));
				Bukkit.broadcastMessage(Main.PREFIX + "All players get effect §6" + NamesUtils.getPotionName(type) + " " + level + "§7 for §6" + duration + "§7s.");
				return true;
				
			}
			
			player = Bukkit.getPlayer(args[3]);
		
			if (player == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§a" + args[3] + "§7' is not online.");
				return true;
				
			}
			
		}
		
		boolean success = player.addPotionEffect(effect, true);
		
		if (!success) {
			
			sender.sendMessage(Main.PREFIX + "The effect could not be added.");
			return true;
			
		}
		
		if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The player '§a" + player.getName() + "§7' get effect §6" + NamesUtils.getPotionName(type) + " " + level + "§7 for §6" + duration + "§7s.");
		
		player.sendMessage(Main.PREFIX + "You've get effect §6" + NamesUtils.getPotionName(type) + " " + level + "§7 for §6" + duration + "§7s.");
		return true;
		
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		Set<String> players = new HashSet<>();
		
		switch (args.length) {
		
			case 1:
				suggestions.add("remove");
				suggestions.add("clear");
				suggestions.addAll(NamesUtils.EFFECTS);
				break;
			case 2:
				if (!args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("clear")) break;
				
				players.clear();
				Bukkit.getOnlinePlayers().stream().forEach(p -> players.add(p.getName()));
				suggestions.addAll(players);
				break;
			case 3:
				if (!args[0].equalsIgnoreCase("remove")) break;
				
				suggestions.addAll(NamesUtils.EFFECTS);
				break;
			case 4:
				players.clear();
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
