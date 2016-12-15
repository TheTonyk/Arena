package com.thetonyk.Arena.Managers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.thetonyk.Arena.Main;

public class PermissionsManager {
	
	private static Map<UUID, PermissionAttachment> permissions = new HashMap<>();
	
	public static void setPermissions(Player player) throws SQLException {
		
		UUID uuid = player.getUniqueId();
		Rank rank = PlayersManager.getRank(uuid);
		
		if (!permissions.containsKey(uuid)) permissions.put(uuid, player.addAttachment(Main.plugin));
		
		PermissionAttachment permission = permissions.get(uuid);
		
		permission.setPermission("bukkit.command.version", false);
		permission.setPermission("bukkit.command.plugins", false);
		permission.setPermission("bukkit.command.help", false);
		permission.setPermission("bukkit.command.me", false);
		permission.setPermission("bukkit.command.tell", false);
		permission.setPermission("bukkit.command.version", false);
		permission.setPermission("minecraft.command.help", false);
		permission.setPermission("minecraft.command.me", false);
		permission.setPermission("minecraft.command.tell", false);
		permission.setPermission("global.lag", true);
		permission.setPermission("global.ping", true);
		permission.setPermission("arena.stats", true);
		permission.setPermission("arena.leaderboards", true);
		permission.setPermission("arena.report", true);
		permission.setPermission("arena.health", true);
		permission.setPermission("arena.list", true);
		
		if (rank == Rank.PLAYER) return;
		
		permission.setPermission("global.fly", true);
		permission.setPermission("global.visible", true);
		
		switch (rank) {
			case FAMOUS:
			case FRIEND:
				return;
			default:
				break;
		}
		
		permission.setPermission("global.build", true);
		permission.setPermission("global.text", true);
		
		if (rank == Rank.BUILDER) return;
		
		permission.unsetPermission("global.build");
		permission.unsetPermission("global.text");
		permission.setPermission("global.bypasswhitelist", true);
		
		if (rank == Rank.MOD) return;
		
		permission.setPermission("global.whitelist", true);
		
		if (rank != Rank.ADMIN) return;
		
		permission.setPermission("global.flycommand", true);
		permission.setPermission("global.gamemode", true);
		permission.setPermission("global.text", true);
		permission.setPermission("global.build", true);
		permission.setPermission("arena.arena", true);
		permission.setPermission("arena.heal", true);
		permission.setPermission("arena.sethealth", true);
		permission.setPermission("arena.give", true);
		player.setOp(true);
		
	}
	
	public static void clearPermissions(Player player) {
		
		PermissionAttachment permission = permissions.remove(player.getUniqueId());
		
		if (permission == null) return;
		
		player.removeAttachment(permission);
		
	}
	
	public static void reloadPermissions(Player player) throws SQLException {
		
		clearPermissions(player);
		setPermissions(player);
		
	}
	
	public enum Rank {
		
		PLAYER("", "§7Player"), FAMOUS("§bFamous §8| ", "§bFamous"), BUILDER("§2Build §8| ", "§2Builder"), STAFF("§cStaff §8| ", "§cStaff"), MOD("§9Mod §8| ", "§9Moderator"), ADMIN("§4Admin §8| ", "§4Admin"), FRIEND("§3Friend §8| ", "§3Friend");
		
		String prefix;
		String name;
		
		private Rank(String prefix, String name) {
			
			this.prefix = prefix;
			this.name = name;
			
		}
		
		public String getPrefix() {
			
			return prefix;
			
		}
		
		public String getName() {
			
			return name;
			
		}
		
	}

}
