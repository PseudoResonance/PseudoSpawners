package io.github.pseudoresonance.pseudospawners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudoapi.bukkit.ConfigOption;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import net.md_5.bungee.api.ChatColor;

public class ConfigOptions implements ConfigOption {

	public static List<EntityType> blacklist;
	public static List<EntityType> spawnable;
	public static String interfaceName;
	public static Material lastPageMaterial;
	public static String lastPageName;
	public static int lastPageInt;
	public static Material nextPageMaterial;
	public static String nextPageName;
	public static int nextPageInt;
	public static ChatColor color;
	public static Map<EntityType, String> names;
	public static Map<String, EntityType> namesReverse;
	
	public static boolean updateConfig() {
		boolean error = false;
		InputStream configin = PseudoSpawners.plugin.getClass().getResourceAsStream("/config.yml"); 
		BufferedReader configreader = new BufferedReader(new InputStreamReader(configin));
		YamlConfiguration configc = YamlConfiguration.loadConfiguration(configreader);
		int configcj = configc.getInt("Version");
		try {
			configreader.close();
			configin.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (PseudoSpawners.plugin.getConfig().getInt("Version") != configcj) {
			try {
				String oldFile = "";
				File conf = new File(PseudoSpawners.plugin.getDataFolder(), "config.yml");
				if (new File(PseudoSpawners.plugin.getDataFolder(), "config.yml.old").exists()) {
					for (int i = 1; i > 0; i++) {
						if (!(new File(PseudoSpawners.plugin.getDataFolder(), "config.yml.old" + i).exists())) {
							conf.renameTo(new File(PseudoSpawners.plugin.getDataFolder(), "config.yml.old" + i));
							oldFile = "config.yml.old" + i;
							break;
						}
					}
				} else {
					conf.renameTo(new File(PseudoSpawners.plugin.getDataFolder(), "config.yml.old"));
					oldFile = "config.yml.old";
				}
				PseudoSpawners.plugin.saveDefaultConfig();
				PseudoSpawners.plugin.reloadConfig();
				Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date! Old config file renamed to " + oldFile + ".");
			} catch (Exception e) {
				Message.sendConsoleMessage(ChatColor.RED + "Error while updating config!");
				error = true;
			}
		}
		if (!error) {
			Message.sendConsoleMessage(ChatColor.GREEN + "Config is up to date!");
		} else {
			return false;
		}
		return true;
	}
	
	public void reloadConfig() {
		String config = "";
		blacklist = new ArrayList<EntityType>();
		spawnable = new ArrayList<EntityType>();
		interfaceName = "&c&lPseudoSpawners";
		lastPageMaterial = Material.PAPER;
		lastPageName = "Page {page}";
		lastPageInt = 1;
		nextPageMaterial = Material.PAPER;
		nextPageName = "Page {page}";
		nextPageInt = 7;
		color = ChatColor.YELLOW;
		names = new HashMap<EntityType, String>();
		namesReverse = new HashMap<String, EntityType>();
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
					Message.sendConsoleMessage(ChatColor.RED + "Invalid configuration for blacklisted types! Unknown entity: " + u);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message.sendConsoleMessage(ChatColor.RED + "Invalid configuration for disallowed mob types at " + config + "!");
		}
		List<String> ent = new ArrayList<String>();
		for (EntityType et : EntityType.values()) {
			ent.add(et.toString());
		}
		Collections.sort(ent, Collator.getInstance());
		for (String st : ent)
			if (EntityType.valueOf(st).isSpawnable())
				if (!blacklist.contains(EntityType.valueOf(st)))
					spawnable.add(EntityType.valueOf(st));
		interfaceName = ChatColor.translateAlternateColorCodes('&', PseudoSpawners.plugin.getConfig().getString("InterfaceName"));
		try {
			lastPageMaterial = Enum.valueOf(Material.class, PseudoSpawners.plugin.getConfig().getString("LastPageItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for LastPage!");
		}
		lastPageName = ChatColor.translateAlternateColorCodes('&', PseudoSpawners.plugin.getConfig().getString("LastPageName"));
		int lastPageLocation = PseudoSpawners.plugin.getConfig().getInt("LastPageLocation");
		if (lastPageLocation >= 1 && lastPageLocation <= 9) {
			lastPageInt = lastPageLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for LastPage!");
		}
		try {
			nextPageMaterial = Enum.valueOf(Material.class, PseudoSpawners.plugin.getConfig().getString("NextPageItem").toUpperCase());
		} catch (IllegalArgumentException e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid item in config for NextPage!");
		}
		nextPageName = ChatColor.translateAlternateColorCodes('&', PseudoSpawners.plugin.getConfig().getString("NextPageName"));
		int nextPageLocation = PseudoSpawners.plugin.getConfig().getInt("NextPageLocation");
		if (nextPageLocation >= 1 && nextPageLocation <= 9 && nextPageLocation != lastPageLocation) {
			nextPageInt = nextPageLocation - 1;
		} else {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid location in config for NextPage!");
		}
		try {
			String c = PseudoSpawners.plugin.getConfig().getString("Color");
			if (c.length() == 1) {
				char ch = c.charAt(0);
				color = ChatColor.getByChar(ch);
			} else {
				color = ChatColor.valueOf(c.replace(" ", "_").toUpperCase());
			}
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid color in config for Color!");
		}
		try {
			Map<EntityType, String> nm = GetNMSName.getNameMap();
			Map<String, EntityType> nrm = GetNMSName.getNameMapReverse();
			Set<String> l = PseudoSpawners.plugin.getConfig().getConfigurationSection("Names").getKeys(false);
			for (String s : l) {
				String u = s.toUpperCase();
				try {
					EntityType et = EntityType.valueOf(u);
					String n = PseudoSpawners.plugin.getConfig().getString("Names." + s);
					nm.put(et, n);
					nrm.put(n, et);
				} catch (IllegalArgumentException e) {
					Message.sendConsoleMessage(ChatColor.RED + "Invalid configuration for mob type names! Unknown entity: " + u);
				}
			}
			names = nm;
			namesReverse = nrm;
		} catch (Exception e) {
			Message.sendConsoleMessage(ChatColor.RED + "Invalid configuration for mob type names!");
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

}