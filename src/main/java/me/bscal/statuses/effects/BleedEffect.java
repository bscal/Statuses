package me.bscal.statuses.effects;

import me.bscal.statuses.core.StatusInstance;
import me.bscal.statuses.utils.SpigotUtils;
import org.bukkit.ChatColor;

public class BleedEffect extends TickEffect
{

	@Override
	public void OnTick(int tick, StatusInstance instance)
	{
		SpigotUtils.Damage(instance.sPlayer.player, 1);
		instance.sPlayer.player.sendMessage(ChatColor.RED + "You are bleeding...");
	}
}
