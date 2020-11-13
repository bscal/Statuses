package me.bscal.statuses;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.DevTec.TheAPI.ConfigAPI.Config;
import me.bscal.logcraft.LogCraft;

public class Statuses extends JavaPlugin
{

	public static boolean Debug;

	private Config m_config;

	private static Statuses m_singleton;

	public void onEnable ()
	{
		m_singleton = this;
		Debug = true;

		LogCraft.Init(this);

		saveDefaultConfig();

		m_config = new Config(getName() + File.separator + "config.yml");
	}

	public void onDisable ()
	{

	}

	public static Statuses Get ()
	{
		return m_singleton;
	}

	public Config GetConfigFile ()
	{
		return m_config;
	}

}
