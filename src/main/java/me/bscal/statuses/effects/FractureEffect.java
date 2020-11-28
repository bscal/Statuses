package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.triggers.StatusTrigger;
import org.bukkit.event.entity.EntityDamageEvent;

public class FractureEffect extends TriggerEffect
{
	@Override public void OnTrigger(StatusInstance instance, StatusTrigger trigger)
	{
		EntityDamageEvent e = (EntityDamageEvent) trigger.GetEvent();
		if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
		{
			e.getEntity().sendMessage("Fracture Hurt");
		}
	}
}
