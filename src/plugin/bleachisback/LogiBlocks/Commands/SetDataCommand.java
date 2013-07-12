package plugin.bleachisback.LogiBlocks.Commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import plugin.bleachisback.LogiBlocks.LogiBlocksMain;

public class SetDataCommand extends BaseCommand
{

	public SetDataCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		plugin.handleData(LogiBlocksMain.parseEntity(args[0], location.getWorld()),args[1]);
		return true;
	}

}
