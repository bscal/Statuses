package me.bscal.statuses.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

import me.bscal.statuses.storage.DBKeyValue;
import org.bukkit.Bukkit;

import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.storage.DBObject;
import org.bukkit.entity.Player;

public class StatusInstance implements DBObject
{

	/**
	 * Usually the nanoTime() of when the instance was create
	 */
	public long id;
	/**
	 * Contains info on the Player
	 */
	public StatusPlayer sPlayer;
	/**
	 * The status this instances was created from
	 */
	public StatusBase status;
	/**
	 * A unique key. Defaults to an empty string if unused
	 */
	public String key;

	public float duration;
	public int stackCount;

	public boolean hasStarted;
	/**
	 * Used to mark if the status should be removed naturally.
	 */
	public boolean shouldRemove;

	public StatusInstance(final StatusPlayer p)
	{
		this.sPlayer = p;
	}

	public StatusInstance(final StatusPlayer sp, final StatusBase status)
	{
		this(sp, status, status.baseDuration);
	}

	public StatusInstance(final StatusPlayer sp, final StatusBase status, final float duration)
	{
		this(sp, status, duration, "");
	}

	public StatusInstance(final StatusPlayer sp, final StatusBase status, final float duration,
			final String key)
	{
		this.id = System.nanoTime();
		this.sPlayer = sp;
		this.status = status;
		this.duration = duration;
		this.key = key;
	}

	public Player GetPlayer()
	{
		return sPlayer.player;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof StatusInstance))
			return false;

		StatusInstance otherInst = (StatusInstance) other;
		return this.id == otherInst.id && this.status.equals(otherInst.status) && this.key
				.equals(otherInst.key);
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("StatusInstance[{0}::{1}::{2}]", status.name, key, id);
	}

	@Override
	public DBKeyValue[] GetColumns()
	{
		return new DBKeyValue[] { new DBKeyValue("UUID", sPlayer.player.getUniqueId().toString()),
				new DBKeyValue("status_id", id), new DBKeyValue("name", status.name),
				new DBKeyValue("key", key), new DBKeyValue("duration", duration),
				new DBKeyValue("stacks", stackCount),
		};
	}

	;

	@Override
	public Object ToObject(ResultSet rs)
	{
		try
		{
			id = rs.getLong(2);
			//sPlayer.player = Bukkit.getPlayer(UUID.fromString(rs.getString(3)));
			status = Statuses.Get().GetStatusMgr().GetStatus(rs.getString(4));
			key = rs.getString(5);
			duration = rs.getInt(6);
			stackCount = rs.getInt(7);
			hasStarted = true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return this;

	}

}
