package io.github.pseudoresonance.pseudospawners.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudospawners.ConfigOptions;
import io.github.pseudoresonance.pseudospawners.GUISetPage;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;

public class InventoryClickEH implements Listener {
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent e) {
		try {
			Player p = (Player) e.getViewers().get(0);
			CraftingInventory inv = e.getInventory();
			ItemStack[] matrix = inv.getMatrix();
			if (matrix.length == 9) {
				if (matrix[0] != null && matrix[1] != null && matrix[2] != null && matrix[3] != null && matrix[4] != null && matrix[5] != null && matrix[6] != null && matrix[7] != null && matrix[8] != null) {
					if (matrix[0].getType() == Material.IRON_BARS && matrix[1].getType() == Material.IRON_BARS && matrix[2].getType() == Material.IRON_BARS && matrix[3].getType() == Material.IRON_BARS && isEgg(matrix[4].getType()) && matrix[5].getType() == Material.IRON_BARS && matrix[6].getType() == Material.IRON_BARS && matrix[7].getType() == Material.IRON_BARS && matrix[8].getType() == Material.IRON_BARS) {
						Material m = matrix[4].getType();
						EntityType et = EntityType.valueOf(m.name().replace("_SPAWN_EGG", ""));
						if (p.hasPermission("pseudospawners.craft")) {
							try {
								if (p.hasPermission("pseudospawners.craft." + et.toString().toLowerCase()) || p.hasPermission("pseudospawners.override")) {
									ItemStack spawner = newSpawner(ConfigOptions.color + ConfigOptions.getName(et) + " Spawner");
									inv.setResult(spawner);
									p.updateInventory();
									return;
								}
							} catch (NullPointerException ex) {}
							inv.setResult(null);
							p.updateInventory();
						}
					}
				}
			}
		} catch (IndexOutOfBoundsException ex) {}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory i = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();
		if (i instanceof AnvilInventory) {
			if (e.getCurrentItem().getType() == Material.SPAWNER) {
				e.setCancelled(true);
				PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "You can't put a spawner in an anvil!");
			}
		} else {
			if (i != null) {
				String t = e.getView().getTitle();
				if (t.equalsIgnoreCase(ConfigOptions.interfaceName)) {
					ItemStack is = e.getCurrentItem();
					if (is != null) {
						ItemMeta im = is.getItemMeta();
						if (im != null) {
							if (im.hasDisplayName()) {
								String name = im.getDisplayName();
								int page = PseudoSpawners.getPage(p.getName());
								if (name.startsWith("§1§f")) {
									int o = page + 1;
									GUISetPage.setPage(p, o, i);
									e.setCancelled(true);
								} else if (name.startsWith("§2§f")) {
									int o = page - 1;
									GUISetPage.setPage(p, o, i);
									e.setCancelled(true);
								} else if (isEgg(is)) {
									String entityName = ChatColor.stripColor(name);
									EntityType entity = ConfigOptions.getEntity(entityName);
									for (EntityType et : ConfigOptions.spawnable) {
										if (et == entity) {
											if (p.hasPermission("pseudospawners.spawner")) {
												if (p.getInventory().getItemInMainHand().getType() == Material.SPAWNER) {
													ItemStack item = p.getInventory().getItemInMainHand();
													ItemMeta meta = item.getItemMeta();
													meta.setDisplayName(ConfigOptions.color + ConfigOptions.getName(entity) + " Spawner");
													item.setItemMeta(meta);
													p.getInventory().setItemInMainHand(item);
												} else if (p.getInventory().getItemInOffHand().getType() == Material.SPAWNER) {
													ItemStack item = p.getInventory().getItemInOffHand();
													ItemMeta meta = item.getItemMeta();
													meta.setDisplayName(ConfigOptions.color + ConfigOptions.getName(entity) + " Spawner");
													item.setItemMeta(meta);
													p.getInventory().setItemInOffHand(item);
												} else {
													Set<Material> set = new HashSet<Material>();
													set = null;
													Block b = p.getTargetBlock(set, 5);
													if (b.getType() == Material.SPAWNER) {
														CreatureSpawner s = (CreatureSpawner) b.getState();
														s.setSpawnedType(entity);
														s.update();
													} else {
														if (p.getGameMode() == GameMode.CREATIVE || p.hasPermission("pseudospawners.spawn")) {
															HashMap<Integer, ItemStack> drop = p.getInventory().addItem(newSpawner(ConfigOptions.color + ConfigOptions.getName(entity) + " Spawner"));
															if (drop.containsKey(0)) {
																p.getWorld().dropItem(p.getLocation(), drop.get(0));
															}
														} else {
															PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "You are not holding or looking at a spawner!");
														}
													}
												}
												p.closeInventory();
											} else {
												PseudoSpawners.message.sendPluginError(p, Errors.NO_PERMISSION, "set a spawner type without an egg!");
											}
											e.setCancelled(true);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean isEgg(ItemStack is) {
		Material m = is.getType();
		if (m.isItem() && m.name().endsWith("_SPAWN_EGG") || m == Material.BARRIER) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEgg(Material m) {
		if (m.isItem() && m.name().endsWith("_SPAWN_EGG") || m == Material.BARRIER) {
			return true;
		} else {
			return false;
		}
	}
	
	private static ItemStack newSpawner(String name) {
		ItemStack is = new ItemStack(Material.SPAWNER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

}
