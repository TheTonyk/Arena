package com.thetonyk.Arena.Managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class PunishmentsManager {
	
	public static int isPunished(UUID uuid, Punishment type) throws SQLException {
		
		int player = Integer.valueOf(PlayersManager.getField(uuid, "id"));
		long now = new Date().getTime();
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT * FROM punishments WHERE player = " + player + " AND type = '" + type.toString() + "' ORDER BY date;")) {
			
			while (query.next()) {
				
				int id = query.getInt("id");
				long duration = query.getLong("duration");
				long date = query.getLong("date");
				int cancelled = query.getInt("cancelled");
				
				if (duration >= 0 && date + duration < now) continue;
				
				if (cancelled > 0) continue;
				else return id;
				
			}
			
		}
		
		return -1;
		
	}
	
	public static Set<UUID> isIPBanned(UUID uuid) throws SQLException {
		
		Set<UUID> alts = PlayersManager.getAlts(uuid).keySet();
		Iterator<UUID> iterator = alts.iterator();
		
		while (iterator.hasNext()) {
			
			if (isPunished(iterator.next(), Punishment.BAN) < 0) iterator.remove();
			
		}
		
		return alts;
		
	}
	
	public static boolean isLifetimeBanned(UUID uuid) throws SQLException {
		
		int banned = isPunished(uuid, Punishment.BAN);
		
		if (banned >= 0) {
			
			long duration = Long.valueOf(PunishmentsManager.getField(banned, "duration"));
			
			if (duration < 0) return true;
			
		}
		
		Set<UUID> ipBanned = isIPBanned(uuid);
		
		for (UUID alt : ipBanned) {
			
			int altBanned = isPunished(alt, Punishment.BAN);
			long duration = Long.valueOf(PunishmentsManager.getField(altBanned, "duration"));
			
			if (duration < 0) return true;
			
		}
		
		return false;
		
	}
	
	public static String getField(int id, String field) throws SQLException {
		
		try (Connection connection = DatabaseManager.getConnection();
		Statement statement = connection.createStatement();
		ResultSet query = statement.executeQuery("SELECT " + field + " FROM punishments WHERE id = " + id + ";")) {
			
			if (!query.next()) return null;
			
			return query.getString(field);
			
		}
		
	}
	
	public enum Punishment {
		
		BAN("Banned", "Ban", true), KICK("Kicked", "Kick", false), MUTE("Muted", "Mute", true);
		
		private String verb;
		private String shortName;
		private boolean withDuration;
		
		private Punishment(String verb, String shortName, boolean withDuration) {
			
			this.verb = verb;
			this.shortName = shortName;
			this.withDuration = withDuration;
			
		}
		
		public String getVerb() {
			
			return this.verb;
			
		}
		
		public String getShortName() {
			
			return this.shortName;
			
		}
		
		public boolean withDuration() {
			
			return this.withDuration;
			
		}
		
	}
	
}
