package me.bscal.statuses.storage;

/**
 * SQL wrapper for key value pairs. Used for Insert statements and in other DB classes.
 */
public class DBKeyValue
{

	public final String colName;
	public final Object colVal;

	public DBKeyValue(final String colName, final Object colVal)
	{
		this.colName = colName;
		this.colVal = colVal;
	}

	public String GetValue()
	{
		if (colVal instanceof String)
			return "`" + colVal + "`";
		return colVal.toString();
	}

	public String ToPrepared()
	{
		return colName + "=?";
	}

}
