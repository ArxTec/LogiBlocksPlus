package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class ExplodeCommand extends BaseCommand
{

	public ExplodeCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Location expLoc=LogiBlocksMain.parseLocation(args[0], location);
		try
		{
			if(args.length>=4)
			{
				location.getWorld().createExplosion(expLoc.getX(),expLoc.getY(),expLoc.getZ(),Integer.parseInt(args[1]),Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]));
			}
			else if(args.length==3)
			{
				location.getWorld().createExplosion(expLoc,Integer.parseInt(args[1]),Boolean.parseBoolean(args[2]));
			}
			else
			{
				location.getWorld().createExplosion(expLoc,Integer.parseInt(args[1]));
			}
		}
		catch(NumberFormatException e)
		{}		
		return true;
	}

}
