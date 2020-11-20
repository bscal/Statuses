package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;

public class StatusManager implements Listener
{

	Map<Player, StatusPlayer> players = new HashMap<Player, StatusPlayer>();

	List<StatusBase> statuses = new ArrayList<StatusBase>();
	Map<TriggerType, List<StatusBase>> statusesByTrigger = new HashMap<TriggerType, List<StatusBase>>();

	public StatusManager()
	{

	}

	public void StartRunnable()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Statuses.Get(), new Runnable()
		{

			@Override
			public void run()
			{
				for (var pair : players.entrySet())
				{
					if (pair.getKey().isOnline())
					{
						pair.getValue().OnTick(Bukkit.getCurrentTick());
					}
				}
			}

		}, 0L, 1L);
	}

	public void Register(StatusBase status, TriggerType trigger)
	{
		if (status == null || trigger == null)
			return;

		if (!statuses.contains(status))
			statuses.add(status);

		if (!statusesByTrigger.containsKey(trigger))
		{
			statusesByTrigger.put(trigger, new ArrayList<StatusBase>());
		}
		else if (statusesByTrigger.get(trigger).contains(status))
		{
			LogCraft.LogErr("Failed to register. Error: Status already registered.");
			return;
		}

		statusesByTrigger.get(trigger).add(status);

		LogCraft.LogErr("[ ok ] Registering status: ", status.name, trigger.toString());
	}

	public void Trigger(TriggerData data)
	{
		if (!statusesByTrigger.containsKey(data.type))
			return;
	}

	public void TriggerPlayer(TriggerPlayerData data)
	{
		if (!statusesByTrigger.containsKey(data.type))
			return;

		for (var status : statusesByTrigger.get(data.type))
		{
			StatusPlayer sPlayer = players.get(data.GetPlayer());
			var instances = sPlayer.FindInstances(status);
		}
	}

	public void OnEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		LivingEntity damager = (LivingEntity) e.getDamager();
		LivingEntity damagee = (LivingEntity) e.getEntity();

		if (damager instanceof Player)
		{
			Trigger(new TriggerData(e, TriggerType.PLAYER_ATTACK, damager));
		}

		if (damagee instanceof Player)
		{
			Trigger(new TriggerData(e, TriggerType.PLAYER_DAMAGE, damagee));
		}
	}

	public static enum TriggerType
	{
		PLAYER_DAMAGE,
		PLAYER_ATTACK;
	}

	public class TriggerData
	{
		/*** Event used by the Trigger */
		public final Event event;
		/*** Trigger's type */
		public final TriggerType type;
		/*** The Entity that the status is on. */
		public final LivingEntity entity;

		public TriggerData(Event e, TriggerType type, LivingEntity entity)
		{
			this.event = e;
			this.type = type;
			this.entity = entity;
		}
	}

	public class TriggerPlayerData extends TriggerData
	{

		public TriggerPlayerData(Event e, TriggerType type, Player entity)
		{
			super(e, type, entity);
		}
		
		public Player GetPlayer()
		{
			return (Player)entity;
		}

	}

}
