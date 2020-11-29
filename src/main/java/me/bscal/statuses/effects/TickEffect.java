package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

/**
 * An effect that is called everytime a status is updated. This by default is not every server tick
 * but rather a X amount of ticks specified in the Status.
 */
public abstract class TickEffect extends StatusEffect
{

	/**
	 * Called on every status update tick specified by the status
	 *
	 * @param tick - Current server tick
	 * @param instance
	 */
	public abstract void OnTick(int tick, StatusInstance instance);

}
