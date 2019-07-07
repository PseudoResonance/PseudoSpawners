package io.github.pseudoresonance.pseudospawners.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudospawners.Config;

public class SpawnerTC implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			for (EntityType et : Config.spawnable) {
				if (sender.hasPermission("pseudospawners.spawner." + et.toString().toLowerCase())) {
					possible.add(Config.getName(et).replace(" ", "_"));
				}
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
		}
		return null;
	}

}
