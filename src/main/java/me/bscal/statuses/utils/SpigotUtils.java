package me.bscal.statuses.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.io.IOException;

public class SpigotUtils
{

	/***
	 * Damages the Entity without raising an Event.
	 * @param entity
	 * @param damage
	 */
	public static void Damage(LivingEntity entity, double damage)
	{
		if (entity.isDead())
			return;

		double finalDamage = entity.getHealth() - damage;
		entity.setHealth(Math.max(finalDamage, 0));
	}

	public static FileConfiguration CreateConfig(final String path)
	{
		try
		{
			FileConfiguration config = new YamlConfiguration();
			config.load(new File(path));
			return config;
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
