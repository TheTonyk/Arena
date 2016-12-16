package com.thetonyk.Arena.Inventories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Utils.ItemsUtils;
import com.thetonyk.Arena.Managers.PermissionsManager.Rank;

public class SelectorInventory implements Listener {
	
	private static Map<UUID, Inventory> viewers = new HashMap<UUID, Inventory>();
	private static Map<UUID, Integer> page = new HashMap<UUID, Integer>();
	
	public static Inventory getInventory(UUID uuid) {
		
		if (!viewers.containsKey(uuid)) viewers.put(uuid, Bukkit.createInventory(null, 54, "§8⫸ §4Player Selector"));
		
		return viewers.get(uuid);
		
	}
	
	private static void update() {
		
		for (UUID viewer : viewers.keySet()) {
			
			if (Bukkit.getPlayer(viewer) == null || !Bukkit.getPlayer(viewer).getOpenInventory().getTopInventory().equals(viewers.get(viewer))) {
				
				viewers.remove(viewer);
				
				if (page.containsKey(viewer)) page.remove(viewer);
				
				continue;
				
			}
			
			if (!page.containsKey(viewer)) page.put(viewer, 0);
			
			Map<String, String> players = new HashMap<String, String>();
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				
				Rank rank;
				
				try {
					
					rank = PlayersManager.getRank(player.getUniqueId());
					
				} catch (SQLException exception) {
					
					rank = Rank.PLAYER;
					
				}
				
				players.put(player.getName(), rank.getPrefix() + "§7" + player.getName());
				
			}
			
			viewers.get(viewer).clear();
			
			int playersNumber = 36 * page.get(viewer);
			
			for (int i = 0; i <= 35; i++) {
				
				if (players.size() <= i + playersNumber) break;
				
				String player = (String) players.keySet().toArray()[i + playersNumber];
				String name = players.get(player);
				List<String> lore = new ArrayList<>();
				
				lore.add("");
				lore.add("   §8⫸ §6Left-click §7to teleport   ");
				lore.add("   §8⫸ §6Middle-click §7to enter into him   ");
				lore.add("   §8⫸ §6Right-click §7to see inventory   ");
				lore.add("");
				
				ItemStack item = ItemsUtils.getSkull("§8⫸ " + name + " §8⫷", 1, lore, player);
				
				viewers.get(viewer).setItem(i, item);
				
			}
			
			if (page.get(viewer) > 0) viewers.get(viewer).setItem(45, ItemsUtils.getSkull("§8⫷ §7Previous", 1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwZjZlOGFmNDZhYzZmYWY4ODkxNDE5MWFiNjZmMjYxZDY3MjZhNzk5OWM2MzdjZjJlNDE1OWZlMWZjNDc3In19fQ=="));
			if (page.get(viewer) < Math.ceil(players.size() / 36)) viewers.get(viewer).setItem(53, ItemsUtils.getSkull("§7Next §8⫸", 1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmM2EyZGZjZTBjM2RhYjdlZTEwZGIzODVlNTIyOWYxYTM5NTM0YThiYTI2NDYxNzhlMzdjNGZhOTNiIn19fQ=="));
			
		}
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		ClickType click = event.getClick();
		
		if (inventory == null || !inventory.equals(viewers.get(player.getUniqueId()))) return;
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		int pageNumber = page.containsKey(player.getUniqueId()) ? page.get(player.getUniqueId()) : 0;
		
		if (name.startsWith("§7Next §8⫸")) {
			
			page.put(player.getUniqueId(), pageNumber + 1);
			update();
			return;
			
		}
		
		if (name.startsWith("§8⫷ §7Previous") && pageNumber > 0) {
			
			page.put(player.getUniqueId(), pageNumber - 1);
			update();
			return;
			
		}
			
		if (click != ClickType.LEFT && click != ClickType.MIDDLE && click != ClickType.RIGHT) return;
		
		String clickeds[] = item.getItemMeta().getDisplayName().substring(4, item.getItemMeta().getDisplayName().length() - 4).split("§");
		Player clicked = Bukkit.getPlayer(clickeds[clickeds.length - 1].substring(1));
		
		if (clicked == null) {
			
			player.sendMessage(Main.PREFIX + "This player is not currently online.");
			return;
		
		}
		
		player.closeInventory();
		
		if (click == ClickType.LEFT) {
			
			player.teleport(clicked);
			
		} else if (click == ClickType.MIDDLE) {
			
			player.setSpectatorTarget(clicked);
			
		} else if (click == ClickType.RIGHT) {
			
			//Open Inventory Player
			
		}
		
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		
		if (!(event.getPlayer() instanceof Player)) return;
		
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getPlayer();
		
		if (inventory == null  || !inventory.equals(viewers.get(player.getUniqueId()))) return;
		
		update();
		
	}
	
	@EventHandler
	public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, SelectorInventory::update, 1);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, SelectorInventory::update, 1);
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, SelectorInventory::update, 1);
		
	}

}
