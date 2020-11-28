package me.bscal.statuses.statuses;

import java.util.ArrayList;
import java.util.List;

import me.bscal.statuses.effects.TickEffect;
import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.core.StatusPlayer;
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
	public static final float TICKS_PER_SECOND = 20;

	public final String name;
	public final StatusGroup group;
	public final List<StatusEffect> effects = new ArrayList<>();
	public final List<StatusTrigger> triggers = new ArrayList<>();

	/** Number of ticks to wait before an Update Tick will occur */
	public final int ticksPerUpdate;
	/** The duration in seconds of the status */
	public final float baseDuration;

	/** Should the status be removed on death */
	public boolean shouldRemoveOnDeath;
	/** Should the status be saved after logging off */
	public boolean isPersistent;
	/** Does the Entity need to be alive for ticks to happen */
	public boolean shouldBeAliveToTick;
	/** Can the status stack */
	public boolean isStackable;
	/** Can the status have multiple instances */
	public boolean isMultiInstance;
	/** Should the duration be added if status is single instance */
	public boolean shouldAddDuration;
	/** Should multiple instances with the same key be allowed */
	public boolean multiInstanceKeys;

	/**
	 * The max duration in seconds if <code>shouldAddDuration</code> is true. -1 is
	 * no max duration
	 */
	public float maxDuration = NO_MAX_DURATION;

	public StatusBase(final String name, final StatusGroup group, final float duration)
	{
		this(name, group, duration, (int) TICKS_PER_SECOND);
	}

	public StatusBase(final String name, final StatusGroup group, final float duration, final int ticksPerUpdate)
	{
		this.name = name;
		this.group = group;
		this.baseDuration = duration;
		this.ticksPerUpdate = ticksPerUpdate;
	}

	public StatusInstance CreateInstance(Player p)
	{
		return CreateInstance(Statuses.Get().GetStatusMgr().GetPlayer(p));
	}

	public StatusInstance CreateInstance(StatusPlayer sPlayer)
	{
		return new StatusInstance(sPlayer, this, baseDuration, "");
	}
	
	public StatusInstance CreateInstance(StatusPlayer sPlayer, String key)
	{
		return new StatusInstance(sPlayer, this, baseDuration, key);
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
		if (instance.hasStarted && tick % ticksPerUpdate == 0 && instance.sPlayer.player.isOnline()
				&& !instance.sPlayer.player.isDead())
		{

			if (Statuses.Debug)
			{
				LogCraft.Log("Status ", instance.status.name, " updating...");
			}

			instance.duration -= TICKS_PER_SECOND / ticksPerUpdate;

			if (instance.shouldRemove)
			{
				instance.sPlayer.RemoveStatus(instance);
				return false;
			}

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
		if (InternalTick(tick, instance))
		{
			effects.forEach((effect) ->
			{
				if (effect instanceof TickEffect)
					((TickEffect) effect).OnTick(tick, instance);
			});
		}

	}

	@Override
	public boolean equals(Object other)
	{
		if (this == null || other == null || !(other instanceof StatusBase))
			return false;

		return name.equals(((StatusBase)other).name);
	}

	public abstract boolean ShouldApply(StatusTrigger trigger, Player p);

	public abstract String GetKey(StatusTrigger trigger, Player p);

	public void HandleStack(StatusInstance statusInstance)
	{
	}

}
