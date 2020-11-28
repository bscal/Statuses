package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageDoneTrigger extends PlayerTrigger
{
	/** Event used by the Trigger */
	protected EntityDamageByEntityEvent m_event;

	public PlayerDamageDoneTrigger()
	{
		super(EntityDamageByEntityEvent.class);
	}

	@Override
	public boolean IsValid()
	{
		return m_event.getDamager() instanceof Player && m_event.getDamage() > 0;
	}

	@Override
	public Entity GetEntity()
	{
		return m_event.getDamager();
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
