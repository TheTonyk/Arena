package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.DataManager;

public class SidebarFeature implements Listener {
	
	public static void setup() {
		
		Bukkit.getOnlinePlayers().stream().forEach(p -> create(p));
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		create(player);
		update(player);
		
	}
	
	private static void create(Player player) {
		
		Scoreboard scoreboard = player.getScoreboard();
		Objective sidebar = scoreboard.getObjective("sidebar");
		
		if (sidebar == null) {
			
			sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
			sidebar.setDisplayName(Main.PREFIX + "§oStats");
			sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
			
		}
		
	}
	
	public static void update(Player player) {
		
		Scoreboard scoreboard = player.getScoreboard();
		
		scoreboard.getEntries().stream().filter(s -> s.startsWith("  ")).forEach(s -> scoreboard.resetScores(s));
		
		int i = 15;
		Objective sidebar = scoreboard.getObjective("sidebar");
		
		sidebar.getScore("   ").setScore(i);i--;
		
		if (DataManager.best.entrySet().isEmpty()) {
			
			sidebar.getScore("  §8⫸ §7There is no best player").setScore(i);i--;
			
		} else {
		
			Iterator<Map.Entry<String, Integer>> iterator = DataManager.best.entrySet().iterator();
			int j = 1;
			
			while (iterator.hasNext()) {
				
				Map.Entry<String, Integer> score = iterator.next();
				
				sidebar.getScore("  §6" + j + " §8⫸ §7" + score.getKey() + " §8⫸ §a" + score.getValue()).setScore(i);
				i--;j++;
				
			}
		
		}
		
		Map<String, Integer> scores;
		
		try {
			
			scores = DataManager.getScores(player.getUniqueId());
			
		} catch (SQLException exception) {return;}
		
		String ratio = new DecimalFormat("##.##").format(scores.get("deaths") < 1 ? 0 : (double) scores.get("kills") /  (double) scores.get("deaths"));
		
		sidebar.getScore("  ").setScore(i);i--;
		sidebar.getScore("  §7Your kills §8⫸ §a" + scores.get("kills")).setScore(i);i--;
		sidebar.getScore("  §7Your deaths §8⫸ §a" + scores.get("deaths")).setScore(i);i--;
		sidebar.getScore("  §7Your ratio §8⫸ §a" + ratio).setScore(i);i--;
		
	}

}
