package plugin.arxtec.LogiBlocksPlus.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class RedstoneCommand extends BaseCommand
{

	public RedstoneCommand(LogiBlocksMain plugin) 
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
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				for(Block lever:$levers)
				{
					lever.setData((byte) (lever.getData()-8),true);
				}
			}
		}, Integer.parseInt(args[0]));
		return true;
	}

}
