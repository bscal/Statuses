package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

public class BleedEffect extends StatusEffect
{

	@Override
	public void OnTick(int tick, StatusInstance instance)
	{
		instance.player.damage(1);
	}

}
