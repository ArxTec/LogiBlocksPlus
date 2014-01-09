package plugin.arxtec.LogiBlocksPlus.Listeners;

import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class LogiBlocksInteractListener implements Listener
{
	WeakHashMap<Player,Block> awaitingPlayers=new WeakHashMap<Player,Block>();
	LogiBlocksMain plugin;
	
	public LogiBlocksInteractListener(LogiBlocksMain plugin)
	{	
		this.plugin=plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK&&!(e.getPlayer().isOp()&&e.getPlayer().getGameMode()==GameMode.CREATIVE))
		{
			if(e.getClickedBlock().getType()==Material.COMMAND)
			{
				if(e.getPlayer().hasPermission("c.edit"))
				{
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.GREEN+"Current Command:");
					e.getPlayer().sendMessage(ChatColor.GRAY+((CommandBlock) e.getClickedBlock().getState()).getCommand());
					e.getPlayer().sendMessage(ChatColor.GREEN+"Please enter in your new command in chat, or type \"esc\" to cancel");
					awaitingPlayers.put(e.getPlayer(),e.getClickedBlock());
				}				
			}
		}		
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		if(awaitingPlayers.containsKey(e.getPlayer()))
		{
			e.setCancelled(true);
			if(e.getMessage().trim().equalsIgnoreCase("esc"))
			{
				e.getPlayer().sendMessage(ChatColor.RED+"Command editing cancelled");
				awaitingPlayers.remove(e.getPlayer());
				return;
			}
			else if(awaitingPlayers.get(e.getPlayer()).getType()==Material.COMMAND)
			{
				for(String perm:plugin.getConfig().getConfigurationSection("permissions").getKeys(false))
				{
					if(e.getPlayer().hasPermission("c.permission."+perm))
					{
						loop: for(String command:plugin.getConfig().getStringList("permissions."+perm))
						{
							command=command.replace("/", "");
							if(e.getMessage().length()>command.length())
							{
								for(int i=0;i<command.length();i++)
								{
									if(command.charAt(i)!=e.getMessage().charAt(i))
									{
										continue loop;
									}
								}
								BlockState state=awaitingPlayers.get(e.getPlayer()).getState();
								((CommandBlock) state).setCommand(e.getMessage());
								state.update();
								e.getPlayer().sendMessage(ChatColor.GREEN+"Command set to "+ChatColor.GRAY+e.getMessage());
								return;
							}
						}
					}
				}
				e.getPlayer().sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			else
			{
				e.getPlayer().sendMessage(ChatColor.RED+"The command block doesn't exist anymore!");
			}
			awaitingPlayers.remove(e.getPlayer());
		}
	}
}
