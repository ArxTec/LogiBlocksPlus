package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class SetHealthCommand extends BaseCommand
{

	public SetHealthCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Entity hpEnt = LogiBlocksMain.parseEntity(args[0], location.getWorld());
		if(hpEnt == null)
		{
			sender.sendMessage(ChatColor.RED+"Entity not found.");
			return true;
		}
		if(!(hpEnt instanceof LivingEntity))
		{
			sender.sendMessage(ChatColor.RED+"That's not living!");
			return true;
		}
		double setHp=0.0;
		try
		{
			switch(args[1].charAt(0))
			{
				case '+':
					setHp=((LivingEntity)hpEnt).getHealth()+Integer.parseInt(args[1].replace("+",""));
					break;
				case '-':
					setHp=((LivingEntity)hpEnt).getHealth()-Integer.parseInt(args[1].replace("-",""));
					break;
				default:
					try
					{
						setHp=Integer.parseInt(args[1]);
					}
					catch(NumberFormatException e)
					{
						Entity _hpEnt = LogiBlocksMain.parseEntity(args[1], location.getWorld());
						if(_hpEnt == null)
						{						
							sender.sendMessage(args[1]+ChatColor.RED+" is not a valid number or organism");
							return true;
						}
						else if(!(_hpEnt instanceof LivingEntity))
						{
							sender.sendMessage(args[1]+ChatColor.RED+" is not a valid number or organism");
							return true;
						}
						setHp=((LivingEntity)_hpEnt).getHealth();					
					}
					break;
			}
		}
		catch(NumberFormatException e)
		{
			sender.sendMessage(args[1]+ChatColor.RED+" is not a valid number.");
			return true;
		}				
		if(setHp<0)
		{
			setHp=0;
		}
		else if(setHp>((LivingEntity)hpEnt).getMaxHealth())
		{
			setHp=((LivingEntity)hpEnt).getMaxHealth();
		}
		((LivingEntity)hpEnt).setHealth(setHp);
		
		return true;
	}

}
