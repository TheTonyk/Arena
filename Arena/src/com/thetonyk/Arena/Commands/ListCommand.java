package com.thetonyk.Arena.Commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PermissionsManager.Rank;
import com.thetonyk.Arena.Managers.PlayersManager;

public class ListCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (Bukkit.getOnlinePlayers().isEmpty()) {
			
			sender.sendMessage(Main.PREFIX + "The server is empty.");
			return true;
			
		}
		
		sender.sendMessage(Main.PREFIX + "List of online players:");
		
		try {
		
			for (Player player : Bukkit.getOnlinePlayers()) {
				
				Rank rank = PlayersManager.getRank(player.getUniqueId());
				int health = (int) (player.getHealth() / 2) * 10;
				
				sender.sendMessage("§8⫸ " + rank.getPrefix() + "§7" + player.getName() + " §8- §6" + health + "§4♥");
				
			}
			
		} catch (SQLException exception) {
			
			sender.sendMessage(Main.PREFIX + "An error has occured while processing the command. Please try again later.");
			return true;
			
		}
		
		sender.sendMessage(Main.PREFIX + "§6" + Bukkit.getOnlinePlayers().size() + "§7 online players listed.");
		return true;
		
	}

}
