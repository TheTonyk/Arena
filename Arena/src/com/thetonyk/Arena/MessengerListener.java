package com.thetonyk.Arena;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.Arena.Features.SidebarFeature;
import com.thetonyk.Arena.Managers.PermissionsManager;
import com.thetonyk.Arena.Managers.PlayersManager;

public class MessengerListener implements PluginMessageListener {

	public synchronized void onPluginMessageReceived(String channel, Player sender, byte[] message) {
		
		if (channel.equals(Main.CHANNEL)) {
		
			try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(message))) {
				
				String subchannel = input.readUTF();
				
				if (subchannel.equals("updateRank")) {
					
					UUID uuid = UUID.fromString(input.readUTF());
					Player player = Bukkit.getPlayer(uuid);
					
					if (player == null) return;
					
					PlayersManager.updateNametag(player);
					PermissionsManager.reloadPermissions(player);
					return;
					
				}
				
			} catch (IOException | SQLException exception) {}
			
		}
		
		if (channel.equals("BungeeCord")) {
			
			try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(message))) {
				
				String subchannel = input.readUTF();
				
				if (subchannel.equals("GetServer")) {
					
					Main.SERVER = input.readUTF();
					return;
					
				}
				
			} catch (IOException exception) {
				
				Player player = Bukkit.getOnlinePlayers().iterator().next();
				
				if (player == null) return;
				
				getServer(player);
				
			}
			
		}
		
	}
	
	private static void getServer(Player player) {
		
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		
		try (DataOutputStream output = new DataOutputStream(array)) {
			
			output.writeUTF("GetServer");
			
		} catch (IOException exception) {}
		
		player.sendPluginMessage(Main.plugin, "BungeeCord", array.toByteArray());
		
	}
	
	public static void setup() {
		
		new BukkitRunnable() {

			public void run() {
				
				if (Main.SERVER != null && Main.SERVER.length() > 0) {
					
					Bukkit.getOnlinePlayers().stream().forEach(p -> SidebarFeature.update(p));
					cancel();
					return;
					
				}
				
				if (Bukkit.getOnlinePlayers().size() < 1) return;
				
				Player player = Bukkit.getOnlinePlayers().iterator().next();
				
				getServer(player);
				
			}
			
		}.runTaskTimer(Main.plugin, 0, 5);
		
	}

}
