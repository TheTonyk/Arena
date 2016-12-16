package com.thetonyk.Arena.Utils;

import java.util.List;

import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

public class NamesUtils {
	
	public static List<String> EFFECTS = Lists.newArrayList(
			"speed",
			"instant_health",
			"strength",
			"water_breathing",
			"invisibility",
			"resistance",
			"blindness",
			"haste",
			"instant_damage",
			"poison",
			"slowness",
			"hunger",
			"weakness",
			"fire_resistance",
			"saturation",
			"nausea",
			"jump_boost",
			"mining_fatigue",
			"wither",
			"absorption",
			"night_vision",
			"health_boost",
			"regeneration");
	
	public static PotionEffectType getPotionEffect(String name) {
		
		String[] names = name.toLowerCase().split(":");
		
		switch(names[names.length - 1]) {
		
			case "speed":
				return PotionEffectType.SPEED;
			case "instant_health":
				return PotionEffectType.HEAL;
			case "strength":
				return PotionEffectType.INCREASE_DAMAGE;
			case "water_breathing":
				return PotionEffectType.WATER_BREATHING;
			case "invisibility":
				return PotionEffectType.INVISIBILITY;
			case "resistance":
				return PotionEffectType.DAMAGE_RESISTANCE;
			case "blindness":
				return PotionEffectType.BLINDNESS;
			case "haste":
				return PotionEffectType.FAST_DIGGING;
			case "instant_damage":
				return PotionEffectType.HARM;
			case "poison":
				return PotionEffectType.POISON;
			case "slowness":
				return PotionEffectType.SLOW;
			case "hunger":
				return PotionEffectType.HUNGER;
			case "weakness":
				return PotionEffectType.WEAKNESS;
			case "fire_resistance":
				return PotionEffectType.FIRE_RESISTANCE;
			case "saturation":
				return PotionEffectType.SATURATION;
			case "nausea":
				return PotionEffectType.CONFUSION;
			case "jump_boost":
				return PotionEffectType.JUMP;
			case "mining_fatigue":
				return PotionEffectType.SLOW_DIGGING;
			case "wither":
				return PotionEffectType.WITHER;
			case "absorption":
				return PotionEffectType.ABSORPTION;
			case "night_vision":
				return PotionEffectType.NIGHT_VISION;
			case "health_boost":
				return PotionEffectType.HEALTH_BOOST;
			case "regeneration":
				return PotionEffectType.REGENERATION;
			default:
				return null;
		
		}
		
	}

	public static String getPotionName(PotionEffectType type) {
		
        switch (type.getName().toLowerCase()) {
        
	        case "speed":
	            return "Speed";
	        case "slow":
	            return "Slowness";
	        case "fast_digging":
	            return "Haste";
	        case "slow_digging":
	            return "Mining fatigue";
	        case "increase_damage":
	            return "Strength";
	        case "heal":
	            return "Instant Health";
	        case "harm":
	            return "Instant Damage";
	        case "jump":
	            return "Jump Boost";
	        case "confusion":
	            return "Nausea";
	        case "regeneration":
	            return "Regeneration";
	        case "damage_resistance":
	            return "Resistance";
	        case "fire_resistance":
	            return "Fire Resistance";
	        case "water_breathing":
	            return "Water breathing";
	        case "invisibility":
	            return "Invisibility";
	        case "blindness":
	            return "Blindness";
	        case "night_vision":
	            return "Night Vision";
	        case "hunger":
	            return "Hunger";
	        case "weakness":
	            return "Weakness";
	        case "poison":
	            return "Poison";
	        case "wither":
	            return "Wither";
	        case "health_boost":
	            return "Health Boost";
	        case "absorption":
	            return "Absorption";
	        case "saturation":
	            return "Saturation";
	        default:
	            return "???";
            
        }
		
	}
	
}
