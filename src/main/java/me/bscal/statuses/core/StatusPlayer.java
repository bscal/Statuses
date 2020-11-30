package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.bscal.statuses.storage.SQLAPI;
import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;

public class StatusPlayer
{

	public static final int MAX_STATUSES = 63;
	public static final int MAX_INSTANCES = 15;

	public Player player;
	public List<StatusInstance> statuses = new ArrayList<StatusInstance>();
	public Map<StatusBase, List<StatusInstance>> instanceMap = new HashMap<StatusBase, List<StatusInstance>>();

	public StatusPlayer(final Player p)
	{
		player = p;
	}

	public void AddStatus(final StatusBase status, final String key)
	{
		if (status == null || statuses.size() > MAX_STATUSES)
			return;

		if (!instanceMap.containsKey(status))
			instanceMap.put(status, new ArrayList<StatusInstance>());

		var list = instanceMap.get(status);

		if (!list.isEmpty())
		{
			if (status.isStackable) // New instances are stacked on current instance.
			{
				list.get(0).status.HandleStack(list.get(0));
			}
			else if (status.isMultiInstance) // New instances create new instances.
			{
				StatusInstance instance = status.CreateInstance(player);
				if (list.size() > MAX_INSTANCES)
				{
					list.remove(0);
				}
				AddInstance(instance);
			}
			else if (status.shouldAddDuration) // New instances add to current duration.
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
			AddInstance(status.CreateInstance(this, key));
		}
	}

	private void AddInstance(StatusInstance instance)
	{
		statuses.add(instance);
		instanceMap.get(instance.status).add(instance);

		if (instance.status.effects != null && instance.status.effects.size() > 0)
			Statuses.Get().GetStatusMgr().AddTriggerEffect(instance);

		instance.status.OnInitialize(instance);

		if (!instance.hasStarted)
		{
			instance.status.OnStart(instance);
			instance.hasStarted = true;
		}

		if (Statuses.Debug)
			LogCraft.Log("Adding status to:", player.getName(), instance.status.name);
	}

	public void RemoveStatus(StatusInstance instance)
	{
		RemoveStatus(instance, -1);
	}

	public void RemoveStatus(StatusInstance instance, int index)
	{
		if (instance == null)
			return;

		instance.status.OnCleanup(instance);

		if (instance.shouldRemove)
			instance.status.OnEnd(instance);

		if (index == -1)
			statuses.remove(instance);
		else
			statuses.remove(index); // minor optimization

		instanceMap.get(instance.status).remove(instance);
		Statuses.Get().GetStatusMgr().RemoveTriggerEffect(instance);

		if (Statuses.Debug)
			LogCraft.Log("Removing status from:", player.getName(), instance.status.name);

		instance = null;
	}

	public void RemoveAllByStatus(StatusBase status)
	{
		if (status == null)
			return;

		for (int i = statuses.size() - 1; i > -1; i--)
		{
			if (statuses.get(i).status == status)
			{
				RemoveStatus(statuses.get(i), i);
			}
		}
	}

	public void RemoveAll()
	{
		StatusInstance inst = null;
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			if (statuses.get(i) != null)
			{
				inst = statuses.get(i);
				RemoveStatus(inst, i);
				inst.status.OnCleanup(inst);
			}
		}
	}

	public void RemoveAllAndSave(String table)
	{
		StatusInstance inst = null;
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			if (statuses.get(i) != null)
			{
				inst = statuses.get(i);
				SaveInstance(inst, table);
				RemoveStatus(inst, i);
			}
		}
	}

	public void SaveInstance(StatusInstance instance, String table)
	{
		Statuses.Get().GetDB().Insert(table, instance.GetColumns(), instance.GetValues());
	}

	public void Destroy()
	{
		player = null;
		statuses = null;
		instanceMap = null;
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
		if (Statuses.Debug)
			LogCraft.Log("Updating...", player.getName(), "# of ", statuses.size());
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			statuses.get(i).status.OnTick(tick, statuses.get(i));
		}
	}

	public void LoadStatus(StatusInstance instance)
	{
		if (!instanceMap.containsKey(instance.status))
			instanceMap.put(instance.status, new ArrayList<StatusInstance>());

		AddInstance(instance);
	}
}
