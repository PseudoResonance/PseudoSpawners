package io.github.pseudoresonance.pseudoplayers;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
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
	
	private static ConfigOptions configOptions;
	
	@Override
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		PlayerDataController.addColumn(new Column("logoutLocation", "VARCHAR(81)", "NULL"));
		configOptions = new ConfigOptions();
		ConfigOptions.updateConfig();
		message = new Message(this);
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		configOptions.reloadConfig();
		PseudoAPI.registerConfig(configOptions);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	public static ConfigOptions getConfigOptions() {
		return PseudoPlayers.configOptions;
	}

	private void initializeCommands() {
		this.getCommand("pseudoplayers").setExecutor(mainCommand);
		this.getCommand("player").setExecutor(new PlayerSC());
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("player", new PlayerSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoplayers").setTabCompleter(new PseudoPlayersTC());
		this.getCommand("player").setTabCompleter(new PlayerTC());
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