package com.thetonyk.Arena.Features;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Managers.DataManager;

public class ProtectionFeature implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onManipulateArmorStand(PlayerArmorStandManipulateEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onBed(PlayerBedEnterEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		event.getBlockClicked().getState().update(true, true);
		
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		event.getBlockClicked().getState().update(true, true);
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onExp(PlayerExpChangeEvent event) {
		
		event.setAmount(0);
		
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		event.setDroppedExp(0);
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		event.setDeathMessage(null);
		event.setDroppedExp(0);
		event.getDrops().clear();
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
			
			if (player.isDead()) player.spigot().respawn();
			
		}, 10);
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		Location location = world.getSpawnLocation();
		
		if (DataManager.spawnspoints != null) {
			
			List<Location> locations = DataManager.spawnspoints.stream().filter(l -> l.getWorld().equals(world)).collect(Collectors.toList());
			location = locations.isEmpty() ? world.getSpawnLocation() : locations.get(new Random().nextInt(locations.size()));
			
		}
		
		event.setRespawnLocation(location);
		player.setGameMode(GameMode.ADVENTURE);
		PlayersManager.clearPlayer(player);
		
		if (player.hasPermission("global.fly")) player.setAllowFlight(true);
		
	}
	
	@EventHandler
	public void onChangeGameMode(PlayerGameModeChangeEvent event) {
		
		Player player = event.getPlayer();
		GameMode gamemode = event.getNewGameMode();
		
		switch (gamemode) {
		
		case CREATIVE:
		case SPECTATOR:
			PlayersManager.showCoords(player);
			break;
		default:
			PlayersManager.hideCoords(player);
			
			if (player.hasPermission("global.fly")) player.setAllowFlight(true);
			break;
		
		}
		
	}
	
	@EventHandler
	public void onShear(PlayerShearEntityEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (!(event.getDamager() instanceof Player)) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Player player = (Player) event.getDamager();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		
		event.setCancelled(true);
		event.setFoodLevel(20);
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (event.getTo().getY() > 0) return;
		
		player.teleport(world.getSpawnLocation());
		
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		switch (entity.getType()) {
		
			case ARMOR_STAND:
				return;
			default:
				event.setCancelled(true);
				break;
		
		}
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		switch (event.getAction()) {
		
			case PHYSICAL:
				event.setCancelled(true);
				return;
			case RIGHT_CLICK_BLOCK:
				
				switch (event.getClickedBlock().getType()) {
				
					case ANVIL:
					case BEACON:
					case BREWING_STAND:
					case CHEST:
					case WORKBENCH:
					case DISPENSER:
					case DROPPER:
					case ENCHANTMENT_TABLE:
					case ENDER_CHEST:
					case FURNACE:
					case BURNING_FURNACE:
					case HOPPER:
					case ITEM_FRAME:
					case LEVER:
					case BED_BLOCK:
					case TRAPPED_CHEST:
						event.setCancelled(true);
						return;
					default:
						break;
				
				}
				
				break;
			default:
				break;
		
		}
		
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onGrow(BlockGrowEvent event) {
		
		if (!event.getNewState().getType().equals(Material.SUGAR_CANE_BLOCK)) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onMoveItem(InventoryMoveItemEvent event) {
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
				
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		
		if (!(event.getEntity() instanceof Arrow)) return;
		
		Arrow arrow = (Arrow) event.getEntity();
		arrow.remove();
		
	}
	
}
