package me.bscal.statuses.storage;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public final class DBUtils
{

	private DBUtils()
	{
	}

	public static DBKeyValue PlayerKV(final Player p)
	{
		return new DBKeyValue("UUID", p.getUniqueId().toString());
	}

	public static DBKeyValue[] PlayerKVArray(final Player p)
	{
		return new DBKeyValue[] { new DBKeyValue("UUID", p.getUniqueId().toString()) };
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

	public static String KVPrepare(DBKeyValue[] kv)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < kv.length; i++)
		{
			sb.append(kv[i].colName).append('=').append('?');
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
