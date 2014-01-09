package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class AccelerateCommand extends BaseCommand
{

	public AccelerateCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Entity entity = LogiBlocksMain.parseEntity(args[0], location.getWorld());
		if(entity == null)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Entity not found.");
			return true;
		}
		
		try
		{
			entity.setVelocity(new Vector(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3])));
		}
		catch(NumberFormatException e)
		{}
		
		return true;
	}

}
