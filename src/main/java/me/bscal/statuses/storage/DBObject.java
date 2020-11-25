package me.bscal.statuses.storage;

import java.sql.ResultSet;

public interface DBObject
{

	public String GetColumns();
	
	public Object[] GetValues();
	
	public Object ToObject(ResultSet rs);
	
}
