package io.github.pseudoresonance.pseudospawners.events;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudospawners.Config;
import io.github.pseudoresonance.pseudospawners.PseudoSpawners;

public class PlayerInteractEH implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = p.getTargetBlock(new HashSet<Material>(), 5);
			if (b.getType() == Material.SPAWNER) {
				ItemStack is = e.getItem();
				if (isEgg(is)) {
					Material m = is.getType();
					EntityType entity = EntityType.valueOf(m.name().replace("_SPAWN_EGG", ""));
					if (!entity.isSpawnable()) {
						PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_invalid_entity"));
						return;
					}
					if (p.hasPermission("pseudospawners.override")) {
						if (p.hasPermission("pseudospawners.modify")) {
							if (e.getHand() == EquipmentSlot.HAND) {
								p.getInventory().setItemInMainHand(null);
								CreatureSpawner s = (CreatureSpawner) b.getState();
								s.setSpawnedType(entity);
								s.update();
								e.setCancelled(true);
							} else if (e.getHand() == EquipmentSlot.OFF_HAND) {
								p.getInventory().setItemInOffHand(null);
								CreatureSpawner s = (CreatureSpawner) b.getState();
								s.setSpawnedType(entity);
								s.update();
								e.setCancelled(true);
							}
						} else {
							PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.NO_PERMISSION, LanguageManager.getLanguage(p).getMessage("pseudospawners.permission_set_type"));
						}
					} else {
						if (Config.spawnable.contains(entity)) {
							if (p.hasPermission("pseudospawners.spawner." + entity.toString().toLowerCase())) {
								if (p.hasPermission("pseudospawners.modify")) {
									if (e.getHand() == EquipmentSlot.HAND) {
										p.getInventory().setItemInMainHand(null);
										CreatureSpawner s = (CreatureSpawner) b.getState();
										s.setSpawnedType(entity);
										s.update();
										e.setCancelled(true);
									} else if (e.getHand() == EquipmentSlot.OFF_HAND) {
										p.getInventory().setItemInOffHand(null);
										CreatureSpawner s = (CreatureSpawner) b.getState();
										s.setSpawnedType(entity);
										s.update();
										e.setCancelled(true);
									}
								} else {
									PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.NO_PERMISSION, LanguageManager.getLanguage(p).getMessage("pseudospawners.permission_set_type"));
								}
							} else {
								PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.NO_PERMISSION, LanguageManager.getLanguage(p).getMessage("pseudospawners.permission_set_type_specific", Config.getName(entity)));
							}
						}
					}
				}
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

}
