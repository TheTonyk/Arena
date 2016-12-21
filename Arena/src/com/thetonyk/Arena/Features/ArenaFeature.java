package com.thetonyk.Arena.Features;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.github.paperspigot.Title;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.DataManager;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Utils.ItemsUtils;

public class ArenaFeature implements Listener {
	
	private static Set<UUID> joining = new HashSet<>();
	private static Set<UUID> arena = new HashSet<>();
	
	public static boolean isJoining(UUID uuid) {
		
		return joining.contains(uuid);
		
	}
	
	public static boolean isArena(UUID uuid) {
		
		return arena.contains(uuid);
		
	}
	
	public static void addJoining(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		if (isJoining(uuid)) return;
		
		joining.add(uuid);
		
	}
	
	public static void addArena(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		if (isArena(uuid)) return;
		
		arena.add(uuid);
		
	}
	
	public static void removeJoining(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		if (!isJoining(uuid)) return;
				
		joining.remove(uuid);
		
	}
	
	public static void removeArena(Player player) {
		
		UUID uuid = player.getUniqueId();
		
		if (!isArena(uuid)) return;
				
		arena.remove(uuid);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onConsume(PlayerItemConsumeEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if ((!isJoining(uuid) && !isArena(uuid)) || !event.isCancelled()) return;
		
		event.setCancelled(false);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();

		if ((!isJoining(uuid) && !isArena(uuid)) || !event.isCancelled()) return;
		
		if (event.getCause() == DamageCause.FALL) {
			
			if (isJoining(uuid)) {
				
				removeJoining(player);
				addArena(player);
				return;
				
			}
			
			if (player.getFallDistance() < 7.0) return;
			
			event.setDamage(event.getDamage() / 2);
			
		}
		
		event.setCancelled(false);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (!(event.getDamager() instanceof Player)) return;		
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		
		if (!isArena(player.getUniqueId()) || !isArena(damager.getUniqueId()) || !event.isCancelled()) return;
		
		event.setCancelled(false);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getClickedInventory();
		
		if (!isArena(player.getUniqueId()) || !event.isCancelled()) return;
		if (inventory == null || !inventory.equals(player.getInventory())) return;
		if (event.getSlotType() == SlotType.ARMOR) return;
		
		event.setCancelled(false);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		DataManager.updateBests();
		player.setWalkSpeed(0.6f);
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		handleDeath(player);
		
		removeJoining(player);
		removeArena(player);
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		handleDeath(player);
		
	}
	
	public static void handleDeath(Player player) {
		
		Player killer = player.getKiller();
		
		if (!isArena(player.getUniqueId())) return;
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(player.getUniqueId());
			
			scores.put("deaths", scores.get("deaths") + 1d);
			if (scores.get("killstreak") < player.getLevel()) scores.put("killstreak", Double.valueOf(player.getLevel()));
			DataManager.updateScores(player.getUniqueId(), scores);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the death in your stats.");
			
		}
		
		SidebarFeature.update(player);
		
		if (killer == null || !isArena(killer.getUniqueId()) || killer.getUniqueId().equals(player.getUniqueId())) return;
		
		int health = (int) ((killer.getHealth() / 2) * 10);
		
		player.sendMessage(Main.PREFIX + "You were killed by '§a" + killer.getName() + "§7' §8(§6" + health + "§7%§8)");
		killer.sendMessage(Main.PREFIX + "You killed '§a" + player.getName() + "§7' §8(§6" + health + "§7%§8)");
		
		try {
			
			Map<String, Double> scores = DataManager.getScores(killer.getUniqueId());
			
			scores.put("kills", scores.get("kills") + 1d);
			DataManager.updateScores(killer.getUniqueId(), scores);
			
			if (!DataManager.best.isEmpty() && scores.get("kills") >= DataManager.best.values().stream().mapToInt(Integer::intValue).min().getAsInt()) {
				
				DataManager.updateBests();
				Bukkit.getOnlinePlayers().stream().forEach(p -> SidebarFeature.update(p));
				
			} else SidebarFeature.update(killer);
			
		} catch (SQLException exception) {
			
			player.sendMessage(Main.PREFIX + "§cUnable to add the kill in your stats.");
			
		}
		
		killer.setLevel(killer.getLevel() + 1);
		killer.playSound(killer.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
		killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false));
		killer.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1, false, false));
		
		if (killer.getLevel() > 0 && killer.getLevel() % 5 == 0) {
			
			ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
			ItemStack arrows = new ItemStack(Material.ARROW, 8);
			
			killer.getInventory().addItem(gapple, arrows);
			killer.sendMessage(Main.PREFIX + "§6Killsteak§7: §a1 §7golden apple & §a8 §7arrows");
			
		}
		
		if (killer.getLevel() > 0 && killer.getLevel() == 10) {
			
			ItemStack[] armors = new ItemStack[4];
			
			armors[0] = ItemsUtils.createItem(Material.LEATHER_BOOTS, "§6Swag Armor", 1);
			armors[1] = ItemsUtils.createItem(Material.LEATHER_LEGGINGS, "§6Swag Armor", 1);
			armors[2] = ItemsUtils.createItem(Material.LEATHER_CHESTPLATE, "§6Swag Armor", 1);
			armors[3] = ItemsUtils.createItem(Material.LEATHER_HELMET, "§6Swag Armor", 1);
			
			for (ItemStack armor : armors) {
				
				LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
				meta.setColor(Color.fromRGB(255, 170, 0));
				armor.setItemMeta(meta);
				
				armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
				armor.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
				
			}
			
			killer.getInventory().setArmorContents(armors);
			killer.sendMessage(Main.PREFIX + "§6Killsteak§7: §aSwag Armor");
			
		}
		
		if (killer.getLevel() > 0 && killer.getLevel() == 15) {
			
			
			ItemStack potion = ItemsUtils.createItem(Material.POTION, "§cSuper Damage Potion", 2, 16460);
			PotionMeta meta = (PotionMeta) potion.getItemMeta();
			meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 255), true);
			meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 3), true);
			meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 400, 3), true);
			meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 3), true);
			meta.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 100, 3), true);
			potion.setItemMeta(meta);
			
			killer.getInventory().addItem(potion);
			killer.sendMessage(Main.PREFIX + "§6Killsteak§7: §a1 §cSuper Damage Potion");
			
		}
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		
		player.setWalkSpeed(0.6f);
		removeJoining(player);
		removeArena(player);
		
		try {
			
			PlayersManager.updatePlayers(player);
			
		} catch (SQLException exception) {
			
			player.kickPlayer(Main.PREFIX + "An error has occured while respawning on §a" + Main.NAME);
			return;
			
		}
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (isArena(uuid) || isJoining(uuid)) return;
		if (SpecFeature.isSpectator(player)) return;
		if (DataManager.y < 1) return;
		if (DataManager.y < 1 || event.getTo().getY() > DataManager.y) return;
		
		addJoining(player);
		
		player.setWalkSpeed(0.2f);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(GameMode.ADVENTURE);
		PlayersManager.clearPlayer(player);
		
		Title title = new Title("", "§cTeaming not allowed", 5, 30, 5);
		
		player.sendTitle(title);
		
		PlayerInventory inventory = player.getInventory();
		ItemStack[] armors = {getUnbreakable(Material.IRON_BOOTS), getUnbreakable(Material.IRON_LEGGINGS), getUnbreakable(Material.IRON_CHESTPLATE), getUnbreakable(Material.IRON_HELMET)};
		
		for (ItemStack armor : armors) {
			
			armor.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
			
		}
		
		ItemStack sword = getUnbreakable(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		
		ItemStack bow = getUnbreakable(Material.BOW);
		ItemStack arrows = new ItemStack(Material.ARROW, 16);
		
		inventory.setArmorContents(armors);
		inventory.setItem(0, sword);
		inventory.setItem(1, bow);
		inventory.setItem(2, arrows);
		inventory.setHeldItemSlot(0);
		
		try {
			
			PlayersManager.updatePlayers(player);
			
		} catch (SQLException exception) {
			
			player.kickPlayer(Main.PREFIX + "An error has occured while joining the arena on §a" + Main.NAME);
			return;
			
		}
		
	}
	
	private static ItemStack getUnbreakable(Material material) {
		
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		
		return item;
		
	}

}
