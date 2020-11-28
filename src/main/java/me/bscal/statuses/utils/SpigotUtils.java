package me.bscal.statuses.utils;

import org.bukkit.entity.LivingEntity;

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

}
