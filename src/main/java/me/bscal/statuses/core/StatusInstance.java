package me.bscal.statuses.core;

import java.sql.ResultSet;
import java.sql.SQLException;
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

	public float duration;
	public boolean hasStarted;
	public boolean shouldRemove;
	public int stackCount;

	public StatusInstance(final StatusPlayer p)
	{
		this.sPlayer = p;
	}

	public StatusInstance(final StatusPlayer sp, final StatusBase status)
	{
		this.id = System.currentTimeMillis();
		this.sPlayer = sp;
		this.status = status;
	}

	public StatusInstance(final StatusPlayer sp, final StatusBase status, final float duration)
	{
		this.id = System.currentTimeMillis();
		this.sPlayer = sp;
		this.status = status;
		this.duration = duration;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == null || other == null)
			return false;

		if (!(other instanceof StatusInstance))
			return false;

		return id == ((StatusInstance) other).id && status.equals(((StatusInstance) other).status);
	}

	@Override
	public String GetColumns()
	{
		return "UUID,status_id,name,duration,stacks";
	}

	@Override
	public Object[] GetValues()
	{
		return new Object[]
		{ sPlayer.player.getUniqueId().toString(), id, status.name, duration, stackCount
		};
	}

	@Override
	public Object ToObject(ResultSet rs)
	{
		try
		{
			sPlayer.player = Bukkit.getPlayer(UUID.fromString(rs.getString("UUID")));
			id = rs.getLong("status_id");
			status = Statuses.Get().GetStatusMgr().GetStatus(rs.getString("name"));
			duration = (float) rs.getInt("duration");
			stackCount = rs.getInt("stacks");
			hasStarted = true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return this;

	}

}
