package com.thetonyk.Arena.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ReportCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		sender.sendMessage(Main.PREFIX + "Report a hacker:");
		sender.sendMessage("§7If a staff member is online, tell him who is the hacker.");
		sender.sendMessage("§7Else, try to record and upload the hacker.");
		
		ComponentBuilder message = new ComponentBuilder("Send us the evidence on Twitter: ").color(ChatColor.GRAY)
		.append(Main.TWITTER).color(ChatColor.AQUA)
		.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
				new ComponentBuilder("Click to see our ").color(ChatColor.GRAY)
				.append("Twitter").color(ChatColor.GREEN)
				.append(".").color(ChatColor.GRAY).create()))
		.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/" + Main.TWITTER.substring(1)));
		
		player.sendMessage(message.create());
		return true;
		
	}

}
