package com.thetonyk.Arena.Features;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.Arena.Main;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class ActionBarFeature {
	
	private static Map<UUID, String> messages = new HashMap<>();
	private static Map<UUID, Integer> timers = new HashMap<>();
	
	public static void setup() {
		
		new BukkitRunnable() {

			public void run() {
				
				for (Map.Entry<UUID, Integer> entry : timers.entrySet()) {
					
					UUID uuid = entry.getKey();
					int time = entry.getValue();
					
					if (!messages.containsKey(uuid)) continue;
					
					Player player = Bukkit.getPlayer(uuid);
					String message = messages.get(uuid);
					
					if (time < 1 || player == null) {
						
						if (player != null) sendActionBar(player, "");
						
						messages.remove(uuid);
						timers.remove(uuid);
						continue;
						
					}
					
					if (time % 20 == 0) sendActionBar(player, message);
					timers.put(uuid, time - 1);
					
				}
				
			}
			
		}.runTaskTimer(Main.plugin, 0, 1);
		
	}
	
	public static void sendMessage(UUID uuid, String message, int time) {
		
		messages.put(uuid, message);
		timers.put(uuid, time * 20);
		
	}
	
	private static void sendActionBar(Player player, String message) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		IChatBaseComponent jsonText = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(jsonText, (byte) 2);
		
		nmsPlayer.playerConnection.sendPacket(packet);
		
	}

}
