package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.triggers.PlayerTrigger;
import me.bscal.statuses.triggers.StatusTrigger;

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

	public void RegisterTrigger(StatusTrigger trigger)
	{
		if (trigger == null)
			return;
		
		Class<? extends Event> event = trigger.eventClass;

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

	public void TriggerPlayer(PlayerTrigger trigger, Player p)
	{
		for (var status : triggerToStatus.get(trigger))
		{
			StatusPlayer sPlayer = players.get(p);
			sPlayer.AddStatus(status);
		}
	}

	public void OnEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		var map = eventToTrigger.get(e.getClass());

		for (var triggers : map.values())
	{
			for (var trig : triggers)
			{
				if (trig.IsValid(e))
				{
					if (trig instanceof PlayerTrigger)
						TriggerPlayer((PlayerTrigger)trig, ((PlayerTrigger)trig).GetPlayer());
				}
			}
		}
	}

}
