package io.github.Gogolinolett.WW1Job.SimpleDBMTadapter;

import org.bukkit.Location;
import io.github.Gogolinolett.WW1Job.WW1Plugin;
import io.github.Gogolinolett.WW1Job.SimpleDBMTadapter.DBLocation;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBCompareable;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBString;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBvalue;

public class DBLocation implements DBvalue {

	private Location location;

	public void Parse(String text) {
		// TODO Auto-generated method stub
		String[] split = text.split("[,]");
		location = new Location(WW1Plugin.plugin.getServer().getWorld(split[3]),
				 Double.parseDouble(split[0]),  Double.parseDouble(split[1]), Double.parseDouble(split[2]),
				Float.parseFloat(split[4]), Float.parseFloat(split[5]));
		
	}

	public DBLocation(Location location) {
		this.location = location;
	}

	public DBLocation() {

	}

	public String Save() {
		return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getWorld() + ","
				+ location.getYaw() + "," + location.getPitch();

	}

	public boolean Equals(DBCompareable value2) {
		if(value2 instanceof DBLocation){
			return location.equals(((DBLocation) value2).location);
		}
		if (value2 instanceof DBString){
			return Display().equals(((DBString) value2).getValue());
		}
		return false;
	}

	public String Display() {
		return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getWorld() + ","
				+ location.getYaw() + "," + location.getPitch();

	}

	public Location getValue() {
		return location;
	}

	public void setValue(Location location) {
		this.location = location;
	}

}
