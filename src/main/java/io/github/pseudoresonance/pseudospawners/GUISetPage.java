package io.github.pseudoresonance.pseudospawners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;

public class GUISetPage {

	public static void setPage(Player p, int page) {
		setPage(p, page, null);
	}

	public static void setPage(Player p, int page, Inventory inv) {
		List<EntityType> entities = Config.spawnable;
		if (entities.size() >= 1) {
			boolean newInv = false;
			if (inv == null) {
				newInv = true;
				inv = Bukkit.createInventory(new PseudoSpawnersHolder(), 54, LanguageManager.getLanguage(p).getMessage("pseudospawners.interface_name"));
			} else {
				inv.clear();
			}
			int total = (entities.size() - 1) / 45 + 1;
			if (page > total || page <= 0) {
				page = 1;
			}
			PseudoSpawners.setPage(p.getName(), page);
			if (total > 1) {
				if (page < total)
					inv.setItem(Config.nextPageLocation, newStack(Config.nextPageMaterial, 1, LanguageManager.getLanguage(p).getMessage("pseudospawners.interface_next_page_name", page + 1)));
				if (page > 1)
					inv.setItem(Config.lastPageLocation, newStack(Config.lastPageMaterial, 1, LanguageManager.getLanguage(p).getMessage("pseudospawners.interface_last_page_name", page - 1)));
			}
			int invIndex = 8;
			for (int i = (page - 1) * 45; i < page * 45; i++) {
				invIndex++;
				if (i < entities.size()) {
					EntityType et = entities.get(i);
					inv.setItem(invIndex, newEgg(et));
				} else
					break;
			}
			if (newInv)
				p.openInventory(inv);
			else
				p.updateInventory();
		} else {
			PseudoSpawners.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudospawners.error_no_entities"));
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
		im.setDisplayName(Config.color + Config.getName(et));
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
	
	public static class PseudoSpawnersHolder implements InventoryHolder {  
	    @Override
	    public Inventory getInventory() {
	        return null;
	    }
	}

}
