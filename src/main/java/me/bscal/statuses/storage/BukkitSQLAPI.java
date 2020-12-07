package me.bscal.statuses.storage;

import me.bscal.statuses.Statuses;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.function.Consumer;

public final class BukkitSQLAPI
{

	private BukkitSQLAPI() {}

	public static void AsyncInsert(final String table, final DBTable... columns)
	{
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
			cb.accept(Statuses.Get().GetDB().SelectVar(table, column, p));
		});
	}

	public static void SetVar(final String table, final String column, final Object value,
			final Player p)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Statuses.Get(), () -> {
			Statuses.Get().GetDB().UpdateVar(table, column, value, p);
		});
	}
}
