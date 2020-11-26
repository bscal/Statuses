package me.bscal.statuses.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.core.StatusPlayer;

public class SQLAPI
{

	Connection c;
	PreparedStatement stmt;

	private boolean m_debug;
	private boolean m_mysqlEnabled;

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
				String port	= Statuses.Get().getConfig().getString("mysql.port");
				String db	= Statuses.Get().getConfig().getString("mysql.database");
				String user	= Statuses.Get().getConfig().getString("mysql.username");
				String pass	= Statuses.Get().getConfig().getString("mysql.password");
				
				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection(MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", host, port, db), user,
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

				stmt = c.prepareStatement("CREATE TABLE IF NOT EXISTS user_statuses ("
						+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "`status_id` int(11) NOT NULL, "
						+ "`UUID` VARCHAR(64) NOT NULL, " + "`name` VARCHAR(32) NOT NULL, "
						+ "`duration` int(11) NOT NULL, " + "`stacks` int(11) NOT NULL default 0);");
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
			if (c != null || c.isClosed())
				c.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/*-
	 * ************************************* 
	 * * Saving and Loading Status Players *
	 * *************************************
	 */

	public void LoadPlayer(String table, StatusPlayer sp)
	{
		String sql = MessageFormat.format("SELECT * FROM {0} WHERE UUID = ?;", table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, sp.player.getUniqueId().toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				sp.LoadStatus((StatusInstance) new StatusInstance(sp).ToObject(rs));
			}
			if (m_debug)
				Log("[ LoadPlayer ]", sp.player.getName(), rs.getStatement());
			Statuses.Get().GetDB().Delete(table, sp.player);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void SavePlayer(String table, StatusPlayer sp)
	{
		Log(sp.statuses.size());
		for (int i = 0; i < sp.statuses.size(); i++)
		{
			if (sp.statuses.get(i) != null)
				Insert(table, sp.statuses.get(i).GetColumns(), sp.statuses.get(i).GetValues());
		}
		if (m_debug)
			Log("[ SavePlayer ]", sp.player.getName());
	}

	/*-
	 * ************************
	 * * SQL Insert functions *
	 * ************************
	 */

	public void Insert(String table, Player p)
	{
		String sql = MessageFormat.format("INSERT INTO {0} (UUID) VALUES (?);", table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, p.getUniqueId().toString());
			int count = stmt.executeUpdate();
			if (m_debug)
				Log("[ Inserting ] Updates: ", count, table);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Inserts columns into tables with values. The VALUES string will be generated
	 * for you based of vals size.
	 * 
	 * @param table - table name.
	 * @param cols  - column string. Should be sql format. Ie: "UUID, name"
	 * @param vals  - objects to insert. Should be in order.
	 */
	public void Insert(String table, String cols, Object[] vals)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vals.length; i++)
		{
			if (i == vals.length - 1)
				sb.append("?");
			else
				sb.append("?,");
		}
		String sql = MessageFormat.format("INSERT INTO {0} ({1}) VALUES ({2});", table, cols, sb.toString());
		try
		{
			PreparedStatement stmt = c.prepareStatement(sql);
			for (int i = 0; i < vals.length; i++)
			{
				stmt.setObject(i + 1, vals[i]);
			}
			int count = stmt.executeUpdate();
			if (m_debug)
				Log("[ Inserting ] Updates: ", count, table, cols);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
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

			ResultSet rs = stmt.executeQuery();

			return rs;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns ResultSet from the Player's UUID only.
	 * 
	 * @param table  - tables name.
	 * @param column - Column(s) name(s), or "*".
	 * @param p      - Player to search for.
	 */
	public ResultSet SelectVar(String table, String column, Player p)
	{
		String sql = MessageFormat.format("SELECT {0} FROM {1} WHERE UUID = ?;", column, table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, p.getUniqueId().toString());
			ResultSet rs = stmt.executeQuery();
			if (m_debug)
				Log("[ SelectVar ] Size: ", rs.getFetchSize(), " | Var: ", column, " | Player: ", p.getName());
			if (rs.next())
				return rs;
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
	 * Updates value by player uuid.
	 * 
	 * @param table - tables name.
	 * @param col   - column to update. Only supports 1 column.
	 * @param val   - object to update column with.
	 * @param p
	 */
	public void UpdateVar(String table, String col, Object val, Player p)
	{
		String sql = MessageFormat.format("UPDATE {0} SET {1}=? where UUID = ?", table, col);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setObject(1, val);
			stmt.setString(2, p.getUniqueId().toString());
			int count = stmt.executeUpdate();
			if (m_debug)
				Log("[ SelectVar ] Updates: ", count, " | Player: ", p.getName());
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
	public void UpdateVarWhere(String table, String col, Object updatedVal, String where, Object... whereVals)
	{
		String sql = MessageFormat.format("UPDATE {0} SET {1}=? WHERE {2}", table, col, where);
		try
		{
			PreparedStatement stmt = c.prepareStatement(sql);
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

	/*-
	 * ************************ 
	 * * SQL Delete functions *
	 * ************************
	 */

	public void Delete(String table, Player p)
	{
		String sql = MessageFormat.format("DELETE FROM {0} where UUID = ?", table);
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setString(1, p.getUniqueId().toString());
			stmt.executeUpdate();
			if (m_debug)
				Log("[ Delete ] Deleted player", p.getName());
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Deletes rows based on where clause.
	 * 
	 * @param table - table name.
	 * @param where - where clause. As sql prepared statement: "UUID = ? AND name =
	 *              ?"
	 * @param vals  - updated values in order.
	 */
	public void DeleteWhere(String table, String where, Object... vals)
	{
		String sql = MessageFormat.format("DELETE FROM {0} WHERE {1}", table, where);
		try
		{
			stmt = c.prepareStatement(sql);
			for (int i = 0; i < vals.length; i++)
			{
				stmt.setObject(i + 1, vals[i]);
			}
			stmt.executeUpdate();
			if (m_debug)
				Log("[ Delete ] Deleted player", vals);
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void Log(Object... msg)
	{
		LogCraft.Log(msg);
	}
}
