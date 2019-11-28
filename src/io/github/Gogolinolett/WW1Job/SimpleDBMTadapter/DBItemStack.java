package io.github.Gogolinolett.WW1Job.SimpleDBMTadapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.inventory.ItemStack;

import io.github.Gogolinolett.WW1Job.Utils;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBCompareable;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBvalue;

public class DBItemStack implements DBvalue {

	private ItemStack value;

	@Override
	public void Parse(String text) {
		Map<String, Object> map = (Map<String, Object>) Arrays.asList(text.split(",")).stream().map(s -> s.split(":"))
				.collect(Collectors.toMap(e -> e[0], e -> {
					try {
						return Utils.deserialize(e[1]);
					} catch (ClassNotFoundException | IOException e1) {
						throw new RuntimeException("Could not deserialize string: \"" + e[1] + "\"");
					}
				}));
		value = ItemStack.deserialize(map);
	}

	@Override
	public String Save() {
		Map<String, Object> map = value.serialize();
		String out = "";

		String[] keys = (String[]) map.keySet().toArray();
		Object[] values = map.values().toArray();
		for (int i = 0; i < map.size() - 1; i++) {
			out += keys[i] + "=" + String.valueOf(values[i]) + ",";
		}
		if (map.size() != 0) {
			out += keys[map.size() - 1] + "=" + String.valueOf(values[map.size() - 1]);
		}
		return out;
	}

	@Override
	public boolean Equals(DBCompareable value2) {
		throw new NotImplementedException(
				"Ha ha... nein. Du hast die Methode doch noch gar nicht implementiert *facepalm*");
	}

	@Override
	public String Display() {
		return Save();
	}

	public ItemStack getValue() {
		return value;
	}

	public void setValue(ItemStack value) {
		this.value = value;
	}

}
