package com.thetonyk.Arena;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.thetonyk.Arena.Features.HealthScore;
import com.thetonyk.Arena.Features.SidebarFeature;
import com.thetonyk.Arena.Features.TablistFeature;
import com.thetonyk.Arena.Inventories.LeaderboardsInventory;
import com.thetonyk.Arena.Managers.ConnectionsAverager;
import com.thetonyk.Arena.Managers.DataManager;
import com.thetonyk.Arena.Managers.DatabaseManager;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	public static String PREFIX = "§9§lArena §8⫸ §7";
	public static String NAME;
	public static String TWITTER;
	public static String CHANNEL;
	public static String SERVER;
	
	public void onEnable() {
		
		plugin = this;
		
		if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();
		
		File config = new File(this.getDataFolder(), "config.yml");
		
		try {
			
			if (!config.exists()) {
				
				config.createNewFile();
				
				FileConfiguration configuration = this.getConfig();
				configuration.set("name", "Server");
				configuration.set("twitter", "@TheTonyk");
				configuration.set("channel", "server");
				configuration.set("SQLHost", "localhost");
				configuration.set("SQLDatabase", "database");
				configuration.set("SQLUser", "user");
				configuration.set("SQLPass", "pass");
				
				this.saveConfig();
				
			}
			
		} catch (IOException exception) {
			
			this.getLogger().severe("[Main] Unable to get configuration !");
			this.getServer().shutdown();
			return;
			
		}
		
		NAME = this.getConfig().getString("name", "Server");
		TWITTER = this.getConfig().getString("twitter", "@TheTonyk");
		CHANNEL = this.getConfig().getString("channel", "server");
		
		try {
			
			Iterator<Class<?>> classes = getClasses(Lists.newArrayList("Settings", "SettingsInventory", "StatsInventory", "PlayerInventory")).iterator();
			
			while (classes.hasNext()) {
				
				Object instance = classes.next().newInstance();
				
				if (instance instanceof Listener) {
					
					Bukkit.getPluginManager().registerEvents((Listener) instance, this);
					
				}
				
				if (instance instanceof CommandExecutor) {
					
					CommandExecutor command = (CommandExecutor) instance;
					String name = command.getClass().getSimpleName();
					
					this.getCommand(name.substring(0, name.length() - 7).toLowerCase()).setExecutor(command);
					
				}
				
				classes.remove();
				
			}
			
		} catch (InstantiationException | IllegalAccessException | IOException exception) {
			
			this.getLogger().severe("[Main] Unable to register listeners and commands !");
			this.getServer().shutdown();
			return;
			
		}
		
		MessengerListener listener = new MessengerListener();
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", listener);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL, listener);
		
		ConnectionsAverager.setup();
		DataManager.setup();
		HealthScore.setup();
		LeaderboardsInventory.setup();
		MessengerListener.setup();
		SidebarFeature.setup();
		TablistFeature.setup();
		
		Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
		Bukkit.setIdleTimeout(0);
		Bukkit.setSpawnRadius(0);
		
		for (World world : Bukkit.getWorlds()) {
			
			world.getWorldBorder().reset();
			world.setDifficulty(Difficulty.NORMAL);
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("doEntityDrops", "false");
			world.setGameRuleValue("doFireTick", "false");
			world.setGameRuleValue("doMobLoot", "false");
			world.setGameRuleValue("doMobSpawning", "false");
			world.setGameRuleValue("mobGriefing", "false");
			world.setPVP(true);
			world.setSpawnFlags(false, false);
			world.setStorm(false);
			world.setThundering(false);
			world.setTime(6000);
			
		}
		
	}
	
	public void onDisable() {
		
		plugin = null;
		
		try {
			
			DatabaseManager.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[Main] Unable to properly close the connection pool.");
			
		}
		
	}
	
	private List<Class<?>> getClasses(List<String> excludes) throws IOException {
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		JarFile jar = new JarFile(this.getFile());
		Enumeration<JarEntry> entries = jar.entries();
		
		if (excludes == null) excludes = new ArrayList<>();
		
		while (entries.hasMoreElements()) {
			
			JarEntry entry = entries.nextElement();
			String file = entry.getName().replace("/", ".");
			
			if (!file.startsWith("com.thetonyk") || !file.endsWith(".class") || file.contains("$")) continue;
			
			int index = file.substring(0, file.length() - 6).lastIndexOf(".");
			String name = file.substring(index + 1, file.length() - 6);
			
			if (excludes.contains(name)) continue;
			
			Class<?> instance;
			
			try {
				
				instance = Class.forName(file.substring(0, file.length() - 6));
				
			} catch (ClassNotFoundException exception) { continue; }
			
			if (instance.equals(this.getClass())) continue;
			
			classes.add(instance);
			
		}
		
		jar.close();
		return classes;
		
	}

}
