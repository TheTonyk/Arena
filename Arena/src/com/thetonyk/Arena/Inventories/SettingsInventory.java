package com.thetonyk.Arena.Inventories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Managers.Settings;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class SettingsInventory implements Listener {

	public static Map<UUID, SettingsInventory> inventories = new HashMap<>();
	private Inventory inventory;
	private UUID player;
	
	public SettingsInventory(Player player) {
		
		this.inventory = Bukkit.createInventory(null, 27, "§8⫸ §4Settings");
		this.player = player.getUniqueId();
		
		addGlasses();
		update();
		inventories.put(player.getUniqueId(), this);
		
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		
	}
	
	public static SettingsInventory getInventory(Player player) {
		
		if (inventories.containsKey(player.getUniqueId())) return inventories.get(player.getUniqueId());
		
		return new SettingsInventory(player);
		
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
		Settings settings;
		
		try {
			
			settings = Settings.getSettings(this.player);
			
		} catch (SQLException exception) {
			
			this.inventory.clear();
			addGlasses();
			this.inventory.setItem(13, ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cUnable to get your settings. §7Try again later.", 1));
			return;
			
		}
		
		lore.add("");
		lore.add("   §7Toggle the visibility of   ");
		lore.add("   §7others players in the hub.   ");
		lore.add("");
		lore.add("   §cOnly work in the hubs.   ");
		lore.add("");
		
		ItemStack item = ItemsUtils.createItem(Material.EYE_OF_ENDER, "§8⫸ §7Player visibility§8: " + (settings.getPlayers() ? "§aEnabled" : "§cDisabled") + " §8⫷", 1, 0, lore);
		if (settings.getPlayers()) item = ItemsUtils.setGlowing(item);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(10, item);
		
		lore.clear();
		
		lore.add("");
		lore.add("   §7Toggle the visibility of   ");
		lore.add("   §7the chat in the hub.   ");
		lore.add("");
		
		item = ItemsUtils.createItem(Material.PAPER, "§8⫸ §7Chat visibility§8: " + (settings.getChat() ? "§aEnabled" : "§cDisabled") + " §8⫷", 1, 0, lore);
		if (settings.getChat()) item = ItemsUtils.setGlowing(item);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(12, item);
		
		lore.clear();
		
		lore.add("");
		lore.add("   §7When this setting is enable,   ");
		lore.add("   §7you wil receive a sound alert   ");
		lore.add("   §7when your name is mentionned   ");
		lore.add("   §7in the public chat.   ");
		lore.add("");
		
		item = ItemsUtils.createItem(Material.MAP, "§8⫸ §7Mentions alert§8: " + (settings.getMentions() ? "§aEnabled" : "§cDisabled") + " §8⫷", 1, 0, lore);
		if (settings.getMentions()) item = ItemsUtils.setGlowing(item);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(14, item);
		
		lore.clear();
		
		lore.add("");
		lore.add("   §7Toggle your private messages.   ");
		lore.add("   §7You will not be able to send   ");
		lore.add("   §7or receive messages.   ");
		lore.add("");
		lore.add("   §7You can also use §6/ignore§7.   ");
		lore.add("");
		
		item = ItemsUtils.createItem(Material.BOOK_AND_QUILL, "§8⫸ §7Private messages§8: " + (settings.getMessages() ? "§aEnabled" : "§cDisabled") + " §8⫷", 1, 0, lore);
		if (settings.getMessages()) item = ItemsUtils.setGlowing(item);
		item = ItemsUtils.hideFlags(item);
		inventory.setItem(16, item);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();	
		
		try {
			
			Settings settings = Settings.getSettings(this.player);
			
			if (!player.getUniqueId().equals(this.player)) return;
			if (inventory == null || !inventory.equals(this.inventory)) return;
			if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
			
			String name = item.getItemMeta().getDisplayName();
			
			if (name.startsWith("§8⫸ §7Player visibility")) {
				
				settings.setPlayers(!settings.getPlayers());
				PlayersManager.updatePlayers(player, settings);
				
			} else if (name.startsWith("§8⫸ §7Chat visibility")) {
				
				settings.setChat(!settings.getChat());
				
			} else if (name.startsWith("§8⫸ §7Mentions alert")) {
				
				settings.setMentions(!settings.getMentions());
				
			} else if (name.startsWith("§8⫸ §7Private messages")) {
				
				settings.setMessages(!settings.getMessages());
				
			} else return;
			
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			update();
		
		} catch (SQLException exception) {return;}
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		if (!event.getPlayer().getUniqueId().equals(this.player)) return;
		
		if (inventories.containsKey(player)) inventories.remove(player);
		
		HandlerList.unregisterAll(this);
		
	}

}
