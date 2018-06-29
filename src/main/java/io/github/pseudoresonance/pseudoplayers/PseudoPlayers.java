package io.github.pseudoresonance.pseudoplayers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.Column;
import io.github.pseudoresonance.pseudoapi.bukkit.playerdata.PlayerDataController;
import io.github.pseudoresonance.pseudoplayers.commands.ReloadSC;
import io.github.pseudoresonance.pseudoplayers.commands.ResetSC;
import io.github.pseudoresonance.pseudoplayers.commands.PlayerSC;
import io.github.pseudoresonance.pseudoplayers.completers.PlayerTC;
import io.github.pseudoresonance.pseudoplayers.completers.PseudoPlayersTC;
import io.github.pseudoresonance.pseudoplayers.listeners.PlayerJoinLeaveL;

public class PseudoPlayers extends PseudoPlugin {

	public static PseudoPlugin plugin;
	public static Message message;

	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;

	private static PlayerSC playerSubCommand;
	private static PlayerTC playerTabCompleter;

	private static ConfigOptions configOptions;

	public static Object economy = null;
	
	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}

	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		PlayerDataController.addColumn(new Column("logoutLocation", "VARCHAR(81)", "NULL"));
		configOptions = new ConfigOptions();
		ConfigOptions.updateConfig();
		playerSubCommand = new PlayerSC();
		playerTabCompleter = new PlayerTC();
		configOptions.reloadConfig();
		message = new Message(this);
		initVault();
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		PseudoAPI.registerConfig(configOptions);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public static ConfigOptions getConfigOptions() {
		return PseudoPlayers.configOptions;
	}

	private void initVault() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			try {
				RegisteredServiceProvider<?> economyProvider = getServer().getServicesManager().getRegistration(Class.forName("net.milkbowl.vault.economy.Economy"));
				if (economyProvider != null)
					economy = economyProvider.getProvider();
				else
					message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "No Vault economy is loaded! Player balance will not be shown!");
			} catch (ClassNotFoundException e) {
				message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Vault is not loaded! Player balance will not be shown!");
			}
		} else
			message.sendPluginError(Bukkit.getConsoleSender(), Errors.CUSTOM, "Vault is not loaded! Player balance will not be shown!");
	}

	private void initializeCommands() {
		this.getCommand("pseudoplayers").setExecutor(mainCommand);
		this.getCommand("player").setExecutor(playerSubCommand);
	}
	
	public static void registerPCommand() {
		plugin.registerDynamicCommand("p", playerSubCommand, playerTabCompleter);
	}
	
	public static void unregisterPCommand() {
		plugin.unregisterDynamicCommand("p");
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("player", new PlayerSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoplayers").setTabCompleter(new PseudoPlayersTC());
		this.getCommand("player").setTabCompleter(playerTabCompleter);
	}

	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new PlayerJoinLeaveL(), this);
	}

	private void setCommandDescriptions() {
		commandDescriptions.add(new CommandDescription("pseudoplayers", "Shows PseudoPlayers information", ""));
		commandDescriptions.add(new CommandDescription("pseudoplayers help", "Shows PseudoPlayers commands", ""));
		commandDescriptions.add(new CommandDescription("pseudoplayers reload", "Reloads PseudoPlayers config", "pseudoplayers.reload"));
		commandDescriptions.add(new CommandDescription("pseudoplayers reset", "Resets PseudoPlayers config", "pseudoplayers.reset"));
		commandDescriptions.add(new CommandDescription("player", "Shows information on a player", "pseudoplayers.view"));
	}

}