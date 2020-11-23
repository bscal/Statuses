package me.bscal.statuses.statuses;

import java.util.*;

import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.effects.StatusEffect;
import me.bscal.statuses.triggers.StatusTrigger;

/***
 * 
 * The base class for all Statuses.
 *
 */
public abstract class StatusBase
{

	public static final float NO_MAX_DURATION = -1.0f;
	public static final int TICKS_PER_SECOND = 20;

	public final String name;
	public final StatusGroup group;
	public final List<StatusEffect> effects = new ArrayList<StatusEffect>();
	public final List<StatusTrigger> triggers = new ArrayList<StatusTrigger>();

	public int ticksPerUpdate = 20;

	public boolean removeOnDeath;
	public boolean persistent;
	public boolean aliveForDecay;
	public boolean stackable;
	public boolean multiInstance;
	public boolean addDuration;

	public float maxDuration = NO_MAX_DURATION;

	public float baseDuration;

	public StatusBase(final String name, final StatusGroup group)
	{
		this.name = name;
		this.group = group;
	}

	public StatusBase(final String name, final StatusGroup group, final int ticksPerUpdate)
	{
		this.name = name;
		this.group = group;
		this.ticksPerUpdate = ticksPerUpdate;
	}

	public StatusInstance CreateInstance(Player p)
	{
		return new StatusInstance(p, this, baseDuration);
	}

	/**
	 * Handles general info on ticks. Handle duration, should be removed, should
	 * tick. Most cases you will not need to call this.
	 * 
	 * @param tick
	 * @param instance
	 * @return true if OnTick should run, false if not.
	 */
	public boolean InternalTick(int tick, StatusInstance instance)
	{
		if (instance.hasStarted && ticksPerUpdate % TICKS_PER_SECOND == 0 && instance.player.isOnline()
				&& !instance.player.isDead())
		{

			if (Statuses.Debug)
			{
				LogCraft.Log("Status ", instance.status.name, " updating...");
			}

			instance.duration -= ticksPerUpdate;

			if (instance.duration <= 0)
			{
				instance.shouldRemove = true;
			}

			return true;
		}
		return false;
	}

	public void OnStart(StatusInstance instance)
	{
		effects.forEach((effect) ->
		{
			effect.OnStart(instance);
		});
	}

	public void OnEnd(StatusInstance instance)
	{
		effects.forEach((effect) ->
		{
			effect.OnEnd(instance);
		});

	}

	public void OnDeath(StatusInstance instance)
	{
		effects.forEach((effect) ->
		{
			effect.OnDeath(instance);
		});
	}

	public void OnRespawn(StatusInstance instance)
	{
		effects.forEach((effect) ->
		{
			effect.OnRespawn(instance);
		});
	}

	public void OnTick(int tick, StatusInstance instance)
	{
		effects.forEach((effect) ->
		{
			effect.OnTick(tick, instance);
		});
	}

}
