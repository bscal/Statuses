package me.bscal.statuses.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class StatusPlayer
{

	public Player player;
	public List<StatusInstance> statuses = new ArrayList<StatusInstance>();
	
	public StatusPlayer(Player p) {
		player = p;
	}
	
	public void AddStatus(StatusInstance status) {
		if (status == null) return;
		
		if (statuses.size() > 63) return;
		
		statuses.add(status);
	}
	
	public void RemoveStatus(StatusInstance status) {
		if (status == null) return;
		
		statuses.remove(status);
	}
	
	public void OnTick(int tick) {
		statuses.forEach((status) -> {
			if (status.hasStarted && !status.shouldRemove && status.status.wait % 20 == 0) {
				status.status.OnTick(tick, status);
			}
		});
	}
	
}
