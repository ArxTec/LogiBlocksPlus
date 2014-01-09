package plugin.arxtec.LogiBlocksPlus.Commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class BaseCommandListener implements CommandExecutor
{

	public BaseCommandListener(LogiBlocksMain plugin)
	{		
		//Load aliases and enabled commands from config
		for(BaseCommands command : BaseCommands.values())
		{
			command.initialise(plugin);
			if(plugin.getConfig().contains("commands."+command.getName()))
			{
				if(plugin.getConfig().getBoolean("commands."+command.getName()+".enabled"))
				{
					command.setEnabled(true);
					for(String alias:plugin.getConfig().getStringList("commands."+command.getName()+".aliases"))
					{
						command.addAlias(alias);
					}
				}
			}
			else
			{
				plugin.getLogger().info("No profile detected for command: "+command.getName());
				plugin.getConfig().set("commands."+command.getName()+".enabled", true);
				plugin.getConfig().set("commands."+command.getName()+".aliases", "");
				plugin.saveConfig();
				plugin.getLogger().info("Default profile for "+command.getName()+" created");
			}
		}
		
		if(Bukkit.getPluginManager().getPlugin("VoxelSniper")==null)
		{
			BaseCommands.VOXELSNIPER.setEnabled(false);
		}
		else if(BaseCommands.VOXELSNIPER.isEnabled())
		{
			plugin.getLogger().info("Voxelsniper detected - adding support");
		}
	}

	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
	{
		//Anything that isn't a player or CommandBlock can't use these commands, as they rely on locations
		if(!(sender instanceof BlockCommandSender)&&!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED+"That command can only be used in-game!");
			return true;
		}
		//Every command has a sub-command
		if(args.length<1)
		{
			return false;
		}
		Location location;
		BlockCommandSender block = null;
		if(sender instanceof BlockCommandSender)
		{
			block=(BlockCommandSender) sender;
			location=block.getBlock().getLocation();			
		}
		else
		{
			if(!sender.hasPermission("c.command"))
			{
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission for that!");
				return true;
			}
			location=((Player) sender).getLocation();
		}
		BaseCommands command = BaseCommands.getByName(args[0]);
		if(command == null)
		{
			sender.sendMessage(ChatColor.DARK_RED+"That sub-command doesn't exist!");
			return false;
		}
		//Each command has a minimum number of arguments required for the command to work
		if(args.length <= command.getMinArgs())
		{
			sender.sendMessage(ChatColor.DARK_RED+"That's now how you use that!");
			return false;
		}
		return command.execute(sender, Arrays.copyOfRange(args, 1, args.length), location);
	}
}
