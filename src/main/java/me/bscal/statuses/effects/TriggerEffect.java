package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.triggers.StatusTrigger;

/**
 * An effect that is linked to 1 or more triggers. Triggers are located within the status's
 * triggers list and are not tied per instance but per status.
 * TriggerEffects are called BEFORE statuses are applied.
 */
public abstract class TriggerEffect extends StatusEffect
{

	/**
	 * Called whenever any trigger from the status's trigger list is triggered
	 * @param instance
	 * @param trigger
	 */
	public abstract void OnTrigger(StatusInstance instance, StatusTrigger trigger);

}
