package io.github.pseudoresonance.pseudospawners.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudospawners.Config;
import io.github.pseudoresonance.pseudospawners.GUISetPage;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;

public class SpawnerSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (sender.hasPermission("pseudospawners.spawner")) {
				List<EntityType> ent = Config.spawnable;
				List<EntityType> entities = new ArrayList<EntityType>();
				for (EntityType enti : ent) {
					if (p.hasPermission("pseudospawners.spawner." + enti.toString())) {
						entities.add(enti);
					}
				}
				if (entities.size() >= 1) {
					if (args.length == 0) {
						PseudoSpawners.setPage(p.getName(), 1);
						GUISetPage.setPage(p, 1);
					} else {
						String build = "";
						for (int i = 0; i < args.length; i++) {
							if (i == 0) {
								build = args[i];
							} else {
								build = build + " " + args[i];
							}
						}
						EntityType entity = Config.getEntity(build.toUpperCase());
						if (entity == null) {
							try {
								entity = EntityType.valueOf(build.toUpperCase());
							} catch (IllegalArgumentException e) {
								PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "That is an invalid entity type!");
								return true;
							}
						}
						if (!entity.isSpawnable()) {
							PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "That is an invalid entity type!");
							return true;
						}
						if (p.hasPermission("pseudospawners.override")) {
							if (p.getInventory().getItemInMainHand().getType() == Material.SPAWNER) {
								ItemStack item = p.getInventory().getItemInMainHand();
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(Config.color + Config.getName(entity) + " Spawner");
								item.setItemMeta(meta);
								p.getInventory().setItemInMainHand(item);
							} else if (p.getInventory().getItemInOffHand().getType() == Material.SPAWNER) {
								ItemStack item = p.getInventory().getItemInOffHand();
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(Config.color + Config.getName(entity) + " Spawner");
								item.setItemMeta(meta);
								p.getInventory().setItemInOffHand(item);
							} else {
								try {
									Set<Material> set = new HashSet<Material>();
									set = null;
									Block b = p.getTargetBlock(set, 5);
									if (b.getType() == Material.SPAWNER) {
										CreatureSpawner s = (CreatureSpawner) b.getState();
										s.setSpawnedType(entity);
										s.update();
									} else {
										if (p.getGameMode() == GameMode.CREATIVE || p.hasPermission("pseudospawners.spawn")) {
											HashMap<Integer, ItemStack> drop = p.getInventory().addItem(newSpawner(Config.color + Config.getName(entity) + " Spawner"));
											if (drop.containsKey(0)) {
												p.getWorld().dropItem(p.getLocation(), drop.get(0));
											}
										} else {
											PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "You are not holding or looking at a spawner!");
										}
									}
								} catch (Exception e) {
									PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Minecraft disallows that entity type!");
								}
							}
							p.closeInventory();
							return true;
						} else {
							for (EntityType et : Config.spawnable) {
								if (et == entity) {
									if (p.getInventory().getItemInMainHand().getType() == Material.SPAWNER) {
										ItemStack item = p.getInventory().getItemInMainHand();
										ItemMeta meta = item.getItemMeta();
										meta.setDisplayName(Config.color + Config.getName(entity) + " Spawner");
										item.setItemMeta(meta);
										p.getInventory().setItemInMainHand(item);
									} else if (p.getInventory().getItemInOffHand().getType() == Material.SPAWNER) {
										ItemStack item = p.getInventory().getItemInOffHand();
										ItemMeta meta = item.getItemMeta();
										meta.setDisplayName(Config.color + Config.getName(entity) + " Spawner");
										item.setItemMeta(meta);
										p.getInventory().setItemInOffHand(item);
									} else {
										try {
											Set<Material> set = new HashSet<Material>();
											set = null;
											Block b = p.getTargetBlock(set, 5);
											if (b.getType() == Material.SPAWNER) {
												CreatureSpawner s = (CreatureSpawner) b.getState();
												s.setSpawnedType(entity);
												s.update();
											} else {
												if (p.getGameMode() == GameMode.CREATIVE || p.hasPermission("pseudospawners.spawn")) {
													HashMap<Integer, ItemStack> drop = p.getInventory().addItem(newSpawner(Config.color + Config.getName(entity) + " Spawner"));
													if (drop.containsKey(0)) {
														p.getWorld().dropItem(p.getLocation(), drop.get(0));
													}
												} else {
													PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "You are not holding or looking at a spawner!");
												}
											}
										} catch (Exception e) {
											PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "Minecraft disallows that entity type!");
										}
									}
									p.closeInventory();
									return true;
								}
							}
						}
						PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "That entity type is disabled!");
					}
				} else {
					PseudoSpawners.message.sendPluginError(sender, Errors.CUSTOM, "There are no entities enabled on the server!");
				}
			} else {
				PseudoSpawners.message.sendPluginError(p, Errors.NO_PERMISSION, "set a spawner type without an egg!");
				return true;
			}
		} else {
			PseudoSpawners.message.sendPluginError(sender, Errors.CUSTOM, "This command is for players only!");
			return true;
		}
		return true;
	}
	
	private static ItemStack newSpawner(String name) {
		ItemStack is = new ItemStack(Material.SPAWNER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

}
