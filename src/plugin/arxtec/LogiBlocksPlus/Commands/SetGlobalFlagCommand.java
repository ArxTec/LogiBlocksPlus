package plugin.arxtec.LogiBlocksPlus.Commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.FlagFailureException;
import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class SetGlobalFlagCommand extends BaseCommand
{

	public SetGlobalFlagCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		if(args[1].toLowerCase().equals("true"))
		{
			plugin.getFlagConfig().set("global."+args[0], true);
		}
		else if(args[1].toLowerCase().equals("false"))
		{
			plugin.getFlagConfig().set("global."+args[0], false);
		}
		else if(plugin.getFlagListeners().containsKey(args[1]))
		{					
			try 
			{
				plugin.getFlagConfig().set("global."+args[0], plugin.getFlagListeners().get(args[1]).onFlag(args[1], Arrays.copyOfRange(args, 3, args.length+1), (BlockCommandSender) sender));
			} 
			catch (FlagFailureException e) 
			{
				return true;
			}
		}
		
		try 
		{
			plugin.getFlagConfig().save(plugin.getFlagFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return true;
	}

}
