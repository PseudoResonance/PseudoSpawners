package io.github.pseudoresonance.pseudospawners.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.Config;
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
									PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_unknown_setting", setting));
									error = true;
								}
							} catch (NumberFormatException e) {
								PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.NOT_A_NUMBER, value);
								error = true;
							}
						} else {
							PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_unknown_setting", setting));
							error = true;
						}
						if (!error) {
							String msg = SpawnerSettings.setSettings(b, meta);
							if (msg.equals(""))
								PseudoSpawners.plugin.getChat().sendPluginMessage(p, LanguageManager.getLanguage(p).getMessage("pseudospawners.set_setting", ChatColor.RED + setting + Config.textColor, ChatColor.RED + value + Config.textColor));
							else
								PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, msg);
						}
					} else if (args.length == 1) {
						String setting = args[0];
						PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_add_value", setting));
					} else {
						PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_add_setting_value"));
					}
				} else {
					PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_not_looking"));
				}
			}
		} else {
			PseudoSpawners.plugin.getChat().sendPluginError(sender, Errors.CUSTOM, LanguageManager.getLanguage(sender).getMessage("pseudospawners.error_players_only"));
			return true;
		}
		return true;
	}

}
