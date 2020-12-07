package me.bscal.statuses.storage;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.text.MessageFormat;

public class SQLAPI
{
	Connection c;
	PreparedStatement stmt;

	private final boolean m_debug;
	private final boolean m_mysqlEnabled;

	public SQLAPI(final boolean debug, final boolean mysqlEnabled)
	{
		m_debug = debug;
		m_mysqlEnabled = mysqlEnabled;
	}

	public void Connect()
	{
		try
		{
			if (c != null)
				return;

			if (m_mysqlEnabled)
			{
				String host = Statuses.Get().getConfig().getString("mysql.host");
				String port = Statuses.Get().getConfig().getString("mysql.port");
				String db = Statuses.Get().getConfig().getString("mysql.database");
				String user = Statuses.Get().getConfig().getString("mysql.username");
				String pass = Statuses.Get().getConfig().getString("mysql.password");

				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection(
						MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", host, port, db), user,
						pass);
			}
			else
			{
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:plugins/Statuses/users.db");
			}

			if (!c.isClosed())
			{
				Log("[ ok ] Connected to database success!");

				stmt = c.prepareStatement(
						"CREATE TABLE IF NOT EXISTS user_statuses (" + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "`status_id` BIGINT NOT NULL, " + "`UUID` VARCHAR(36) NOT NULL, " + "`name` VARCHAR(32) NOT NULL, " + "`key` VARCHAR(64) NOT NULL, " + "`duration` INTEGER NOT NULL, " + "`stacks` TINYINT NOT NULL default 0);");
				stmt.executeUpdate();
				stmt.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void Close()
	{
		try
		{
			if (c == null || c.isClosed())
				return;
			c.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/*-
	 * ************************
	 * * SQL Create functions *
	 * ************************
	 */

	/**
	 * Used for creating tables.
	 *
	 * @param table  - Name of table
	 * @param autoID - Will automatically insert id primary key
	 * @param objs   - DBTable objects representing columns.
	 */
	public void Create(String table, boolean autoID, DBTable... objs)
	{
		String sql = MessageFormat.format("CREATE TABLE IF NOT EXISTS {0} ({1}{2});", table,
				(autoID) ? "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " : "",
				StringUtils.join(objs, ", "));
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}


	/*-
	 * ************************
	 * * SQL Insert functions *
	 * ************************
	 */

	/**
	 * Creates a INSERT prepared statement
	 *
	 * @param table - table name
	 * @param cols  - column names in sql syntax
	 * @param vals  - values to insert
	 */
	public void Insert(String table, String cols, Object[] vals)
	{
		String sql = MessageFormat.format("INSERT INTO {0} ({1}) VALUES ({2});", table, cols,
				DBUtils.PrepareValues(vals.length));
		try
		{
			PreparedStatement stmt = c.prepareStatement(sql);
			for (int i = 0; i < vals.length; i++)
			{
				stmt.setObject(i + 1, vals[i]);
			}
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void Insert(String table, DBKeyValue... columns)
	{
		String sql = MessageFormat
				.format("INSERT INTO {0} ({1}) VALUES ({2});", table, DBUtils.JoinKeys(columns),
						DBUtils.PrepareValues(columns.length));
		try
		{
			stmt = c.prepareStatement(sql);
			for (int i = 0; i < columns.length; i++)
				stmt.setObject(i + 1, columns[i]);
			stmt.executeUpdate();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	/*-
	 * ************************
	 * * SQL Select functions *
	 * ************************
	 */

	/**
	 * Returns ResultSet. *Important* You will want to format the where string for a
	 * prepared statement. For example "UUID = ? AND name = ?"
	 *
	 * @param table - Table name to select from.
	 * @param where - Where string for the sql statement.
	 * @param vals  - All the parameters *IN ORDER* for the where statement.
	 * @return Results as ResultSet
	 */
	public ResultSet Select(String table, String where, Object... vals)
	{
		String sql = MessageFormat.format("SELECT * FROM {0} WHERE {1};", table, where);
		try
		{
			stmt = c.prepareStatement(sql);
			for (int i = 0; i < vals.length; i++)
			{
				stmt.setObject(i + 1, vals[i]);
			}
			return stmt.executeQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the ResultSet of 1 column by player's uuid.
	 */
	public ResultSet SelectVar(String table, String column, String key)
	{
		String sql = MessageFormat.format("SELECT {0} FROM {1} WHERE UUID = ?;", column, table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, key);
			ResultSet rs = stmt.executeQuery();
			if (m_debug)
				Log("[ SelectVar ] Size: ", rs.getFetchSize(), " | Var: ", column, " | Player: ",
						key);
			if (rs.next())
				return rs;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet Select(String table, DBSelect select)
	{
		String sql = MessageFormat.format("SELECT {0} FROM {1} WHERE {2};", select.columns, table,
				DBUtils.KVPrepare(select.wheres));
		try
		{
			stmt = c.prepareStatement(sql);

			for (int i = 0; i < select.wheres.length; i++)
				stmt.setObject(i + 1, select.wheres[i].colVal);

			return stmt.executeQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*-
	 * ************************
	 * * SQL Update functions *
	 * ************************
	 */

	/**
	 * Updates 1 column with the value where the player's uuid matches
	 */
	public void UpdateVar(String table, String col, Object val, String key)
	{
		String sql = MessageFormat.format("UPDATE {0} SET {1}=? where UUID = ?", table, col);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setObject(1, val);
			stmt.setString(2, key);
			int count = stmt.executeUpdate();
			if (m_debug)
				Log("[ SelectVar ] Updates: ", count, " | Player: ", key);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Updates a column with a objects value using where string in the statement.
	 *
	 * @param table      - table name.
	 * @param col        - column name. Only 1.
	 * @param updatedVal - updated value.
	 * @param where      - where string. *!* Format as sql prepared statement where
	 *                   clause: "UUID = ? AND name = ?"
	 * @param whereVals  - objects to insert into where caluse for prepared
	 *                   statement.
	 */
	public void UpdateVarWhere(String table, String col, Object updatedVal, String where,
			Object... whereVals)
	{
		String sql = MessageFormat.format("UPDATE {0} SET {1}=? WHERE {2}", table, col, where);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setObject(1, updatedVal);
			for (int i = 1; i < whereVals.length + 1; i++)
			{
				stmt.setObject(i + 1, whereVals[i]);
			}
			int count = stmt.executeUpdate();
			if (m_debug)
				Log("[ SelectVar ] Updates: ", count);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void Update(String table, DBUpdate columns)
	{
		String SET = DBUtils.KVPrepare(columns.updates);
		String WHERE = DBUtils.KVPrepare(columns.wheres);
		String sql = MessageFormat.format("UPDATE {0} SET {1} WHERE {2}", table, SET, WHERE);

		try
		{
			stmt = c.prepareStatement(sql);

			int updatesLength = columns.updates.length;
			for (int i = 0; i < updatesLength; i++)
				stmt.setObject(i + 1, columns.updates[i].colVal);

			for (int i = updatesLength; i < columns.wheres.length + updatesLength; i++)
				stmt.setObject(i + 1, columns.wheres[i].colVal);

			stmt.executeUpdate();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	/*-
	 * ************************
	 * * SQL Delete functions *
	 * ************************
	 */

	public void Delete(String table, String key)
	{
		String sql = MessageFormat.format("DELETE FROM {0} where UUID = ?", table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.executeUpdate();
			if (m_debug)
				Log("[ Delete ] Deleted player", key);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void Delete(String table, DBUpdate columns)
	{
		String sql = MessageFormat
				.format("DELETE FROM {0} WHERE {1}", table, DBUtils.KVPrepare(columns.wheres));

		try
		{
			stmt = c.prepareStatement(sql);

			for (int i = 0; i < columns.wheres.length; i++)
				stmt.setObject(i + 1, columns.wheres[i].colVal);

			stmt.executeUpdate();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	private void Log(Object... msg)
	{
		LogCraft.Log(msg);
	}
}
