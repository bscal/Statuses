package me.bscal.statuses.storage;

/**
 * Wrapper for Select and Delete statements. Columns represent the sql columns values for the statement,
 * this could be '*', 1, or several columns to fetch. Wheres are an array of DBKeyValue pairs used for the where clause.
 */
public class DBSelect
{

	public final String columns;
	public final DBKeyValue[] wheres;

	public DBSelect(final String columns, DBKeyValue[] wheres)
	{
		this.columns = columns;
		this.wheres = wheres;
	}

}
