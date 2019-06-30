package io.github.pseudoresonance.pseudospawners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudospawners.commands.EditSpawnerSC;
import io.github.pseudoresonance.pseudospawners.commands.ReloadSC;
import io.github.pseudoresonance.pseudospawners.commands.ResetSC;
import io.github.pseudoresonance.pseudospawners.commands.SpawnerSC;
import io.github.pseudoresonance.pseudospawners.completers.EditSpawnerTC;
import io.github.pseudoresonance.pseudospawners.completers.PseudoSpawnersTC;
import io.github.pseudoresonance.pseudospawners.completers.SpawnerTC;
import io.github.pseudoresonance.pseudospawners.events.BlockBreakEH;
import io.github.pseudoresonance.pseudospawners.events.BlockPlaceEH;
import io.github.pseudoresonance.pseudospawners.events.InventoryClickEH;
import io.github.pseudoresonance.pseudospawners.events.PlayerInteractEH;

public class PseudoSpawners extends PseudoPlugin {

	public static PseudoPlugin plugin;
	public static Message message;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	
	private static ConfigOptions configOptions;

	private static String bukkitVersion;
	
	private static Map<String, Integer> page = new HashMap<String, Integer>();
	
	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}
	
	public void onEnable() {
		super.onEnable();
		bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		this.saveDefaultConfig();
		plugin = this;
		GetNMSName.getNames();
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
		createRecipes();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	public static ConfigOptions getConfigOptions() {
		return PseudoSpawners.configOptions;
	}

	private void initializeCommands() {
		this.getCommand("pseudospawners").setExecutor(mainCommand);
		this.getCommand("spawner").setExecutor(new SpawnerSC());
		this.getCommand("editspawner").setExecutor(new EditSpawnerSC());
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("spawner", new SpawnerSC());
		subCommands.put("editspawner", new EditSpawnerSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudospawners").setTabCompleter(new PseudoSpawnersTC());
		this.getCommand("spawner").setTabCompleter(new SpawnerTC());
		this.getCommand("editspawner").setTabCompleter(new EditSpawnerTC());
	}
	
	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new InventoryClickEH(), this);
		getServer().getPluginManager().registerEvents(new BlockPlaceEH(), this);
		getServer().getPluginManager().registerEvents(new BlockBreakEH(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractEH(), this);
	}

	private void setCommandDescriptions() {
		commandDescriptions.add(new CommandDescription("pseudospawners", "Shows PseudoSpawners information", ""));
		commandDescriptions.add(new CommandDescription("pseudospawners help", "Shows PseudoSpawners commands", ""));
		commandDescriptions.add(new CommandDescription("pseudospawners reload", "Reloads PseudoSpawners config", "pseudospawners.reload"));
		commandDescriptions.add(new CommandDescription("pseudospawners reset", "Resets PseudoSpawners config", "pseudospawners.reset"));
		commandDescriptions.add(new CommandDescription("pseudospawners spawner", "Sets the type of the spawner you are looking at", "pseudospawners.spawner"));
		commandDescriptions.add(new CommandDescription("pseudospawners editspawner", "Edits the data of the spawner you are looking at", "pseudospawners.edit"));
	}
	
	public static Map<String, Integer> getPages() {
		return page;
	}
	
	public static int getPage(String p) {
		return page.get(p);
	}
	
	public static void removePage(String p) {
		page.remove(p);
	}
	
	public static void setPage(String p, int i) {
		page.put(p, i);
	}
	
	protected static ItemStack newSpawner(String name) {
		ItemStack is = new ItemStack(Material.SPAWNER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

	private static void createRecipes() {
		for (Material m : Material.values()) {
			if (m.isItem() && m.name().endsWith("_SPAWN_EGG")) {
				ShapedRecipe rec;
				String mob = m.name().substring(0, m.name().length() - 10);
				ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
				NamespacedKey key = new NamespacedKey(plugin, "spawner_" + mob);
				rec = new ShapedRecipe(key, spawner);
				rec.shape("***", "*%*", "***");
				rec.setIngredient('*', Material.IRON_BARS);
				rec.setIngredient('%', m);
				Bukkit.getServer().addRecipe(rec);
			}
		}
	}
	
	public static String getBukkitVersion() {
		return bukkitVersion;
	}

}