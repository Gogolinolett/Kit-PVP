package io.github.Gogolinolett.WW1Job;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WW1CommandExecutor implements CommandExecutor {

	public static String prefix = "[WW1Job]";

	public static String PermissionDenied(String[] permissions) {
		String output = prefix + "§4§lYou do not have permission to run this command.\n" + prefix
				+ "Permissions that grant access to this command:\n";
		for (String permission : permissions) {
			output += prefix + " - " + permission + "\n";
		}
		return output;
	}

	public static boolean hasPermission(CommandSender sender, String[] permissions) {
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
		if (args[0].equalsIgnoreCase("test")){
			player.sendMessage("Test");
		}

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
					
					player.sendMessage("tping");
					Location location = WW1Plugin.getLocation(args[1], player, Integer.parseInt(args[2]));

					player.teleport(location);
				}

			}
		

		return false;

	}
}
