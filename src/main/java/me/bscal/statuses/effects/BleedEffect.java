package me.bscal.statuses.effects;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.Particle;
import me.DevTec.TheAPI.Utils.NMS.ParticleColor;
import me.bscal.statuses.core.StatusInstance;

public class BleedEffect extends StatusEffect
{

	@Override
	public void OnTick(int tick, StatusInstance instance)
	{
		instance.player.damage(1);
		instance.player.sendMessage(ChatColor.RED + "You are bleeding...");

		Location loc = instance.player.getLocation();
		Object packet = NMSAPI.getPacketPlayOutWorldParticles(Particle.REDSTONE, (float) loc.getX(), (float) loc.getY(),
				(float) loc.getZ(), 1.0f, 3, new ParticleColor(255, 0, 0));
		NMSAPI.sendPacket(instance.player, packet);
	}
}
