package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;

public abstract class PassiveEffect extends StatusEffect
{

	public void OnInitialize(StatusInstance instance)
	{
		OneTimeEffects(instance);
		ApplyEffect(instance);
	}

	public void OnRespawn(StatusInstance instance)
	{
		ApplyEffect(instance);
	}

	public abstract void OneTimeEffects(StatusInstance instance);

	public abstract void ApplyEffect(StatusInstance instance);

}
