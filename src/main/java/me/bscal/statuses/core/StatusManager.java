package me.bscal.statuses.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.bscal.statuses.Statuses;

public class StatusManager
{

	Map<Player, StatusPlayer> players = new HashMap<Player, StatusPlayer>();

	public StatusManager()
	{

	}

	public void StartRunnable ()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Statuses.Get(), new Runnable()
		{

			@Override
			public void run ()
			{
				for (var pair : players.entrySet())
				{
					if (pair.getKey().isOnline()) {
						pair.getValue().OnTick(Bukkit.getCurrentTick());
					}
				}
			}

		}, 0L, 1L);
	}

}
