package plugin.bleachisback.LogiBlocks.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import plugin.bleachisback.LogiBlocks.LogiBlocksMain;

public class LogiBlocksCraftListener implements Listener
{
	public LogiBlocksCraftListener(LogiBlocksMain plugin)
	{
	}
	
	@EventHandler
	public void onPlayerCraftItem(CraftItemEvent e)
	{
		if(e.getRecipe().getResult().getType()==Material.COMMAND&&!e.getWhoClicked().hasPermission("c.craft"))
		{
			e.setCancelled(true);
		}
	}
}
