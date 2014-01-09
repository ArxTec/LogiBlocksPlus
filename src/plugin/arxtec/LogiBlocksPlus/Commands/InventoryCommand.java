package plugin.arxtec.LogiBlocksPlus.Commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class InventoryCommand extends BaseCommand
{
	private HashMap<String,Integer> inventorySubs=new HashMap<String,Integer>();
	
	public InventoryCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
		
		inventorySubs.put("add",1);
		inventorySubs.put("remove",1);
		inventorySubs.put("removeall",1);
		inventorySubs.put("clear",0);
		inventorySubs.put("refill",0);
		inventorySubs.put("set",2);
		inventorySubs.put("copy",1);
		inventorySubs.put("show",1);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		ArrayList<Inventory> inventoryList = new ArrayList<Inventory>();
		if((args[0].startsWith("@l[") && args[0].endsWith("]")) || inventorySubs.containsKey(args[0]))
		{
			Location invLocation = LogiBlocksMain.parseLocation(args[0], location);
			for(int x = -1; x <= 1; x++)
			{
				for(int y = -1; y <= 1; y++)
				{
					for(int z = -1; z <= 1; z++)
					{
						Block testBlock = invLocation.clone().add(x, y, z).getBlock();
						if(testBlock.getState() instanceof InventoryHolder)
						{
							inventoryList.add(((InventoryHolder) testBlock.getState()).getInventory());
						}
					}
				}
			}
		}
		else
		{
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null)
			{
				return false;
			}
			inventoryList.add(player.getInventory());
		}
		int subIndex = inventorySubs.containsKey(args[0])?0:1;
		if(!inventorySubs.containsKey(args[subIndex]))
		{
			return false;
		}
		if(inventorySubs.get(args[subIndex]) + subIndex + 1 > args.length)
		{
			return false;
		}
		for(Inventory inventory : inventoryList)
		{					
			switch(args[subIndex])
			{
				case "add":
					for(int i = subIndex + 1; i < args.length; i++)
					{
						ItemStack item1=LogiBlocksMain.parseItemStack(args[i]);
						if(item1==null)
						{
							continue;
						}
						inventory.addItem(item1);
					}
					break;
				case "remove":
					for(int i=subIndex+1;i<args.length;i++)
					{
						ItemStack removeItem=LogiBlocksMain.parseItemStack(args[i]);
						if(removeItem==null)
						{
							continue;
						}
						for(ItemStack targetItem:inventory.getContents())
						{
							if(targetItem==null)
							{
								continue;
							}
							if(targetItem.isSimilar(removeItem))
							{
								if(removeItem.getAmount()>targetItem.getAmount())
								{
									removeItem.setAmount(removeItem.getAmount()-targetItem.getAmount());
									inventory.remove(removeItem);
								}
								else
								{
									targetItem.setAmount(targetItem.getAmount()-removeItem.getAmount());
									break;										
								}
							}
						}
					}							
					break;
				case "removeall":
					for(int i=subIndex+1;i<args.length;i++)
					{
						ItemStack removeItem=LogiBlocksMain.parseItemStack(args[i]);
						for(int j=1;j<=64;j++)
						{
							removeItem.setAmount(j);
							inventory.remove(removeItem);
						}								
					}
					break;
				case "clear":
					inventory.clear();
					break;
				case "refill":
					for(ItemStack itemStack:inventory.getContents())
					{
						if(itemStack==null)
						{
							continue;
						}
						itemStack.setAmount((args.length>subIndex+1?Boolean.parseBoolean(args[subIndex+1])?64:itemStack.getMaxStackSize():itemStack.getMaxStackSize()));
					}
					break;
				case "set":
					int slot=Integer.parseInt(args[subIndex+1]);
					inventory.setItem(slot>=inventory.getSize()?inventory.getSize()-1:slot, LogiBlocksMain.parseItemStack(args[subIndex+2]));
					break;
				case "copy":
					Inventory targetInventory;
					if(args[subIndex+1].startsWith("@l[")&&args[subIndex+1].endsWith("]"))
					{
						Block targetBlock=LogiBlocksMain.parseLocation(args[subIndex+1], location).getBlock();
						if(targetBlock.getState() instanceof InventoryHolder)
						{
							targetInventory=((InventoryHolder) targetBlock.getState()).getInventory();
						}
						else
						{
							return false;
						}
					}
					else
					{
						Player player=Bukkit.getPlayer(args[subIndex+1]);
						if(player==null)
						{
							return false;
						}
						targetInventory=player.getInventory();
					}
					for(int i=0;i<inventory.getSize()&&i<targetInventory.getSize();i++)
					{
						targetInventory.setItem(i, inventory.getItem(i));
					}
					break;
				case "show":
					Player player=Bukkit.getPlayer(args[subIndex+1]);
					if(player==null)
					{
						return false;
					}
					if(args.length>subIndex+2)
					{
						if(Boolean.parseBoolean(args[subIndex+2]))
						{
							Inventory fakeInventory=Bukkit.createInventory(inventory.getHolder(), inventory.getType());
							fakeInventory.setContents(inventory.getContents());
							player.openInventory(fakeInventory);
							return true;
						}
					}
					else
					{
						player.openInventory(inventory);
					}
					break;
			}
		}
		
		return true;
	}

}
