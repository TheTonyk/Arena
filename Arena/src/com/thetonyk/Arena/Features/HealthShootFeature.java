package com.thetonyk.Arena.Features;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PlayersManager;

public class HealthShootFeature implements Listener {
	
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
		
		new BukkitRunnable() {
			
			public void run() {
				
				int health = (int) ((hit.getHealth() / 2) * 10);
				int maxHealth = (int) ((hit.getMaxHealth() / 2) * 10);
				String text = "§4";
				
				for (int i = 0; i < Math.floor(health / 10); i++) {
					
					text += "❤";
					
				}
				
				if (health % 10 > 0) text += "§c❤";
				
				int emptyHealth = maxHealth - health;
				text += "§f";
				
				for (int i = 0; i < Math.floor(emptyHealth / 10); i++) {
					
					text += "❤";
					
				}
				
				text += " §7⫸ §6" + new DecimalFormat("##.#").format(health) + "§7%";
				
				PlayersManager.sendActionBar(player, text);
				
			}
			
		}.runTaskLater(Main.plugin, 1);
		
	}

}
