package com.thetonyk.Arena.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.DataManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ArenaCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		World world = player.getWorld();
		Location location = player.getLocation();
		
		try {
		
			if (args.length > 0) {
				
				if (args[0].equalsIgnoreCase("y")) {
					
					if (args.length < 2) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " y <y>");
						return true;
						
					}
					
					int y;
					
					try {
						
						y = Integer.valueOf(args[1]);
						
					} catch (NumberFormatException exception) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " y <y>");
						return true;
						
					}
					
					if (y < 1) {
						
						sender.sendMessage(Main.PREFIX + "The y can't be negative.");
						return true;
						
					}
					
					DataManager.y = y;
					DataManager.updateValue(String.valueOf(DataManager.y), "y");
					
					sender.sendMessage(Main.PREFIX + "The y has been set to §6" + y + "§7.");
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("spawnpoints")) {
						
					if (args.length >= 2) {
						
						if (DataManager.spawnspoints == null) {
							
							sender.sendMessage(Main.PREFIX + "Unable to get spawnpoints. Try again.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("list")) {
							
							if (DataManager.spawnspoints.isEmpty()) {
								
								sender.sendMessage(Main.PREFIX + "There is no spawn points in this world.");
								return true;
								
							}
							
							sender.sendMessage(Main.PREFIX + "List of spawn points:");
							
							for (Location spawn : DataManager.spawnspoints) {
								
								ComponentBuilder message = new ComponentBuilder("⫸ ").color(ChatColor.DARK_GRAY)
								.append("x: ").color(ChatColor.GRAY)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("Click to remove.").color(ChatColor.GRAY).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " spawnpoints remove " + spawn.getX() + " " + spawn.getY() + " " + spawn.getZ()))
								.append(String.valueOf(spawn.getBlockX())).color(ChatColor.GOLD)
								.append(", y: ").color(ChatColor.GRAY)
								.append(String.valueOf(spawn.getBlockY())).color(ChatColor.GOLD)
								.append(", z: ").color(ChatColor.GRAY)
								.append(String.valueOf(spawn.getBlockZ())).color(ChatColor.GOLD);
								
								player.spigot().sendMessage(message.create());
								
							}
							
							sender.sendMessage(Main.PREFIX + "§6" + DataManager.spawnspoints.size() + "§7 spawn points listed.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("remove")) {
							
							if (args.length < 5) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " spawnpoints remove [x] [y] [z]");
								return true;
								
							}
							
							try {
			
								location = new Location(world, Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]));
								
							} catch (NumberFormatException exception) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " spawnpoints remove [x] [y] [z]");
								return true;
								
							}
							
							location = ArenaCommand.getLocation(location, DataManager.spawnspoints);
							
							if (location == null) sender.sendMessage(Main.PREFIX + "Unable to find this spawn point."); 
							else {
								
								sender.sendMessage(Main.PREFIX + "The spawn point at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been removed.");
								
								DataManager.spawnspoints.remove(location);
								DataManager.updateValue(DataManager.getFormatted(DataManager.spawnspoints), "spawnpoints");
							
							}
							
							return true;
							
						}
						
					}
					
					if (args.length < 4) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " spawnpoints [x] [y] [z]");
						return true;
						
					}
					
					try {

						location = new Location(world, Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3]));
						
					} catch (NumberFormatException exception) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " spawnpoints [x] [y] [z]");
						return true;
						
					}
					
					sender.sendMessage(Main.PREFIX + "The spawn point at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been added.");
					
					DataManager.spawnspoints.add(location);
					DataManager.updateValue(DataManager.getFormatted(DataManager.spawnspoints), "spawnpoints");
					return true;	
					
				}
				
				if (args[0].equalsIgnoreCase("stats")) {
						
					if (args.length >= 2) {
						
						if (DataManager.stats == null) {
							
							sender.sendMessage(Main.PREFIX + "Unable to get stats locations. Try again.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("list")) {
							
							if (DataManager.stats.isEmpty()) {
								
								sender.sendMessage(Main.PREFIX + "There is no stats locations in this world.");
								return true;
								
							}
							
							sender.sendMessage(Main.PREFIX + "List of stats locations:");
							
							for (Location stats : DataManager.stats) {
								
								ComponentBuilder message = new ComponentBuilder("⫸ ").color(ChatColor.DARK_GRAY)
								.append("x: ").color(ChatColor.GRAY)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("Click to remove.").color(ChatColor.GRAY).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " stats remove " + stats.getX() + " " + stats.getY() + " " + stats.getZ()))
								.append(String.valueOf(stats.getBlockX())).color(ChatColor.GOLD)
								.append(", y: ").color(ChatColor.GRAY)
								.append(String.valueOf(stats.getBlockY())).color(ChatColor.GOLD)
								.append(", z: ").color(ChatColor.GRAY)
								.append(String.valueOf(stats.getBlockZ())).color(ChatColor.GOLD);
								
								player.spigot().sendMessage(message.create());
								
							}
							
							sender.sendMessage(Main.PREFIX + "§6" + DataManager.stats.size() + "§7 stats locations listed.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("remove")) {
							
							if (args.length < 5) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " stats remove [x] [y] [z]");
								return true;
								
							}
							
							try {
			
								location = new Location(world, Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]));
								
							} catch (NumberFormatException exception) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " stats remove [x] [y] [z]");
								return true;
								
							}
							
							location = ArenaCommand.getLocation(location, DataManager.spawnspoints);
							
							if (location == null) sender.sendMessage(Main.PREFIX + "Unable to find this stats location."); 
							else {
								
								sender.sendMessage(Main.PREFIX + "The stats location at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been removed.");
								
								DataManager.stats.remove(location);
								DataManager.updateValue(DataManager.getFormatted(DataManager.stats), "stats");
							
							}
							
							return true;
							
						}
						
					}
					
					if (args.length < 4) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " stats [x] [y] [z]");
						return true;
						
					}
					
					try {

						location = new Location(world, Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3]));
						
					} catch (NumberFormatException exception) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " stats [x] [y] [z]");
						return true;
						
					}
					
					sender.sendMessage(Main.PREFIX + "The stats location at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been added.");
					
					DataManager.stats.add(location);
					DataManager.updateValue(DataManager.getFormatted(DataManager.stats), "stats");
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("bests")) {
					
					if (args.length >= 2) {
						
						if (DataManager.bests == null) {
							
							sender.sendMessage(Main.PREFIX + "Unable to get betsts locations. Try again.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("list")) {
							
							if (DataManager.bests.isEmpty()) {
								
								sender.sendMessage(Main.PREFIX + "There is no bests locations in this world.");
								return true;
								
							}
							
							sender.sendMessage(Main.PREFIX + "List of bests locations:");
							
							for (Location bests : DataManager.bests) {
								
								ComponentBuilder message = new ComponentBuilder("⫸ ").color(ChatColor.DARK_GRAY)
								.append("x: ").color(ChatColor.GRAY)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("Click to remove.").color(ChatColor.GRAY).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " bests remove " + bests.getX() + " " + bests.getY() + " " + bests.getZ()))
								.append(String.valueOf(bests.getBlockX())).color(ChatColor.GOLD)
								.append(", y: ").color(ChatColor.GRAY)
								.append(String.valueOf(bests.getBlockY())).color(ChatColor.GOLD)
								.append(", z: ").color(ChatColor.GRAY)
								.append(String.valueOf(bests.getBlockZ())).color(ChatColor.GOLD);
								
								player.spigot().sendMessage(message.create());
								
							}
							
							sender.sendMessage(Main.PREFIX + "§6" + DataManager.bests.size() + "§7 bests locations listed.");
							return true;
							
						}
						
						if (args[1].equalsIgnoreCase("remove")) {
							
							if (args.length < 5) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " bests remove [x] [y] [z]");
								return true;
								
							}
							
							try {
			
								location = new Location(world, Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]));
								
							} catch (NumberFormatException exception) {
								
								sender.sendMessage(Main.PREFIX + "Usage: /" + label + " bests remove [x] [y] [z]");
								return true;
								
							}
							
							location = ArenaCommand.getLocation(location, DataManager.spawnspoints);
							
							if (location == null) sender.sendMessage(Main.PREFIX + "Unable to find this bests location."); 
							else {
								
								sender.sendMessage(Main.PREFIX + "The bests location at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been removed.");
								
								DataManager.bests.remove(location);
								DataManager.updateValue(DataManager.getFormatted(DataManager.bests), "bests");
							
							}
							
							return true;
							
						}
						
					}
					
					if (args.length < 4) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " bests [x] [y] [z]");
						return true;
						
					}
					
					try {

						location = new Location(world, Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3]));
						
					} catch (NumberFormatException exception) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /" + label + " bests [x] [y] [z]");
						return true;
						
					}
					
					sender.sendMessage(Main.PREFIX + "The bests location at x:§6" + location.getBlockX() + " §7y:§6" + location.getBlockY() + " §7z:§6" + location.getBlockZ() + " §7has been added.");
					
					DataManager.bests.add(location);
					DataManager.updateValue(DataManager.getFormatted(DataManager.bests), "bests");
					return true;
					
				}
				
			}
			
		} catch (SQLException exception) {
			
			sender.sendMessage(Main.PREFIX + "An error has occured while processing the command. Please try again later.");
			return true;
			
		}
		
		sender.sendMessage(Main.PREFIX + "Usage of /" + label+ ":");
		sender.sendMessage("§8⫸ §6/" + label+ " y <y> §8- §7Set the y max of the arena area.");
		sender.sendMessage("§8⫸ §6/" + label+ " spawnpoints <list|remove> §8- §7Manager spawnpoints.");
		sender.sendMessage("§8⫸ §6/" + label+ " spawnpoints <x> <y> <z> §8- §7Add a spawnpoint.");
		sender.sendMessage("§8⫸ §6/" + label+ " stats <list|remove> §8- §7Manager stats locations.");
		sender.sendMessage("§8⫸ §6/" + label+ " stats <x> <y> <z> §8- §7Add a stats location.");
		sender.sendMessage("§8⫸ §6/" + label+ " bests <list|remove> §8- §7Manager bests locations.");
		sender.sendMessage("§8⫸ §6/" + label+ " bests <x> <y> <z> §8- §7Add a bests location.");
		return true;
		
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
		
			case 1:
				suggestions.add("y");
				suggestions.add("spawnpoints");
				suggestions.add("stats");
				suggestions.add("bests");
				break;
			case 2:
				
				switch (args[0]) {
				
					case "spawnpoints":
					case "stats":
					case "bests":
						suggestions.add("list");
						suggestions.add("remove");
						break;
					default:
						break;
				
				}
				
			default:
				break;
			
		}
		
		if (!args[args.length - 1].isEmpty()) {
			
			suggestions = suggestions.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
			
		}
		
		return suggestions;
		
	}
	
	public static Location getLocation(Location location, List<Location> locations) throws SQLException {
		
		Iterator<Location> iterator = locations.iterator();
		
		while (iterator.hasNext()) {
			
			Location loc = iterator.next();
			
			if (loc.getX() != location.getX() || loc.getY() != location.getY() || loc.getZ() != location.getZ() || !loc.getWorld().equals(location.getWorld())) continue;
		
			iterator.remove();
			return loc;
			
		}
		
		return null;
		
	}

}