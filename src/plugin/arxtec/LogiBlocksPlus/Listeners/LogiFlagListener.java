package plugin.arxtec.LogiBlocksPlus.Listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import plugin.arxtec.LogiBlocksPlus.FlagFailureException;
import plugin.arxtec.LogiBlocksPlus.FlagListener;
import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class LogiFlagListener implements FlagListener
{
	LogiBlocksMain plugin;
	
	private HashMap<String,Integer> inventorySubs=new HashMap<String,Integer>();
	
	public LogiFlagListener(LogiBlocksMain plugin)
	{
		this.plugin=plugin;
		
		inventorySubs.put("contains",1);
		inventorySubs.put("containsexact",1);
		inventorySubs.put("isfull",0);
		inventorySubs.put("isempty",0);
		inventorySubs.put("slot",2);
	}
	
	public boolean onFlag(String flag, String[] args, BlockCommandSender sender) throws FlagFailureException
	{
		switch(flag)
		{
			case "getflag":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				if(plugin.getFlagConfig().contains("local."+sender.getName()+"."+args[0]))
				{
					return plugin.getFlagConfig().getBoolean("local."+sender.getName()+"."+args[0]);
				}
				else
				{
					throw new FlagFailureException();
				}
				//end getflag
			case "isflag":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				if(plugin.getFlagConfig().contains("local."+sender.getName()+"."+args[0]))
				{
					return true;
				}
				else
				{
					return false;
				}
				//end isflag
			case "getglobalflag":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				if(plugin.getFlagConfig().contains("global."+args[0]))
				{
					return plugin.getFlagConfig().getBoolean("global."+args[0]);
				}
				else
				{
					throw new FlagFailureException();
				}
				//end getglobalflag
			case "isglobalflag":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				if(plugin.getFlagConfig().contains("global."+args[0]))
				{
					return true;
				}
				else
				{
					return false;
				}
				//end isglobalflag
			case "hasequip":
				switch(args.length)
				{
					case 1:
						Entity equipEntity=LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld());				
						if(equipEntity==null||!(equipEntity instanceof LivingEntity))
						{
							equipEntity=Bukkit.getPlayer(args[0]);
							if(equipEntity==null)
							{
								throw new FlagFailureException();
							}
						}
						ItemStack[] equip=((LivingEntity) equipEntity).getEquipment().getArmorContents();
						return equip[0].getType()==Material.AIR?equip[1].getType()==Material.AIR?equip[2].getType()==Material.AIR?equip[3].getType()==Material.AIR?false:true:true:true:true;
					case 2:
						Entity equipEntity1=LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld());				
						if(equipEntity1==null||!(equipEntity1 instanceof LivingEntity))
						{
							equipEntity1=Bukkit.getPlayer(args[0]);
							if(equipEntity1==null)
							{
								throw new FlagFailureException();
							}
						}
						ItemStack[] equip1=((LivingEntity) equipEntity1).getEquipment().getArmorContents();
						switch(args[1])
						{
							case "head":
							case "helmet":
								return equip1[3].getType()!=Material.AIR;
							case "chest":
							case "chestplate":
							case "shirt":
								return equip1[2].getType()!=Material.AIR;
							case "legs":
							case "leggings":
							case "pants":
								return equip1[1].getType()!=Material.AIR;
							case "feet":
							case "boots":
							case "shoes":
								return equip1[0].getType()!=Material.AIR;
						}
						break;
					case 3:
						ItemStack item=LogiBlocksMain.parseItemStack(args[2]);
						if(item==null)
						{
							throw new FlagFailureException();
						}
						Entity equipEntity2=LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld());				
						if(equipEntity2==null||!(equipEntity2 instanceof LivingEntity))
						{
							equipEntity2=Bukkit.getPlayer(args[0]);
							if(equipEntity2==null)
							{
								throw new FlagFailureException();
							}
						}
						ItemStack[] equip2=((LivingEntity) equipEntity2).getEquipment().getArmorContents();
						switch(args[1])
						{
							case "head":
							case "helmet":
								return equip2[3].equals(item);
							case "chest":
							case "chestplate":
							case "shirt":
								return equip2[2].equals(item);
							case "legs":
							case "leggings":
							case "pants":
								return equip2[1].equals(item);
							case "feet":
							case "boots":
							case "shoes":
								return equip2[0].equals(item);
						}
						break;
				}
				break;
				//end hasequip
			case "inventory":
				Inventory inventory= null;
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				if((args[0].startsWith("@l[")&&args[1].endsWith("]"))||inventorySubs.containsKey(args[0]))
				{
					Location invLocation=LogiBlocksMain.parseLocation(args[0], sender.getBlock().getLocation());
					loop: for(int x=-1;x<=1;x++)
					{
						for(int y=-1;y<=1;y++)
						{
							for(int z=-1;z<=1;z++)
							{
								Block testBlock=invLocation.clone().add(x, y, z).getBlock();
								if(testBlock.getState() instanceof InventoryHolder)
								{
									inventory=((InventoryHolder) testBlock.getState()).getInventory();
									break loop;
								}
							}
						}
					}
				}
				else
				{
					Player player=Bukkit.getPlayer(args[0]);
					if(player==null)
					{
						throw new FlagFailureException();
					}
					inventory=player.getInventory();
				}
				if(inventory==null)
				{
					throw new FlagFailureException();
				}
				int subIndex=inventorySubs.containsKey(args[0])?0:1;
				if(!inventorySubs.containsKey(args[subIndex]))
				{
					throw new FlagFailureException();
				}
				if(inventorySubs.get(args[subIndex])+subIndex+1>args.length)
				{
					throw new FlagFailureException();
				}					
				switch(args[subIndex].toLowerCase())
				{
					case "contains":
						ItemStack item=LogiBlocksMain.parseItemStack(args[subIndex+1]);
						if(item==null)
						{
							throw new FlagFailureException();
						}
						for(ItemStack testItem:inventory.getContents())
						{
							if(testItem==null)
							{
								continue;
							}
							else if(item.isSimilar(testItem))
							{
								return true;
							}
						}
						return false;
					case "containsexact":
						ItemStack item1=LogiBlocksMain.parseItemStack(args[subIndex+1]);
						if(item1==null)
						{
							throw new FlagFailureException();
						}
						return inventory.contains(item1);
					case "isfull":
						return inventory.firstEmpty()==-1;
					case "isempty":
						for(ItemStack testItem:inventory.getContents())
						{
							if(testItem!=null)
							{
								throw new FlagFailureException();
							}
						}
						return true;
					case "slot":
						int slot=Integer.parseInt(args[subIndex+1]);
						if(slot>=inventory.getSize())
						{
							slot=inventory.getSize()-1;
						}
						if(slot<0)
						{
							slot=0;
						}
						switch(args[subIndex+2].toLowerCase())
						{
							case "is":
								if(args.length<=subIndex+3)
								{
									throw new FlagFailureException();
								}
								ItemStack item2=LogiBlocksMain.parseItemStack(args[subIndex+3]);
								if(item2==null)
								{
									throw new FlagFailureException();
								}
								return item2.isSimilar(inventory.getItem(slot));
							case "isexact":
								if(args.length<=subIndex+3)
								{
									throw new FlagFailureException();
								}
								ItemStack item3=LogiBlocksMain.parseItemStack(args[subIndex+3]);
								if(item3==null)
								{
									throw new FlagFailureException();
								}
								return item3.equals(inventory.getItem(slot));
							case "isenchanted":
								ItemStack item4=inventory.getItem(slot);
								if(item4==null)
								{
									throw new FlagFailureException();
								}
								return !item4.getEnchantments().isEmpty();
						}
						break;
				}
				break;
				//end inventory
			case "exists":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				return LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld())!=null;
				//end exists
			case "haspassenger":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				Entity entity = LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld());
				if(entity==null)
				{
					throw new FlagFailureException();
				}
				return entity.getPassenger()!=null;
				//end hasPassanger
			case "ispassenger":
				if(args.length<1)
				{
					throw new FlagFailureException();
				}
				Entity entity1 = LogiBlocksMain.parseEntity(args[0],sender.getBlock().getWorld());
				if(entity1==null)
				{
					throw new FlagFailureException();
				}
				return entity1.getVehicle()!=null;
				//end isPassenger
			case "random":
				int chance=50;
				if(args.length>=1)
				{
					try
					{
						chance=Integer.parseInt(args[0]);
						chance=chance>100?100:chance;
						chance=chance<0?0:chance;
					}
					catch(NumberFormatException e)
					{}
				}
				return Math.random()*100<chance;
				//end random
		}
		throw new FlagFailureException();
	}
}
