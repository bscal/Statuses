package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageRecievedTrigger extends PlayerTrigger
{
	/*** Event used by the Trigger */
	public EntityDamageByEntityEvent event;

	public PlayerDamageRecievedTrigger()
	{
		super(EntityDamageByEntityEvent.class);
	}

	@Override
	public boolean IsValid(Event evt)
	{
		event = (EntityDamageByEntityEvent) evt;
		return event.getEntity() instanceof Player && event.getDamage() > 0;
	}

	@Override
	public Entity GetEntity()
	{
		return event.getEntity();
	}
}