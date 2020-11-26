package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.triggers.PlayerTrigger;
import me.bscal.statuses.triggers.StatusTrigger;

public class StatusManager implements Listener
{

	Map<Player, StatusPlayer> players = new HashMap<Player, StatusPlayer>();

	List<StatusBase> statuses = new ArrayList<StatusBase>();
	List<StatusTrigger> triggers = new ArrayList<StatusTrigger>();
	Map<StatusTrigger, List<StatusBase>> triggerToStatus = new HashMap<StatusTrigger, List<StatusBase>>();
	Map<Class<? extends Event>, TreeMap<Integer, List<StatusTrigger>>> eventToTrigger = new HashMap<Class<? extends Event>, TreeMap<Integer, List<StatusTrigger>>>();

	public void StartRunnable()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Statuses.Get(), new Runnable()
		{
			@Override
			public void run()
			{
				for (var pair : players.entrySet())
				{
					pair.getValue().OnTick(Bukkit.getCurrentTick());
				}
			}

		}, 0L, Math.max(1, Statuses.Get().getConfig().getLong("TicksPerUpdate")));
	}

	public void AddPlayer(StatusPlayer sPlayer)
	{
		if (players.containsKey(sPlayer.player))
			return;

		players.put(sPlayer.player, sPlayer);
	}

	public void RemovePlayer(StatusPlayer sPlayer)
	{
		players.remove(sPlayer.player);
	}

	public StatusPlayer GetPlayer(Player p)
	{
		return players.get(p);
	}

	public StatusTrigger RegisterTrigger(StatusTrigger trigger)
	{
		Class<? extends Event> event = trigger.eventClass;

		if (!eventToTrigger.containsKey(event))
		{
			eventToTrigger.put(event, new TreeMap<Integer, List<StatusTrigger>>());
			var map = eventToTrigger.get(event);
			map.put(trigger.GetWeight(), new ArrayList<StatusTrigger>());
			var list = map.get(trigger.GetWeight());
			list.add(trigger);
		}
		else
		{
			var map = eventToTrigger.get(event);
			if (!map.containsKey(trigger.GetWeight()))
				map.put(trigger.GetWeight(), new ArrayList<StatusTrigger>()).add(trigger);
			else
				map.get(trigger.GetWeight()).add(trigger);
		}
		triggers.add(trigger);

		LogCraft.LogErr("[ ok ] Registering trigger: ", event.getSimpleName(), trigger.getClass().getSimpleName());

		return trigger;
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

		LogCraft.LogErr("[ ok ] Registering status: ", status.name, trigger.getClass().getSimpleName());
	}

	public StatusBase GetStatus(String name)
	{
		for (int i = 0; i < statuses.size(); i++)
		{
			if (statuses.get(i).name.equalsIgnoreCase(name))
				return statuses.get(i);
		}
		return null;
	}

	public StatusTrigger GetTrigger(String name)
	{
		for (int i = 0; i < statuses.size(); i++)
		{
			if (triggers.get(i).name.equalsIgnoreCase(name))
				return triggers.get(i);
		}
		return null;
	}

	public int TriggerCount()
	{
		return triggers.size();
	}

	public int StatusCount()
	{
		return statuses.size();
	}

	public void TriggerPlayer(PlayerTrigger trigger, Player p)
	{
		for (var status : triggerToStatus.get(trigger))
		{
			if (status.ShouldApply(trigger, p))
			{
				StatusPlayer sPlayer = players.get(p);
				sPlayer.AddStatus(status);
			}
		}
	}

	@EventHandler
	public void OnJoin(PlayerJoinEvent e)
	{
		StatusPlayer sPlayer = new StatusPlayer(e.getPlayer());
		Statuses.Get().GetDB().LoadPlayer("user_statuses", sPlayer);
		AddPlayer(sPlayer);
	}

	@EventHandler
	public void OnExit(PlayerQuitEvent e)
	{
		StatusPlayer sPlayer = players.get(e.getPlayer());
		Statuses.Get().GetDB().SavePlayer("user_statuses", sPlayer);
		RemovePlayer(sPlayer);
	}

	@EventHandler
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
						TriggerPlayer((PlayerTrigger) trig, ((PlayerTrigger) trig).GetPlayer());
				}
			}
		}
	}
}
