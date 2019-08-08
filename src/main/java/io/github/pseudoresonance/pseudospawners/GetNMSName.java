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

import org.bukkit.entity.EntityType;

import io.github.pseudoresonance.pseudoapi.bukkit.Message.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.Utils;

public class GetNMSName {

	private static HashMap<Integer, String> nameMap = new HashMap<Integer, String>();

	public static HashMap<EntityType, String> names = new HashMap<EntityType, String>();
	public static HashMap<String, EntityType> namesReverse = new HashMap<String, EntityType>();

	public static void getNames() {
		try {
			Class<?> localeClass = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".LocaleLanguage");
			Class<?> entityTypes = Class.forName("net.minecraft.server." + Utils.getBukkitVersion() + ".EntityTypes");
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
					for (Field f : mobFields) {
						for (EntityType et : EntityType.values()) {
							String entityName = et.getKey().getKey();
							if (entityName == null || f.getName() == null)
								continue;
							if (entityName.equalsIgnoreCase(f.getName())) {
								String friendlyName = (String) trans.invoke(locale, "entity.minecraft." + f.getName().toLowerCase());
								if (!friendlyName.startsWith("entity.minecraft.")) {
									names.put(et, friendlyName);
								}
								break;
							}
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
							} catch (IndexOutOfBoundsException e) {}
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
			PseudoSpawners.message.sendConsolePluginError(Errors.CUSTOM, "Unable to fetch mob names from server locale!");
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
