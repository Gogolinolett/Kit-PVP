package io.github.Gogolinolett.WW1Job;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.mcduckler.splatcraft.main.Main;

public class WW1Plugin extends JavaPlugin {

	public static WW1Plugin plugin;
	public static Connection sqlc;

	WW1CommandExecutor ww1CommandExecutor;

	Main craftTheftAuto;

	public void onEnable() {
		plugin = this;

		try {
			Files.createDirectories(Paths.get("plugins/WW1Plugin"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			setupDB();
		} catch (SQLException e) {
			getLogger().info("Cannot continue!");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		ww1CommandExecutor = new WW1CommandExecutor();

		getCommand("WW1").setExecutor(ww1CommandExecutor);
		craftTheftAuto = (Main) getServer().getPluginManager().getPlugin("CraftTheftAuto");
		getServer().getPluginManager().registerEvents(new JoinListener(), this);

	}

	public class JoinListener implements Listener {

		@EventHandler
		public void onPJoin(PlayerJoinEvent event) {
			// hier wird bei jedem spieler join die in datenbank gegebenenfals
			// eingefügt
			// try {
			// ResultSet rs = runSQLQuery("SELECT Team, Map FROM Players WHERE
			// UUID =\""
			// + event.getPlayer().getUniqueId().toString() + "\" ");

			// if (!rs.next()) {

			// runSQL("INSERT INTO Players (UUID) VALUES (\"" +
			// event.getPlayer().getUniqueId().toString()
			// + "\")");

			// } else {
			// System.out.println(rs.getString("Map"));
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			try {
				ResultSet rs = runSQLQuery(
						"SELECT UUID FROM Players WHERE UUID = \"" + event.getPlayer().getUniqueId().toString() + "\"");

				if (!rs.next()) {
					runSQL("INSERT INTO Players (UUID, Map) VAlUES (\"" + event.getPlayer().getUniqueId().toString()
							+ "\", \"-1\")");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@EventHandler
		public void onPSpawn(PlayerRespawnEvent event) {

			if (getPlayerMap(event.getPlayer()) != null) {
				// event.getPlayer().teleport(getMapLocation(getPlayerMap(event.getPlayer()),
				// event.getPlayer()));
				event.setRespawnLocation(getMapLocation(getPlayerMap(event.getPlayer()), event.getPlayer()));

				ww1CommandExecutor.getPInv(event.getPlayer());
			}

		}

	}

	public static void setPlayerTeam(Player player, int team) {
		try {
			if (team > 0 && team < 3) {

				// ResultSet rs = runSQL("SELECT Team FROM Players WHERE UUID
				// =\"" + player.getUniqueId().toString() + "\" ");

				// if (!rs.next()) {
				// runSQL("INSERT INTO Players (UUID, Team) VALUES ("+
				// player.getUniqueId().toString() +"," + team + ")");
				// } else {

				runSQL("UPDATE Players SET Team = \"" + team + "\" WHERE UUID =\"" + player.getUniqueId().toString()
						+ "\" ");
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Location getStandardSpawn() {
		try {
			ResultSet rs = runSQLQuery("SELECT world,x,y,z FROM Standard");
			if (!rs.next()) {

				throw new RuntimeException("Was befindet sich hier? NICHTS!!!");
			}

			return new Location(plugin.getServer().getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"),
					rs.getInt("z"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static int getPlayerTeam(Player player) {
		try {
			ResultSet rs = runSQLQuery(
					"SELECT Team FROM Players WHERE UUID = \"" + player.getUniqueId().toString() + "\"");

			if (!rs.next()) {
				player.sendMessage("No Team Selected");

			} else {
				return rs.getInt("Team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		player.sendMessage("An Error occured! You were assigned Team 1");
		return 1;

	}

	public static void setStandardSpawn(Player player) {
		try {

			ResultSet rs = runSQLQuery("SELECT World FROM Standard");

			if (!rs.next()) {
				runSQL("INSERT INTO Standard (world, x, y, z) VAlUES (\"" + player.getLocation().getWorld().getName()
						+ "\"," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
						+ player.getLocation().getZ() + ")");
			} else {
				runSQL("UPDATE Standard SET (world, x, y, z) VAlUES (\"" + player.getLocation().getWorld().getName()
						+ "\"," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
						+ player.getLocation().getZ() + ")");

			}

		} catch (Exception e) {

		}

	}

	public static String getPlayerMap(Player player) {
		try {
			ResultSet rs = runSQLQuery(
					"SELECT Map FROM Players WHERE UUID = \"" + player.getUniqueId().toString() + "\"");
			if (!rs.next()) {
				return null;
			}
			if (rs.getString("Map") == null) {
				return null;
			} else if (rs.getString("Map").equals("-1")) {
				return null;

			} else {
				return rs.getString("Map");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static void setPlayerMap(Player player, String map) {

		try {

			// ResultSet rs = runSQL("SELECT Map FROM Players WHERE UUID =\""+
			// player.getUniqueId().toString() +"\"");

			// if (!rs.next()) {
			// runSQL("INSERT INTO Players (UUID, Map) VALUES ("+
			// player.getUniqueId().toString() +"," + map +")");

			// }else{
			if (map == null) {
				runSQL("UPDATE Players SET Map = null WHERE UUID = \"" + player.getUniqueId().toString() + "\"");
			} else {
				runSQL("UPDATE Players SET Map = \"" + map + "\" WHERE UUID = \"" + player.getUniqueId().toString()
						+ "\"");
			}
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void deletePlayerMap(Player player) {

		runSQL("UPDATE Players SET (Map) WHERE UUID = \"" + player.getUniqueId().toString() + " VALUES (\"-1\")");

	}

	public static void setupDB() throws SQLException {
		boolean isNew = !Files.exists(Paths.get(plugin.getDataFolder().getAbsolutePath() + "\\database.db"));
		sqlc = connect(plugin.getDataFolder().getAbsolutePath() + "\\database.db");

		if (isNew) {
			plugin.getLogger().info("Creating DB!");
			runSQL("CREATE TABLE TeamSpawns (world1 STRING , x1 REAL, y1 REAL, z1 REAL, world2 STRING, x2 REAL, y2 REAL , z2 REAL,name STRING PRIMARY KEY)");
			runSQL("CREATE TABLE Players (Map STRING, Team REAL, UUID STRING PRIMARY KEY)");
			runSQL("CREATE TABLE Standard (world STRING, x REAL, y REAL, z REAL)");
		}
	}

	public static void setMapSpawn(String name, Player player, int team) {

		if (3 > team && team > 0) {

			runSQL("UPDATE TeamSpawns SET world" + team + " = \"" + player.getWorld().getName() + "\",x" + team + "="
					+ player.getLocation().getX() + ",y" + team + "=" + player.getLocation().getY() + ",z" + team + "="
					+ player.getLocation().getZ());

			/*
			 * dataBaseQuery.Update("WW1Job", TeamSpawns, new SearchedValue[] {
			 * new SearchedValue("name", new DBString(name)) }, new
			 * SearchedValue[] { new SearchedValue("location" + team, new
			 * DBLocation(player.getLocation())) });
			 * 
			 * QueryResult queryResult = dataBaseQuery.Run("WW1Job", TeamSpawns,
			 * new String[] { "name" }, new SearchedValue[] { new
			 * SearchedValue("name", new DBString(name)) });
			 */

			try {
				if (!runSQLQuery("SELECT world" + team + " FROM TeamSpawns WHERE name = \"" + name + "\"").next()) {
					player.sendMessage("This Map does not exist!");
				}
			} catch (Exception e) {
				player.sendMessage("This Map does not exist!");
			}

		} else {
			player.sendMessage("This team does not exist");
		}

	}

	public static void createMap(String name, Player player) {

		try {
			ResultSet rs = runSQLQuery("SELECT name FROM TeamSpawns WHERE name =\"" + name + "\"");

			if (!rs.next()) {

				runSQL("INSERT INTO TeamSpawns (name) VALUES (\"" + name + "\" )");

			} else {
				player.sendMessage("This Map exists! Please Choose a different name!");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Location getMapLocation(String name, Player player) {

		int team = getPlayerTeam(player);
		ResultSet rs = runSQLQuery("SELECT x" + team + ", y" + team + ", z" + team + ", world" + team
				+ " FROM TeamSpawns WHERE name = \"" + name + "\"");

		try {
			return new Location(plugin.getServer().getWorld(rs.getString("world" + team)), rs.getDouble("x" + team),
					rs.getDouble("y" + team), rs.getDouble("z" + team));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static Connection connect(String path) throws SQLException {
		// SQLite connection string
		String url = "jdbc:sqlite:" + path;
		Connection conn = null;

		conn = DriverManager.getConnection(url);
		return conn;
	}

	public static ResultSet runSQLQuery(String sql) {
		try {
			System.out.println(sql);
			return sqlc.createStatement().executeQuery(sql);
		} catch (SQLException e) {

			throw new RuntimeException(e);

		}
	}

	public static void runSQL(String sql) {

		try {
			System.out.println(sql);
			sqlc.createStatement().execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

}
