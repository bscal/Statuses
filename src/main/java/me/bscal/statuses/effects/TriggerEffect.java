package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.triggers.StatusTrigger;

public abstract class TriggerEffect extends StatusEffect
{

	public abstract void OnTrigger(StatusInstance instance, StatusTrigger trigger);

}
