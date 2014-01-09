package plugin.arxtec.LogiBlocksPlus.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class LogiBlocksRedstoneListener implements Listener
{
	private LogiBlocksMain plugin;
	
	public LogiBlocksRedstoneListener(LogiBlocksMain plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlateTrigger(EntityInteractEvent e)
	{
		if(e.getBlock().getType()==Material.LEVER||
				e.getBlock().getType()==Material.STONE_BUTTON||
				e.getBlock().getType()==Material.WOOD_BUTTON||
				e.getBlock().getType()==Material.STONE_PLATE||
				e.getBlock().getType()==Material.WOOD_PLATE)
		{
			plugin.setLastRedstone(e.getEntity());
		}
	}
	
	@EventHandler
	public void onPlayerRedstone(PlayerInteractEvent e)
	{
		if(e.getAction()==Action.LEFT_CLICK_BLOCK||
				e.getAction()==Action.LEFT_CLICK_AIR||
				e.getAction()==Action.RIGHT_CLICK_AIR)
		{
			return;
		}
		if(e.getClickedBlock().getType()==Material.LEVER||
				e.getClickedBlock().getType()==Material.STONE_BUTTON||
				e.getClickedBlock().getType()==Material.WOOD_BUTTON||
				e.getClickedBlock().getType()==Material.STONE_PLATE||
				e.getClickedBlock().getType()==Material.WOOD_PLATE)
		{
			plugin.setLastRedstone(e.getPlayer());
		}
	}
}
