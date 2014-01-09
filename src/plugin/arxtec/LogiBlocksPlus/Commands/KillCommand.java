package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class KillCommand extends BaseCommand
{

	public KillCommand(LogiBlocksMain plugin) 
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
		if(entity instanceof LivingEntity)
		{
			((LivingEntity)entity).setHealth(0);
		}
		else
		{
			entity.remove();
		}		
		return true;
	}

}
