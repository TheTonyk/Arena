package com.thetonyk.Arena.Managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.github.paperspigot.Title;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.ArenaFeature;
import com.thetonyk.Arena.Features.SpecFeature;
import com.thetonyk.Arena.Managers.PermissionsManager.Rank;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;

public class PlayersManager implements Listener {
	
	public static UUID getUUID(int id) throws SQLException {
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT uuid FROM users WHERE id = " + id + ";")) {
			
			if (!query.next()) return null;
				
			return UUID.fromString(query.getString("uuid"));
			
		}
		
	}
	
	public static UUID getUUID(String name) throws SQLException {
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT uuid FROM users WHERE name = '" + name + "';")) {
			
			if (!query.next()) return null;
				
			return UUID.fromString(query.getString("uuid"));
			
		}
		
	}
	
	public static String getField(UUID uuid, String field) throws SQLException {
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT " + field + " FROM users WHERE uuid = '" + uuid.toString() + "';")) {
			
			if (!query.next()) return null;
				
			return query.getString(field);
			
		}
		
	}
	
	public static Rank getRank(UUID uuid) throws SQLException {
		
		return Rank.valueOf(getField(uuid, "rank"));
		
	}
	
	public static void updateNametag(Player player) throws SQLException {
		
		String name = player.getName();
		Rank rank = getRank(player.getUniqueId());
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			
			String onlineName = online.getName();
			Rank onlineRank = getRank(online.getUniqueId());
			Scoreboard scoreboard = online.getScoreboard();
			Team team = scoreboard.getTeam(name);
			
			if (team == null) {
				
				team = scoreboard.registerNewTeam(name);
				team.setAllowFriendlyFire(true);
				team.setCanSeeFriendlyInvisibles(false);
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				
			}
			
			team.setPrefix(rank.getPrefix() + "§7");
			team.setSuffix("§7");
			team.addEntry(name);
			
			scoreboard = player.getScoreboard();
			team = scoreboard.getTeam(onlineName);
			
			if (team == null) {
				
				team = scoreboard.registerNewTeam(onlineName);
				team.setAllowFriendlyFire(true);
				team.setCanSeeFriendlyInvisibles(false);
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				
			}
			
			team.setPrefix(onlineRank.getPrefix() + "§7");
			team.setSuffix("§7");
			team.addEntry(onlineName);
			
		}
		
	}
	
	public static void hideCoords(Player player) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(nmsPlayer, (byte) 22);
		
		nmsPlayer.playerConnection.sendPacket(packet);
		
	}
	
	public static void showCoords(Player player) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(nmsPlayer, (byte) 23);
		
		nmsPlayer.playerConnection.sendPacket(packet);
		
	}
	
	public static void sendActionBar(Player player, String message) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		IChatBaseComponent jsonText = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(jsonText, (byte) 2);
		
		nmsPlayer.playerConnection.sendPacket(packet);
		
	}
	
	public static void clearPlayer(Player player) {
		
		PlayerInventory inventory = player.getInventory();
		InventoryView openedInventory = player.getOpenInventory();
		
		inventory.clear();
		inventory.setArmorContents(null);
		player.setItemOnCursor(new ItemStack(Material.AIR));
		
		if (openedInventory.getType() == InventoryType.CRAFTING) openedInventory.getTopInventory().clear();
		
		player.setLevel(0);
		player.setTotalExperience(0);
		player.setExp(0f);
		player.setFoodLevel(20);
		player.setSaturation(5f);
		player.setExhaustion(0f);
		player.setHealth(player.getMaxHealth());
		player.getActivePotionEffects().stream().forEach(e -> player.removePotionEffect(e.getType()));
		
	}
	
	public static void healPlayer(Player player) {
		
		player.setHealth(player.getMaxHealth());
		player.setSaturation(5.0f);
		player.setExhaustion(0f);
		player.setFoodLevel(20);
		
	}
	
	public static void updatePlayers(Player player) throws SQLException {
		
		updatePlayers(player, Settings.getSettings(player.getUniqueId()));
		
	}
	
	public static void updatePlayers(Player player, Settings settings) throws SQLException {
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			
			if (online.equals(player)) continue;
			
			if ((ArenaFeature.isArena(player.getUniqueId()) || ArenaFeature.isJoining(player.getUniqueId())) && (ArenaFeature.isArena(online.getUniqueId()) || ArenaFeature.isJoining(online.getUniqueId()))) {
				
				player.showPlayer(online);
				online.showPlayer(player);
				continue;
				
			}
			
			Settings onlineSettings = Settings.getSettings(online.getUniqueId());
			
			if ((!settings.getPlayers() && !online.hasPermission("global.visible") && !ArenaFeature.isArena(online.getUniqueId()) && !ArenaFeature.isJoining(online.getUniqueId())) || SpecFeature.isSpectator(online)) player.hidePlayer(online);
			else player.showPlayer(online);
			
			if ((!onlineSettings.getPlayers() && !player.hasPermission("global.visible") && !ArenaFeature.isArena(player.getUniqueId()) && !ArenaFeature.isJoining(player.getUniqueId())) || SpecFeature.isSpectator(player)) online.hidePlayer(player);
			else online.showPlayer(player);
			
		}
		
	}	
	
	private static void error(PlayerLoginEvent event) {
		
		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Main.PREFIX + "An error has occured while connecting to §a" + Main.NAME);
		
	}
	
	private static void error(PlayerJoinEvent event) {
		
		event.setJoinMessage(null);
		event.getPlayer().kickPlayer(Main.PREFIX + "An error has occured while connecting to §a" + Main.NAME);
		
	}

	@EventHandler
	public void onConnect(PlayerLoginEvent event) {
		
		Player player = event.getPlayer();
		
		try {
			
			PermissionsManager.setPermissions(player);
			
		} catch (SQLException exception) {
			
			error(event);
			return;
			
		}
		
		if (event.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) return;
			
		if (player.isOp() || player.hasPermission("global.bypasswhitelist")) event.allow();
		else event.setKickMessage(Main.PREFIX + "You are not whitelisted");
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		World world = player.getWorld();
		
		try {
			
			Rank rank = PlayersManager.getRank(uuid);
			
			if (player.isDead()) player.spigot().respawn();
			
			Title title = new Title("§9" + Main.NAME, "§7Welcome on the Arena §7⋯ §a" + Bukkit.getOnlinePlayers().size() + " §7players", 0, 60, 10);
			Location location = world.getSpawnLocation();
			
			if (DataManager.spawnspoints != null) {
				
				List<Location> locations = DataManager.spawnspoints.stream().filter(l -> l.getWorld().equals(world)).collect(Collectors.toList());
				location = locations.isEmpty() ? world.getSpawnLocation() : locations.get(new Random().nextInt(locations.size()));
				
			}
			
			player.sendTitle(title);
			event.setJoinMessage("§7[§a+§7] " + rank.getPrefix() + "§7" + player.getName());
			player.teleport(location);
			player.setGameMode(GameMode.ADVENTURE);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			updateNametag(player);
			hideCoords(player);
			clearPlayer(player);
			
			if (player.hasPermission("global.fly")) player.setAllowFlight(true);
			
			updatePlayers(player);
			
		} catch (SQLException exception) {
			
			error(event);
			return;
			
		}
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		try {
			
			Rank rank = PlayersManager.getRank(uuid);
			
			event.setQuitMessage("§7[§c-§7] " + rank.getPrefix() + "§7" + player.getName());
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			showCoords(player);
			
			if (player.isInsideVehicle()) player.leaveVehicle();
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				Scoreboard scoreboard = online.getScoreboard();
				Team team = scoreboard.getTeam(player.getName());
				
				if (team != null) team.unregister();
				
			}
			
		} catch (SQLException exception) {
			
			event.setQuitMessage(null);
			return;
			
		}
		
		PermissionsManager.clearPermissions(player);
		
	}

}
