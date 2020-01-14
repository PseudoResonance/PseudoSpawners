package io.github.pseudoresonance.pseudospawners.events;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudospawners.Config;
import io.github.pseudoresonance.pseudospawners.SpawnerSettings;

public class BlockBreakEH implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
		Player p = e.getPlayer();
		if (e.getBlock().getType() == Material.SPAWNER) {
			if (is != null) {
				Block b = e.getBlock();
				boolean silk = false;
				if (is.getType().toString().contains("PICKAXE")) {
					if (is.containsEnchantment(Enchantment.SILK_TOUCH) || p.hasPermission("pseudospawners.collect.nosilk")) {
						silk = true;
					}
					if (silk && p.getGameMode() != GameMode.CREATIVE) {
						CreatureSpawner cs = (CreatureSpawner) b.getState();
						EntityType entity = cs.getSpawnedType();
						if (p.hasPermission("pseudospawners.override")) {
							ArrayList<String> lore = SpawnerSettings.getSettings(b);
							String name = Config.getName(entity);
							String full = Config.color + name + " Spawner";
							ItemStack spawner = newSpawner(full, lore);
							b.getWorld().dropItem(b.getLocation(), spawner);
							e.setExpToDrop(0);
						} else {
							if (Config.spawnable.contains(entity)) {
								if (p.hasPermission("pseudospawners.spawner." + entity.toString().toLowerCase())) {
									ArrayList<String> lore = SpawnerSettings.getSettings(b);
									String name = Config.getName(entity);
									String full = Config.color + name + " Spawner";
									ItemStack spawner = newSpawner(full, lore);
									b.getWorld().dropItem(b.getLocation(), spawner);
									e.setExpToDrop(0);
								}
							}
						}
					}
				}
			}
		}
	}

	private static ItemStack newSpawner(String name, ArrayList<String> lore) {
		ItemStack is = new ItemStack(Material.SPAWNER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

}
