package me.bscal.statuses.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.storage.DBObject;

public class StatusInstance implements DBObject
{

	public long id;
	public StatusPlayer sPlayer;
	public StatusBase status;
	public String key;

	public float duration;
	public int stackCount;

	public boolean hasStarted;
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

	public StatusInstance(final StatusPlayer sp, final StatusBase status, final float duration, final String key)
	{
		this.id = System.nanoTime();
		this.sPlayer = sp;
		this.status = status;
		this.duration = duration;
		this.key = key;
	}

	@Override public boolean equals(Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof StatusInstance))
			return false;

		StatusInstance otherInst = (StatusInstance) other;
		return this.id == otherInst.id && this.status.equals(otherInst.status) && this.key.equals(otherInst.key);
	}

	@Override public String toString()
	{
		return MessageFormat.format("StatusInstance[{0}::{1}::{2}]", status.name, key, id);
	}

	@Override public String GetColumns()
	{
		return "UUID,status_id,name,key,duration,stacks";
	}

	@Override public Object[] GetValues()
	{
		return new Object[] { sPlayer.player.getUniqueId().toString(), id, status.name, key, duration, stackCount
		};
	}

	@Override public Object ToObject(ResultSet rs)
	{
		try
		{
			id = rs.getLong(2);
			sPlayer.player = Bukkit.getPlayer(UUID.fromString(rs.getString(3)));
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
