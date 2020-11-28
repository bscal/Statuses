package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public abstract class StatusTrigger
{

	/**
	 * Class of the event to use
	 */
	public final Class<? extends Event> eventClass;
	public final String name;

	public StatusTrigger(Class<? extends Event> eventClass)
	{
		this.name = this.getClass().getSimpleName();
		this.eventClass = eventClass;
	}

	/**
	 * Check to see if the event passed is valid for this Trigger. If the event is
	 * valid will return true and activate this trigger.
	 */
	public abstract boolean IsValid();

	/**
	 * Returns the Entity involved in this Trigger. Should be called after
	 * IsValid().
	 */
	public abstract Entity GetEntity();

	/**
	 * Returns the Event object that is tied to the Trigger.
	 */
	public abstract Event GetEvent();

	/**
	 * Sets the event.
	 */
	public abstract void SetEvent(Event e);

	public boolean DoesExtendEvent(Event e)
	{
		return eventClass.isInstance(e);
	}

	/**
	 * Returns the weight of the given trigger, lowering the weight will cause the
	 * trigger to be checked before other triggers
	 */
	public int GetWeight()
	{
		return 0;
	}
}
