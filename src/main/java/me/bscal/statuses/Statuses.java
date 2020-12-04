package me.bscal.statuses;

import me.DevTec.TheAPI.ConfigAPI.Config;
import me.bscal.logcraft.LogCraft;
import me.bscal.logcraft.LogLevel;
import me.bscal.statuses.core.StatusManager;
import me.bscal.statuses.effects.BleedEffect;
import me.bscal.statuses.effects.FractureEffect;
import me.bscal.statuses.statuses.BleedStatus;
import me.bscal.statuses.statuses.FractureStatus;
import me.bscal.statuses.storage.SQLAPI;
import me.bscal.statuses.triggers.EntityDamagedTrigger;
import me.bscal.statuses.triggers.PlayerDamageDoneTrigger;
import me.bscal.statuses.triggers.PlayerDamageRecievedTrigger;
import me.bscal.statuses.triggers.StatusTrigger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Statuses extends JavaPlugin
{
	private static Statuses m_singleton;

	public static final String SQL_USER_TBL = "status_users";

	public static boolean Debug;

	private Config m_config;

	private StatusManager m_sm;

	private SQLAPI m_database;

	public void onEnable()
	{
		m_singleton = this;

		saveDefaultConfig();

		m_config = new Config(getName() + File.separator + "config.yml");
		Debug = m_config.getBoolean("DebugModeEnabled");

		LogCraft.Init(this, LogLevel.IntToLevel(m_config.getInt("LogLevel")));

		m_database = new SQLAPI(Debug, m_config.getBoolean("EnableMysql"));
		m_database.Connect();

		m_sm = new StatusManager();
		Bukkit.getPluginManager().registerEvents(m_sm, this);

		// Triggers
		StatusTrigger dmgDoneToEntTrig = m_sm.RegisterTrigger(new PlayerDamageDoneTrigger());
		StatusTrigger dmgRecByEntTrig = m_sm.RegisterTrigger(new PlayerDamageRecievedTrigger());
		StatusTrigger dmgRecTrig = m_sm.RegisterTrigger(new EntityDamagedTrigger());

		// Effects
		m_sm.RegisterEffect(new BleedEffect());
		m_sm.RegisterEffect(new FractureEffect());

		// Statuses
		m_sm.Register(new BleedStatus(), dmgRecByEntTrig);
		m_sm.Register(new FractureStatus(), dmgRecTrig);

		LogCraft.Log("StatusManager Loaded. Trigger count:", m_sm.TriggerCount(), "Status Count:", m_sm.StatusCount(),
				"Effect Count:", m_sm.EffectsCount());
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
