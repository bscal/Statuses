package me.bscal.statuses.core;

import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.effects.StatusEffect;
import me.bscal.statuses.effects.TriggerEffect;
import me.bscal.statuses.statuses.StatusBase;
import me.bscal.statuses.storage.BukkitSQLAPI;
import me.bscal.statuses.triggers.PlayerTrigger;
import me.bscal.statuses.triggers.StatusTrigger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class StatusManager implements Listener
{

	final Map<Player, StatusPlayer> players = new HashMap<>();

	final List<StatusBase> statuses = new ArrayList<>();
	final List<StatusTrigger> triggers = new ArrayList<>();
	final Map<String, StatusEffect> effects = new HashMap<>();

	final Map<StatusTrigger, List<StatusBase>> triggerToStatus = new HashMap<>();
	final Map<Class<? extends Event>, TreeMap<Integer, List<StatusTrigger>>> eventToTrigger = new HashMap<>();
	final Map<StatusTrigger, List<StatusInstance>> triggerEffects = new HashMap<>();

	/**
	 * Status Loops
	 */
	public void StartRunnable()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Statuses.Get(), () -> {
			for (var pair : players.entrySet())
			{
				pair.getValue().OnTick(Bukkit.getCurrentTick());
			}
		}, 0L, Math.max(1, Statuses.Get().getConfig().getLong("TicksPerUpdate")));
	}

	public void AddPlayer(final StatusPlayer sPlayer)
	{
		if (players.containsKey(sPlayer.player))
			return;

		BukkitSQLAPI.LoadPlayer(Statuses.SQL_USER_TBL, sPlayer);
		players.put(sPlayer.player, sPlayer);
	}

	public void RemovePlayer(StatusPlayer sPlayer, final boolean save)
	{
		if (save)
			BukkitSQLAPI.SavePlayer(Statuses.SQL_USER_TBL, sPlayer);
		players.remove(sPlayer.player);
		sPlayer.Destroy();
		sPlayer = null;
	}

	public StatusPlayer GetPlayer(final Player p)
	{
		return players.get(p);
	}

	/**
	 * Registers a trigger. Can handle registering both event triggers (Triggers linked to an event) or
	 * manual triggers.
	 * @param trigger - Trigger
	 * @return The StatusTrigger
	 */
	public StatusTrigger RegisterTrigger(final StatusTrigger trigger)
	{
		if (trigger.IsEventTrigger(trigger.eventClass))
		{
			Class<? extends Event> event = trigger.eventClass;

			if (!eventToTrigger.containsKey(event))
			{
				eventToTrigger.put(event, new TreeMap<>());
				var map = eventToTrigger.get(event);
				map.put(trigger.GetWeight(), new ArrayList<>());
				var list = map.get(trigger.GetWeight());
				list.add(trigger);
			}
			else
			{
				var map = eventToTrigger.get(event);
				if (!map.containsKey(trigger.GetWeight()))
					map.put(trigger.GetWeight(), new ArrayList<>()).add(trigger);
				else
					map.get(trigger.GetWeight()).add(trigger);
			}
		}

		triggers.add(trigger);

		LogCraft.Log("[ ok ] Registering trigger: ", trigger.getClass().getSimpleName());

		return trigger;
	}

	/**
	 * Registers a Status as StatusBase
	 * @param status - status
	 * @param trigger - trigger
	 */
	public void Register(final StatusBase status, final StatusTrigger trigger)
	{
		if (status == null || trigger == null)
			return;

		if (!statuses.contains(status))
			statuses.add(status);

		if (!triggerToStatus.containsKey(trigger))
		{
			triggerToStatus.put(trigger, new ArrayList<StatusBase>());
		}
		else if (triggerToStatus.get(trigger).contains(status))
		{
			LogCraft.LogErr("Failed to register. Error: Status already registered.");
			return;
		}

		triggerToStatus.get(trigger).add(status);

		LogCraft.Log("[ ok ] Registering status: ", status.name, trigger.getClass().getSimpleName());
	}

	public void RegisterEffect(final StatusEffect effect)
	{
		RegisterEffect(effect, effect.getClass().getSimpleName());
	}

	public void RegisterEffect(final StatusEffect effect, String name)
	{
		effects.put(name, effect);

		LogCraft.Log("[ ok ] Registering effect: ", name);
	}

	/**
	 * Sets an instance to have any TriggerEffects updated.
	 *
	 * @param instance
	 */
	public void AddTriggerEffect(final StatusInstance instance)
	{
		if (instance == null)
		{
			LogCraft.LogErr("[ AddTriggerEffect] Instance or effects were null.");
			return;
		}

		if (Statuses.Debug)
			LogCraft.Log("Adding TriggerEffect", instance.status.name);

		for (var trig : instance.status.triggers)
		{
			if (!triggerEffects.containsKey(trig))
			{
				triggerEffects.put(trig, new ArrayList<>());
			}

			triggerEffects.get(trig).add(instance);
		}
	}

	public void RemoveTriggerEffect(final StatusInstance instance)
	{
		if (instance == null)
			return;

		if (Statuses.Debug)
			LogCraft.Log("Removing TriggerEffect", instance.status.name);

		for (int i = 0; i < instance.status.triggers.size(); i++)
		{
			List<StatusInstance> list = triggerEffects.get(instance.status.triggers.get(i));
			list.remove(instance);
		}
	}

	public StatusBase GetStatus(final String name)
	{
		for (int i = 0; i < statuses.size(); i++)
		{
			if (statuses.get(i).name.equalsIgnoreCase(name))
				return statuses.get(i);
		}
		return null;
	}

	/**
	 * Gets a Trigger by name. Use <code>Trigger.class.getSimpleName()</code> the correct name of the trigger.
	 *
	 * @param name
	 * @return
	 */
	public StatusTrigger GetTrigger(final String name)
	{
		for (int i = 0; i < triggers.size(); i++)
		{
			if (triggers.get(i).name.equalsIgnoreCase(name))
				return triggers.get(i);
		}

		LogCraft.LogErr("Trying to get Trigger (" + name + ") but does not exist.");

		return null;
	}

	public StatusEffect GetEffect(final String name)
	{
		if (effects.containsKey(name) && LogLevel.Is(LogLevel.INFO_ONLY))
			LogCraft.LogErr("[ error ] No registered effect named " + name);

		return effects.get(name);
	}

	public int TriggerCount()
	{
		return triggers.size();
	}

	public int StatusCount()
	{
		return statuses.size();
	}

	public int EffectsCount()
	{
		return effects.size();
	}

	/**
	 * Attempts to apply a status to a play from a trigger.
	 * @param trigger - Trigger linked to status
	 * @param p - Player to apply status to
	 * @return true if applied
	 */
	public boolean TriggerPlayer(final PlayerTrigger trigger, final Player p)
	{
		if (!triggerToStatus.containsKey(trigger))
			return false;

		// Updates and TriggerEffects that are linked to this trigger.
		if (triggerEffects.containsKey(trigger))
		{
			for (StatusInstance instance : triggerEffects.get(trigger))
			{
				for (int i = 0; i < instance.status.effects.size(); i++)
				{
					if (instance.status.effects.get(i) instanceof TriggerEffect)
						((TriggerEffect) instance.status.effects.get(i)).OnTrigger(instance, trigger);
				}
			}
		}

		for (var status : triggerToStatus.get(trigger))
		{
			if (Statuses.Debug)
				LogCraft.Log("Trying to Apply status", status.name, trigger.name, p.getName(),
						status.ShouldApply(trigger, p));

			if (status.ShouldApply(trigger, p))
			{
				StatusPlayer sPlayer = players.get(p);
				sPlayer.AddStatus(status, status.GetKey(trigger, p));

				if (!status.procAllTriggers)
					return true;
			}
		}
		return false;
	}

	@EventHandler public void OnJoin(PlayerJoinEvent e)
	{
		StatusPlayer sPlayer = new StatusPlayer(e.getPlayer());
		AddPlayer(sPlayer);
	}

	@EventHandler public void OnExit(PlayerQuitEvent e)
	{
		StatusPlayer sPlayer = players.get(e.getPlayer());
		RemovePlayer(sPlayer, true);
	}

	/**
	 * Properly handle events. If a trigger exists linking to the inputted event.
	 * If so updates any instances with TriggerEffects so the effects can update.
	 * And <code>TriggerPlayer()</code> will be called to attempt to apply on player.
	 * Triggers are processed by the Trigger's GetWeight() then by order of registration into that weight.
	 * So
	 *
	 * @param e - Event from bukkit listener
	 */
	private void HandleEvent(final Event e)
	{
		var map = eventToTrigger.get(e.getClass());

		LogCraft.LogMap(map);

		boolean handled = false;
		for (List<StatusTrigger> triggers : map.values())
		{
			for (StatusTrigger trig : triggers)
			{
				trig.SetEvent(e);
				if (trig.IsValid() && trig instanceof PlayerTrigger)
				{
					// If trigger should fire call TriggerPlayer to see if the status should be applied.
					handled = TriggerPlayer((PlayerTrigger) trig, ((PlayerTrigger) trig).GetPlayer());
					break;
				}
			}
			if (handled)
				break;
		}
	}

	@EventHandler public void OnDeath(PlayerDeathEvent e)
	{
		StatusPlayer sp = players.get(e.getEntity());
		for (int i = sp.statuses.size() - 1; i > -1; i--)
		{
			StatusInstance inst = sp.statuses.get(i);
			inst.status.OnDeath(inst);
			if (inst.status.shouldRemoveOnDeath)
			{
				inst.shouldRemove = true;
				sp.RemoveStatus(inst, i);
			}
		}

		HandleEvent(e);
	}

	@EventHandler public void OnEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		HandleEvent(e);
	}

	@EventHandler public void OnEntityDamage(EntityDamageEvent e)
	{
		if (e.getEntity() instanceof Player)
			HandleEvent(e);
	}

}
