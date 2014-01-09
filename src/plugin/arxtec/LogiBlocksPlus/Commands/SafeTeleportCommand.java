package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class SafeTeleportCommand extends BaseCommand
{

	public SafeTeleportCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Entity safeTper=LogiBlocksMain.parseEntity(args[0], location.getWorld());
		if(safeTper==null)
		{
			sender.sendMessage(ChatColor.DARK_RED+"Entity not found.");
			return true;
		}
		Location safeTpLocation=LogiBlocksMain.parseLocation(args[1], safeTper.getLocation());
		for(int i=0;i<64;i++)
		{
			loop: for(int y=0-i;y<=i;y++)
			{
				for(int x=0-i;x<=i;x++)
				{
					for(int z=0-i;z<=i;z++)
					{								
						Location testSafeTpLocation=safeTpLocation.clone().add(x, y, z);
						if(testSafeTpLocation.getBlockY()>256||testSafeTpLocation.getBlockY()<2)
						{
							continue loop;
						}
						if(testSafeTpLocation.distance(safeTpLocation)>=i&&testSafeTpLocation.distance(safeTpLocation)<i+1)
						{
							
							if(testSafeTpLocation.getBlock().getType()==Material.AIR
									||testSafeTpLocation.getBlock().getType()==Material.STATIONARY_WATER)
							{
								if(testSafeTpLocation.getBlock().getRelative(BlockFace.UP).getType()==Material.AIR
										||testSafeTpLocation.getBlock().getRelative(BlockFace.UP).getType()==Material.STATIONARY_WATER)
								{
									if(!(testSafeTpLocation.getBlock().getRelative(BlockFace.DOWN).getType()==Material.AIR
											||testSafeTpLocation.getBlock().getRelative(BlockFace.DOWN).getType()==Material.LAVA
											||testSafeTpLocation.getBlock().getRelative(BlockFace.DOWN).getType()==Material.STATIONARY_LAVA
											||testSafeTpLocation.getBlock().getRelative(BlockFace.DOWN).getType()==Material.CACTUS))
									{
										plugin.teleport(safeTper,testSafeTpLocation);
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		sender.sendMessage(ChatColor.DARK_RED+"No nearby safe areas could be found!");
		return true;
	}

}
