package me.bscal.statuses.core;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class StatusTrigger
{

	public abstract boolean IsValid();

	public abstract Entity GetEntity();

	public int GetWeight()
	{
		return 0;
	}

	/**
	 *
	 */

	public abstract class PlayerTrigger extends StatusTrigger
	{

		public Player GetPlayer()
		{
			return (Player) GetEntity();
		}

	}

	public class PlayerDamageDoneTrigger extends PlayerTrigger
	{

		/*** Event used by the Trigger */
		public final EntityDamageByEntityEvent event;

		public PlayerDamageDoneTrigger(final EntityDamageByEntityEvent evt)
		{
			event = evt;
		}

		@Override
		public boolean IsValid()
		{
			return event.getDamager() instanceof Player && event.getDamage() > 0;
		}

		@Override
		public Entity GetEntity()
		{
			return (Player) event.getDamager();
		}
	}

	public class PlayerDamageRecievedTrigger extends PlayerTrigger
	{

		/*** Event used by the Trigger */
		public final EntityDamageByEntityEvent event;

		public PlayerDamageRecievedTrigger(final EntityDamageByEntityEvent evt)
		{
			event = evt;
		}

		@Override
		public boolean IsValid()
		{
			return event.getEntity() instanceof Player && event.getDamage() > 0;
		}

		@Override
		public Entity GetEntity()
		{
			return event.getEntity();
		}
	}

}
