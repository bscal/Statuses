package me.bscal.statuses.storage;

import org.apache.commons.lang.StringUtils;

public final class DBUtils
{

	private DBUtils()
	{
	}

	public static String JoinKeys(DBKeyValue[] kv)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < kv.length; i++)
		{
			sb.append(kv[i].colName);
			if (i < kv.length - 1)
				sb.append(" ,");
		}
		return sb.toString();
	}

	public static String JoinValues(DBKeyValue[] kv)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < kv.length; i++)
		{
			sb.append(kv[i].colVal);
			if (i < kv.length - 1)
				sb.append(" ,");
		}
		return sb.toString();
	}

	public static String PrepareValues(int length)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			sb.append('?');
			if (i < length - 1)
				sb.append(" ,");
		}
		return sb.toString();
	}

	public static String KVFormat(DBKeyValue[] kv)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < kv.length; i++)
		{
			sb.append(kv[i].colName).append('=').append(kv[i].GetValue());
			if (i < kv.length - 1)
				sb.append(" ,");
		}
		return sb.toString();
	}

	public static String JoinTable(DBTable[] tableColumns)
	{
		return StringUtils.join(tableColumns, ", ");
	}
}
