package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class StatusPlayer
{

	public Player player;
	public List<StatusInstance> statuses = new ArrayList<StatusInstance>();
	public Map<StatusBase, List<StatusInstance>> instanceMap = new HashMap<StatusBase, List<StatusInstance>>();

	public StatusPlayer(Player p)
	{
		player = p;
	}

	public void AddStatus(StatusBase status, StatusInstance instance)
	{
		if (status == null || instance == null || statuses.size() > 63)
			return;

		if (!instanceMap.containsKey(status))
			instanceMap.put(status, new ArrayList<StatusInstance>());

		var list = instanceMap.get(status); 
		if (list.size() > 15)
			list.add(0, instance);
		else
			list.add(instance);
	}

	public void RemoveStatus(StatusInstance status)
	{
		if (status == null)
			return;

		statuses.remove(status);
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
