package io.github.Gogolinolett.WW1Job;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WW1CommandExecutor implements CommandExecutor {

	public static String prefix = "[WW1Job]";
	public static TreeMap<Player, Player> challenges = new TreeMap<Player, Player>();

	private static List<ItemStack> inv;
	private static List<ItemStack> armor;
	private static YamlConfiguration yml;
	private static File file;

	public String PermissionDenied(String[] permissions) {
		String output = prefix + "�4�lYou do not have permission to run this command.\n" + prefix
				+ "Permissions that grant access to this command:\n";
		for (String permission : permissions) {
			output += prefix + " - " + permission + "\n";
		}
		return output;
	}

	public boolean hasPermission(CommandSender sender, String[] permissions) {
		if (sender instanceof Server) {
			return true;
		}
		if (sender.isOp()) {
			if (sender instanceof Player) {

				return true;
			}

		}
		for (String permission : permissions) {
			if (sender.hasPermission(permission)) {
				return true;
			}
		}
		sender.sendMessage(PermissionDenied(permissions));
		return false;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("test")) {
			player.sendMessage("Test");
		}

		if (args.length > 1) {

			if (hasPermission(sender, new String[] { "WW1.setSpawn", "WW1.*", "WW1.Admin" }) == true) {

				if (args[0].equalsIgnoreCase("setspawn")) {
					// /ww1 setspawn <name> <team>
					player.sendMessage("setting mapspawn");

					WW1Plugin.setMapSpawn(args[1], player, Integer.parseInt(args[2]));

				}

			}

			if (hasPermission(sender, new String[] { "WW1.createMap", "WW1.*", "WW1.Admin" }) == true) {

				if (args[0].equalsIgnoreCase("create")) {
					player.sendMessage("creating map");
					WW1Plugin.createMap(args[1], player);
				}
			}

			if (hasPermission(sender, new String[] { "WW1.tp", "WW1.*", "WW1.Admin", "WW1.Player", "WW1.tp.*",
					"WW1.tp." + args[1] }) == true) {

				if (args[0].equalsIgnoreCase("tp")) {
					
					tpToMap(player, args);
					//if (WW1Plugin.getPlayerMap(player) != null) {

					//	player.sendMessage("you are in a Map!");

					//} else {
					//	savePInv(player);
					//	WW1Plugin.setPlayerTeam(player, Integer.parseInt(args[2]));
					//	WW1Plugin.setPlayerMap(player, args[1]);
						//
					//	player.sendMessage("tping");

					//	Location location = WW1Plugin.getMapLocation(args[1], player);

					//	setInv(player, args[1]);

					//	player.teleport(location);

					//}

				}

			}

			if (hasPermission(sender, new String[] { "WW1.tp", "WW1.*", "WW1.Admin", "WW1.Player", "setkit" }) == true) {
				if (args[0].equalsIgnoreCase("setkit")) {

					saveInv(player, args[1]);
					player.sendMessage("Kit set");

				}

			}

		}

		if (hasPermission(sender,
				new String[] { "WW1.leave", "WW1.*", "WW1.Admin", "WW1.Player", "WW1.tp.*" }) == true) {

			if (args[0].equalsIgnoreCase("leave")) {

				//if (WW1Plugin.getPlayerMap(player) == null) {
				//	player.sendMessage("You are not in a Map");

				//} else {
				//	WW1Plugin.setPlayerMap(player, null);
				//	getPInv(player);
				//	player.teleport(WW1Plugin.getStandardSpawn());
				//	player.sendMessage("You left the map");
				//}
				leaveMap(player);
			}

		}

		if (hasPermission(sender, new String[] { "WW1.setStandardSpawn", "WW1.*", "WW1.Admin", }) == true) {

			if (args[0].equalsIgnoreCase("setStandardSpawn")) {
				player.sendMessage("setStandardSpawn");
				WW1Plugin.setStandardSpawn(player);

			}

		}

		// dan's code

		if (args[0].equalsIgnoreCase("dump")) {
			try {
				if (args[1].equalsIgnoreCase("TeamPlayers")) {
					sender.sendMessage("world1 - x1 - y1 - z1 - world2 - x2 - y2 - z2 - name");
					ResultSet rs = WW1Plugin.runSQLQuery("SELECT * FROM TeamSpawns");
					while (rs.next()) {
						sender.sendMessage(rs.getString("world1") + " - " + rs.getInt("x1") + " - " + rs.getInt("y1")
								+ " - " + rs.getInt("z1") + " - " + rs.getString("world2") + " - " + rs.getInt("x2")
								+ " - " + rs.getInt("y2") + " - " + rs.getInt("z2"));
					}
				} else if (args[1].equalsIgnoreCase("Players")) {
					sender.sendMessage("Map - Team - UUID");
					ResultSet rs = WW1Plugin.runSQLQuery("SELECT * FROM Players");
					while (rs.next()) {
						sender.sendMessage(
								rs.getString("Map") + " - " + rs.getInt("Team") + " - " + rs.getString("UUID"));
					}
				} else if (args[1].equalsIgnoreCase("Standard")) {
					sender.sendMessage("world - x - y - z");
					ResultSet rs = WW1Plugin.runSQLQuery("SELECT * FROM Standard");
					while (rs.next()) {
						sender.sendMessage(rs.getString("world") + " - " + rs.getDouble("x") + " - " + rs.getDouble("y")
								+ " - " + rs.getDouble("z"));
					}
				}
			} catch (SQLException e) {
				sender.sendMessage("SQL error!");
				e.printStackTrace();
			}
		}
		
		if (hasPermission(sender, new String[] { "WW1.Join", "WW1.*", "WW1.Admin", "WW1.Player", "Join" }) == true) {
			if (args[0].equalsIgnoreCase("1v1")){
				Player challenged = player.getServer().getPlayer(args[1]);
				if(challenges.get(challenged).equals(player)){
					
				}else if(challenges.containsKey(player)){
					challenges.replace(player, challenged);
				}
				
				
			}
		}
		
		return false;

	}
	
	public static void leaveMap(Player player){
		if (WW1Plugin.getPlayerMap(player) == null) {
			player.sendMessage("You are not in a Map");

		} else {
			WW1Plugin.setPlayerMap(player, null);
			getPInv(player);
			player.teleport(WW1Plugin.getStandardSpawn());
			player.sendMessage("You left the map");
		}
	}
	
	public static void tpToMap(Player player, String[] args){
		if (WW1Plugin.getPlayerMap(player) != null) {

			player.sendMessage("you are in a Map!");

		} else {
			savePInv(player);
			WW1Plugin.setPlayerTeam(player, Integer.parseInt(args[2]));
			WW1Plugin.setPlayerMap(player, args[1]);

			player.sendMessage("tping");

			Location location = WW1Plugin.getMapLocation(args[1], player);

			setInv(player, args[1]);

			player.teleport(location);

		}
	}

	public static void getPInv(Player p) {

		File file = new File(WW1Plugin.plugin.getDataFolder(), p.getUniqueId() + ".yml");

		if (file.exists()) {

			yml = YamlConfiguration.loadConfiguration(file);

			ItemStack[] i = toAnArray(yml.getList("Inventory"));
			ItemStack[] ar = toAnArray(yml.getList("Armor"));

			p.getInventory().setContents(i);

		}
	}

	public static void setInv(Player p, String name) {

		File file = new File(WW1Plugin.plugin.getDataFolder(), name + ".yml");

		if (file.exists()) {

			yml = YamlConfiguration.loadConfiguration(file);

			ItemStack[] i = toAnArray(yml.getList("Inventory"));
			ItemStack[] ar = toAnArray(yml.getList("Armor"));

			p.getInventory().setContents(i);

		}
	}

	public static void savePInv(Player p) {
		checkFolder();

		File file = new File(WW1Plugin.plugin.getDataFolder(), p.getUniqueId() + ".yml");

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		yml = YamlConfiguration.loadConfiguration(file);

		yml.set("Inventory", toList(p.getInventory().getContents()));
		yml.set("Armor", toList(p.getInventory().getArmorContents()));

		try {
			yml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void saveInv(Player p, String name) {
		checkFolder();

		File file = new File(WW1Plugin.plugin.getDataFolder(), name + ".yml");

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		yml = YamlConfiguration.loadConfiguration(file);

		yml.set("Inventory", toList(p.getInventory().getContents()));
		yml.set("Armor", toList(p.getInventory().getArmorContents()));

		try {
			yml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void checkFolder() {
		File f = new File((String) WW1Plugin.plugin.getDataFolder().getName());
		if (f.exists()) {
			return;
		}
		f.mkdir();
	}

	public static List<ItemStack> toList(ItemStack[] in) {
		ArrayList<ItemStack> l = new ArrayList<>();

		for (int i = 0; i < in.length; i++) {
			l.add(in[i]);
		}
		return l;
	}

	public static ItemStack[] toAnArray(List<?> list) {
		ItemStack[] is = new ItemStack[list.size()];

		for (int i = 0; i < list.size(); i++) {
			is[i] = (ItemStack) list.get(i);
		}

		return is;
	}
}
