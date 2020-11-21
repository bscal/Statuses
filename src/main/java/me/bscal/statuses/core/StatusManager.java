package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
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
	Map<StatusTrigger, List<StatusBase>> triggerToStatus = new HashMap<StatusTrigger, List<StatusBase>>();
	Map<Class<?>, TreeMap<Integer, List<StatusTrigger>>> eventToTrigger = new HashMap<Class<?>, TreeMap<Integer, List<StatusTrigger>>>();

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

	public void RegisterTrigger(Class<?> event, StatusTrigger trigger)
	{
		if (event == null || trigger == null)
			return;

		if (!event.isInstance(Event.class))
		{
			LogCraft.LogErr("Trigger failed to register. Error: event is not a Bukkit Event.");
			return;
		}

		if (!eventToTrigger.containsKey(event))
		{
			var map = eventToTrigger.put(event, new TreeMap<Integer, List<StatusTrigger>>());
			map.put(trigger.GetWeight(), new ArrayList<StatusTrigger>()).add(trigger);
		}
		else
		{
			var map = eventToTrigger.get(event);
			if (!map.containsKey(trigger.GetWeight()))
				map.put(trigger.GetWeight(), new ArrayList<StatusTrigger>()).add(trigger);
			else
				map.get(trigger.GetWeight()).add(trigger);
		}

	}

	public void Register(StatusBase status, StatusTrigger trigger)
	{
		if (status == null || trigger == null)
			return;

		if (!statuses.contains(status))
			statuses.add(status);

		if (!triggerToStatus.containsKey(trigger))
		{
			triggerToStatus.put(trigger, new ArrayList<StatusBase>());
		}
		else if (triggerToStatus.get(trigger).contains(status))
		{
			LogCraft.LogErr("Failed to register. Error: Status already registered.");
			return;
		}

		triggerToStatus.get(trigger).add(status);

		LogCraft.LogErr("[ ok ] Registering status: ", status.name, trigger.toString());
	}

	public void Trigger(TriggerData data)
	{
		if (!triggerToStatus.containsKey(data.type))
			return;
	}

	public void TriggerPlayer(TriggerPlayerData data)
	{
		if (!triggerToStatus.containsKey(data.type))
			return;

		for (var status : triggerToStatus.get(data.type))
		{
			StatusPlayer sPlayer = players.get(data.GetPlayer());
			sPlayer.AddStatus(status);
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

		StatusTrigger trigger;
	}

	public class TriggerData
	{
		/** Event used by the Trigger */
		public final Event event;
		/** Trigger's type */
		public final TriggerType type;
		/** The Entity that the status is on. */
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
			return (Player) entity;
		}

	}

}
