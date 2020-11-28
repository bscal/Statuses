package me.bscal.statuses.effects;

import me.bscal.statuses.utils.SpigotUtils;
import org.bukkit.ChatColor;

import me.DevTec.TheAPI.ParticlesAPI.Particle;
import me.DevTec.TheAPI.ParticlesAPI.ParticleAPI;
import me.DevTec.TheAPI.ParticlesAPI.ParticleData;
import me.DevTec.TheAPI.Utils.Position;
import me.bscal.statuses.core.StatusInstance;

public class BleedEffect extends TickEffect
{

	@Override
	public void OnTick(int tick, StatusInstance instance)
	{
		SpigotUtils.Damage(instance.sPlayer.player, 1);
		instance.sPlayer.player.sendMessage(ChatColor.RED + "You are bleeding...");
		
		Position pos = new Position(instance.sPlayer.player.getEyeLocation());
		Particle particle = new Particle("o", new ParticleData.RedstoneOptions(1, 255, 0, 0));
		ParticleAPI.spawnParticle(instance.sPlayer.player, particle, pos);
	}
}
