package me.bscal.statuses.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PlayerTrigger extends StatusTrigger
{
	public PlayerTrigger(Class<? extends Event> eventClass)
	{
		super(eventClass);
	}

	public Player GetPlayer()
	{
		return (Player) GetEntity();
	}

	public boolean IsPlayer() { return GetEntity() instanceof Player; }
}