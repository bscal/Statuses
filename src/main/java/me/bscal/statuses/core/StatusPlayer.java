package me.bscal.statuses.core;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.storage.BukkitSQLAPI;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StatusPlayer
{

	public static final int MAX_STATUSES = 63;
	public static final int MAX_INSTANCES = 15;

	public final Player player;
	public final List<StatusInstance> statuses = new ArrayList<>();
	public final Map<StatusBase, List<StatusInstance>> instanceMap = new HashMap<>();

	public StatusPlayer(final Player p)
	{
		player = p;
	}

	public void AddStatus(final StatusBase status, final String key)
	{
		if (status == null || statuses.size() > MAX_STATUSES)
			return;

		if (!instanceMap.containsKey(status))
			instanceMap.put(status, new ArrayList<>());

		var list = instanceMap.get(status);

		if (!list.isEmpty())
		{
			if (status.isStackable) // New instances are stacked on current instance.
			{
				status.HandleStack(list.get(0));
			}
			else if (status.isMultiInstance) // New instances create new instances.
			{
				if (status.noMatchingKeys)
				{
					for (StatusInstance inst : list)
					{
						if (inst.key.equals(key))
							return;
					}
				}

				if (list.size() > MAX_INSTANCES)
				{
					list.remove(0);
				}
				AddInstance(status.CreateInstance(this, key));
			}
			else if (status.shouldAddDuration) // New instances add to current duration.
			{
				StatusInstance inst = list.get(0);
				if (status.maxDuration == StatusBase.NO_MAX_DURATION)
					inst.duration += status.baseDuration;
				else
					inst.duration = Math.min(status.maxDuration, inst.duration + status.baseDuration);
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

	private void AddInstance(final StatusInstance instance)
	{
		statuses.add(instance);
		instanceMap.get(instance.status).add(instance);

		if (instance.status.effects.size() > 0)
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

	public void RemoveStatus(final StatusInstance instance)
	{
		RemoveStatus(instance, -1);
	}

	public void RemoveStatus(StatusInstance instance, final int index)
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

	public void RemoveAllByStatus(final StatusBase status)
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

	public void RemoveAll(final boolean triggerCleanup)
	{
		StatusInstance inst;
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			if (statuses.get(i) != null)
			{
				inst = statuses.get(i);
				RemoveStatus(inst, i);
				if (triggerCleanup)
					inst.status.OnCleanup(inst);
			}
		}
	}

	public void RemoveAllAndSave(final String table)
	{
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			StatusInstance inst = statuses.get(i);
			if (inst != null && inst.status.isPersistent)
			{
				inst = statuses.get(i);
				BukkitSQLAPI.SaveInstance(inst, table);
				RemoveStatus(inst, i);
			}
		}
	}

	public void Destroy()
	{
		instanceMap.clear();
		statuses.clear();
	}

	public StatusInstance FindFirst(final StatusBase status)
	{
		return FindInstances(status).get(0);
	}

	public List<StatusInstance> FindInstances(final StatusBase status)
	{
		if (!instanceMap.containsKey(status))
			return null;

		return instanceMap.get(status);
	}

	public List<StatusInstance> FindByKey(final StatusBase status, final String key)
	{
		if (!instanceMap.containsKey(status))
			return null;

		return instanceMap.get(status).stream().filter(inst -> inst.key.equals(key)).collect(Collectors.toList());
	}

	public void OnTick(final int tick)
	{
		if (Statuses.Debug)
			LogCraft.Log("Updating...", player.getName(), "# of ", statuses.size());
		for (int i = statuses.size() - 1; i > -1; i--)
		{
			statuses.get(i).status.OnTick(tick, statuses.get(i));
		}
	}

	public void LoadStatus(final StatusInstance instance)
	{
		if (!instanceMap.containsKey(instance.status))
			instanceMap.put(instance.status, new ArrayList<>());

		AddInstance(instance);
	}

	public boolean HasStatus(final StatusBase status) {
		return true;
	}


	@Override public String toString() {
		return MessageFormat.format("StatusPlayer:{0}, Size: {1}", player.getName(), statuses.size());
	}
}
