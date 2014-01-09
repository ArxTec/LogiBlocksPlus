package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.FlagFailureException;
import plugin.arxtec.LogiBlocksPlus.FlagListener;
import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class LogiCommandListener implements CommandExecutor
{
	private LogiBlocksMain plugin;
	
	public LogiCommandListener(LogiBlocksMain plugin)
	{
		this.plugin=plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof BlockCommandSender))
		{
			return false;
		}
		
		int stage=0;
		String[] args1=null;
		String cmdTrue="";
		String cmdFalse="";
		int fromWhen=0;
		
		//Goes through each argument, and makes sure that it is a valid if
		//Separates the flag statement, and the two commands at the same time
		int nestedIfs=0;
		for(int i=0;i<args.length;i++)
		{
			String arg=args[i];
			if(arg.equals("?"))
			{
				switch(stage)
				{
					case 0:
						stage++;
						fromWhen=i+1;
						args1=new String[i-1];
						for(int j=1;j<i;j++)
						{
							args1[j-1]=args[j];
						}
						break;
					case 1:
						nestedIfs++;
						break;
				}				
			}
			else if(stage==1&&arg.equals(":"))
			{				
				if(nestedIfs>0)
				{
					nestedIfs--;
				}
				else
				{
					stage++;				
					for(int j=0;j<i-fromWhen;j++)
					{
						cmdTrue=cmdTrue+args[j+fromWhen]+" ";
					}				
					cmdTrue.trim();
					fromWhen=i+1;
					for(int j=fromWhen;j<args.length;j++)
					{
						if(args[j].equals("&"))
						{
							args[j]="&&";
						}
						else if(args[j].equals("\\&"))
						{
							args[j]="&";
						}
						cmdFalse=cmdFalse+args[j]+" ";
					}
					cmdFalse.trim();
					break;
				}
			}
			else if(arg.equals("&"))
			{
				args[i]="&&";
			}
			else if(arg.equals("\\&"))
			{
				args[i]="&";
			}
		}		
		if(stage!=2)
		{
			return false;
		}
		//Checks to see if any plugin has registered that flag
		FlagListener listener=plugin.getFlagListeners().get(args[0].toLowerCase());
		if(listener==null)
		{
			return false;
		} 
		else try 
		{
			if(listener.onFlag(args[0].toLowerCase(), args1, (BlockCommandSender) sender))
			{
				Bukkit.dispatchCommand(sender, cmdTrue);
				return true;
			}
			else
			{
				Bukkit.dispatchCommand(sender, cmdFalse);
				return true;
			}
		} 
		catch (FlagFailureException e) 
		{
			return true;
		}
	}
}
