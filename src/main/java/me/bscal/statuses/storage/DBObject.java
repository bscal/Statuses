package me.bscal.statuses.storage;

import java.sql.ResultSet;

public interface DBObject
{

	/**
	 * Returns an array of DBKeyValues for the name of the column and value assigned to it.
	 */
	public DBKeyValue[] GetColumns();

	/**
	 * Assigns values from the ResultSet to the current object instance.
	 */
	public Object ToObject(ResultSet rs);
	
}
