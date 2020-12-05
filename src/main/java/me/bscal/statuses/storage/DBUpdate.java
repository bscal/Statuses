package me.bscal.statuses.storage;

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
}
