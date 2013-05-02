package plugin.bleachisback.LogiBlocks;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.Packet39AttachEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperBrushes;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
import com.thevoxelbox.voxelsniper.brush.perform.vPerformer;

public class BaseCommandListener implements CommandExecutor
{
	private LogiBlocksMain plugin;
	private Server server;
	private HashMap<String,Integer> minArgs=new HashMap<String,Integer>();
	private HashMap<String,String> aliases=new HashMap<String,String>();
	private HashMap<String,Integer> inventorySubs=new HashMap<String,Integer>();
	private HashMap<String,Sniper> snipers=new HashMap<String,Sniper>();

	public BaseCommandListener(LogiBlocksMain plugin)
	{
		this.plugin=plugin;
		this.server=plugin.getServer();
		minArgs.put("eject", 2);
		minArgs.put("kill", 2);
		minArgs.put("accelerate", 5);
		minArgs.put("delay", 3);
		minArgs.put("redstone", 2);
		minArgs.put("explode", 5);
		minArgs.put("equip", 4);
		minArgs.put("repeat", 4);
		minArgs.put("setflag", 3);
		minArgs.put("setglobalflag", 3);
		minArgs.put("inventory", 2);
		minArgs.put("teleport", 3);
		
		if(Bukkit.getPluginManager().getPlugin("VoxelSniper")!=null)
		{
			plugin.log.info("VoxelSniper detected - adding support");
			minArgs.put("voxelsniper", 1);
		}
		
		//Load aliases and disabled commands from config
		for(String name:minArgs.keySet().toArray(new String[0]))
		{
			if(plugin.config.contains("commands."+name))
			{
				if(plugin.config.getBoolean("commands."+name+".enabled"))
				{
					for(String alias:plugin.config.getStringList("commands."+name+".aliases"))
					{
						minArgs.put(alias.toLowerCase(), minArgs.get(name));
						aliases.put(alias.toLowerCase(), name);
					}
					aliases.put(name, name);
				}
				else
				{
					minArgs.remove(name);
				}
			}
			else
			{
				plugin.log.info("No profile detected for command: "+name);
				plugin.config.set("commands."+name+".enabled", true);
				plugin.config.set("commands."+name+".aliases", "");
				plugin.saveConfig();
				plugin.log.info("Default profile for "+name+" created");
			}
		}
		
		inventorySubs.put("add",1);
		inventorySubs.put("remove",1);
		inventorySubs.put("removeall",1);
		inventorySubs.put("clear",0);
		inventorySubs.put("refill",0);
		inventorySubs.put("set",2);
		inventorySubs.put("copy",1);
		inventorySubs.put("show",1);
	}

	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof BlockCommandSender)&&!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED+"That command can only be used in-game!");
			return true;
		}
		if(args.length<1)
		{
			return false;
		}
		Location loc;
		BlockCommandSender block = null;
		if(sender instanceof BlockCommandSender)
		{
			block=(BlockCommandSender) sender;
			loc=block.getBlock().getLocation();			
		}
		else
		{
			loc=((Player) sender).getLocation();
		}		
		if(!LogiBlocksMain.filter(args, sender, cmd, loc))
		{
			return false;
		}
		
		if(!minArgs.containsKey(args[0]))
		{
			return false;
		}
		if(args.length<minArgs.get(args[0]))
		{
			return false;
		}
		Entity entity=null;
		switch(aliases.get(args[0].toLowerCase()))
		{
			case "eject":
				entity=LogiBlocksMain.parseEntity(args[1],loc.getWorld());
				if(entity==null)
				{
					return false;
				}
				entity.leaveVehicle();
				break;
				//end eject
			case "kill":
				entity=LogiBlocksMain.parseEntity(args[1],loc.getWorld());
				if(entity==null)
				{
					return false;
				}
				if(entity instanceof LivingEntity)
				{
					((LivingEntity)entity).setHealth(0);
				}
				else
				{
					entity.remove();
				}
				break;
				//end kill
			case "accelerate":
				entity=LogiBlocksMain.parseEntity(args[1],loc.getWorld());
				if(entity==null)
				{
					return false;
				}
				entity.setVelocity(new Vector(Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4])));
				break;
				//end accelerate
			case "delay":
				String command="";
				for(int i=2;i<args.length;i++)
				{
					command=command+args[i]+" ";
				}
				command.trim();
				final String $command=command;
				server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					public void run()
					{
						server.dispatchCommand(sender, $command);
					}
				}, Integer.parseInt(args[1]));
				break;
				//end delay
			case "redstone":
				if(!(sender instanceof BlockCommandSender))
				{
					sender.sendMessage(ChatColor.DARK_RED+"That can only be used by command blocks");
					return true;
				}
				ArrayList<Block> levers=new ArrayList<Block>();
				for(BlockFace face:BlockFace.values())
				{					
					Block $block=block.getBlock().getRelative(face);
					if($block.getType()==Material.LEVER&&$block.getData()<8)
					{
						$block.setData((byte)($block.getData()+8),true);
						levers.add($block);
					}
				}
				if(levers.isEmpty())
				{
					return false;
				}
				final ArrayList<Block> $levers=levers;
				server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					public void run()
					{
						for(Block lever:$levers)
						{
							lever.setData((byte) (lever.getData()-8),true);
						}
					}
				}, Integer.parseInt(args[1]));
				break;
				//end redstone
			case "explode":
				Location expLoc=LogiBlocksMain.parseLocation(args[1], loc);
				if(args.length>=7)
				{
					loc.getWorld().createExplosion(expLoc.getX(),expLoc.getY(),expLoc.getZ(),Integer.parseInt(args[2]),Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]));
				}
				else if(args.length==6)
				{
					loc.getWorld().createExplosion(expLoc,Integer.parseInt(args[2]),Boolean.parseBoolean(args[3]));
				}
				else
				{
					loc.getWorld().createExplosion(expLoc,Integer.parseInt(args[2]));
				}
				break;
				//end explode
			case "equip":
				switch(args[2].toLowerCase())
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
				ItemStack item=LogiBlocksMain.parseItemStack(args[3]);
				Entity equipEntity=LogiBlocksMain.parseEntity(args[1],loc.getWorld());				
				if(equipEntity==null||!(equipEntity instanceof LivingEntity))
				{
					return false;
				}
				switch(args[2].toLowerCase())
				{
					case "head":
					case "helmet":
						((LivingEntity) equipEntity).getEquipment().setHelmet(item);
						break;
					case "chest":
					case "chestplate":
					case "shirt":
						((LivingEntity) equipEntity).getEquipment().setChestplate(item);
						break;
					case "legs":
					case "leggings":
					case "pants":
						((LivingEntity) equipEntity).getEquipment().setLeggings(item);
						break;
					case "feet":
					case "boots":
					case "shoes":
						((LivingEntity) equipEntity).getEquipment().setBoots(item);
						break;
				}
				break;
				//end equip
			case "repeat":
				String command1="";
				for(int i=3;i<args.length;i++)
				{
					command1=command1+args[i]+" ";
				}
				command1.trim();
				final String $command1=command1;
				final int id=server.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
				{
					public void run()
					{
						server.dispatchCommand(sender, $command1);
					}
				}, 0, Integer.parseInt(args[1]));
				server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					public void run()
					{
						server.getScheduler().cancelTask(id);
					}
				}, Integer.parseInt(args[2])*Integer.parseInt(args[1]));
				break;
				//end repeat
			case "setflag":
				if(args[2].toLowerCase().equals("true"))
				{
					plugin.flagConfig.set("local."+block.getName()+"."+args[1], true);
				}
				else if(args[2].toLowerCase().equals("false"))
				{
					plugin.flagConfig.set("local."+block.getName()+"."+args[1], false);
				}
				else if(plugin.flags.containsKey(args[2]))
				{					
					try 
					{
						plugin.flagConfig.set("local."+block.getName()+"."+args[1], plugin.flags.get(args[2]).onFlag(args[2], Arrays.copyOfRange(args, 3, args.length+1), (BlockCommandSender) sender));
					} 
					catch (FlagFailureException e) 
					{
						return true;
					}
				}
				try 
				{
					plugin.flagConfig.save(LogiBlocksMain.flagFile);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				break;
				//end setflag
			case "setglobalflag":
				if(args[2].toLowerCase().equals("true"))
				{
					plugin.flagConfig.set("global."+args[1], true);
				}
				else if(args[2].toLowerCase().equals("false"))
				{
					plugin.flagConfig.set("global."+args[1], false);
				}
				else if(plugin.flags.containsKey(args[2]))
				{					
					try 
					{
						plugin.flagConfig.set("global."+args[1], plugin.flags.get(args[2]).onFlag(args[2], Arrays.copyOfRange(args, 3, args.length+1), (BlockCommandSender) sender));
					} 
					catch (FlagFailureException e) 
					{
						return true;
					}
				}
				try 
				{
					plugin.flagConfig.save(LogiBlocksMain.flagFile);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				break;
				//end setglobalflag
			case "inventory":
				ArrayList<Inventory> inventoryList= new ArrayList<Inventory>();
				if((args[1].startsWith("@l[")&&args[1].endsWith("]"))||inventorySubs.containsKey(args[1]))
				{
					Location invLocation=LogiBlocksMain.parseLocation(args[1], loc);
					for(int x=-1;x<=1;x++)
					{
						for(int y=-1;y<=1;y++)
						{
							for(int z=-1;z<=1;z++)
							{
								Block testBlock=invLocation.clone().add(x, y, z).getBlock();
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
					Player player=Bukkit.getPlayer(args[1]);
					if(player==null)
					{
						return false;
					}
					inventoryList.add(player.getInventory());
				}
				int subIndex=inventorySubs.containsKey(args[1])?1:2;
				if(!inventorySubs.containsKey(args[subIndex]))
				{
					return false;
				}
				if(inventorySubs.get(args[subIndex])+subIndex+1>args.length)
				{
					return false;
				}
				for(Inventory inventory:inventoryList)
				{					
					switch(args[subIndex])
					{
						case "add":
							for(int i=subIndex+1;i<args.length;i++)
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
								Block targetBlock=LogiBlocksMain.parseLocation(args[subIndex+1], loc).getBlock();
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
				break;
				//end inventory
			case "voxelsniper":
				if(!(sender instanceof BlockCommandSender))
				{
					sender.sendMessage(ChatColor.DARK_RED+"That can only be used by command blocks");
					return true;
				}
				//Allows undos based on the "network" created by command block names
				if(args[1].equalsIgnoreCase("undo")||args[1].equalsIgnoreCase("u"))
				{
					if(snipers.containsKey(block.getName()))
					{
						Sniper sniper=snipers.get(block.getName());
						int undos=1;
						if(args.length>2)
						{
							try
							{
								undos=Integer.parseInt(args[2]);
							}
							catch(NumberFormatException e)
							{							
							}
						}
						LinkedList<Undo> undoList=sniper.getUndoList();
						if(undoList.isEmpty())
						{
							return false;
						}
						for(int i=0;i<undos;i++)
						{
							Undo undo=undoList.pollLast();
							if(undo!=null)
							{
								undo.undo();
							}
							else
							{
								break;
							}
						}						
					}
					return true;
				}
				//Sets where the brush should be used at, should by the first arg after sub-command
				Location location=LogiBlocksMain.parseLocation(args[1], block.getBlock().getRelative(BlockFace.UP).getLocation());
				//Default brush attributes
				String brushName="snipe";
				String performer="m";
				int brushSize=3;
				int voxelId=0;
				int replaceId=0;
				byte data=0;
				byte replaceData=0;
				int voxelHeight=1;
				BlockFace blockFace=BlockFace.UP;
				
				ArrayList<String> voxelArgs = new ArrayList<String>();
				
				//goes through every following arg and parses the attributes
				//Attributes are named similarly to their voxelsniper command, meaning voxelId is v and replaceId is vr
				for(int i=2; i<args.length; i++)
				{
					//Brush arguments are recognised if they do not have an equal sign in them
					if(!args[i].contains("="))
					{
						if(voxelArgs.isEmpty())
						{
							voxelArgs.add("");
						}
						voxelArgs.add(args[i]);
						continue;
					}
					
					args[i]=args[i].replace("-", "");	
					String att=args[i].substring(args[i].indexOf('=')+1,args[i].length());
					
					switch(args[i].substring(0, args[i].indexOf('=')))
					{
						case "b":
							//Check if brush exists
							if(SniperBrushes.hasBrush(att))
							{
								brushName=att;
							}
							//If not, try to set brush size instead
							else
							{
								try
								{
									brushSize=Integer.parseInt(att);
								}
								catch(NumberFormatException e)
								{
								}
							}
							break;
						case "p":
							//Check if performer exists
							//Only works with short names
							if(PerformerE.has(att))
							{
								performer=att;
							}
							break;
						case "v":
							try
							{
								voxelId=Integer.parseInt(att);
							}
							catch(NumberFormatException e)
							{								
							}
							break;
						case "vr":
							try
							{
								replaceId=Integer.parseInt(att);
							}
							catch(NumberFormatException e)
							{								
							}
							break;
						case "vi":
							try
							{
								data=Byte.parseByte(att);
							}
							catch(NumberFormatException e)
							{								
							}
							break;
						case "vir":
							try
							{
								replaceData=Byte.parseByte(att);
							}
							catch(NumberFormatException e)
							{								
							}
							break;
						case "vh":
							try
							{
								voxelHeight=Integer.parseInt(att);
							}
							catch(NumberFormatException e)
							{								
							}
							break;
						case "bf":
							blockFace=BlockFace.valueOf(att);
							if(blockFace==null)
							{
								blockFace=BlockFace.UP;
							}
							break;
					}					
				}
				//end for				
				//Each sniper is based on the network created by named command blocks
				//Each "network" will store its own undos
				Sniper sniper=new Sniper();
				if(snipers.containsKey(block.getName()))
				{
					sniper=snipers.get(block.getName());
				}
				else
				{
					snipers.put(block.getName(), sniper);
				}
				//Instantiates a new SnipeData object from VoxelSniper
				SnipeData snipeData=new SnipeData(sniper);
				snipeData.setBrushSize(brushSize);
				snipeData.setData(data);
				snipeData.setReplaceData(replaceData);
				snipeData.setReplaceId(replaceId);
				snipeData.setVoxelHeight(voxelHeight);
				snipeData.setVoxelId(voxelId);
								
				try 
				{
					//gets a VoxelSniper brush instance, and sets the variables to be able to run
					Brush brush=(Brush) SniperBrushes.getBrushInstance(brushName);
					
					//Check if brush is on blacklist
					List<String> blacklist=plugin.config.getStringList("voxelsniper-blacklist");
					for(String blackbrush:blacklist)
					{
						if(blackbrush.equalsIgnoreCase(brush.getName()))
						{
							return true;
						}
					}
					
					//Set brush arguments
					if(!voxelArgs.isEmpty())
					{
						try
						{
							brush.parameters(voxelArgs.toArray(new String[0]), snipeData);
						}
						catch(NullPointerException e)
						{
						}
					}					
					
					Field field=Brush.class.getDeclaredField("blockPositionX");					
					field.setAccessible(true);
					field.set(brush, location.getBlockX());

					field=Brush.class.getDeclaredField("blockPositionY");					
					field.setAccessible(true);
					field.set(brush, location.getBlockY());
					
					field=Brush.class.getDeclaredField("blockPositionZ");					
					field.setAccessible(true);
					field.set(brush, location.getBlockZ());
					
					field=Brush.class.getDeclaredField("world");					
					field.setAccessible(true);
					field.set(brush, location.getWorld());
					
					field=Brush.class.getDeclaredField("targetBlock");					
					field.setAccessible(true);
					field.set(brush, location.getBlock());
					
					field=Brush.class.getDeclaredField("lastBlock");					
					field.setAccessible(true);
					field.set(brush, location.getBlock().getRelative(blockFace));
					
					if(brush instanceof PerformBrush)
					{						
						vPerformer vperformer=PerformerE.getPerformer(performer);
						
						field=PerformBrush.class.getDeclaredField("current");
						field.setAccessible(true);
						field.set(brush, vperformer);
						
						field=vPerformer.class.getDeclaredField("w");
						field.setAccessible(true);
						field.set(vperformer, location.getWorld());
						
						for(Field testField:vperformer.getClass().getDeclaredFields())
						{
							switch(testField.getName())
							{
								case "i":
									testField.setAccessible(true);
									testField.set(vperformer, voxelId);
									break;
								case "d":
									testField.setAccessible(true);
									testField.set(vperformer, data);
									break;
								case "r":
									testField.setAccessible(true);
									testField.set(vperformer, replaceId);
									break;
								case "dr":
									testField.setAccessible(true);
									testField.set(vperformer, replaceData);
									break;
							}
						}
						
						vperformer.setUndo();
					}
					
					//Runs the brush
					Method method=Brush.class.getDeclaredMethod("arrow", SnipeData.class);
					method.setAccessible(true);
					method.invoke(brush, snipeData);
				} 
				catch (NoSuchMethodException | NoSuchFieldException | SecurityException | InvocationTargetException | IllegalAccessException e) 
				{
					e.printStackTrace();
				}
				break;
				//end voxelsniper
			case "teleport":
				Entity tper=LogiBlocksMain.parseEntity(args[1], loc.getWorld());
				if(tper==null)
				{
					return false;
				}
				while(tper.getVehicle()!=null)
				{
					tper=tper.getVehicle();
				}				
				Location tpLocation=LogiBlocksMain.parseLocation(args[2], tper.getLocation());
				Entity tpPassenger=tper.getPassenger();
				tper.eject();
				tper.teleport(tpLocation);
				tper.setPassenger(tpPassenger);
				if(tpPassenger instanceof Player)
				{
					final EntityPlayer entPlayer=((CraftPlayer)tpPassenger).getHandle();
					server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
					{
						public void run() 
						{
							entPlayer.playerConnection.sendPacket(new Packet39AttachEntity(entPlayer,entPlayer.vehicle));
						}						
					}, 1);
				}
				break;
				//end teleport
		}
		return false;
	}
}
