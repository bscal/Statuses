package me.bscal.statuses.storage;

/**
 * SQL wrapper for create table statements
 */
public class DBTable
{

	public final String columnName;
	public final String columnType;
	public final boolean notNull;
	public final Object defaultVal;

	public DBTable(final String columnName, final String columnType)
	{
		this(columnName, columnType, true, null);
	}

	public DBTable(final String columnName, final String columnType, final boolean notNull)
	{
		this(columnName, columnType, notNull, null);
	}

	public DBTable(final String columnName, final String columnType, final boolean notNull,
			final Object defaultVal)
	{
		this.columnName = columnName;
		this.columnType = columnType;
		this.notNull = notNull;
		this.defaultVal = defaultVal;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('`').append(columnName).append('`').append(' ').append(columnType);

		if (notNull)
			sb.append(" NOT NULL");

		if (defaultVal != null && defaultVal.getClass()
				.isPrimitive() || defaultVal instanceof String)
			sb.append(" default ").append(defaultVal);

		return sb.append(';').toString();
	}

}
