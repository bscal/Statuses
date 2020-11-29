package me.bscal.statuses.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * You should usually always extend from PlayerTrigger. However nothing special just some
 * helpful functions dealing with Player objects.
 */
public abstract class PlayerTrigger extends StatusTrigger
{
	public PlayerTrigger(Class<? extends Event> eventClass)
	{
		super(eventClass);
	}

	public Player GetPlayer()
	{
		return (Player) GetEntity();
	}

	public boolean IsPlayer() { return GetEntity() instanceof Player; }
}