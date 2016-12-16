package com.thetonyk.Arena.Features;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.thetonyk.Arena.Main;
import com.thetonyk.Arena.Managers.ConnectionsAverager;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class TablistFeature {
	
	public static void setup() {
		
		Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> Bukkit.getOnlinePlayers().stream().forEach(TablistFeature::sendTab), 1, 1);
		
	}
	
	private static void sendTab(Player player) {
		
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		String name = player.getName();
		int ping = (int) ConnectionsAverager.getPing(player.getUniqueId(), 100);
		Format format = new DecimalFormat("00.00");
		String tps = format.format(ConnectionsAverager.getTps(100));
		int players = Bukkit.getOnlinePlayers().stream().filter(p -> !SpecFeature.isSpectator(p)).collect(Collectors.toList()).size();
		
		IChatBaseComponent jsonHeader = ChatSerializer.a("{\"text\":\"\n §7Welcome on the Arena, §6" + name + " §7! \n §b" + Main.TWITTER + "  §7⋯  TS: §acommandspvp.com \n\"}");
		IChatBaseComponent jsonFooter = ChatSerializer.a("{\"text\":\"\n §7Players: §a" + players + "  §7⋯  Ping: §a" + (ping < 1 ? "Not calculated" : ping + "§7ms") + "  §7⋯  TPS: §a" + tps + " \n\"}");
		
		PacketPlayOutPlayerListHeaderFooter tabHeader = new PacketPlayOutPlayerListHeaderFooter(jsonHeader);
		
		try {
		
			Field field = tabHeader.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(tabHeader, jsonFooter);
			field.setAccessible(false);
		
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
			
			Bukkit.getLogger().severe("[TablistFeature] Unable to send custom tab to a player");
			return;
			
		}
		
		nmsPlayer.playerConnection.sendPacket(tabHeader);
		
	}

}
