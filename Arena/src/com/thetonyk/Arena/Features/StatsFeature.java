package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.DataManager;

public class StatsFeature implements Listener {
	
	public static Map<UUID, Long> joinTime = new HashMap<>();
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		joinTime.put(player.getUniqueId(), new Date().getTime());
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		Long join = joinTime.remove(player.getUniqueId());
		
		if (join == null) return;
		
		long time = new Date().getTime() - join;
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(player.getUniqueId());
			
			scores.put("time", scores.get("time") + time);
			DataManager.updateScores(player.getUniqueId(), scores);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the time played in your stats.");
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (!ArenaFeature.isJoining(uuid) && !ArenaFeature.isArena(uuid)) return;
		
		if (event.getItem().getType() != Material.GOLDEN_APPLE) return;
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(player.getUniqueId());
			
			scores.put("gapple", scores.get("gapple") + 1d);
			DataManager.updateScores(player.getUniqueId(), scores);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the golden apple eated in your stats.");
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		
		if (!ArenaFeature.isJoining(uuid) && !ArenaFeature.isArena(uuid)) return;
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(player.getUniqueId());
			
			scores.put("shot", scores.get("shot") + 1d);
			DataManager.updateScores(player.getUniqueId(), scores);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the shot in your stats.");
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDamager() instanceof Arrow)) return;
		
		Arrow arrow = (Arrow) event.getDamager();
		Player hit = (Player) event.getEntity();
		UUID uuid = hit.getUniqueId();
		
		if (!ArenaFeature.isJoining(uuid) && !ArenaFeature.isArena(uuid)) return;
		if (!(arrow.getShooter() instanceof Player)) return;
		
		Player player = (Player) arrow.getShooter();
		uuid = player.getUniqueId();
		
		if (!ArenaFeature.isJoining(uuid) && !ArenaFeature.isArena(uuid)) return;
		
		double distance = player.getLocation().distance(hit.getLocation());
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(player.getUniqueId());
			
			scores.put("hit", scores.get("hit") + 1d);
			if (scores.get("longshot") < distance) scores.put("longshot", distance);
			DataManager.updateScores(player.getUniqueId(), scores);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the hit in your stats.");
			
		}
		
	}

}
