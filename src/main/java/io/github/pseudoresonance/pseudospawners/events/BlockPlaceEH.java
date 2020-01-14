package io.github.pseudoresonance.pseudospawners.events;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudospawners.Config;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;
import io.github.pseudoresonance.pseudospawners.SpawnerSettings;

public class BlockPlaceEH implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemStack is = e.getItemInHand();
		Player p = e.getPlayer();
		if (is.getType() == Material.SPAWNER) {
			Block b = e.getBlockPlaced();
			ItemMeta im = is.getItemMeta();
			if (im.hasDisplayName()) {
				String name = im.getDisplayName();
				name = name.replace(" Spawner", "");
				String entityName = ChatColor.stripColor(name);
				EntityType entity;
				try {
					entity = Config.getEntity(entityName);
				} catch (IllegalArgumentException ex) {
					PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_invalid_spawner"));
					e.setCancelled(true);
					return;
				}
				if (!entity.isSpawnable()) {
					PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_invalid_entity"));
					return;
				}
				if (p.hasPermission("pseudospawners.override")) {
					try {
						if (b.getType() == Material.SPAWNER) {
							CreatureSpawner s = (CreatureSpawner) b.getState();
							s.setSpawnedType(entity);
							s.update();
							if (im.hasLore()) {
								setData(p, b, im.getLore());
							}
							return;
						} else {
							PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_not_looking"));
							return;
						}
					} catch (Exception ex) {
						PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_minecraft_disallows"));
						return;
					}
				} else {
					if (Config.spawnable.contains(entity)) {
						try {
							if (b.getType() == Material.SPAWNER) {
								if (p.hasPermission("pseudospawners.spawner." + entity.toString().toLowerCase())) {
									CreatureSpawner s = (CreatureSpawner) b.getState();
									s.setSpawnedType(entity);
									s.update();
									if (im.hasLore()) {
										setData(p, b, im.getLore());
									}
								} else {
									PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.NO_PERMISSION, LanguageManager.getLanguage(p).getMessage("pseudospawners.permission_use_spawner", Config.getName(entity)));
									e.setCancelled(true);
								}
								return;
							} else {
								PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_not_looking"));
								return;
							}
						} catch (Exception ex) {
							PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_minecraft_disallows"));
							return;
						}
					}
				}
				PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_type_disabled"));
				e.setCancelled(true);
			}
		}
	}

	public boolean isEgg(ItemStack is) {
		Material m = is.getType();
		if (m.isItem() && m.name().endsWith("_SPAWN_EGG")) {
			return true;
		} else {
			return false;
		}
	}

	private void setData(Player p, Block b, List<String> lore) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String line : lore) {
			String strip = ChatColor.stripColor(line);
			String[] split = strip.split(": ");
			map.put(split[0], split[1]);
		}
		String msg = SpawnerSettings.setSettings(b, map);
		if (!msg.equals("")) {
			PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, msg);
		}
	}

}
