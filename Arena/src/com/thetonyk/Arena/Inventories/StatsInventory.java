package com.thetonyk.Arena.Inventories;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.StatsFeature;
import com.thetonyk.Arena.Managers.DataManager;
import com.thetonyk.Arena.Utils.DateUtils;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class StatsInventory implements Listener {

	public static Map<UUID, StatsInventory> inventories = new HashMap<>();
	private Inventory inventory;
	private UUID player;
	
	public StatsInventory(Player player) {
		
		this.inventory = Bukkit.createInventory(null, 27, "§8⫸ §4Stats§8: §7" + player.getName());
		this.player = player.getUniqueId();
		
		addGlasses();
		update();
		inventories.put(player.getUniqueId(), this);
		
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		
		new BukkitRunnable() {

			public void run() {
				
				if (inventory.getViewers().isEmpty()) return;
				
				update();
				
			}
			
		}.runTaskTimer(Main.plugin, 1, 1);
		
	}
	
	public static StatsInventory getInventory(Player player) {
		
		if (inventories.containsKey(player.getUniqueId())) return inventories.get(player.getUniqueId());
		
		return new StatsInventory(player);
		
	}
	
	public Inventory getInventory() {
		
		return this.inventory;
		
	}

	private void addGlasses() {
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7" + Main.NAME, 1, 7);
		
		for (int i = 0; i < this.inventory.getSize(); i++) {
			
			if (this.inventory.getItem(i) != null) continue;
			
			this.inventory.setItem(i, separator);
			
		}
		
	}
	
	private void update() {
		
		List<String> lore = new ArrayList<>();
		Map<String, Double> scores;
		
		try {
			
			scores = DataManager.getScores(this.player);
			
		} catch (SQLException exception) {
			
			this.inventory.clear();
			addGlasses();
			this.inventory.setItem(13, ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cUnable to get the stats. §7Try again later.", 1));
			return;
			
		}
		
		String ratio = new DecimalFormat("##.##").format(scores.get("deaths") < 1 ? 0 : scores.get("kills") /  scores.get("deaths"));
		
		lore.add("");
		lore.add("   §7Kills: §a" + scores.get("kills").intValue() + "   ");
		lore.add("   §7Deaths: §a" + scores.get("deaths").intValue() + "   ");
		lore.add("   §7Ratio: §a" + ratio + "   ");
		lore.add("   §7Best Killsteak: §a" + scores.get("killstreak").intValue() + "   ");
		lore.add("");
		
		ItemStack item = ItemsUtils.createItem(Material.IRON_SWORD, "§8⫸ §6PVP Stats §8⫷", 1, 0, lore);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(11, item);
		
		lore.clear();
		
		String accuracy = new DecimalFormat("##.##").format(scores.get("shot") < 1 ? 0 : (scores.get("hit") /  scores.get("shot")) * 100);
		String longshot = new DecimalFormat("##.##").format(scores.get("longshot"));
		
		lore.add("");
		lore.add("   §7Arrows Shot: §a" + scores.get("shot").intValue() + "   ");
		lore.add("   §7Arrows Hits: §a" + scores.get("hit").intValue() + "   ");
		lore.add("   §7Bow Accuracy: §a" + accuracy + "§7%   ");
		lore.add("   §7Longest Shot: §a" + longshot + "§7m   ");
		lore.add("");
		
		item = ItemsUtils.createItem(Material.BOW, "§8⫸ §6Bow Stats §8⫷", 1, 0, lore);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(13, item);
		
		lore.clear();
		
		long time = StatsFeature.joinTime.containsKey(this.player) ? new Date().getTime() - StatsFeature.joinTime.get(this.player) : 0;
		
		lore.add("");
		lore.add("   §7Golden Apples eaten: §a" + scores.get("gapple").intValue() + "   ");
		lore.add("   §7Time played: §a" + DateUtils.toText(scores.get("time").longValue() + time, true) + "   ");
		lore.add("");
		
		item = ItemsUtils.createItem(Material.NAME_TAG, "§8⫸ §6Others stats §8⫷", 1, 0, lore);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(15, item);
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		if (!event.getPlayer().getUniqueId().equals(this.player)) return;
		
		if (inventories.containsKey(player)) inventories.remove(player);
		
		HandlerList.unregisterAll(this);
		
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		
		Inventory inventory = event.getInventory();
		
		if (inventory == null  || !inventory.equals(this.inventory)) return;
		
		update();
		
	}

}
