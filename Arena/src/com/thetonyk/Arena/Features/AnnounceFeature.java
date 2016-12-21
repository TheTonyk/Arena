package com.thetonyk.Arena.Features;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

import com.google.common.collect.Lists;
import com.thetonyk.Arena.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class AnnounceFeature {
	
	private static List<BaseComponent[]> ANNOUNCES = Lists.newArrayList(
			getPrefix().append("You want to know if the server is lagging? Use ").color(ChatColor.GRAY)
				.append("/lag").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lag")).create(),
			getPrefix().append("You want to see the exact health? Use ").color(ChatColor.GRAY)
				.append("/health").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/health ")).create(),
			getPrefix().append("You want to see who is connected? Use ").color(ChatColor.GRAY)
				.append("/list").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/list")).create(),
			getPrefix().append("You want to see the ping of someone? Use ").color(ChatColor.GRAY)
				.append("/ping").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ping ")).create(),
			getPrefix().append("You can see the top players with ").color(ChatColor.GRAY)
				.append("/leaderboards").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/leaderboards")).create(),
			getPrefix().append("Check the stats of someone else with ").color(ChatColor.GRAY)
				.append("/stats <player>").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/stats ")).create(),
			getPrefix().append("You can hide players in the hub in your settings.").color(ChatColor.GRAY).create(),
			getPrefix().append("You can hide your stats in your settings.").color(ChatColor.GRAY).create(),
			getPrefix().append("You can disable the chat in your settings.").color(ChatColor.GRAY).create(),
			getPrefix().append("You can disable your messages in your settings.").color(ChatColor.GRAY).create(),
			getPrefix().append("Someone bother you? Ignore him with ").color(ChatColor.GRAY)
				.append("/ignore <player>").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ignore ")).create(),
			getPrefix().append("Any questions? Ask the staff with ").color(ChatColor.GRAY)
				.append("/helpop").color(ChatColor.GOLD)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/helpop ")).create());
	
	public static void setup() {
		
		Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> Bukkit.getOnlinePlayers().stream().forEach(p -> p.spigot().sendMessage(ANNOUNCES.get(new Random().nextInt(ANNOUNCES.size())))), 12000, 12000);
		
	}
	
	private static ComponentBuilder getPrefix() {
		
		return new ComponentBuilder("Info ").color(ChatColor.GREEN).bold(true).append("⫸ ").color(ChatColor.DARK_GRAY).bold(false);
		
	}

}
