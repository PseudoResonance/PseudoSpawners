package io.github.pseudoresonance.pseudospawners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Message;
import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import net.md_5.bungee.api.ChatColor;

public class GUISetPage {
	
	public static void setPage(Player p, int page) {
		setPage(p, page, null);
	}
	
	public static void setPage(Player p, int page, Inventory inv) {
		List<EntityType> entities = ConfigOptions.spawnable;
		if (entities.size() >= 1) {
			boolean newInv = false;
			if (inv == null) {
				newInv = true;
				inv = Bukkit.createInventory(null, 54, ConfigOptions.interfaceName);
			} else {
				inv.clear();
			}
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
			if (newInv)
				p.openInventory(inv);
			else
				p.updateInventory();
		} else {
			PseudoSpawners.message.sendPluginError(p, Errors.CUSTOM, "There are no entities enabled on the server!");
		}
	}
	
	protected static ItemStack newEgg(EntityType et) {
		if (!et.isSpawnable())
			throw new IllegalArgumentException("Entity cannot be spawned!");
		Material m = null;
		try {
			m = Material.valueOf(et.getKey().getKey().toUpperCase() + "_SPAWN_EGG");
		} catch (IllegalArgumentException e) {
			m = Material.BARRIER;
		}
		ItemStack is = new ItemStack(m, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ConfigOptions.color + ConfigOptions.getName(et));
		is.setItemMeta(im);
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
