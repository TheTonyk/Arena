package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.util.Iterator;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Managers.Settings;
import com.thetonyk.Arena.Managers.PermissionsManager.Rank;

public class ChatFeature implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		try {
			
			Rank rank = PlayersManager.getRank(player.getUniqueId());
			String format = rank.getPrefix() + "§7" + player.getName() + " §8⫸ §f%2$s";
		
			Iterator<Player> receivers = event.getRecipients().iterator();
			
			while (receivers.hasNext()) {
				
				Player receiver = receivers.next();
				Settings settings = Settings.getSettings(receiver.getUniqueId());
				
				if (!settings.getChat() || settings.getIgnored().contains(player.getUniqueId())) {
					
					receivers.remove();
					continue;
					
				}
				
				if (message.contains(receiver.getName()) && settings.getMentions()) {
					
					receivers.remove();
					receiver.sendMessage(rank.getPrefix() + "§7" + player.getName() + " §8⫸ §f" + message.replaceAll(receiver.getName(), "§a§l" + receiver.getName() + "§r"));
					receiver.playSound(receiver.getLocation(), Sound.ORB_PICKUP, 1, 1);
					continue;
					
				}
				
			}
			
			event.setFormat(format);
		
		} catch (SQLException exception) {
			
			event.setCancelled(true);
			exception.printStackTrace();
			player.sendMessage("An error has occured while processing the command. Please try again later.");
			return;
			
		}
		
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();
		String message = event.getMessage();
		Command command = Main.plugin.getCommand(message.substring(1).split(" ")[0]);
		
		if (!(command == null || !player.hasPermission(command.getPermission())) || player.isOp()) return;
			
		event.setCancelled(true);
		player.sendMessage(Main.PREFIX + "Unknown command.");
		
	}
	
}
