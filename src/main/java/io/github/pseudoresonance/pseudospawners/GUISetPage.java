package io.github.pseudoresonance.pseudospawners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import net.md_5.bungee.api.ChatColor;

public class GUISetPage {
	
	public static void setPage(Player p, int page) {
		List<EntityType> entities = ConfigOptions.spawnable;
		if (entities.size() >= 1) {
			Inventory inv = null;
			if (p.getOpenInventory().getTopInventory().getTitle().contains(ConfigOptions.interfaceName)) {
				inv = p.getOpenInventory().getTopInventory();
				inv.clear();
			} else
				inv = Bukkit.createInventory(null, 54, ConfigOptions.interfaceName);
			int total = (int) Math.ceil((double) entities.size() / 45);
			if (page > total) {
				Message.sendConsoleMessage(ChatColor.RED + "Programming Error! That page number is too high!");
				return;
			} else if (page <= 0) {
				Message.sendConsoleMessage(ChatColor.RED + "Programming Error! Negative page number!");
				return;
			} else if (page == 1) {
				PseudoSpawners.setPage(p.getName(), 1);
				if (entities.size() >= 45) {
					inv.setItem(ConfigOptions.nextPageInt, newStack(ConfigOptions.nextPageMaterial, 1, "§1§f" + ConfigOptions.nextPageName.replace("{page}", "2")));
				}
				for (int i = 0; i <= 44; i++) {
					if (i < entities.size()) {
						EntityType et = entities.get(i);
						inv.setItem(i + 9, newEgg(et));
					}
				}
			} else if (page == total) {
				PseudoSpawners.setPage(p.getName(), total);
				if (entities.size() >= 45) {
					inv.setItem(ConfigOptions.lastPageInt, newStack(ConfigOptions.lastPageMaterial, 1, "§2§f" + ConfigOptions.lastPageName.replace("{page}", Integer.toString(page - 1))));
				}
				int loc = 8;
				for (int i = (page - 1) * 45; i <= ((page - 1) * 45) + 44; i++) {
					loc++;
					if (i < entities.size()) {
						EntityType et = entities.get(i);
						inv.setItem(loc, newEgg(et));
					}
				}
			} else {
				PseudoSpawners.setPage(p.getName(), page);
				if (entities.size() >= 45) {
					inv.setItem(ConfigOptions.lastPageInt, newStack(ConfigOptions.lastPageMaterial, 1, "§1§f" + ConfigOptions.lastPageName.replace("{page}", Integer.toString(page - 1))));
					inv.setItem(ConfigOptions.nextPageInt, newStack(ConfigOptions.nextPageMaterial, 1, "§2§f" + ConfigOptions.nextPageName.replace("{page}", Integer.toString(page + 1))));
				}
				int loc = 8;
				for (int i = (page - 1) * 45; i <= ((page - 1) * 45) + 44; i++) {
					loc++;
					if (i < entities.size()) {
						EntityType et = entities.get(i);
						inv.setItem(loc, newEgg(et));
					}
				}
			}
			if (p.getOpenInventory().getTopInventory().getName().equals(ConfigOptions.interfaceName))
				p.updateInventory();
			else
				p.openInventory(inv);
		} else {
			PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "There are no entities enabled on the server!");
		}
	}
	
	protected static ItemStack newEgg(EntityType et) {
		ItemStack is = new ItemStack(Material.MONSTER_EGG, 1);
		ItemMeta im = is.getItemMeta();
		SpawnEggMeta sem = (SpawnEggMeta) im;
		try {
			sem.setSpawnedType(et);
		} catch (IllegalArgumentException e) {
			sem.setSpawnedType(null);
		}
		im.setDisplayName(ConfigOptions.color + ConfigOptions.getName(et));
		is.setItemMeta(sem);
		return is;
	}
	
	protected static ItemStack newStack(Material material, int quantity, String name) {
		ItemStack is = new ItemStack(material, quantity);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

}
