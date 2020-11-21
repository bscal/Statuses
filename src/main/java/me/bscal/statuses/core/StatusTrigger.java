package me.bscal.statuses.core;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class StatusTrigger
{

	public abstract boolean IsValid();

	public int GetWeight()
	{
		return 0;
	}

	/**
	 *
	 */

	public abstract class PlayerTrigger extends StatusTrigger
	{
		public final Player player;

		public PlayerTrigger(final Player p)
		{
			player = p;
		}
	}

	public class PlayerDamageDoneTrigger extends PlayerTrigger
	{

		/*** Event used by the Trigger */
		public final EntityDamageByEntityEvent event;

		public PlayerDamageDoneTrigger(final Player p, final EntityDamageByEntityEvent evt)
		{
			super(p);
			event = evt;
		}

		@Override
		public boolean IsValid()
		{
			return event.getDamager() == player && event.getDamage() > 0;
		}
	}
	
	public class PlayerDamageRecievedTrigger extends PlayerTrigger
	{

		/*** Event used by the Trigger */
		public final EntityDamageByEntityEvent event;

		public PlayerDamageRecievedTrigger(final Player p, final EntityDamageByEntityEvent evt)
		{
			super(p);
			event = evt;
		}

		@Override
		public boolean IsValid()
		{
			return event.getEntity() == player && event.getDamage() > 0;
		}
	}

}
