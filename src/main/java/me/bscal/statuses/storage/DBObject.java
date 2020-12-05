package me.bscal.statuses.storage;

import java.sql.ResultSet;

public interface DBObject
{

	/**
	 * Returns the columns this object stores in SQL formatting
	 */
	public String GetColumns();

	/**
	 * Returns an array of objects that are stored. Should be in order that they are defined in GetColumns()
	 */
	public Object[] GetValues();

	/**
	 * Assigns values from the ResultSet to the current object instance.
	 */
	public Object ToObject(ResultSet rs);
	
}
