package me.bscal.statuses.core;

import org.bukkit.entity.Player;

import me.bscal.statuses.statuses.StatusBase;

public class StatusInstance
{
	public final long id;
	public final Player player;
	public final StatusBase status;
	
	public float duration;
	public boolean hasStarted;
	public boolean shouldRemove;
	public int stackCount;

	public StatusInstance(final Player p, final StatusBase status)
	{
		this.id = System.currentTimeMillis();
		this.player = p;
		this.status = status;
	}
	
	public StatusInstance(final Player p, final StatusBase status, final float duration)
	{
		this.id = System.currentTimeMillis();
		this.player = p;
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
		
		return id == ((StatusInstance)other).id && status.equals(((StatusInstance)other).status);
	}
	
	public void HandleStack() {
	
	}
	
}
