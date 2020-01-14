package io.github.pseudoresonance.pseudospawners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class SpawnerSettings {

	public static ArrayList<String> getSettings(Block b) {
		ArrayList<String> list = new ArrayList<String>();
		if (b.getType() == Material.SPAWNER) {
			BlockState bs = b.getState();
			int maxNearbyEntities = 6;
			int requiredPlayerRange = 16;
			int spawnCount = 4;
			int minSpawnDelay = 200;
			int maxSpawnDelay = 800;
			int spawnRange = 4;
			try {
				Class<?> ccs = Class.forName("org.bukkit.craftbukkit." + Utils.getBukkitVersion() + ".block.CraftCreatureSpawner");
				Object data = ccs.cast(bs);
				Method mMaxNearbyEntities = ccs.getMethod("getMaxNearbyEntities");
				Object mne = mMaxNearbyEntities.invoke(data);
				if (mne != null) {
					maxNearbyEntities = (int) mne;
				}
				Method mRequiredPlayerRange = ccs.getMethod("getRequiredPlayerRange");
				Object rpr = mRequiredPlayerRange.invoke(data);
				if (rpr != null) {
					requiredPlayerRange = (int) rpr;
				}
				Method mSpawnCount = ccs.getMethod("getSpawnCount");
				Object sc = mSpawnCount.invoke(data);
				if (sc != null) {
					spawnCount = (int) sc;
				}
				Method mMinSpawnDelay = ccs.getMethod("getMinSpawnDelay");
				Object misd = mMinSpawnDelay.invoke(data);
				if (misd != null) {
					minSpawnDelay = (int) misd;
				}
				Method mMaxSpawnDelay = ccs.getMethod("getMaxSpawnDelay");
				Object msd = mMaxSpawnDelay.invoke(data);
				if (msd != null) {
					maxSpawnDelay = (int) msd;
				}
				Method mSpawnRange = ccs.getMethod("getSpawnRange");
				Object sr = mSpawnRange.invoke(data);
				if (sr != null) {
					spawnRange = (int) sr;
				}
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			if (maxNearbyEntities != 6) {
				list.add(ChatColor.GRAY + "MaxNearbyEntities: " + maxNearbyEntities);
			}
			if (requiredPlayerRange != 16) {
				list.add(ChatColor.GRAY + "RequiredPlayerRange: " + requiredPlayerRange);
			}
			if (spawnCount != 4) {
				list.add(ChatColor.GRAY + "SpawnCount: " + spawnCount);
			}
			if (minSpawnDelay != 200) {
				list.add(ChatColor.GRAY + "MinSpawnDelay: " + minSpawnDelay);
			}
			if (maxSpawnDelay != 800) {
				list.add(ChatColor.GRAY + "MaxSpawnDelay: " + maxSpawnDelay);
			}
			if (spawnRange != 4) {
				list.add(ChatColor.GRAY + "SpawnRange: " + spawnRange);
			}
		}
		return list;
	}

	public static String setSettings(Block b, HashMap<String, String> metadata) {
		if (b.getType() == Material.SPAWNER) {
			BlockState bs = b.getState();
			try {
				Class<?> ccs = Class.forName("org.bukkit.craftbukkit." + Utils.getBukkitVersion() + ".block.CraftCreatureSpawner");
				Object data = ccs.cast(bs);
				short maxNearbyEntities = -1;
				short requiredPlayerRange = -1;
				short spawnCount = -1;
				short minSpawnDelay = -1;
				short maxSpawnDelay = -1;
				short spawnRange = -1;
				for (String name : metadata.keySet()) {
					if (name.equals("MaxNearbyEntities")) {
						maxNearbyEntities = Short.valueOf(metadata.get(name));
					} else if (name.equals("RequiredPlayerRange")) {
						requiredPlayerRange = Short.valueOf(metadata.get(name));
					} else if (name.equals("SpawnCount")) {
						spawnCount = Short.valueOf(metadata.get(name));
					} else if (name.equals("MinSpawnDelay")) {
						minSpawnDelay = Short.valueOf(metadata.get(name));
					} else if (name.equals("MaxSpawnDelay")) {
						maxSpawnDelay = Short.valueOf(metadata.get(name));
					} else if (name.equals("SpawnRange")) {
						spawnRange = Short.valueOf(metadata.get(name));
					}
				}
				if (maxNearbyEntities != -1) {
					Method mMaxNearbyEntities = ccs.getMethod("setMaxNearbyEntities", int.class);
					try {
						mMaxNearbyEntities.invoke(data, maxNearbyEntities);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "MaxNearbyEntities", maxNearbyEntities));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
				if (requiredPlayerRange != -1) {
					Method mRequiredPlayerRange = ccs.getMethod("setRequiredPlayerRange", int.class);
					try {
						mRequiredPlayerRange.invoke(data, requiredPlayerRange);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "RequiredPlayerRange", requiredPlayerRange));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
				if (spawnCount != -1) {
					Method mSpawnCount = ccs.getMethod("setSpawnCount", int.class);
					try {
						mSpawnCount.invoke(data, spawnCount);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "SpawnCount", spawnCount));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
				if (minSpawnDelay != -1) {
					Method mMinSpawnDelay = ccs.getMethod("setMinSpawnDelay", int.class);
					try {
						mMinSpawnDelay.invoke(data, minSpawnDelay);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "MinSpawnDelay", minSpawnDelay));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
				if (maxSpawnDelay != -1) {
					Method mMaxSpawnDelay = ccs.getMethod("setMaxSpawnDelay", int.class);
					try {
						mMaxSpawnDelay.invoke(data, maxSpawnDelay);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "MaxSpawnDelay", maxSpawnDelay));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
				if (spawnRange != -1) {
					Method mSpawnRange = ccs.getMethod("setSpawnRange", int.class);
					try {
						mSpawnRange.invoke(data, spawnRange);
					} catch (NumberFormatException e) {
						PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.error_spawner_data", "SpawnRange", spawnRange));
					} catch (InvocationTargetException e) {
						return e.getCause().getMessage();
					}
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
				return "Error setting spawner data! Please contact an administrator!";
			}
			bs.update(true);
			return "";
		}
		return "Error setting spawner data! Please contact an administrator!";
	}

}
