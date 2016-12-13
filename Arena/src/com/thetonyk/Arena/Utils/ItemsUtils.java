package com.thetonyk.Arena.Utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class ItemsUtils {
	
	public static ItemStack createItem(Material material, String name, int number, int damage, List<String> lore) {
		
		ItemStack item = new ItemStack(material, number, (short) damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		
		if (lore != null) meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
		
	}
	
	public static ItemStack createItem(Material material, String name, int number, int damage) {
		
		return createItem(material, name, number, damage, null);
		
	}
	
	public static ItemStack createItem(Material material, String name, int number) {
		
		return createItem(material, name, number, 0, null);
		
	}
	
	public static ItemStack setGlowing(ItemStack item) {
		
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag;
		
		if (!nmsItem.hasTag()) {
			
			tag = new NBTTagCompound();
			nmsItem.setTag(tag);
			
		} else tag = nmsItem.getTag();
		
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsItem.setTag(tag);
		
		return CraftItemStack.asCraftMirror(nmsItem);
		
	}
	
	public static ItemStack hideFlags(ItemStack item) {
		
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		
		return item;
		
	}

}
