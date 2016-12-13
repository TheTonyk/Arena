package com.thetonyk.Arena.Features;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thetonyk.Arena.Main;

public class HealthScore implements Listener {
	
	public static void setup() {
		
		Bukkit.getOnlinePlayers().stream().forEach(p -> create(p));
		
		new BukkitRunnable() {
			
			public void run() {
				
				Bukkit.getOnlinePlayers().stream().forEach(p -> update(p));
				
			}
			
		}.runTaskTimer(Main.plugin, 1, 1);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		create(player);
		update(player);
		
	}
	
	private static void create(Player player) {
		
		Scoreboard scoreboard = player.getScoreboard();
		Objective below = scoreboard.getObjective("below");
		Objective list = scoreboard.getObjective("list");
		
		if (below == null) {
			
			below = scoreboard.registerNewObjective("below", "dummy");
			below.setDisplayName("§4♥");
			below.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
		}
		
		if (list == null) {
			
			list = scoreboard.registerNewObjective("list", "dummy");
			list.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
		}
		
	}
	
	private static void update(Player player) {
		
		Scoreboard scoreboard = player.getScoreboard();
		Objective below = scoreboard.getObjective("below");
		Objective list = scoreboard.getObjective("list");
		
		for (Player score : Bukkit.getOnlinePlayers()) {
			
			int health = (int) ((score.getHealth() / 2) * 10);
			below.getScore(score.getName()).setScore(health);
			list.getScore(score.getName()).setScore(health);
			
		}
		
	}

}
