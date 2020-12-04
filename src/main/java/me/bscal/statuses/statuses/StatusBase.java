package me.bscal.statuses.statuses;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.effects.TickEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.core.StatusPlayer;
import me.bscal.statuses.effects.StatusEffect;
import me.bscal.statuses.triggers.StatusTrigger;
import org.bukkit.inventory.ItemStack;

/***
 *
 * The base class for all Statuses. You will need to define an individual status once and register it with
 * the StatusManager. Statuses will create a unique StatusInstance with all data needed. StatusInstances will be
 * passed to the StatusEffects which contain the logic.
 *
 * When you register a Status you will need to link it with 1 or more StatusTriggers. This will, when a valid
 * trigger is called, check the Status's ShouldApply() function. If true the status will be applied to the player.
 */
public abstract class StatusBase
{

	public static final float NO_MAX_DURATION = -1.0f;
	public static final float TICKS_PER_SECOND = 20;

	public final String name;
	public final String group;
	public final List<StatusEffect> effects = new ArrayList<>();
	public final List<StatusTrigger> triggers = new ArrayList<>();

	public String desc;
	public ItemStack icon;

	/**
	 * Number of ticks to wait before an Update Tick will occur
	 */
	public final int ticksPerUpdate;
	/**
	 * The duration in seconds of the status
	 */
	public final float baseDuration;

	/**
	 * Should the status be removed on death
	 */
	public boolean shouldRemoveOnDeath = true;
	/**
	 * Should the status be saved after logging off
	 */
	public boolean isPersistent = true;
	/**
	 * Does the Entity need to be alive for ticks to happen
	 */
	public boolean shouldBeAlive = true;
	/**
	 * Can the status stack
	 */
	public boolean isStackable = false;
	/**
	 * Can the status have multiple instances
	 */
	public boolean isMultiInstance = false;
	/**
	 * Should the duration be added if status is single instance
	 */
	public boolean shouldAddDuration = false;
	/**
	 * Should multiple instances with the same key be allowed
	 */
	public boolean noMatchingKeys = true;
	/**
	 * If true, then every trigger that registered will proc even if handled("applied")
	 */
	public boolean procAllTriggers;

	/**
	 * The max duration in seconds if <code>shouldAddDuration</code> is true. -1 is
	 * no max duration
	 */
	public float maxDuration = NO_MAX_DURATION;

	public StatusBase(final String name, final String group, final float duration)
	{
		this(name, group, duration, (int) TICKS_PER_SECOND);
	}

	public StatusBase(final String name, final String group, final float duration, final int ticksPerUpdate)
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
		if (instance.hasStarted && tick % ticksPerUpdate == 0 && instance.sPlayer.player.isOnline())
		{
			if (shouldBeAlive && !instance.sPlayer.player.isDead())
				return false;

			if (Statuses.Debug)
				LogCraft.Log("Status ", instance.status.name, " updating...");

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

	public void OnInitialize(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnInitialized called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnInitialize(instance));
	}

	public void OnCleanup(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnCleanup called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnCleanup(instance));
	}

	public void OnStart(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnStart called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnStart(instance));
	}

	public void OnEnd(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnEnd called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnEnd(instance));

	}

	public void OnDeath(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnDeath called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnDeath(instance));
	}

	public void OnRespawn(StatusInstance instance)
	{
		if (LogLevel.Is(LogLevel.DEVELOPER))
			LogCraft.Log("OnRespawn called. Status", instance.status.name);

		effects.forEach((effect) -> effect.OnRespawn(instance));
	}

	public void OnTick(int tick, StatusInstance instance)
	{
		if (InternalTick(tick, instance))
		{
			effects.forEach((effect) -> {
				if (effect instanceof TickEffect)
					((TickEffect) effect).OnTick(tick, instance);
			});
		}

	}

	public void HandleStack(StatusInstance statusInstance)
	{
	}

	public void CreateItemStack()
	{
		icon = new ItemStack(Material.AIR);
	}

	@Override public boolean equals(Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof StatusBase))
			return false;

		return this.name.equals(((StatusBase) other).name);
	}

	@Override public String toString()
	{
		return MessageFormat.format("Status[{0}::{1}] - Ticks({2}), Dur({3}), effects({4}), trigs({5})", name, group,
				ticksPerUpdate, baseDuration, effects.size(), triggers.size());
	}

	public abstract boolean ShouldApply(StatusTrigger trigger, Player p);

	public abstract String GetKey(StatusTrigger trigger, Player p);

}
