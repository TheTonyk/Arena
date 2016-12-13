package com.thetonyk.Arena.Managers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.thetonyk.Arena.Main;

public class ConnectionsAverager implements Listener {
	
	private static LinkedList<Long> tps = new LinkedList<>();
	private static boolean firstTps = true;
	private static long lastTickTps;
	
	private static Map<UUID, PlayerPing> pings = new HashMap<>();
	private static Set<UUID> pingsWaiting = new HashSet<>();
	
	public static void setup() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.plugin, PacketType.Play.Client.KEEP_ALIVE) {
			
			public void onPacketReceiving(PacketEvent event) {
				
				long now = System.nanoTime();
				int id = event.getPacket().getIntegers().read(0);
				Player player = event.getPlayer();
				PlayerPing ping = pings.get(player.getUniqueId());
				
				if (ping == null) return;
				if (!ping.pingSent.containsKey(id)) return;
				
				long sentTime = ping.pingSent.remove(id);
				ping.ping.add(now - sentTime);
				
				if (ping.ping.size() > 6000) ping.ping.removeFirst();
				
			}
			
		});
		
		new BukkitRunnable() {

			int id = 0;
			
			public void run() {
				
				long now = System.nanoTime();
				int currentId = id;
				PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.KEEP_ALIVE);
				
				id++;
				packet.getIntegers().write(0, currentId);
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					if (pingsWaiting.contains(player.getUniqueId())) continue;
					
					try {
						
						ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
						
					} catch (InvocationTargetException exception) {}
					
					pings.get(player.getUniqueId()).pingSent.put(currentId, now);
					
				}
				
			}
			
		}.runTaskTimer(Main.plugin, 1, 1);
		
		Bukkit.getScheduler().runTaskTimer(Main.plugin, ConnectionsAverager::tick, 1, 1);
		Bukkit.getOnlinePlayers().stream().forEach(p -> addPlayer(p));
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		
		addPlayer(event.getPlayer());
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		pings.remove(player.getUniqueId());
		
	}
	
	private static void addPlayer(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		pings.put(uuid, new PlayerPing());
		pingsWaiting.add(uuid);
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> pingsWaiting.remove(uuid), 40);
		
	}
	
	private static void tick() {
		
		long now = System.nanoTime();
		
		if (firstTps) {
			
			firstTps = false;
			lastTickTps = now;
			return;
			
		}
		
		tps.add(now - lastTickTps);
		lastTickTps = now;
		
		if (tps.size() > 6000) tps.removeFirst();
		
	}
	
	public static double getTps(int amount) {
		
		if (tps.isEmpty()) return 20;
		
		amount = Math.min(amount, tps.size());
		List<Long> selectedTps = tps.subList(tps.size() - amount, tps.size());
		long sum = selectedTps.stream().mapToLong(Long::longValue).sum();
		double total = sum / amount;
		
		return 1_000_000_000 / total;
		
	}
	
	public static double getPing(UUID uuid, int amount) {
		
		PlayerPing player = pings.get(uuid);
		
		if (player == null) return -2;
		
		return player.getPing(amount);
		
	}
	
	private static class PlayerPing {
		
		private LinkedList<Long> ping = new LinkedList<>();
		private Map<Integer, Long> pingSent = new HashMap<>();
		
		public double getPing(int amount) {
			
			if (ping.isEmpty()) return -1;
			
			int size = ping.size();
			amount = Math.min(amount, size);
			List<Long> selectedPing = ping.subList(size - amount, size);
			long sum = new ArrayList<Long>(selectedPing).stream().mapToLong(Long::longValue).sum();
			
			return (sum / amount) / 1_000_000;
			
		}
		
	}

}
