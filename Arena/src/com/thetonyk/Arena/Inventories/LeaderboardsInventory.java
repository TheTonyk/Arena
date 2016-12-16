package com.thetonyk.Arena.Inventories;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.StatsFeature;
import com.thetonyk.Arena.Managers.DataManager;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Utils.DateUtils;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class LeaderboardsInventory implements Listener {

	private static Inventory inventory;
	
	public static void setup() {
		
		inventory = Bukkit.createInventory(null, 45, "§8⫸ §4Leaderboards");
		
		addGlasses();
		update();
		
	}
	
	public static Inventory getInventory() {
		
		return inventory;
		
	}

	private static void addGlasses() {
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7" + Main.NAME, 1, 7);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			
			if (inventory.getItem(i) != null) continue;
			
			inventory.setItem(i, separator);
			
		}
		
	}
	
	private static LinkedHashMap<UUID, Double> sortByValue(Map<UUID, Double> map) {
		
		LinkedHashMap<UUID, Double> sortedMap = new LinkedHashMap<UUID, Double>();
		List<Double> values = new ArrayList<>(map.values());
		List<UUID> keys = new ArrayList<>(map.keySet());
		
		Collections.sort(values, new Comparator<Double>() {

			public int compare(Double o1, Double o2) {
				
				return o2.compareTo(o1);
				
			}
			
		});

		Iterator<Double> iterator = values.iterator();
		
		while (iterator.hasNext()) {
			
			double value = iterator.next();
			Iterator<UUID> iterator2 = keys.iterator();
			
			while (iterator2.hasNext()) {
				
				UUID key = iterator2.next();
				
				if (map.get(key) != value) continue;
				
				iterator2.remove();
				sortedMap.put(key, value);
				break;
				
			}
			
		}
		
		return sortedMap;
		
	}
	
	private static ItemStack getItem(LinkedHashMap<UUID, String> scores, String name, List<String> misc) throws SQLException {
		
		List<String> lore = new ArrayList<>();
		Iterator<Map.Entry<UUID, String>> iterator = scores.entrySet().iterator();
		String head = "TheTonyk";
		int j = 1;
		
		lore.add("");
		
		if (scores.isEmpty()) lore.add("   §7There is no enough stats.   ");
		
		while (iterator.hasNext()) {
			
			Map.Entry<UUID, String> entry = iterator.next();
			String player = PlayersManager.getField(entry.getKey(), "name");
			
			if (j == 1) head = player;
			
			lore.add("   §6" + j + " §8⫸ §7" + player + " §8⫸ §a" + entry.getValue() + "   ");
			
			if (j >= 10) break;
			
			j++;
			
		}
		
		if (!misc.isEmpty()) {
			
			lore.add("");
			
			Iterator<String> iterator2 = misc.iterator();
			
			while (iterator2.hasNext()) {
				
				lore.add("   " + iterator2.next() + "   ");
				
			}
			
		}
		
		lore.add("");
		
		ItemStack item = ItemsUtils.getSkull("§8⫸ §6" + name + " §8⫷", 1, lore, head);
		item = ItemsUtils.hideFlags(item);
		return item;
		
	}
	
	private static ItemStack getItem(LinkedHashMap<UUID, String> scores, String name) throws SQLException {
		
		return getItem(scores, name, new ArrayList<>());
		
	}
	
	private static void update() {
		
		inventory.clear();
		addGlasses();
		inventory.setItem(22, ItemsUtils.createItem(Material.WATCH, "§8⫸ §7Loading... §8⫷", 1));
		
		new BukkitRunnable() {
			
			public void run() {
				
				Map<UUID, Map<String, Double>>  scores;
				
				try {
				
					scores = DataManager.getAllScores();
					
				} catch (SQLException exception) {
					
					inventory.setItem(22, ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cUnable to get the leadersboards. §7Try again later.", 1));
					return;
					
				}
				
				Map<UUID, Double> buffer = new HashMap<>();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("kills")));
				LinkedHashMap<UUID, String> kills = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> kills.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("deaths")));
				LinkedHashMap<UUID, String> deaths = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> deaths.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().filter(e -> e.getValue().get("kills") >= 25).forEach(e -> buffer.put(e.getKey(), e.getValue().get("deaths") < 1 ? 0 : e.getValue().get("kills") /  e.getValue().get("deaths")));
				LinkedHashMap<UUID, String> ratio = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> ratio.put(e.getKey(), new DecimalFormat("##.##").format(e.getValue())));
				buffer.clear();	
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("killstreak")));
				LinkedHashMap<UUID, String> killstreak = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> killstreak.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("shot")));
				LinkedHashMap<UUID, String> shot = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> shot.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("hit")));
				LinkedHashMap<UUID, String> hit = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> hit.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().filter(e -> e.getValue().get("shot") >= 50).forEach(e -> buffer.put(e.getKey(), e.getValue().get("shot") < 1 ? 0 : (e.getValue().get("hit") /  e.getValue().get("shot")) * 100));
				LinkedHashMap<UUID, String> accuracy = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> accuracy.put(e.getKey(), new DecimalFormat("##.##").format(e.getValue().intValue()) + "§7%"));
				buffer.clear();
				
				scores.entrySet().stream().filter(e -> e.getValue().get("longshot") > 0).forEach(e -> buffer.put(e.getKey(), e.getValue().get("longshot")));
				LinkedHashMap<UUID, String> longshot = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> longshot.put(e.getKey(), new DecimalFormat("##.##").format(e.getValue().intValue()) + "§7m"));
				buffer.clear();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("gapple")));
				LinkedHashMap<UUID, String> gapple = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> gapple.put(e.getKey(), String.valueOf(e.getValue().intValue())));
				buffer.clear();
				
				scores.entrySet().stream().forEach(e -> buffer.put(e.getKey(), e.getValue().get("time") + (StatsFeature.joinTime.containsKey(e.getKey()) ? new Date().getTime() - StatsFeature.joinTime.get(e.getKey()) : 0)));
				LinkedHashMap<UUID, String> time = new LinkedHashMap<>();
				sortByValue(buffer).entrySet().stream().forEach(e -> time.put(e.getKey(), e.getValue().longValue() > 0 ? DateUtils.toText(e.getValue().longValue(), true) : "None"));
				buffer.clear();
						
				try {
					
					inventory.setItem(11, getItem(kills, "Kills"));
					inventory.setItem(13, getItem(deaths, "Deaths"));
					inventory.setItem(15, getItem(ratio, "Ratio", Lists.newArrayList("§8(§7Min §625 §7kills§8)")));
					inventory.setItem(19, getItem(killstreak, "Best Killstreak"));
					inventory.setItem(21, getItem(shot, "Arrows Shot"));
					inventory.setItem(23, getItem(hit, "Arrows Hits"));
					inventory.setItem(25, getItem(accuracy, "Bow Accuracy", Lists.newArrayList("§8(§7Min §650 §7arrows shot§8)")));
					inventory.setItem(29, getItem(longshot, "Longest Shot"));
					inventory.setItem(31, getItem(gapple, "Golden Apples eaten"));
					inventory.setItem(33, getItem(time, "Time played"));
					
				} catch (SQLException exception) {
					
					inventory.clear();
					addGlasses();
					inventory.setItem(22, ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cUnable to get the stats. §7Try again later.", 1));
					return;
					
				}
				
				inventory.setItem(22, null);
				addGlasses();
				
			}
			
		}.runTaskAsynchronously(Main.plugin);
		
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		
		Inventory inventory = event.getInventory();
		
		if (inventory == null  || !inventory.equals(LeaderboardsInventory.inventory)) return;
		
		update();
		
	}

}
