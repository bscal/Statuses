package me.bscal.statuses.core;

import org.bukkit.entity.Player;

public class StatusInstance
{
	public final Player player;
	public final StatusBase status;
	
	public float duration;
	public boolean hasStarted;
	public boolean shouldRemove;
	public int stackCount;

	public StatusInstance(final Player p, final StatusBase status)
	{
		this.player = p;
		this.status = status;
	}
}
