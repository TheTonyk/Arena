package com.thetonyk.Arena.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Inventories.LeaderboardsInventory;

public class LeaderboardsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		player.openInventory(LeaderboardsInventory.getInventory());
		return true;
		
	}
	
}
