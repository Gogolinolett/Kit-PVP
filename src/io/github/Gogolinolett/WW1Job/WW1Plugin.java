package io.github.Gogolinolett.WW1Job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class WW1Plugin extends JavaPlugin {

	public static WW1Plugin plugin;
	public static Connection sqlc;


	public void onEnable() {
		plugin = this;

		setupDB();

		
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
		
		runSQL("UPDATE Players SET Team \""+ team +"\" WHERE UUID =\"" +player.getUniqueId().toString() +"\" ");
	dataBaseQuery.Update("WW1Job", "Players", new SearchedValue[] {new SearchedValue("UUID", new DBString(player.getUniqueId().toString()))  }  ,
			new SearchedValue[] {new SearchedValue("Team", new DBint(team)) });	
		
	}

	public static Location getStandardSpawn() {
		runSQL("");
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

	public void setupDB() {
		boolean isNew = Files.exists(Paths.get(getDataFolder().getAbsolutePath() + "/database.db"));
		sqlc = connect(getDataFolder().getAbsolutePath() + "/database.db");

		if (isNew) {
			runSQL("CREATE TABLE Locations (world1 STRING , x1 REAL, y1 REAL, z1 REAL, world2 STRING, x2 REAL, y2 REAL , z2 REAL,name STRING PRIMARY KEY); CREATE TABLE Players(Map STRING, Team REAL, UUID STRING PRIMARY KEY); CREATE TABLE Standard(world STRING, x REAL, y REAL, z REAL)");
		}
	}

	public static void setMapSpawn(String name, Player player, int team)  {
		
		if (3 > team && team > 0) {
			
			runSQL("UPDATE Locations SET world"+ team +" = \"" + player.getWorld().getName() + "\",x"+ team +"=" + player.getLocation().getX()
					+ ",y"+ team +"=" + player.getLocation().getY() + ",z"+ team +"=" + player.getLocation().getZ());
			
			
			
			/* dataBaseQuery.Update("WW1Job", "Locations",
					new SearchedValue[] { new SearchedValue("name", new DBString(name)) },
					new SearchedValue[] { new SearchedValue("location" + team, new DBLocation(player.getLocation())) });

			QueryResult queryResult = dataBaseQuery.Run("WW1Job", "Locations", new String[] { "name" },
					new SearchedValue[] { new SearchedValue("name", new DBString(name)) });
					*/
			
			try{
				if (!runSQL("SELECT world"+ team +"FROM Locations WHERE name = \"" + name + "\"")){
					player.sendMessage("This Map does not exist!");
				}
			}catch(Exception e){
				player.sendMessage("This Map does not exist!");
			}
			

			
		} else {
			player.sendMessage("This team does not exist");
		}

	}

	public static void createMap(String name, Player player) {
		
		
		ResultSet rs = runSQL("SELECT name from");

		if (queryResult.isEmpty()) {
			
			
			runSQL("INSERT INTO Locations (name) VALUES(\""+ name + "\" )");
			
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
	
	public static Connection connect(String path) {
		// SQLite connection string
		String url = "jdbc:sqlite:" + path;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static ResultSet runSQL(String sql) {
		try {
			return sqlc.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			throw new RuntimeException("See SQLException");
		}
	}


}
