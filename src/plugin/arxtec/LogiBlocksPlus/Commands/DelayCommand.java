package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class DelayCommand extends BaseCommand
{

	public DelayCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(final CommandSender sender, String[] args, Location location) 
	{
		String command="";
		for(int i=1;i<args.length;i++)
		{
			command=command+args[i]+" ";
		}
		command.trim();
		
		try
		{
			final String $command=command;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					Bukkit.dispatchCommand(sender, $command);
				}
			}, Integer.parseInt(args[0]));
		}
		catch(NumberFormatException e)
		{}
		
		return true;
	}

}
