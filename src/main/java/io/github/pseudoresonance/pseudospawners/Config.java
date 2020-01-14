package io.github.pseudoresonance.pseudospawners;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;
import net.md_5.bungee.api.ChatColor;

public class Config extends PluginConfig {
	
	public static Material lastPageMaterial = Material.PAPER;
	public static int lastPageLocation = 1;
	public static Material nextPageMaterial = Material.PAPER;
	public static int nextPageLocation = 7;

	public static List<EntityType> blacklist;
	public static List<EntityType> spawnable;

	public static String color = "§e";
	public static ChatColor[] colorArray = new ChatColor[] { ChatColor.YELLOW };;
	public static HashMap<EntityType, String> names;
	public static HashMap<String, EntityType> namesReverse;

	
	public void reloadConfig() {
		FileConfiguration fc = PseudoSpawners.plugin.getConfig();
		boolean locationError = false;
		lastPageMaterial = PluginConfig.getMaterial(fc, "LastPageMaterial", lastPageMaterial);
		int lastPageLocation = PluginConfig.getInt(fc, "LastPageLocation", Config.lastPageLocation);
		if (lastPageLocation >= 0 && lastPageLocation <= 8) {
			Config.lastPageLocation = lastPageLocation;
		} else {
			locationError = true;
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid config option for LastPageLocation!");
		}
		nextPageMaterial = PluginConfig.getMaterial(fc, "NextPageMaterial", nextPageMaterial);
		int nextPageLocation = PluginConfig.getInt(fc, "NextPageLocation", Config.nextPageLocation);
		if (nextPageLocation >= 0 && nextPageLocation <= 8 && nextPageLocation != lastPageLocation) {
			Config.nextPageLocation = nextPageLocation;
		} else {
			locationError = true;
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid config option for NextPageLocation!");
		}
		if (locationError) {
			lastPageLocation = 1;
			nextPageLocation = 7;
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Default item locations have been used!");
		}
		
		blacklist = new ArrayList<EntityType>();
		spawnable = new ArrayList<EntityType>();
		String config = "";
		try {
			List<String> l = PseudoSpawners.plugin.getConfig().getStringList("Blacklist");
			Collections.sort(l, Collator.getInstance());
			for (String s : l) {
				String u = s.toUpperCase();
				config = u;
				try {
					EntityType et = EntityType.valueOf(u);
					blacklist.add(et);
				} catch (IllegalArgumentException e) {
					PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid configuration for blacklisted types! Unknown entity: " + u);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid configuration for disallowed mob types at " + config + "!");
		}
		List<String> ent = new ArrayList<String>();
		for (EntityType et : EntityType.values()) {
			if (et.isSpawnable())
				ent.add(et.toString());
		}
		Collections.sort(ent, Collator.getInstance());
		for (String st : ent)
			if (EntityType.valueOf(st).isSpawnable())
				if (!blacklist.contains(EntityType.valueOf(st)))
					spawnable.add(EntityType.valueOf(st));
		
		colorArray = PluginConfig.getColorCodes(fc, "Color", colorArray);
		color = StringUtils.join(colorArray);

		names = new HashMap<EntityType, String>();
		namesReverse = new HashMap<String, EntityType>();
		try {
			HashMap<EntityType, String> nm = GetNMSName.getNameMap();
			HashMap<String, EntityType> nrm = GetNMSName.getNameMapReverse();
			ConfigurationSection cs = PseudoSpawners.plugin.getConfig().getConfigurationSection("Names");
			if (cs != null) {
				Set<String> l = cs.getKeys(false);
				for (String s : l) {
					String u = s.toUpperCase();
					try {
						EntityType et = EntityType.valueOf(u);
						String n = PseudoSpawners.plugin.getConfig().getString("Names." + s);
						nm.put(et, n);
						nrm.put(n, et);
					} catch (IllegalArgumentException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid configuration for mob type names! Unknown entity: " + u);
					}
				}
			}
			names = nm;
			namesReverse = nrm;
		} catch (Exception e) {
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(ChatColor.RED + "Invalid configuration for mob type names!");
		}
	}
	
	public static String getName(EntityType et) {
		if (names.containsKey(et)) {
			return names.get(et);
		} else {
			return et.toString();
		}
	}
	
	public static EntityType getEntity(String name) throws IllegalArgumentException {
		for (String n : namesReverse.keySet()) {
			if (n.equalsIgnoreCase(name)) {
				try {
					return namesReverse.get(n);
				} catch (IllegalArgumentException e) {
					throw e;
				}
			}
		}
		try {
			return EntityType.valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public Config(PseudoPlugin plugin) {
		super(plugin);
	}

}