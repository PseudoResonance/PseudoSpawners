package io.github.pseudoresonance.pseudospawners.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Config;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;
import io.github.pseudoresonance.pseudospawners.SpawnerSettings;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import net.md_5.bungee.api.ChatColor;

public class EditSpawnerSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (sender.hasPermission("pseudospawners.edit")) {
				Set<Material> set = new HashSet<Material>();
				set = null;
				Block b = p.getTargetBlock(set, 5);
				if (b.getType() == Material.SPAWNER) {
					HashMap<String, String> meta = new HashMap<String, String>();
					boolean error = false;
					if (args.length >= 2) {
						String setting = args[0];
						String value = args[1];
						if (setting.equalsIgnoreCase("MaxNearbyEntities") || setting.equalsIgnoreCase("RequiredPlayerRange") || setting.equalsIgnoreCase("SpawnCount") || setting.equalsIgnoreCase("MinSpawnDelay") || setting.equalsIgnoreCase("MaxSpawnDelay") || setting.equalsIgnoreCase("SpawnRange")) {
							try {
								short s = Short.valueOf(value);
								if (setting.equalsIgnoreCase("MaxNearbyEntities")) {
									meta.put("MaxNearbyEntities", Short.toString(s));
								} else if (setting.equalsIgnoreCase("RequiredPlayerRange")) {
									meta.put("RequiredPlayerRange", Short.toString(s));
								} else if (setting.equalsIgnoreCase("SpawnCount")) {
									meta.put("SpawnCount", Short.toString(s));
								} else if (setting.equalsIgnoreCase("MinSpawnDelay")) {
									meta.put("MinSpawnDelay", Short.toString(s));
								} else if (setting.equalsIgnoreCase("MaxSpawnDelay")) {
									meta.put("MaxSpawnDelay", Short.toString(s));
								} else if (setting.equalsIgnoreCase("SpawnRange")) {
									meta.put("SpawnRange", Short.toString(s));
								} else {
									PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Error parsing data! Unknown setting: " + setting + "!");
									error = true;
								}
							} catch (NumberFormatException e) {
								PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Error parsing data! Value for setting: " + setting + " must be an integer!");
								error = true;
							}
						} else {
							PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Error parsing data! Unknown setting: " + setting + "!");
							error = true;
						}
						if (!error) {
							String msg = SpawnerSettings.setSettings(b, meta);
							if (msg.equals(""))
								PseudoSpawners.message.sendPluginMessage(p, "Set " + ChatColor.RED + setting + Config.textColor + " to " + ChatColor.RED + value + Config.textColor + "!");
							else
								PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, msg);
						}
					} else if (args.length == 1) {
						String setting = args[0];
						PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Error parsing data! Please add a value for setting: " + setting + "!");
					} else {
						PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Error parsing data! Please add a a setting and value!");
					}
				} else {
					PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "You are not looking at a spawner!");
				}
			}
		} else {
			PseudoSpawners.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return true;
		}
		return true;
	}

}
