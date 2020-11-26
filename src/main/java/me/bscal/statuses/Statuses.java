package me.bscal.statuses;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.DevTec.TheAPI.ConfigAPI.Config;
import me.bscal.logcraft.LogCraft;
import me.bscal.statuses.core.StatusManager;
import me.bscal.statuses.statuses.BleedStatus;
import me.bscal.statuses.storage.SQLAPI;
import me.bscal.statuses.triggers.PlayerDamageDoneTrigger;
import me.bscal.statuses.triggers.PlayerDamageRecievedTrigger;

public class Statuses extends JavaPlugin
{

	private static Statuses m_singleton;

	public static boolean Debug;

	private Config m_config;

	private StatusManager m_sm;

	private SQLAPI m_database;

	public void onEnable()
	{
		m_singleton = this;

		LogCraft.Init(this);

		saveDefaultConfig();

		m_config = new Config(getName() + File.separator + "config.yml");
		Debug = m_config.getBoolean("DebugModeEnabled");
		m_database = new SQLAPI(Debug, m_config.getBoolean("EnableMysql"));
		m_database.Connect();
		
		m_sm = new StatusManager();
		Bukkit.getPluginManager().registerEvents(m_sm, this);
		var dmgDoneTrig = m_sm.RegisterTrigger(new PlayerDamageDoneTrigger());
		var dmgRecTrig = m_sm.RegisterTrigger(new PlayerDamageRecievedTrigger());

		m_sm.Register(new BleedStatus(), dmgRecTrig);

		LogCraft.Log("StatusManager Loaded. Trigger count:", m_sm.TriggerCount(), "Status Count:", m_sm.StatusCount());
		m_sm.StartRunnable();
	}

	public void onDisable()
	{
		m_database.Close();
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

	public SQLAPI GetDB()
	{
		return m_database;
	}

}
