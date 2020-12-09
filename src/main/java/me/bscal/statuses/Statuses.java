package me.bscal.statuses;

import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.core.StatusManager;
import me.bscal.statuses.storage.SQLAPI;
import me.bscal.statuses.triggers.EntityDamagedTrigger;
import me.bscal.statuses.triggers.PlayerDamageDoneTrigger;
import me.bscal.statuses.triggers.PlayerDamageRecievedTrigger;
import me.bscal.statuses.triggers.StatusTrigger;
import me.bscal.statuses.utils.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Statuses extends JavaPlugin
{
	public static final String SQL_USER_TBL = "user_statuses";
	public static boolean Debug;
	public static LogCraft Logger;

	private static Statuses m_singleton;

	private FileConfiguration m_config;

	private StatusManager m_sm;

	private SQLAPI m_database;

	public void onEnable()
	{
		m_singleton = this;

		saveDefaultConfig();

		m_config = SpigotUtils.CreateConfig(getDataFolder() + File.separator + "config.yml");
		Debug = m_config.getBoolean("DebugModeEnabled");
		Logger = new LogCraft(this, LogLevel.IntToLevel(m_config.getInt("LogLevel")));

		m_database = new SQLAPI(Debug, m_config.getBoolean("EnableMysql"));
		m_database.Connect();

		m_sm = new StatusManager();
		Bukkit.getPluginManager().registerEvents(m_sm, this);

		// Triggers
		StatusTrigger dmgDoneToEntTrig = m_sm.RegisterTrigger(new PlayerDamageDoneTrigger());
		StatusTrigger dmgRecByEntTrig = m_sm.RegisterTrigger(new PlayerDamageRecievedTrigger());
		StatusTrigger dmgRecTrig = m_sm.RegisterTrigger(new EntityDamagedTrigger());

		// Effects
		//m_sm.RegisterEffect(new BleedEffect());
		//m_sm.RegisterEffect(new FractureEffect());

		// Statuses
		//m_sm.Register(new BleedStatus(), dmgRecByEntTrig);
		//m_sm.Register(new FractureStatus(), dmgRecTrig);

		Logger.Log("StatusManager Loaded. Trigger count:", m_sm.TriggerCount(), "Status Count:",
				m_sm.StatusCount(), "Effect Count:", m_sm.EffectsCount());

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

	public FileConfiguration GetConfigFile()
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
