package com.thetonyk.Arena.Inventories;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Features.StatsFeature;
import com.thetonyk.Arena.Managers.DataManager;
import com.thetonyk.Arena.Managers.PlayersManager;
import com.thetonyk.Arena.Utils.DateUtils;
import com.thetonyk.Arena.Utils.ItemsUtils;
import com.thetonyk.Arena.Utils.NamesUtils;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public class PlayerInventory implements Listener {
	
	public static Map<UUID, PlayerInventory> inventories = new HashMap<>();
	private Inventory inventory;
	private UUID player;
	
	public PlayerInventory(UUID player) throws IllegalArgumentException, SQLException {
		
		this.inventory = Bukkit.createInventory(null, 54, "§7Inventory §8⫸ §4" + PlayersManager.getField(player, "name"));
		this.player = player;
		
		update();
		inventories.put(player, this);
		
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		
		new BukkitRunnable() {

			public void run() {
				
				if (inventory.getViewers().isEmpty()) return;
				
				update();
				
			}
			
		}.runTaskTimer(Main.plugin, 1, 1);
		
	}
	
	public static PlayerInventory getInventory(UUID uuid) throws SQLException {
		
		if (inventories.containsKey(uuid)) return inventories.get(uuid);
		
		return new PlayerInventory(uuid);
		
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
		
		Player player = Bukkit.getPlayer(this.player);
		
		if (player == null) {
			
			inventory.getViewers().stream().forEach(p -> p.closeInventory());
			return;
			
		}
		
		Map<String, Double> scores;
		
		try {
			
			scores = DataManager.getScores(this.player);
			
		} catch (SQLException exception) {
			
			this.inventory.clear();
			addGlasses();
			this.inventory.setItem(22, ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cUnable to get the stats. §7Try again later.", 1));
			return;
			
		}
		
		Format format = new DecimalFormat("##.##");
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		ItemStack itemInHand = player.getItemInHand();
		ItemStack itemOnCursor = player.getItemOnCursor();
		ItemStack[] armors = player.getInventory().getArmorContents();
		ItemStack[] content = player.getInventory().getContents();
		int health = (int) ((player.getHealth() / 2) * 10); 
		int maxHealth = (int) ((player.getMaxHealth() / 2) * 10); 
		int absorptionHealth = (int) ((nmsPlayer.getAbsorptionHearts() / 2) * 10);
		int air = player.getRemainingAir();
		int maxAir = player.getMaximumAir();
		int food = player.getFoodLevel();
		float saturation = player.getSaturation();
		float exhaustion = player.getExhaustion();
		Collection<PotionEffect> effects = player.getActivePotionEffects();
		int currentKillstreak = player.getLevel();
		String ratio = format.format(scores.get("deaths") < 1 ? 0 : scores.get("kills") /  scores.get("deaths"));
		int kills = scores.get("kills").intValue();
		int deaths = scores.get("deaths").intValue();
		int killstreak = scores.get("killstreak").intValue();
		String accuracy = format.format(scores.get("shot") < 1 ? 0 : (scores.get("hit") /  scores.get("shot")) * 100);
		String longshot = scores.get("longshot") > 0 ? format.format(scores.get("longshot")) : "None";
		int shot = scores.get("shot").intValue();
		int hit = scores.get("hit").intValue();
		int gapple = scores.get("gapple").intValue();
		long time = StatsFeature.joinTime.containsKey(this.player) ? new Date().getTime() - StatsFeature.joinTime.get(this.player) : 0;
		String played = scores.get("time").longValue() + time > 0 ? DateUtils.toText(scores.get("time").longValue() + time, true) : "None";
		
		inventory.clear();
		
		List<String> lore = new ArrayList<>();
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7" + Main.NAME, 1, 7);
		
		ItemStack healthBanner = new ItemStack(Material.BANNER);
		BannerMeta banner = (BannerMeta) healthBanner.getItemMeta();
		banner.setBaseColor(DyeColor.RED);
		banner.addPattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
		banner.addPattern(new Pattern(DyeColor.RED, PatternType.HALF_HORIZONTAL));
		banner.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
		banner.addPattern(new Pattern(DyeColor.RED, PatternType.TRIANGLE_TOP));
		banner.setDisplayName("§8⫸ §6Health & Food §8⫷");
		
		lore.add("");
		lore.add("§8⫸ §7Health: §6" + health + "§7%   ");
		lore.add("§8⫸ §7Max Health: §6" + maxHealth + "§7%   ");
		lore.add("§8⫸ §7Absorption: §6" + absorptionHealth + "§7%   ");
		lore.add("");
		lore.add("§8⫸ §7Remaining Air: " + (air < maxAir ? "§6" + (int) air / 20 + "§7s" : "§cNone") + "   ");
		lore.add("");
		lore.add("§8⫸ §7Food level: §6" + food + "   ");
		lore.add("§8⫸ §7Saturation: §6" + format.format(saturation) + "   ");
		lore.add("§8⫸ §7Exhaustion: §6" + format.format(exhaustion) + "   ");
		lore.add("");
		
		banner.setLore(lore);
		banner.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		healthBanner.setItemMeta(banner);
		
		lore.clear();
		lore.add("");
		
		if (effects.isEmpty()) {
			
			lore.add("§8⫸ §7None");
			lore.add("");
			
		} else {
			
			for (PotionEffect effect : effects) {
				
				lore.add("§8⫸ §6" + NamesUtils.getPotionName(effect.getType()) + "§7:   ");
				lore.add("§8⫸   §7Level: §6" + (effect.getAmplifier() + 1) + "   ");
				lore.add("§8⫸   §7Duration: §6" + effect.getDuration() / 20 + "§7s   ");
				lore.add("");
				
			}
			
		}
		
		ItemStack potions = ItemsUtils.createItem(Material.POTION, "§8⫸ §6Potion effects §8⫷", 1, 0, lore);
		potions = ItemsUtils.hideFlags(potions);
		
		lore.clear();
		lore.add("");
		lore.add("   §7Kills: §6" + kills + "   ");
		lore.add("   §7Deaths: §6" + deaths + "   ");
		lore.add("   §7Ratio: §6" + ratio + "   ");
		lore.add("   §7Best Killsteak: §6" + killstreak + "   ");
		lore.add("");
		lore.add("   §7Current Killstreak: §6" + currentKillstreak + "   ");
		lore.add("");
		
		ItemStack pvp = ItemsUtils.createItem(Material.IRON_SWORD, "§8⫸ §6PVP Stats §8⫷", 1, 0, lore);
		pvp = ItemsUtils.hideFlags(pvp);
		
		lore.clear();
		lore.add("");
		lore.add("   §7Arrows Shot: §6" + shot + "   ");
		lore.add("   §7Arrows Hits: §6" + hit + "   ");
		lore.add("   §7Bow Accuracy: §6" + accuracy + "§7%   ");
		lore.add("   §7Longest Shot: §6" + longshot + "§7m   ");
		lore.add("");
		
		ItemStack bow = ItemsUtils.createItem(Material.BOW, "§8⫸ §6Bow Stats §8⫷", 1, 0, lore);
		bow = ItemsUtils.hideFlags(bow);
		
		lore.clear();
		lore.add("");
		lore.add("   §7Golden Apples eaten: §6" + gapple + "   ");
		lore.add("   §7Time played: §6" + played + "   ");
		lore.add("");
		
		ItemStack misc = ItemsUtils.createItem(Material.NAME_TAG, "§8⫸ §6Others stats §8⫷", 1, 0, lore);
		misc = ItemsUtils.hideFlags(misc);
		
		inventory.setItem(0, separator);
		inventory.setItem(1, healthBanner );
		inventory.setItem(2, potions);
		inventory.setItem(3, separator);
		inventory.setItem(4, pvp);
		inventory.setItem(5, bow);
		inventory.setItem(6, misc);
		inventory.setItem(7, separator);
		inventory.setItem(8, separator);
		
		for (int i = 9; i < content.length; i++) {
			
			inventory.setItem(i, content[i]);
			
		}
		
		inventory.setItem(36, separator);
		inventory.setItem(37, itemInHand);
		inventory.setItem(38, ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7⫷ §6Item in hand", 1, 7));
		inventory.setItem(39, itemOnCursor);
		inventory.setItem(40, ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7⫷ §6Item on cursor", 1, 7));
		inventory.setItem(41, armors[3]);
		inventory.setItem(42, armors[2]);
		inventory.setItem(43, armors[1]);
		inventory.setItem(44, armors[0]);
		
		for (int i = 0; i <= 8; i++) {
			
			inventory.setItem(i + 45, content[i]);
			
		}
		
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		
		Inventory inventory = event.getInventory();
		
		if (inventory == null  || !inventory.equals(this.inventory)) return;
		
		update();
		
	}

}
