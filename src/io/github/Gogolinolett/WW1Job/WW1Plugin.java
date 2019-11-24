package io.github.Gogolinolett.WW1Job;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.Gogolinolett.WW1Job.SimpleDBMTadapter.DBLocation;
import io.github.SebastianDanielFrenz.SimpleDBMT.CrashedDBstock;
import io.github.SebastianDanielFrenz.SimpleDBMT.DataBaseHandler;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.DataBaseQuery;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.DefaultDataBaseQuery;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.QueryResult;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.SearchedValue;
import io.github.SebastianDanielFrenz.SimpleDBMT.registry.TypeRegistry;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBString;

public class WW1Plugin extends JavaPlugin {

	public static WW1Plugin plugin;
	public static DataBaseHandler dbh;
	public static DataBaseQuery dataBaseQuery;

	public void onEnable() {
		plugin = this;

		setupDB();
		dataBaseQuery = new DefaultDataBaseQuery(dbh);

		TypeRegistry reg = CrashedDBstock.getDefaultTypeRegistry();
		reg.register(DBLocation.class);

		try {
			dbh.addDataBase("WW1Job.db", reg);
		} catch (IOException e) {
			dbh.createDataBase("WW1Job", "WW1Job.db", reg);
			dbh.getDataBase("WW1Job").createTable("Locations");
			dbh.getDataBase("WW1Job").getTable("Locations").addColumn("name");
			dbh.getDataBase("WW1Job").getTable("Locations").addColumn("location1");
			dbh.getDataBase("WW1Job").getTable("Locations").addColumn("location2");

		}

		getCommand("WW1").setExecutor(new WW1CommandExecutor());
		
	}

	public boolean setupDB() {
		RegisteredServiceProvider<DataBaseHandler> dataBaseProvider = getServer().getServicesManager()
				.getRegistration(DataBaseHandler.class);
		if (dataBaseProvider != null) {
			dbh = dataBaseProvider.getProvider();
		}

		return dataBaseProvider != null;
	}

	public static void setMapSpawn(String name, Player player, int team) {

		if (3 > team && team > 0) {
			dataBaseQuery.Update("WW1Job", "Locations",
					new SearchedValue[] { new SearchedValue("name", new DBString(name)) },
					new SearchedValue[] { new SearchedValue("location" + team, new DBLocation(player.getLocation())) });

			QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Locations", new String[] { "name" },
					new SearchedValue[] { new SearchedValue("name", new DBString(name)) });

			if (queryResult.isEmpty()) {

				player.sendMessage("This Map does not exist!");

			}
		} else {
			player.sendMessage("This team does not exist");
		}

	}

	public static void createMap(String name, Player player) {

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Locations", new String[] { "name" },
				new SearchedValue[] { new SearchedValue("name", new DBString(name)) });

		if (queryResult.isEmpty()) {

			dataBaseQuery.Insert("WW1Job", "Locations",
					new SearchedValue[] { new SearchedValue("name", new DBString(name)),
							new SearchedValue("location1", new DBLocation(null)),
							new SearchedValue("location2", new DBLocation(null)) });
		} else {
			player.sendMessage("This Map exists! Please Choose a different name!");

		}

	}

	public static Location getLocation(String name, Player player, int team) {
		Location location;
		
		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Locations", new String[] { "location1", "location2" },
				new SearchedValue[] { new SearchedValue("name", new DBString(name)) });

		if (queryResult.isEmpty()) {
			player.sendMessage("This dungeon doesnt exist");
			return null;
		} else {
			location = ((DBLocation)queryResult.rows.get(0).get(team - 1)).getValue();
		}

		return location;
	}
	
	
	public static void setKit (String name, Player player){
		
		
		player.getInventory().getContents();
		player.getInventory().getArmorContents();
		
	}

}
