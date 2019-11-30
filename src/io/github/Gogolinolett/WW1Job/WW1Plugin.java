package io.github.Gogolinolett.WW1Job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.Gogolinolett.DungeonPlugin.DungeonPlugin.JoinListener;
import io.github.Gogolinolett.WW1Job.SimpleDBMTadapter.DBLocation;
import io.github.SebastianDanielFrenz.SimpleDBMT.CrashedDBstock;
import io.github.SebastianDanielFrenz.SimpleDBMT.DataBaseHandler;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.DataBaseQuery;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.DefaultDataBaseQuery;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.QueryResult;
import io.github.SebastianDanielFrenz.SimpleDBMT.query.SearchedValue;
import io.github.SebastianDanielFrenz.SimpleDBMT.registry.TypeRegistry;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBString;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBint;

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

			dbh.getDataBase("WW1Job").createTable("Players");
			dbh.getDataBase("WW1Job").getTable("Players").addColumn("UUID");
			dbh.getDataBase("WW1Job").getTable("Players").addColumn("Map");
			dbh.getDataBase("WW1Job").getTable("Players").addColumn("Team");

			dbh.getDataBase("WW1Job").createTable("Standard");
			dbh.getDataBase("WW1Job").getTable("Standard").addColumn("Location");

		}

		getCommand("WW1").setExecutor(new WW1CommandExecutor());

		getServer().getPluginManager().registerEvents(new JoinListener(), this);
	}

	public class JoinListener implements Listener {

		public void onPSpawn(PlayerRespawnEvent event) {

			if (getPlayerMap(event.getPlayer()) != null) {
				event.getPlayer().teleport(getLocation(getPlayerMap(event.getPlayer()), event.getPlayer()));

			}

		}

	}
	
	public static void setPlayerTeam(Player player, int team){
		
	dataBaseQuery.Update("WW1Job", "Players", new SearchedValue[] {new SearchedValue("UUID", new DBString(player.getUniqueId().toString()))  }  ,
			new SearchedValue[] {new SearchedValue("Team", new DBint(team)) });	
		
	}

	public static Location getStandardSpawn() {

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Standard", new String[] { "Location" },
				new SearchedValue[] { new SearchedValue("Location", new DBString("")) });

		return queryResult.rows.size() == 0 ? null : ((DBLocation) queryResult.rows.get(0).get(0)).getValue();
	}

	public static int getPlayerTeam(Player player) {

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Players", new String[] { "Team" },
				new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())) });

		return queryResult.rows.size() == 0 ? null : ((DBint) queryResult.rows.get(0).get(0)).getValue();

	}

	public static void setStandardSpawn(Player player) {

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Standard", new String[] { "Location" },
				new SearchedValue[] { new SearchedValue("Location", new DBString("")) });

		if (queryResult.isEmpty()) {
			dataBaseQuery.Insert("WW1Job", "Standard",
					new SearchedValue[] { new SearchedValue("Location", new DBLocation(player.getLocation())) });

		} else {
			dataBaseQuery.Update("WW1Job", "Standard", new SearchedValue[] {},
					new SearchedValue[] { new SearchedValue("Location", new DBLocation(player.getLocation())) });
		}

	}

	public static String getPlayerMap(Player player) {
		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Players", new String[] { "Map" },
				new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())) });

		return queryResult.rows.size() == 0 ? null : ((DBString) queryResult.rows.get(0).get(0)).getValue();

	}

	public static void setPlayerMap(Player player, String map) {

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Players", new String[] { "Map" },
				new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())) });

		if (queryResult.isEmpty()) {

			dataBaseQuery.Insert("WW1Job", "Players",
					new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())),
							new SearchedValue("Map", new DBString(map)) });
		} else {

			dataBaseQuery.Update("WW1Job", "Players",
					new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())) },
					new SearchedValue[] {new SearchedValue("Map", new DBString(map)) });

		}

	}

	public static void deletePlayerMap(Player player) {

		dataBaseQuery.Delete("WW1Job", "Players",
				new SearchedValue[] { new SearchedValue("UUID", new DBString(player.getUniqueId().toString())) });

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

	public static Location getLocation(String name, Player player) {
		Location location;

		QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Locations", new String[] { "location1", "location2" },
				new SearchedValue[] { new SearchedValue("name", new DBString(name)) });

		if (queryResult.isEmpty()) {
			player.sendMessage("This dungeon doesnt exist");
			return null;
		} else {
			location = ((DBLocation) queryResult.rows.get(0).get((getPlayerTeam(player)) - 1))
					.getValue();
		}

		return location;
	}

}
