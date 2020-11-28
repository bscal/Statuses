package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

public abstract class TickEffect extends StatusEffect
{

	public abstract void OnTick(int tick, StatusInstance instance);

}
