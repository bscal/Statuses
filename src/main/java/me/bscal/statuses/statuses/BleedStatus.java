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
		super("Bleed", StatusGroup.BLEEDS.toString(), 10);
		effects.add(Statuses.Get().GetStatusMgr().GetEffect(BleedEffect.class.getSimpleName()));
	}

	@Override
	public boolean ShouldApply(StatusTrigger trigger, Player p)
	{
		return true;
	}
	
	@Override
	public String GetKey(StatusTrigger trigger, Player p)
	{
		return "";
	}

}
