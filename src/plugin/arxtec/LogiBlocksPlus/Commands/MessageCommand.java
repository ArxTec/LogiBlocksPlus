package plugin.arxtec.LogiBlocksPlus.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public class MessageCommand extends BaseCommand
{

	public MessageCommand(LogiBlocksMain plugin) 
	{
		super(plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args, Location location) 
	{
		Player receiver = Bukkit.getPlayer(args[0]);
		if(receiver == null)
		{
			sender.sendMessage(ChatColor.RED + "That person doesn't exist!");
			return true;
		}
		
		String builder = "";
		for(int i = 1; i < args.length; i++)
		{
			String messagePiece = args[i];
			//format each piece of the message using minecraft format codes
			messagePiece = ChatColor.translateAlternateColorCodes('&', messagePiece);
			builder = builder + messagePiece + " ";
		}
		builder = builder.trim();
		receiver.sendMessage(sender.getName() + ": " + builder);		
		return true;
	}
}
