package io.github.Gogolinolett.WW1Job.SimpleDBMTadapter;

import org.bukkit.inventory.ItemStack;

import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBCompareable;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBvalue;

public class DBItemStack implements DBvalue {

	private ItemStack itemstackValue;
	
	@Override
	public void Parse(String text) {
		// TODO Auto-generated method stub
		itemstackValue = new ItemStack;
		
		
		
	}

	@Override
	public String Save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean Equals(DBCompareable value2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String Display() {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemStack getItemstackValue() {
		return itemstackValue;
	}

	public void setItemstackValue(ItemStack itemstackValue) {
		this.itemstackValue = itemstackValue;
	}

}
