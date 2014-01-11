package plugin.arxtec.LogiBlocksPlus.Commands;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperBrushes;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerE;
import com.thevoxelbox.voxelsniper.brush.perform.vPerformer;

import plugin.arxtec.LogiBlocksPlus.FakePlayer;
import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class VoxelSniperCommand extends BaseCommand
{
	private HashMap<String,Sniper> snipers=new HashMap<String,Sniper>();
	
	public VoxelSniperCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		if(!(sender instanceof BlockCommandSender))
		{
			sender.sendMessage(ChatColor.DARK_RED+"That can only be used by command blocks");
			return true;
		}
		BlockCommandSender block = (BlockCommandSender) sender;
		
		//Allows undos based on the "network" created by command block names
		if(args[0].equalsIgnoreCase("undo")||args[0].equalsIgnoreCase("u"))
		{
			if(snipers.containsKey(block.getName()))
			{
				Sniper sniper=snipers.get(block.getName());
				int undos=1;
				if(args.length>2)
				{
					try
					{
						undos=Integer.parseInt(args[1]);
					}
					catch(NumberFormatException e)
					{}
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
		Location snipeLocation = LogiBlocksMain.parseLocation(args[0], block.getBlock().getRelative(BlockFace.UP).getLocation());
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
		for(int i=1; i<args.length; i++)
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
						{}
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
					{}
					break;
				case "vr":
					try
					{
						replaceId=Integer.parseInt(att);
					}
					catch(NumberFormatException e)
					{}
					break;
				case "vi":
					try
					{
						data=Byte.parseByte(att);
					}
					catch(NumberFormatException e)
					{}
					break;
				case "vir":
					try
					{
						replaceData=Byte.parseByte(att);
					}
					catch(NumberFormatException e)
					{}
					break;
				case "vh":
					try
					{
						voxelHeight=Integer.parseInt(att);
					}
					catch(NumberFormatException e)
					{}
					break;
				case "bf":
					try
					{
						blockFace=BlockFace.valueOf(att);
					}
					catch(IllegalArgumentException e)
					{}							
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
		sniper.setPlayer(new FakePlayer());
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
		
		((FakePlayer)sniper.getPlayer()).setWorld(snipeLocation.getWorld());
						
		try 
		{
			//gets a VoxelSniper brush instance, and sets the variables to be able to run
			Brush brush=(Brush) SniperBrushes.getBrushInstance(brushName);
			
			//Check if brush is on blacklist
			List<String> blacklist=plugin.getConfig().getStringList("voxelsniper-blacklist");
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
				{}
			}					
			
			Field field=Brush.class.getDeclaredField("blockPositionX");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getBlockX());

			field=Brush.class.getDeclaredField("blockPositionY");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getBlockY());
			
			field=Brush.class.getDeclaredField("blockPositionZ");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getBlockZ());
			
			field=Brush.class.getDeclaredField("world");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getWorld());
			
			field=Brush.class.getDeclaredField("targetBlock");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getBlock());
			
			field=Brush.class.getDeclaredField("lastBlock");					
			field.setAccessible(true);
			field.set(brush, snipeLocation.getBlock().getRelative(blockFace));
			
			if(brush instanceof PerformBrush)
			{						
				vPerformer vperformer=PerformerE.getPerformer(performer);
				
				field=PerformBrush.class.getDeclaredField("current");
				field.setAccessible(true);
				field.set(brush, vperformer);
				
				field=vPerformer.class.getDeclaredField("w");
				field.setAccessible(true);
				field.set(vperformer, snipeLocation.getWorld());
				
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
		return true;
	}

}