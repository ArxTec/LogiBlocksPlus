package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class EquipCommand extends BaseCommand
{

	public EquipCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		switch(args[1].toLowerCase())
		{
			case "head":
			case "helmet":
			case "chest":
			case "chestplate":
			case "shirt":
			case "legs":
			case "leggings":
			case "pants":
			case "feet":
			case "boots":
			case "shoes":
				break;
			default:
				return false;
		}
		
		ItemStack item = LogiBlocksMain.parseItemStack(args[2]);
		Entity entity = LogiBlocksMain.parseEntity(args[0], location.getWorld());				
		if(entity == null || !(entity instanceof LivingEntity))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Living Entity not found.");
			return true;
		}
		
		switch(args[1].toLowerCase())
		{
			case "head":
			case "helmet":
				((LivingEntity) entity).getEquipment().setHelmet(item);
				break;
			case "chest":
			case "chestplate":
			case "shirt":
				((LivingEntity) entity).getEquipment().setChestplate(item);
				break;
			case "legs":
			case "leggings":
			case "pants":
				((LivingEntity) entity).getEquipment().setLeggings(item);
				break;
			case "feet":
			case "boots":
			case "shoes":
				((LivingEntity) entity).getEquipment().setBoots(item);
				break;
		}		
		return true;
	}

}
