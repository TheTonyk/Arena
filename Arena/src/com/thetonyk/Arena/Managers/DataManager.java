package com.thetonyk.Arena.Managers;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.DisplayScoresFeature;

public class DataManager {
	
	public static List<Location> spawnspoints = null;
	public static int y = 0;
	public static List<Location> stats = null;
	public static List<Location> bests = null;
	private static Map<UUID, Map<String, Double>> scores = new HashMap<>();
	
	public static Map<String, Integer> best = new LinkedHashMap<>();
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	
	public static void setup() {
		
		new BukkitRunnable() {
			
			public void run() {
		
				if (Main.SERVER == null || Main.SERVER.length() < 1) return;
				
				try (Connection connection = DatabaseManager.getConnection();
				Statement statement = connection.createStatement();
				ResultSet query = statement.executeQuery("SELECT * FROM data WHERE server = '" + Main.SERVER + "';")) {
					
					if (!query.next()) return;
					
					spawnspoints = getUnformatted(query.getString("spawnpoints"));
					y = query.getInt("y");
					stats = getUnformatted(query.getString("stats"));
					bests = getUnformatted(query.getString("bests"));
							
				} catch (SQLException exception) {return;}
				
				updateBests();
				cancel();
				
			}
		
		}.runTaskTimer(Main.plugin, 0, 10);
		
	}
	
	public static void updateBests() {
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT * FROM scores WHERE server = '" + Main.SERVER + "' AND kills > 0 ORDER BY kills DESC LIMIT 10")) {
			
			best.clear();
			
			while (query.next()) {
				
				UUID uuid = PlayersManager.getUUID(query.getInt("id"));
				
				if (uuid == null) continue;
				
				String name = PlayersManager.getField(uuid, "name");
				int kills = query.getInt("kills");
				
				best.put(name, kills);
				
			}
			
			DisplayScoresFeature.updateBests();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[Main] Unable to get update the bests scores !");
			Bukkit.getServer().shutdown();
			
		}
		
	}
	
	public static String getFormatted(List<Location> locations) {
		
		List<Loc> locs = new ArrayList<>();
		locations.stream().forEach(l -> locs.add(new Loc(l)));
		
		Type type = new TypeToken<List<Loc>>(){}.getType();
		
		return locs.isEmpty() ? "" : gson.toJson(locs, type);
		
	}
	
	public static List<Location> getUnformatted(String formatted) {
		
		List<Location> locations = new ArrayList<>();
		
		if (formatted == null) return null;
		
		Arrays.stream(new Gson().fromJson(formatted, Loc[].class)).forEach(l -> locations.add(l.getLocation()));
		
		return locations;
		
	}
	
	public static boolean updateValue(String value, String name) throws SQLException {
		
		if (Main.SERVER == null | Main.SERVER.length() < 1) return false;
		
		if (!DatabaseManager.exist("SELECT * FROM data WHERE server = '" + Main.SERVER + "';")) {
			
			DatabaseManager.updateQuery("INSERT INTO data (`server`, `spawnpoints`, `y`, `stats`, `bests`) VALUES ('" + Main.SERVER + "', '', 0, '', '');");
			
		}
		
		DatabaseManager.updateQuery("UPDATE data SET " + name + " = '" + value + "' WHERE server = '" + Main.SERVER + "';");
		return true;
		
	}
	
	public static Map<String, Double> getScores(UUID uuid) throws SQLException {
		
		if (!scores.containsKey(uuid)) {
			
			Map<String, Double> score = new HashMap<>();
			score.put("kills", 0d);
			score.put("deaths", 0d);
			score.put("killstreak", 0d);
			score.put("gapple", 0d);
			score.put("shot", 0d);
			score.put("hit", 0d);
			score.put("longshot", -1d);
			score.put("time", 0d);
			
			if (Main.SERVER == null || Main.SERVER.length() < 1) return score;
			
			int id = Integer.valueOf(PlayersManager.getField(uuid, "id"));
			
			try (Connection connection = DatabaseManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet query = statement.executeQuery("SELECT * FROM scores WHERE server = '" + Main.SERVER + "' AND id = " + id + ";")) {
				
				if (query.next()) {
				
					score.put("kills", query.getDouble("kills"));
					score.put("deaths", query.getDouble("deaths"));
					score.put("killstreak", query.getDouble("killstreak"));
					score.put("gapple", query.getDouble("gapple"));
					score.put("shot", query.getDouble("shot"));
					score.put("hit", query.getDouble("hit"));
					score.put("longshot", query.getDouble("longshot"));
					score.put("time", query.getDouble("time"));
				
				}
				
			}
			
			scores.put(uuid, score);
			
		}
		
		return scores.get(uuid);
		
	}
	
	public static Map<UUID, Map<String, Double>> getAllScores() throws SQLException {
		
		Map<UUID, Map<String, Double>> scores = new HashMap<>();
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT * FROM scores WHERE server = '" + Main.SERVER + "';")) {
			
			while (query.next()) {
			
				UUID uuid = PlayersManager.getUUID(query.getInt("id"));
				Map<String, Double> score = new HashMap<>();
				
				score.put("kills", query.getDouble("kills"));
				score.put("deaths", query.getDouble("deaths"));
				score.put("killstreak", query.getDouble("killstreak"));
				score.put("gapple", query.getDouble("gapple"));
				score.put("shot", query.getDouble("shot"));
				score.put("hit", query.getDouble("hit"));
				score.put("longshot", query.getDouble("longshot"));
				score.put("time", query.getDouble("time"));
				
				scores.put(uuid, score);
			
			}
			
		}
		
		return scores;
		
	}
	
	public static void updateScores(UUID uuid, Map<String, Double> score) throws SQLException {
		
		scores.put(uuid, score);
		
		if (Main.SERVER == null || Main.SERVER.length() < 1) return;
		
		int id = Integer.valueOf(PlayersManager.getField(uuid, "id"));
		
		if (DatabaseManager.exist("SELECT * FROM scores  WHERE server = '" + Main.SERVER + "' AND id = " + id + ";")) {
			
			DatabaseManager.updateQuery("UPDATE scores SET kills = " + score.get("kills") + ", deaths = " + score.get("deaths") + ", killstreak = " + score.get("killstreak") + ", gapple = " + score.get("gapple") + ", shot = " + score.get("shot") + ", hit = " + score.get("hit") + ", longshot = " + score.get("longshot") + ", time = " + score.get("time") + " WHERE server = '" + Main.SERVER + "' AND id = " + id + ";");
			
		} else {
			
			DatabaseManager.updateQuery("INSERT INTO scores (`server`, `id`, `kills`, `deaths`, `killstreak`, `gapple`, `shot`, `hit`, `longshot`, `time`) VALUES ('" + Main.SERVER + "', '" + id + "', " + score.get("kills") + ", " + score.get("deaths") + ", " + score.get("killstreak") + ", " + score.get("gapple") + ", " + score.get("shot") + ", " + score.get("hit") + ", " + score.get("longshot") + ", " + score.get("time") + ");");
			
		}
		
	}
	
	private static class Loc {
		
		private String world;
		private double x;
		private double y;
		private double z;
		
		public Loc(World world, double x, double y, double z) {
			
			this.world = world.getName();
			this.x = x;
			this.y = y;
			this.z = z;
			
		}
		
		public Loc(Location location) {
			
			this(location.getWorld(), location.getX(), location.getY(), location.getZ());
			
		}
		
		public World getWorld() {
			
			return Bukkit.getWorld(this.world);
			
		}
		
		public double getX() {
			
			return this.x;
			
		}
		
		public double getY() {
			
			return this.y;
			
		}

		public double getZ() {
			
			return this.z;
			
		}
		
		public Location getLocation() {
			
			return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
			
		}
		
	}

}
