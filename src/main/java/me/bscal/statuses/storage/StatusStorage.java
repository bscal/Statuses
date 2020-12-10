package me.bscal.statuses.storage;

import me.bscal.SQLAPI.*;
import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.core.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class StatusStorage
{

	private static BukkitSQLAPI SQLAPI;

	private StatusStorage() {}

	public static void Init(Plugin plugin, FileConfiguration config, LogCraft logger)
	{
		Database db = new Database(plugin, config.getString("mysql.host"),
				config.getString("mysql.port"), config.getString("mysql.database"),
				config.getString("mysql.username"), config.getString("mysql.password"),
				config.getString("mysql.table"));

		SQLAPI = new BukkitSQLAPI(db, logger, config.getBoolean("MySQLEnabled"));
		SQLAPI.Connect();
		if (SQLAPI.IsConnected())
		{
			SQLAPI.Create(db.table, true, new DBTable("status_id", "BIGINT", true),
					new DBTable("UUID", "VARCHAR(36)", true),
					new DBTable("name", "VARCHAR(32)", true),
					new DBTable("key", "VARCHAR(64)", true),
					new DBTable("duration", "INTEGER", true),
					new DBTable("stacks", "TINYINT", true));
		}
	}

	public static void Shutdown()
	{
		SQLAPI.Close();
	}

	public static BukkitSQLAPI GetSQLAPI()
	{
		return SQLAPI;
	}

	public static void LoadPlayer(String table, StatusPlayer sp)
	{
		SQLAPI.AsyncSelect(table, DBSelect.SelectPlayer(sp.player), (res) -> {
			if (res != null)
			{
				while (true)
				{
					try
					{
						if (!res.next())
							break;
					}
					catch (SQLException throwables)
					{
						throwables.printStackTrace();
					}

					sp.LoadStatus((StatusInstance) new StatusInstance(sp).ToObject(res));
				}

				if (Statuses.Logger.IsLevel(LogLevel.INFO_ONLY))
					Statuses.Logger.Log("[ LoadPlayer ]", sp.player.getName());

				SQLAPI.AsyncDelete(table, DBUpdate.DeletePlayer(sp.player));
			}
		});
	}

	public static void SavePlayer(String table, StatusPlayer sp)
	{
		if (Statuses.Logger.IsLevel(LogLevel.INFO_ONLY))
			Statuses.Logger.Log("[ SavePlayer ]", sp.statuses.size(), sp.player.getName());

		sp.RemoveAllAndSave(table);
	}

	public static void SaveInstance(final StatusInstance instance, final String table)
	{
		if (Bukkit.getServer().isStopping())
		{
			SQLAPI.Insert(table, instance.GetColumns());
			return;
		}

		SQLAPI.AsyncInsert(table, instance.GetColumns());
	}

}
