package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class TeleportCommand extends BaseCommand
{

	public TeleportCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Entity tper=LogiBlocksMain.parseEntity(args[0], location.getWorld());
		if(tper==null)
		{
			sender.sendMessage(ChatColor.DARK_RED+"Entity not found.");
			return true;
		}			
		Location tpLocation=LogiBlocksMain.parseLocation(args[1], tper.getLocation());
		plugin.teleport(tper,tpLocation);
		return true;
	}

}
