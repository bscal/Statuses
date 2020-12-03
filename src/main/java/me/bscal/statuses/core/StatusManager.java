package me.bscal.statuses.core;

import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.Statuses;
import me.bscal.statuses.effects.StatusEffect;
import me.bscal.statuses.effects.TriggerEffect;
import me.bscal.statuses.statuses.StatusBase;
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

	Map<Player, StatusPlayer> players = new HashMap<>();

	List<StatusBase> statuses = new ArrayList<>();
	List<StatusTrigger> triggers = new ArrayList<>();
	Map<String, StatusEffect> effects = new HashMap<>();

	Map<StatusTrigger, List<StatusBase>> triggerToStatus = new HashMap<>();
	Map<Class<? extends Event>, TreeMap<Integer, List<StatusTrigger>>> eventToTrigger = new HashMap<>();
	Map<StatusTrigger, List<StatusInstance>> triggerEffects = new HashMap<>();

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

	public void AddPlayer(StatusPlayer sPlayer)
	{
		if (players.containsKey(sPlayer.player))
			return;

		players.put(sPlayer.player, sPlayer);
	}

	public void RemovePlayer(StatusPlayer sPlayer)
	{
		players.remove(sPlayer.player);
		sPlayer.Destroy();
		sPlayer = null;
	}

	public StatusPlayer GetPlayer(Player p)
	{
		return players.get(p);
	}

	public StatusTrigger RegisterTrigger(StatusTrigger trigger)
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

	public void Register(StatusBase status, StatusTrigger trigger)
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

	public void RegisterEffect(StatusEffect effect)
	{
		RegisterEffect(effect, effect.getClass().getSimpleName());
	}

	public void RegisterEffect(StatusEffect effect, String name)
	{
		effects.put(name, effect);

		LogCraft.Log("[ ok ] Registering effect: ", name);
	}

	/**
	 * Sets an instance to have any TriggerEffects updated.
	 *
	 * @param instance
	 */
	public void AddTriggerEffect(StatusInstance instance)
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

	public void RemoveTriggerEffect(StatusInstance instance)
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

	public StatusBase GetStatus(String name)
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
	public StatusTrigger GetTrigger(String name)
	{
		for (int i = 0; i < triggers.size(); i++)
		{
			if (triggers.get(i).name.equalsIgnoreCase(name))
				return triggers.get(i);
		}

		LogCraft.LogErr("Trying to get Trigger (" + name + ") but does not exist.");

		return null;
	}

	public StatusEffect GetEffect(String name)
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

	public int EffectsCount() { return effects.size(); }

	public boolean TriggerPlayer(PlayerTrigger trigger, Player p)
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
		Statuses.Get().GetDB().LoadPlayer("user_statuses", sPlayer);
		AddPlayer(sPlayer);
	}

	@EventHandler public void OnExit(PlayerQuitEvent e)
	{
		StatusPlayer sPlayer = players.get(e.getPlayer());
		Statuses.Get().GetDB().SavePlayer("user_statuses", sPlayer);
		RemovePlayer(sPlayer);
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
