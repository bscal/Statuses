package me.bscal.statuses.effects;

import org.bukkit.ChatColor;

import me.DevTec.TheAPI.ParticlesAPI.Particle;
import me.DevTec.TheAPI.ParticlesAPI.ParticleAPI;
import me.DevTec.TheAPI.Utils.Position;
import me.bscal.statuses.core.StatusInstance;

public class BleedEffect extends StatusEffect
{

	@Override
	public void OnTick(int tick, StatusInstance instance)
	{
		instance.sPlayer.player.damage(1);
		instance.sPlayer.player.sendMessage(ChatColor.RED + "You are bleeding...");
		
		Position pos = new Position(instance.sPlayer.player.getLocation());
		Particle particle = new Particle("w");
		ParticleAPI.spawnParticle(instance.sPlayer.player, particle, pos);
	}
}
