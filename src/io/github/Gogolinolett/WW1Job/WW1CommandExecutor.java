package io.github.Gogolinolett.WW1Job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	List<ItemStack> inv;
	List<ItemStack> armor;
	YamlConfiguration yml;

	File file;

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
					getInv(player, args[1]);
					
					player.teleport(location);
				}

			}
			
			
			if (hasPermission(sender, new String[] { "WW1.tp", "WW1.*", "WW1.Admin", "WW1.Player", "WW1.tp.*",
					"WW1.tp." + args[1] }) == true){
				if (args[0].equalsIgnoreCase("setkit")){
					
					saveInv(player, args[1]);
					
				}
				
				
			}
		

		return false;

	}
	
	
	public void getInv(Player p, String name) {

		
		File file = new File(WW1Plugin.plugin.getDataFolder(), name + ".yml");
		
		if (file.exists()) {

			this.yml = YamlConfiguration.loadConfiguration(file);

			ItemStack[] i = toAnArray(this.yml.getList("Inventory"));
			ItemStack[] ar = toAnArray(this.yml.getList("Armor"));

			p.getInventory().setContents(i);

		}
	}
	
	public void saveInv(Player p, String name) {
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

		this.yml = YamlConfiguration.loadConfiguration(file);

		this.yml.set("Inventory", toList(p.getInventory().getContents()));
		this.yml.set("Armor", toList(p.getInventory().getArmorContents()));

		try {
			this.yml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public void checkFolder() {
		File f = new File((String) WW1Plugin.plugin.getDataFolder().getName());
		if (f.exists()) {
			return;
		}
		f.mkdir();
	}

	public List<ItemStack> toList(ItemStack[] in) {
		ArrayList<ItemStack> l = new ArrayList<>();

		for (int i = 0; i < in.length; i++) {
			l.add(in[i]);
		}
		return l;
	}

	public ItemStack[] toAnArray(List<?> list) {
		ItemStack[] is = new ItemStack[list.size()];

		for (int i = 0; i < list.size(); i++) {
			is[i] = (ItemStack) list.get(i);
		}

		return is;
	}
}
