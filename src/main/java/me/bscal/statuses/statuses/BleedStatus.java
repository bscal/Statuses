package me.bscal.statuses.statuses;

import org.bukkit.entity.Player;

import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.effects.BleedEffect;
import me.bscal.statuses.triggers.StatusTrigger;

public class BleedStatus extends StatusBase
{

	public BleedStatus()
	{
		super("Bleed", StatusGroup.BLEEDS);
		effects.add(new BleedEffect());
	}

	@Override
	public boolean ShouldApply(StatusTrigger trigger, Player p)
	{
		if (Statuses.Debug)
			LogCraft.Log("Trigger ShouldApply: ", trigger.GetEvent().getEventName());
		return true;
	}

}
