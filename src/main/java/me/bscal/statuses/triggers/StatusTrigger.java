package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public abstract class StatusTrigger
{

	/** Class of the event to use */
	public final Class<? extends Event> eventClass;

	public StatusTrigger(Class<? extends Event> eventClass)
	{
		this.eventClass = eventClass;
	}

	/**
	 * Check to see if the event passed is valid for this Trigger. If the event is
	 * valid will return true and activate this trigger.
	 */
	public abstract boolean IsValid(Event e);

	/**
	 * Returns the Entity involved in this Trigger. Should be called after
	 * IsValid().
	 */
	public abstract Entity GetEntity();

	public int GetWeight()
	{
		return 0;
	}
}
