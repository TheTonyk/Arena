package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.DataManager;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class DisplayScoresFeature implements Listener {
	
	private static Map<UUID, List<Location>> sents = new HashMap<>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (Main.SERVER == null || Main.SERVER.length() < 1) return;
		
		if (!sents.containsKey(uuid)) sents.put(uuid, new ArrayList<>());
		
		List<Location> received = new ArrayList<>(sents.get(uuid));
		Location location = event.getTo();
		
		received.stream().filter(l -> !location.getWorld().equals(l.getWorld()) || location.distance(l) > 140).forEach(l -> sents.get(uuid).remove(l));
		
		if (DataManager.stats == null) return;
			
		DataManager.stats.stream().filter(l -> location.getWorld().equals(l.getWorld()) && location.distance(l) <= 140).forEach(l -> spawnStats(l, player));
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Location location = event.getRespawnLocation();
		
		if (!sents.containsKey(uuid)) sents.put(uuid, new ArrayList<>());
		
		List<Location> received = sents.get(uuid);
		
		received.clear();
		
		if (DataManager.stats == null) return;
			
		DataManager.stats.stream().filter(l -> location.getWorld().equals(l.getWorld()) && location.distance(l) <= 140).forEach(l -> spawnStats(l, player));
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Location location = player.getLocation();
		
		if (!sents.containsKey(uuid)) sents.put(uuid, new ArrayList<>());
		
		List<Location> received = sents.get(uuid);
		
		received.clear();
		
		if (DataManager.stats == null) return;
		
		DataManager.stats.stream().filter(l -> location.getWorld().equals(l.getWorld()) && location.distance(l) <= 140).forEach(l -> spawnStats(l, player));
		
	}
	
	private static void spawnStats(Location location, Player player) {
		
		List<Location> received = sents.get(player.getUniqueId());
		
		if (received == null || received.contains(location)) return;
		
		received.add(location);
		
		Map<String, Integer> scores;
		
		try {
			
			scores = DataManager.getScores(player.getUniqueId());
			
		} catch (SQLException exception) {return;}
		
		String ratio = new DecimalFormat("##.##").format(scores.get("deaths") < 1 ? 0 : (double) scores.get("kills") /  (double) scores.get("deaths"));
		
		sendTitle(location.clone().add(0, 1.25, 0), player, "§8⫸ §6Your Stats §8⫷");
		sendTitle(location.clone().add(0, 1, 0), player, "§7Your kills §8⫸ §a" + scores.get("kills"));
		sendTitle(location.clone().add(0, 0.75, 0), player, "§7Your deaths §8⫸ §a" + scores.get("deaths"));
		sendTitle(location.clone().add(0, 0.5, 0), player, "§7Your ratio §8⫸ §a" + ratio);
		
	}
	
	public static void spawnBests(Location location) {
		
		addTitle(location.clone().add(0, 2.5, 0), "§8⫸ §6Kills Leaderboard §8⫷");
		
		if (DataManager.best.entrySet().isEmpty()) {
			
			addTitle(location.clone().add(0, 1, 0), "§8⫸ §7There is no best player");
			return;
			
		}
		
		Iterator<Map.Entry<String, Integer>> iterator = DataManager.best.entrySet().iterator();
		
		double i = 2.25;
		int j = 1;
		
		while (iterator.hasNext()) {
			
			Map.Entry<String, Integer> score = iterator.next();
			
			addTitle(location.clone().add(0, i, 0), "§6" + j + " §8⫸ §7" + score.getKey() + " §8⫸ §a" + score.getValue());
			i -= 0.25;
			j++;
			
		}
		
	}
	
	public static void removeBests(Location location) {
		
		for (double i = 2.50; i >= 0; i -= 0.25) {
			
			for (Entity entity : location.getWorld().getNearbyEntities(location.clone().add(0, i, 0), 0.1, 0.1, 0.1)) {
				
				if (!entity.getType().equals(EntityType.ARMOR_STAND)) continue;
				if (entity.getCustomName() != null && !entity.getCustomName().startsWith("§6") && !entity.getCustomName().startsWith("§8⫸")) continue;
				
				entity.remove();
				
			}
			
		}
		
	}
	
	public static void updateBests() throws SQLException {
		
		if (DataManager.stats == null) return;
		
		for (Location location : DataManager.bests) {
			
			removeBests(location);
			spawnBests(location);
			
		}
		
	}
	
	private static void sendTitle(Location location, Player player, String text) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
		EntityArmorStand armorStand = new EntityArmorStand(nmsWorld);
		
		armorStand.setInvisible(true);
		armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		armorStand.setSmall(true);
		armorStand.setGravity(false);
		armorStand.setCustomName(text);
		armorStand.setCustomNameVisible(true);
		
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
		nmsPlayer.playerConnection.sendPacket(packet);
		
	}
	
	private static void addTitle(Location location, String text) {
		
		ArmorStand title = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		
		title.setVisible(false);
		title.setSmall(true);
		title.setGravity(false);
		title.setCustomName(text);
		title.setCustomNameVisible(true);
		return;
				
	}

}
