package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForCommandListener implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{		
		if(args.length>1)
		{
			String builder="";
			for(int i=1;i<args.length;i++)
			{
				builder=builder+args[i]+" ";
			}
			return Bukkit.dispatchCommand(sender,builder);
		}
		return false;
	}
}
