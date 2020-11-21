package me.bscal.statuses.core;

import java.util.*;

import org.bukkit.entity.Player;

/***
 * 
 * The base class for all Statuses.
 *
 */
public abstract class StatusBase
{

	public static final float NO_MAX_DURATION = -1.0f;
	
	public final String name;
	public final int groupID;
	public final List<StatusEffect> effects = new ArrayList<StatusEffect>();
	public final List<StatusTrigger> triggers = new ArrayList<StatusTrigger>();
	
	public int wait = 20;

	public boolean removeOnDeath;
	public boolean persistent;
	public boolean aliveForDecay;
	public boolean stackable;
	public boolean multiInstance;
	public boolean addDuration;
	
	public float maxDuration = NO_MAX_DURATION;
	
	public float baseDuration;

	public StatusBase(final String name, final Player player, final int groupID)
	{
		this.name = name;
		this.groupID = groupID;
	}
	
	public StatusInstance CreateInstance(Player p) {
		return new StatusInstance(p, this, baseDuration);
	}
	
	public void OnStart(StatusInstance instance) {
		effects.forEach((effect) -> {
			effect.OnStart(instance);
		});
	}
	
	public void OnEnd(StatusInstance instance) {
		effects.forEach((effect) -> {
			effect.OnEnd(instance);
		});
		
	}
	
	public void OnDeath(StatusInstance instance) {
		effects.forEach((effect) -> {
			effect.OnDeath(instance);
		});
	}
	
	public void OnRespawn(StatusInstance instance) {
		effects.forEach((effect) -> {
			effect.OnRespawn(instance);
		});
	}
	
	public void OnTick(int tick, StatusInstance instance) {
		effects.forEach((effect) -> {
			effect.OnTick(tick, instance);
		});
	}
	
	public void OnTrigger(StatusTrigger trigger) {
	}
	
	public void Apply(Player e) {
		
	}

}
