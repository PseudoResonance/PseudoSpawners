package io.github.pseudoresonance.pseudospawners;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.Utils;

public class GetNMSName {

	private static HashMap<Integer, String> nameMap = new HashMap<Integer, String>();

	public static HashMap<EntityType, String> names = new HashMap<EntityType, String>();
	public static HashMap<String, EntityType> namesReverse = new HashMap<String, EntityType>();

	public static void getNames() {
		int minecraftVersion = 0;
		String bukkitPackageName = "";
		String bukkitVersion = "";
		try {
			bukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
			bukkitVersion = bukkitPackageName.substring(bukkitPackageName.lastIndexOf(".") + 1);
			minecraftVersion = Integer.valueOf(bukkitVersion.split("_")[1]);
			PseudoSpawners.plugin.getChat().sendConsolePluginMessage(LanguageManager.getLanguage().getMessage("pseudospawners.loading_minecraft_version", "1." + minecraftVersion));
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			PseudoSpawners.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudospawners.error_getting_minecraft_version", bukkitPackageName));
			minecraftVersion = 17;
		}
		try {
			Class<?> localeClass = null;
			Class<?> entityTypes = null;
			Field idField = null;
			if (minecraftVersion >= 17) {
				localeClass = Class.forName("net.minecraft.locale.LocaleLanguage");
				entityTypes = Class.forName("net.minecraft.world.entity.EntityTypes");
			} else {
				localeClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".LocaleLanguage");
				entityTypes = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".EntityTypes");
			}
			try {
				idField = entityTypes.getDeclaredField("id");
				idField.setAccessible(true);
			} catch (NoSuchFieldException i) {}
			Field localeFields = null;
			for (Field f : localeClass.getDeclaredFields()) {
				if (f.getType().equals(localeClass)) {
					localeFields = f;
					break;
				}
			}
			if (localeFields != null) {
				localeFields.setAccessible(true);
				Object locale = localeFields.get(null);
				if (Integer.valueOf(Utils.getBukkitVersion().split("_")[1]) >= 13) {
					Field[] entityListField = entityTypes.getFields();
					ArrayList<Field> mobFields = new ArrayList<Field>();
					for (Field f : entityListField) {
						if (f.getType().equals(entityTypes)) {
							mobFields.add(f);
						}
					}
					Method trans = Arrays.stream(locale.getClass().getMethods()).filter(m -> m.getReturnType().equals(String.class)).filter(m -> m.getParameterCount() == 1).filter(m -> m.getParameters()[0].getType().equals(String.class)).collect(Collectors.toList()).get(0);
					trans.setAccessible(true);
					for (Field f : mobFields) {
						String minecraftName = idField != null ? (String) idField.get(f.get(null)) : f.getName();
						if (minecraftName == null)
							continue;
						for (EntityType et : EntityType.values()) {
							try {
								String bukkitName = et.getKey().getKey();
								if (minecraftName.equalsIgnoreCase(bukkitName)) {
									String friendlyName = (String) trans.invoke(locale, "entity.minecraft." + minecraftName.toLowerCase());
									if (!friendlyName.startsWith("entity.minecraft.")) {
										names.put(et, friendlyName);
									}
									break;
								}
							} catch (IllegalArgumentException ignore) {
							} // Filter out invalid entities
						}
					}
					updateNames();
				} else if (Integer.valueOf(Utils.getBukkitVersion().split("_")[1]) >= 11) {
					Field nameListField = entityTypes.getDeclaredField("g");
					nameListField.setAccessible(true);
					Object nameList = nameListField.get(null);
					if (nameList instanceof List) {
						@SuppressWarnings("unchecked")
						List<Object> nameIterate = (List<Object>) nameList;
						Method trans = Arrays.stream(locale.getClass().getMethods()).filter(m -> m.getReturnType().equals(String.class)).filter(m -> m.getParameterCount() == 1).filter(m -> m.getParameters()[0].getType().equals(String.class)).collect(Collectors.toList()).get(0);
						for (int id = 0; id < nameIterate.size(); id++) {
							try {
								Object o = nameIterate.get(id);
								if (o instanceof String) {
									String name = (String) o;
									String friendlyName = (String) trans.invoke(locale, "entity." + name + ".name");
									if (!(friendlyName.startsWith("entity.") && friendlyName.endsWith(".name"))) {
										nameMap.put(id, friendlyName);
									}
								}
							} catch (IndexOutOfBoundsException e) {
							}
						}
					}
					updateNamesLegacy();
				} else {
					Field nameListField = entityTypes.getDeclaredField("g");
					nameListField.setAccessible(true);
					Object nameList = nameListField.get(null);
					if (nameList instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<Object, Object> nameIterate = (Map<Object, Object>) nameList;
						Method trans = Arrays.stream(locale.getClass().getMethods()).filter(m -> m.getReturnType().equals(String.class)).filter(m -> m.getParameterCount() == 1).filter(m -> m.getParameters()[0].getType().equals(String.class)).collect(Collectors.toList()).get(0);
						for (Object o : nameIterate.keySet()) {
							Object oid = nameIterate.get(o);
							if (o instanceof String && oid instanceof Integer) {
								String name = (String) o;
								int id = (Integer) oid;
								String friendlyName = (String) trans.invoke(locale, "entity." + name + ".name");
								if (!(friendlyName.startsWith("entity.") && friendlyName.endsWith(".name"))) {
									nameMap.put(id, friendlyName);
								}
							}
						}
					}
					updateNamesLegacy();
				}
			}
		} catch (ClassNotFoundException | IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
			PseudoSpawners.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, LanguageManager.getLanguage().getMessage("pseudospawners.error_unable_get_name"));
			e.printStackTrace();
		}
	}

	private static void updateNames() {
		HashMap<String, EntityType> nrm = new HashMap<String, EntityType>();
		for (EntityType et : names.keySet()) {
			String name = names.get(et);
			nrm.put(name, et);
		}
		namesReverse = nrm;
	}

	private static void updateNamesLegacy() {
		HashMap<EntityType, String> nm = new HashMap<EntityType, String>();
		HashMap<String, EntityType> nrm = new HashMap<String, EntityType>();
		for (int id : nameMap.keySet()) {
			@SuppressWarnings("deprecation")
			EntityType et = EntityType.fromId(id);
			String name = nameMap.get(id);
			nm.put(et, name);
			nrm.put(name, et);
		}
		names = nm;
		namesReverse = nrm;
	}

	public static HashMap<EntityType, String> getNameMap() {
		return names;
	}

	public static HashMap<String, EntityType> getNameMapReverse() {
		return namesReverse;
	}

}
