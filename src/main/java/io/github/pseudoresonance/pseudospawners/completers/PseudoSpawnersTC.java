package io.github.pseudoresonance.pseudospawners.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudospawners.Config;

public class PseudoSpawnersTC implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			possible.add("help");
			if (sender.hasPermission("pseudospawners.reload")) {
				possible.add("reload");
			}
			if (sender.hasPermission("pseudospawners.reset")) {
				possible.add("reset");
			}
			if (sender.hasPermission("pseudospawners.reloadlocalization")) {
				possible.add("reloadlocalization");
			}
			if (sender.hasPermission("pseudospawners.resetlocalization")) {
				possible.add("resetlocalization");
			}
			if (sender.hasPermission("pseudospawners.spawner")) {
				possible.add("spawner");
			}
			if (sender.hasPermission("pseudospawners.edit")) {
				possible.add("editspawner");
			}
			if (args[0].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[0].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spawner") && sender.hasPermission("pseudospawners.spawner")) {
				for (EntityType et : Config.spawnable) {
					if (sender.hasPermission("pseudospawners.spawner." + et.toString().toLowerCase())) {
						possible.add(Config.getName(et));
					}
				}
				if (args[1].equalsIgnoreCase("")) {
					return possible;
				} else {
					List<String> checked = new ArrayList<String>();
					for (String check : possible) {
						if (check.toLowerCase().startsWith(args[1].toLowerCase())) {
							checked.add(check);
						}
					}
					return checked;
				}
			} else if (args[0].equalsIgnoreCase("editspawner") && sender.hasPermission("pseudospawners.edit")) {
				possible.add("MaxNearbyEntities");
				possible.add("RequiredPlayerRange");
				possible.add("SpawnCount");
				possible.add("SpawnData");
				possible.add("MaxSpawnDelay");
				possible.add("SpawnRange");
				possible.add("MinSpawnDelay");
				possible.add("SpawnPotentials");
				if (args[1].equalsIgnoreCase("")) {
					return possible;
				} else {
					List<String> checked = new ArrayList<String>();
					for (String check : possible) {
						if (check.toLowerCase().startsWith(args[1].toLowerCase())) {
							checked.add(check);
						}
					}
					return checked;
				}
			}
		}
		return null;
	}

}
