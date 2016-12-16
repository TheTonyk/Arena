package com.thetonyk.Arena.Commands;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.SpecFeature;
import com.thetonyk.Arena.Managers.PlayersManager;

public class SpectateCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = null;
		
		if (args.length < 1 && !(sender instanceof Player)) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /spectate <player>");
			return true;
			
		} else {
			
			player = (Player) sender;
			
		}
		
		try {
			
			if (args.length > 0) {
				
				if (args[0].equalsIgnoreCase("list")) {
					
					Set<UUID> spectators = SpecFeature.getSpectators();
					
					if (spectators.isEmpty()) {
						
						sender.sendMessage(Main.PREFIX + "There is no spectators.");
						return true;
						
					}
					
					sender.sendMessage(Main.PREFIX + "List of spectators:");
					
					for (UUID spectator : spectators) {
						
						String name = PlayersManager.getField(spectator, "name");
						
						sender.sendMessage("§8⫸ §7'" + (Bukkit.getPlayer(spectator) != null ? "§a" : "§c") + name + "§7'");
						
					}
					
					sender.sendMessage(Main.PREFIX + "§6" + spectators.size() + "§7 spectators listed.");
					return true;
					
				}
				
				player = Bukkit.getPlayer(args[0]);
				
				if (player == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§a" + args[0] + "§7' is not online.");
					return true;
					
				}
				
			}
			
			boolean enabled = SpecFeature.toggle(player);
			
			if (!player.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(Main.PREFIX + "The spectator mode of '§6" + player.getName() + "§7' has been " + (enabled ? "enabled" : "disabled") + ".");
		
			player.sendMessage(Main.PREFIX + "Your spectator mode has been " + (enabled ? "enabled" : "disabled") + ".");
			return true;
			
		} catch (SQLException exception) {
			
			sender.sendMessage(Main.PREFIX + "An error has occured while processing the command. Please try again later.");
			return true;
			
		}
		
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
