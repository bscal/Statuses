package me.bscal.statuses.storage;

import org.bukkit.entity.Player;

/**
 * SQL wrapper for Update statements. Updates are an array of DBKeyValue pairs representing the update clause in
 * the sql statement. Wheres an array of DBKeyValue pairs used in the where clause.
 */
public class DBUpdate
{
	public final DBKeyValue[] updates;
	public final DBKeyValue[] wheres;

	public DBUpdate(final DBKeyValue[] updates, final DBKeyValue[] wheres)
	{
		this.updates = updates;
		this.wheres = wheres;
	}

	public static DBUpdate UpdatePlayer(final Player p, final DBKeyValue... updates) {
		return new DBUpdate(updates, new DBKeyValue[] { DBUtils.PlayerKV(p) });
	}

	public static DBUpdate DeletePlayer(final Player p)
	{
		return new DBUpdate(null, DBUtils.PlayerKVArray(p));
	}
}
