package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

/**
 * StatusEffects are logic that cause Statuses to actually do anything. A status can have multiple types
 * of effects. Effects "usually" do not include data or fields other then constants because they ARE tied to
 * instances of a Status. However all functions have a StatusInstance parameter that should include the
 * data on the player, status, and any of its data linked to that instance like duration.
 */
public abstract class StatusEffect
{

	/**
	 * Call whenever the Status is "applied" or "reapplied". This can be called multiple times,
	 * for example when a status is applied, loaded, respawn...
	 *
	 * @param instance
	 */
	public void OnInitialize(StatusInstance instance)
	{
	}

	/**
	 * Called whenever a status needs to be cleaned. When a status is saved this on a disconnect.
	 *
	 * @param instance
	 */
	public void OnCleanup(StatusInstance instance)
	{
	}

	/**
	 * Called once when the Status is applied onto the target for the 1st time.
	 *
	 * @param instance
	 */
	public void OnStart(StatusInstance instance)
	{
	}

	/**
	 * Called once when the status expires, either from durations hitting 0 or forced removal.
	 *
	 * @param instance
	 */
	public void OnEnd(StatusInstance instance)
	{
	}

	/**
	 * Called on death
	 *
	 * @param instance
	 */
	public void OnDeath(StatusInstance instance)
	{
	}

	/**
	 * Called on respawn
	 *
	 * @param instance
	 */
	public void OnRespawn(StatusInstance instance)
	{
	}

}
