package com.favorlock.ForumBridge;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.favorlock.ForumBridge.commands.BbbCommand;
import com.favorlock.ForumBridge.commands.BbbVersionCommand;
import com.favorlock.ForumBridge.commands.FBanCommand;
import com.favorlock.ForumBridge.commands.FDemoteCommand;
import com.favorlock.ForumBridge.commands.FPromoteCommand;
import com.favorlock.ForumBridge.commands.FRankCommand;
import com.favorlock.ForumBridge.commands.FUnbanCommand;
import com.favorlock.ForumBridge.commands.FUsernameCommand;
import com.favorlock.ForumBridge.commands.FsyncAllCommand;
import com.favorlock.ForumBridge.commands.FsyncCommand;
import com.favorlock.ForumBridge.commands.ResyncCommand;
import com.favorlock.ForumBridge.commands.SyncCommand;
import com.favorlock.ForumBridge.extras.Metrics;
import com.favorlock.ForumBridge.commands.*;

public class ForumBridge extends JavaPlugin {
	public static String name;
	public static String version;
	public static List<String> authors;
	public static ForumBridgeSync sync;
	public static ForumBridgeInternalDB ForumBridgeDb;
	public List<BaseCommand> commands = new ArrayList<BaseCommand>();
	public static ForumBridge p;
	public static Permission perms;
	public static HashMap<String, String> worldUpdate = new HashMap<String, String>();
	//hashmap for player sync. Key is player name and entry is the account name.
	public static HashMap<String, String> playerList = new HashMap<String, String>();

	public void onEnable() {
		p = this;
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		authors = this.getDescription().getAuthors();
		PluginManager pm = this.getServer().getPluginManager();
		ForumBridgeLogger.initialize(this.getLogger());

		ForumBridgeLogger.info("Initializing Vault");
		if (!setupPermissions()) {
			ForumBridgeLogger.info("Permissions plugin not found, shutting down...");
			pm.disablePlugin(this);
		} else {
			//Load the configuration
			ForumBridgeLogger.info("Initializing configuration.");

			new ForumBridgeConfig(this);

			//Loading databases
			ForumBridgeLogger.info("Loading the internal database");
			ForumBridgeDb = new ForumBridgeInternalDB(this);

			//MySQL connect
			ForumBridgeLogger.info("Trying to connect to the external database");
			try {
				new ForumBridgeWebsiteDB(this);
			} catch (SQLException e2) {
				ForumBridgeLogger.error(e2.getMessage());
				pm.disablePlugin(this);
			}

			if (this.isEnabled()) {
				//Load the corresponding link file along with metrics
				ForumBridgeLogger.info("Loading website link");
				try {
					File dir = new File(this.getDataFolder() + "/links");
					if (!dir.exists()) {
						ForumBridgeLogger.info("Links folder not found. Creating it!");
						dir.mkdirs();
					}
					@SuppressWarnings("resource")
					ClassLoader loader = new URLClassLoader(new URL[]{dir.toURI().toURL()}, ForumBridgeSync.class.getClassLoader());
					for (File file : dir.listFiles()) {
						String name = file.getName().substring(0, file.getName().lastIndexOf("."));
						if (name.toLowerCase().equals(ForumBridgeConfig.linkName.toLowerCase())) {

							Class<?> clazz = loader.loadClass(name);
							Object object = clazz.newInstance();
							if (object instanceof ForumBridgeSync) {
								sync = (ForumBridgeSync) object;
								ForumBridgeLogger.info("Website link " + name + " loaded!");
							} else {
								ForumBridgeLogger.error("The class file for " + name + " loForumBridges invalid. Is it downloaded correctly?");
								pm.disablePlugin(this);
							}
						}
					}
					if (sync == null) {
						ForumBridgeLogger.error("Website link " + name + " not found. Be sure it is located in the plugins/com.favorlock.ForumBridge/links folder!");
						pm.disablePlugin(this);
					} else {
						//Loading metrics
						Metrics metrics = new Metrics(this);
						metrics.start();
					}

				} catch (MalformedURLException e) {
					ForumBridgeLogger.info("A error occured while loading the forum link class. Error code 1.");
					pm.disablePlugin(this);
				} catch (InstantiationException e) {
					ForumBridgeLogger.info("A error occured while loading the forum link class. Error code 2");
					pm.disablePlugin(this);
				} catch (IllegalAccessException e) {
					ForumBridgeLogger.info("A error occured while loading the forum link class. Error code 3");
					pm.disablePlugin(this);
				} catch (ClassNotFoundException e) {
					ForumBridgeLogger.info("Forum link class not found, shutting down.... Check if the configuration.forum configuration node is configurated correctly.");
					pm.disablePlugin(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			//Load events

			if (this.isEnabled()) {
				pm.registerEvents(new ForumBridgeEvents(), this);
				ForumBridgeLogger.info("Loading commands.");

				setupCommands();

				ForumBridgeLogger.info("Loading complete!");
			}

		}

	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public void onDisable() {
		ForumBridgeLogger.info("Closing remote MySQL connection");
		ForumBridgeWebsiteDB.dbm.close();

		ForumBridgeLogger.info("Closing local DB connection");
		ForumBridgeDb.close();
	}

	public void disable() {
		Bukkit.getServer().getPluginManager().disablePlugin(this);
		Bukkit.getServer().getScheduler().cancelTask(ForumBridgeWebsiteDB.taskID);
	}

	private void setupCommands() {
		commands.add(new BbbCommand());
		commands.add(new BbbVersionCommand());
		commands.add(new SyncCommand());
		commands.add(new ResyncCommand());
		commands.add(new FsyncCommand());
		commands.add(new FsyncAllCommand());
		commands.add(new FBanCommand());
		commands.add(new FUnbanCommand());
		commands.add(new FPromoteCommand());
		commands.add(new FDemoteCommand());
		commands.add(new FRankCommand());
		commands.add(new FUsernameCommand());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		String commandName = cmd.getName();
		for (BaseCommand ForumBridgeCommand : this.commands) {
			if (ForumBridgeCommand.getCommands().contains(commandName)) {
				ForumBridgeCommand.execute(sender, parameters);
				return true;
			}
		}
		return false;
	}

}
