package me.bscal.statuses.triggers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageDoneTrigger extends PlayerTrigger
{
	/** Event used by the Trigger */
	public EntityDamageByEntityEvent event;

	public PlayerDamageDoneTrigger()
	{
		super(EntityDamageByEntityEvent.class);
	}

	@Override
	public boolean IsValid(Event evt)
	{
		if (!evt.getClass().isInstance(eventClass))
			return false;

		event = (EntityDamageByEntityEvent) evt;
		return event.getDamager() instanceof Player && event.getDamage() > 0;
	}

	@Override
	public Entity GetEntity()
	{
		return event.getDamager();
	}
}
