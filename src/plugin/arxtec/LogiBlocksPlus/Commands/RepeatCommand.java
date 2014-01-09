package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class RepeatCommand extends BaseCommand
{

	public RepeatCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(final CommandSender sender, String[] args, Location location) 
	{
		String command = "";
		for(int i = 2; i < args.length; i++)
		{
			command = command + args[i] + " ";
		}		
		command.trim();
		
		final String $command = command;
		final int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				Bukkit.dispatchCommand(sender, $command);
			}
		}, 0, Integer.parseInt(args[0]));
		
		if(Integer.parseInt(args[1]) != 0 || !plugin.getConfig().getBoolean("allow-infinite-loops"))
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					Bukkit.getScheduler().cancelTask(id);
				}
			}, Integer.parseInt(args[1]) * Integer.parseInt(args[0]));
		}
		return true;
	}

}
