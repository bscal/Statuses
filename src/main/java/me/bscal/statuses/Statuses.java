package me.bscal.statuses;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.DevTec.TheAPI.ConfigAPI.Config;
import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.core.StatusManager;
import me.bscal.statuses.triggers.PlayerDamageDoneTrigger;
import me.bscal.statuses.triggers.PlayerDamageRecievedTrigger;

public class Statuses extends JavaPlugin
{

	private static Statuses m_singleton;
	
	public static boolean Debug;

	private Config m_config;

	private StatusManager m_sm;

	public void onEnable()
	{
		m_singleton = this;

		LogCraft.Init(this);

		saveDefaultConfig();

		m_config = new Config(getName() + File.separator + "config.yml");
		Debug = m_config.getBoolean("DebugModeEnabled");

		m_sm = new StatusManager();
		m_sm.RegisterTrigger(new PlayerDamageDoneTrigger());
		m_sm.RegisterTrigger(new PlayerDamageRecievedTrigger());
	}

	public void onDisable()
	{

	}

	public static Statuses Get()
	{
		return m_singleton;
	}

	public Config GetConfigFile()
	{
		return m_config;
	}

	public StatusManager GetStatusMgr()
	{
		return m_sm;
	}

}
