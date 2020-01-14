package io.github.pseudoresonance.pseudospawners.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;

public class ResetSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudospawners.reset")) {
			try {
				File conf = new File(PseudoSpawners.plugin.getDataFolder(), "config.yml");
				conf.delete();
				PseudoSpawners.plugin.saveDefaultConfig();
				PseudoSpawners.plugin.reloadConfig();
			} catch (Exception e) {
				PseudoSpawners.plugin.getChat().sendPluginError(sender, Chat.Errors.GENERIC);
				return false;
			}
			PseudoSpawners.getConfigOptions().reloadConfig();
			PseudoSpawners.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.config_reset"));
			return true;
		} else {
			PseudoSpawners.plugin.getChat().sendPluginError(sender, Chat.Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_config"));
			return false;
		}
	}

}
