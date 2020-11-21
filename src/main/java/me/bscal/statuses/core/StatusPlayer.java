package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class StatusPlayer
{

	public static final int MAX_STATUSES = 63;
	public static final int MAX_INSTANCES = 15;
	
	public Player player;
	public List<StatusInstance> statuses = new ArrayList<StatusInstance>();
	public Map<StatusBase, List<StatusInstance>> instanceMap = new HashMap<StatusBase, List<StatusInstance>>();

	public StatusPlayer(Player p)
	{
		player = p;
	}

	public void AddStatus(StatusBase status)
	{
		if (status == null || statuses.size() > MAX_STATUSES)
			return;

		if (!instanceMap.containsKey(status))
			instanceMap.put(status, new ArrayList<StatusInstance>());

		var list = instanceMap.get(status);

		if (list.isEmpty())
		{
			if (status.stackable) // New instances are stacked on current instance.
			{
				list.get(0).HandleStack();
			}
			else if (status.multiInstance) // New instances create new instances.
			{
				StatusInstance instance = status.CreateInstance(player);
				if (list.size() > MAX_INSTANCES)
				{
					list.remove(0);
					list.add(instance);
				}
				else
					list.add(instance);
				statuses.add(instance);
				status.OnStart(instance);
				instance.hasStarted = true;
			}
			else if (status.addDuration) // New instances add to current duration.
			{
				if (status.maxDuration == StatusBase.NO_MAX_DURATION)
					list.get(0).duration += status.baseDuration;
				else
					list.get(0).duration = Math.min(status.maxDuration, list.get(0).duration + status.baseDuration);
			}
			else // New instances replaces current instance.
			{
				list.get(0).duration = status.baseDuration;
			}

		}
		else
		{
			StatusInstance instance = status.CreateInstance(player);
			list.add(instance);
			statuses.add(instance);
			status.OnStart(instance);
			instance.hasStarted = true;
		}
	}

	public void RemoveStatus(StatusInstance instance)
	{
		if (instance == null || !instance.hasStarted)
			return;

		instance.status.OnEnd(instance);
		statuses.remove(instance);
		instanceMap.get(instance.status).remove(instance);
	}

	public void RemoveAll(StatusBase status)
	{
		if (status == null)
			return;

		for (int i = statuses.size() - 1; i > -1; i++)
		{
			if (statuses.get(i).status == status)
			{
				status.OnEnd(statuses.get(i));
				statuses.remove(i);
			}
		}

		instanceMap.get(status).clear();
	}

	public StatusInstance[] FindInstances(StatusBase status)
	{
		if (status == null || !instanceMap.containsKey(status))
		{
			return null;
		}

		return (StatusInstance[]) instanceMap.get(status).toArray();
	}

	public void OnTick(int tick)
	{
		statuses.forEach((status) ->
		{
			if (status.hasStarted && !status.shouldRemove && status.status.wait % 20 == 0)
			{
				status.status.OnTick(tick, status);
			}
		});
	}

}
