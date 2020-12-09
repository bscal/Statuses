package me.bscal.statuses.storage;

import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.core.StatusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public final class BukkitSQLAPI
{

	private BukkitSQLAPI()
	{
	}

	/*-
	 * *************************************
	 * * Saving and Loading Status Players *
	 * *************************************
	 */

	public static DBKeyValue ToKey(final Player p) {
		return new DBKeyValue("UUID", p.getUniqueId().toString());
	}

	public static void LoadPlayer(String table, StatusPlayer sp)
	{
		AsyncSelect(table, DBSelect.SelectPlayer(sp.player), (res) -> {
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

				AsyncDelete(table, DBUpdate.DeletePlayer(sp.player));
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
			Statuses.Get().GetDB().Insert(table, instance.GetColumns());
			return;
		}

		AsyncInsert(table, instance.GetColumns());
	}

	/*-
	 * *************************************
	 * * SQL Async functions with Bukkit   *
	 * *************************************
	 */

	public static void AsyncInsert(final String table, final DBKeyValue... columns)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Statuses.Get().GetDB().Insert(table, columns);
			}
		}.runTaskAsynchronously(Statuses.Get());

		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			Statuses.Get().GetDB().Insert(table, columns);
		});
	}

	public static void AsyncSelect(final String table, final DBSelect columns,
			Consumer<ResultSet> cb)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			cb.accept(Statuses.Get().GetDB().Select(table, columns));
		});
	}

	public static void AsyncUpdate(final String table, final DBUpdate columns)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			Statuses.Get().GetDB().Update(table, columns);
		});
	}

	public static void AsyncDelete(final String table, final DBUpdate columns)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			Statuses.Get().GetDB().Delete(table, columns);
		});
	}

	public static void GetVar(final String table, final String column, final Player p,
			Consumer<ResultSet> cb)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			cb.accept(Statuses.Get().GetDB().SelectVar(table, column, p.getUniqueId().toString()));
		});
	}

	public static void SetVar(final String table, final String column, final Object value,
			final Player p)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			Statuses.Get().GetDB().UpdateVar(table, column, value, p.getUniqueId().toString());
		});
	}
}
