package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Inventories.SelectorInventory;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class SpecFeature implements Listener {
	
	private static Set<UUID> spectators = new HashSet<>();
	
	public static void enable(Player player) throws SQLException {
		
		spectators.add(player.getUniqueId());
		
		PlayersManager.updatePlayers(player);
		
		ArenaFeature.handleDeath(player);
		ArenaFeature.removeJoining(player);
		ArenaFeature.removeArena(player);
		
		PlayersManager.clearPlayer(player);
		PlayersManager.healPlayer(player);
		player.getActivePotionEffects().stream().forEach(e -> player.removePotionEffect(e.getType()));
		player.setWalkSpeed(0.2f);
		player.setLevel(0);
		player.setGameMode(GameMode.SPECTATOR);
		
		setItems(player);
		
	}
	
	public static void disable(Player player) {
		
		spectators.remove(player.getUniqueId());
		
		player.setHealth(0);
		
	}
	
	public static boolean toggle(Player player) throws SQLException {
		
		if (isSpectator(player)) disable(player);
		else enable(player);
		
		return isSpectator(player);
		
	}
	
	public static Set<UUID> getSpectators() {
		
		return new HashSet<>(spectators);
		
	}
	
	public static boolean isSpectator(Player player) {
		
		return spectators.contains(player.getUniqueId());
		
	}
	
	private static void setItems(Player spectator) {
		
		ItemStack teleportMiddle = ItemsUtils.createItem(Material.NETHER_STAR, "§6Teleport to the middle §7(Click on it)", 1);
		ItemStack openSelector = ItemsUtils.createItem(Material.COMPASS, "§6Open the selector §7(Click on it)", 1);
		
		spectator.getInventory().setItem(3, teleportMiddle);
		spectator.getInventory().setItem(5, openSelector);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		if (!isSpectator(player)) return;
		
		PlayersManager.clearPlayer(player);
		player.setWalkSpeed(0.2f);
		player.setGameMode(GameMode.SPECTATOR);
		event.setJoinMessage(null);
		
		setItems(player);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		if (!isSpectator(player)) return;
		
		event.setQuitMessage(null);
		
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (!isSpectator(player)) return;
		if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;
		
		List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> ArenaFeature.isArena(p.getUniqueId())).collect(Collectors.toList());
		
		if (players.isEmpty()) {
			
			player.sendMessage(Main.PREFIX + "There is no players in the arena.");
			return;
			
		}
		
		player.teleport(players.get(new Random().nextInt(players.size())));
		
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (!isSpectator(player)) return;
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
		if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock() instanceof InventoryHolder) return;
		
		player.openInventory(SelectorInventory.getInventory(player.getUniqueId()));
		
	}
	
	@EventHandler
	public void onRightClickEntity(PlayerInteractEntityEvent event) {
		
		Player player = event.getPlayer();
		
		if (!(event.getRightClicked() instanceof Player)) return;
		
		Player clicked = (Player) event.getRightClicked();
		
		if (!isSpectator(player) || isSpectator(clicked)) return;
		
		//Open Inventory
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		
		if (inventory == null || !inventory.equals(player.getInventory())) return;
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		String name = item.getItemMeta().getDisplayName();
		
		if (name.startsWith("§6Teleport to the middle §7(Click on it)")) {
			
			World world = player.getWorld();
			Location location = world.getSpawnLocation();
			location.add(0, -20, 0);
			
			player.teleport(location);
			
		} else if (name.startsWith("§6Open the selector §7(Click on it)")) {
			
			player.closeInventory();
			player.openInventory(SelectorInventory.getInventory(player.getUniqueId()));
			
		}
		
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
		
	}

}
