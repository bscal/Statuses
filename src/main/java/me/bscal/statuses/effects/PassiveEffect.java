package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

/**
 * An effect that has no active, onTick, trigger effects but give a passive "buff" or "debuff".
 * Best to override both OnInitialize() (already by default) and OnCleanup() functions to handle
 * any effects to the player.
 */
public abstract class PassiveEffect extends StatusEffect
{

	@Override public void OnInitialize(StatusInstance instance)
	{
		Apply(instance);
	}

	@Override public void OnCleanup(StatusInstance instance)
	{
		Cleanup(instance);
	}

	/**
	 * Used to apply any effects to the current StatusInstance. This by default is called
	 * from OnInitialize()
	 *
	 * @param instance
	 */
	public abstract void Apply(StatusInstance instance);

	/**
	 * Used to cleanup anything with player, status, or server
	 *
	 * @param instance
	 */
	public abstract void Cleanup(StatusInstance instance);

}
