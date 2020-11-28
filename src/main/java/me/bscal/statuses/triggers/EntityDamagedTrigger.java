package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamagedTrigger extends PlayerTrigger
{

	protected EntityDamageEvent m_event;

	public EntityDamagedTrigger()
	{
		super(EntityDamageEvent.class);
	}

	@Override public boolean IsValid()
	{
		return IsPlayer() && m_event.getDamage() > 0;
	}

	@Override public Entity GetEntity()
	{
		return m_event.getEntity();
	}

	@Override public Event GetEvent()
	{
		return m_event;
	}

	@Override public void SetEvent(Event e)
	{
		m_event = (EntityDamageEvent) e;
	}
}
