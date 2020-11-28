package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageRecievedTrigger extends PlayerTrigger
{
	/*** Event used by the Trigger */
	public EntityDamageByEntityEvent m_event;

	public PlayerDamageRecievedTrigger()
	{
		super(EntityDamageByEntityEvent.class);
	}

	@Override
	public boolean IsValid()
	{
		return m_event.getEntity() instanceof Player && m_event.getDamage() > 0;
	}

	@Override
	public Entity GetEntity()
	{
		return m_event.getEntity();
	}

	@Override
	public Event GetEvent()
	{
		return m_event;
	}

	@Override public void SetEvent(Event e)
	{
		m_event = (EntityDamageByEntityEvent) e;
	}
}