package plugin.arxtec.LogiBlocksPlus.Commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class SpawnCommand extends BaseCommand
{

	public SpawnCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Location spawnLocation=location.add(0, 1, 0);
		int amount = 1;
		if(args.length>=3)
		{
			spawnLocation = LogiBlocksMain.parseLocation(args[1], spawnLocation);
		}
		if(args.length>=4)
		{
			try
			{
				amount = Integer.parseInt(args[2]);
			}
			catch(NumberFormatException e)
			{}
		}
		for(int i=0 ; i<amount ; i++)
		{
			spawn(args[0],spawnLocation);
		}
		
		return true;
	}
	
	private void spawn(String stackString,Location spawnLocation)
	{
		ArrayList<String> stackArray=new ArrayList<String>(Arrays.asList(stackString.split(",")));		
		ArrayList<Entity> stack=new ArrayList<Entity>();
		for(int i=0;i<stackArray.size();i++)
		{
			String entString=stackArray.get(i);
			if(entString.equals("&"))
			{
				if(i==0)
				{
					continue;
				}
				entString=stackArray.get(i-1);
				stackArray.set(i, stackArray.get(i-1));
			}
			if(entString.equals("&&"))
			{
				stackArray.remove(i);
				for(int j=i-1;j>=0;j--)
				{
					stackArray.add(i, stackArray.get(j));
				}
				i--;
			}
			String typeString=entString.contains(":")?entString.substring(0,entString.indexOf(":")):entString;
			String dataString=entString.contains(":")?entString.substring(entString.indexOf(":")+1,entString.length()):null;
			EntityType type=null;
			try
			{
				type=EntityType.values()[Integer.parseInt(typeString)];
			}
			catch(NumberFormatException e)
			{
				type=EntityType.fromName(typeString);
			}
			if(type==null)
			{
				type=LogiBlocksMain.entFromAlias(typeString);
				if(type==null)
				{
					continue;
				}
			}
			Entity ent=spawnLocation.getWorld().spawnEntity(spawnLocation, type);			
			plugin.handleData(ent,dataString);
			if(stack.size()>0)
			{
				stack.get(stack.size()-1).setPassenger(ent);
			}
			stack.add(ent);
		}
	}

}
