package com.thetonyk.Arena.Features;

import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Inventories.LeaderboardsInventory;
import com.thetonyk.Arena.Inventories.SettingsInventory;
import com.thetonyk.Arena.Inventories.StatsInventory;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class ItemsFeature implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		give(player);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onRespawn(PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		
		give(player);
		
	}
	
	private static void give(Player player) {
		
		ItemStack item = ItemsUtils.createItem(Material.REDSTONE_COMPARATOR, "§8⫸ §6§lSettings §8(§7Right-Click to open§8) §8⫷", 1);
		item = ItemsUtils.hideFlags(item);
		player.getInventory().setItem(8, item);
		
		item = ItemsUtils.createItem(Material.PAINTING, "§8⫸ §a§lLeaderboards §8(§7Right-Click to open§8) §8⫷", 1);
		item = ItemsUtils.hideFlags(item);
		player.getInventory().setItem(6, item);
		
		item = ItemsUtils.createItem(Material.SIGN, "§8⫸ §a§lStats §8(§7Right-Click to open§8) §8⫷", 1);
		item = ItemsUtils.hideFlags(item);
		player.getInventory().setItem(5, item);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
	
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		
		if (name.startsWith("§8⫸ §6§lSettings")) {
			
			SettingsInventory inventory = SettingsInventory.getInventory(player);
			player.openInventory(inventory.getInventory());
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			return;
			
		}

		if (name.startsWith("§8⫸ §a§lLeaderboards")) {
			
			player.openInventory(LeaderboardsInventory.getInventory());
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			return;
			
		}
		
		if (name.startsWith("§8⫸ §a§lStats")) {
			
			try {
			
				StatsInventory inventory = StatsInventory.getInventory(player.getUniqueId());
				
				player.openInventory(inventory.getInventory());
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
				return;
			
			} catch (SQLException exception) {
				
				player.sendMessage(Main.PREFIX + "§cAn error has occured while opening the stats inventory.");
				return;
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Action action = event.getAction();
		
		if (action == Action.PHYSICAL) return;
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		
		if (name.startsWith("§8⫸ §6§l")) {
			
			SettingsInventory inventory = SettingsInventory.getInventory(player);
			player.openInventory(inventory.getInventory());
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			return;
			
		}
		
		if (name.startsWith("§8⫸ §a§lLeaderboards")) {
			
			player.openInventory(LeaderboardsInventory.getInventory());
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			return;
			
		}
		
		if (name.startsWith("§8⫸ §a§lStats")) {
			
			try {
			
				StatsInventory inventory = StatsInventory.getInventory(player.getUniqueId());
				
				player.openInventory(inventory.getInventory());
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
				return;
			
			} catch (SQLException exception) {
				
				player.sendMessage(Main.PREFIX + "§cAn error has occured while opening the stats inventory.");
				return;
				
			}
			
		}
		
	}

}
