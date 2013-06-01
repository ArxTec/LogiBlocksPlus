package plugin.bleachisback.LogiBlocks;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class FilterCommandListener implements CommandExecutor, Listener
{
	private LogiBlocksMain plugin;
	
	private HashMap<PluginCommand,CommandExecutor> executors=new HashMap<PluginCommand,CommandExecutor>();
	
	public FilterCommandListener(LogiBlocksMain plugin)
	{
		this.plugin=plugin;
		for(Plugin _plugin:Bukkit.getPluginManager().getPlugins())
		{
			if(_plugin.getDescription().getCommands()==null)
			{
				continue;
			}
			else if(plugin.config.getStringList("disable-filtering.plugins").contains(_plugin.getName()))
			{
				continue;
			}
			for(String cmdString:_plugin.getDescription().getCommands().keySet())
			{				
				PluginCommand cmd=Bukkit.getPluginCommand(cmdString);
				if(cmd==null)
				{
					continue;
				}
				else if(plugin.config.getStringList("disable-filtering.commands").contains(cmdString))
				{
					continue;
				}
				executors.put(cmd, cmd.getExecutor());
				cmd.setExecutor(this);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) 
	{
		if(sender instanceof Player||sender instanceof BlockCommandSender)
		{
			Location loc=null;
			if(sender instanceof Player)
			{
				loc=((Player)sender).getLocation();
			}
			else if(sender instanceof BlockCommandSender)
			{
				loc=((BlockCommandSender)sender).getBlock().getLocation();
			}
			if(!plugin.filter(args, sender, cmd, loc))
			{
				return true;
			}
		}
		if(!executors.containsKey(cmd))
		{
			return false;
		}
		else if(executors.get(cmd)==this)
		{
			plugin.log.info("Something went wrong! e1");
			sender.sendMessage(ChatColor.DARK_RED+"Something went wrong! Please notify your server admin.");
			return true;
		}
		return executors.get(cmd).onCommand(sender, cmd, alias, args);
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent e)
	{
		if(e.getPlugin().getDescription().getCommands()==null)
		{
			return;
		}
		for(String cmdString:e.getPlugin().getDescription().getCommands().keySet())
		{
			PluginCommand cmd=Bukkit.getPluginCommand(cmdString);
			if(cmd==null)
			{
				continue;
			}
			if(cmd.getExecutor()==this)
			{
				continue;
			}
			executors.put(cmd, cmd.getExecutor());
			cmd.setExecutor(this);
		}
	}
}
