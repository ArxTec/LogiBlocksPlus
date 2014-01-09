package plugin.arxtec.LogiBlocksPlus.Commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import plugin.arxtec.LogiBlocksPlus.LogiBlocksMain;

public enum BaseCommands 
{
	ACCELERATE(AccelerateCommand.class, "accelerate", 4),
	DELAY(DelayCommand.class, "delay", 2),
	EJECT(EjectCommand.class, "eject", 1),
	EQUIP(EquipCommand.class, "equip", 3),
	EXPLODE(ExplodeCommand.class, "explode", 2),
	INVENTORY(InventoryCommand.class, "inventory", 1),
	KILL(KillCommand.class, "kill", 1),
	MESSAGE(MessageCommand.class, "message", 2),
	RAW_MESSAGE(RawMessageCommand.class, "rawMessage", 2),
	REDSTONE(RedstoneCommand.class, "redstone", 1),
	REPEAT(RepeatCommand.class, "repeat", 3),
	SAFE_TELEPORT(SafeTeleportCommand.class, "safeTeleport", 2),
	SET_DATA(SetDataCommand.class, "setData", 2),
	SET_FLAG(SetFlagCommand.class, "setFlag", 2),
	SET_GLOBAL_FLAG(SetGlobalFlagCommand.class, "setGlobalFlag", 2),
	SET_HEALTH(SetHealthCommand.class, "setHealth", 2),
	SPAWN(SpawnCommand.class, "spawn", 1),
	TELEPORT(TeleportCommand.class, "teleport", 2),
	VOXELSNIPER(VoxelSniperCommand.class, "voxelsniper", 1);
	
	private String name;
	private int minArgs;
	private ArrayList<String> aliases = new ArrayList<String>();
	private boolean enabled = false;
	private Class<? extends BaseCommand> commandClass;
	private BaseCommand command = null;
	
	
	private static HashMap<String, BaseCommands> BY_NAME = new HashMap<String, BaseCommands>();
	
	BaseCommands(Class<? extends BaseCommand> commandClass, String name, int minArgs)
	{
		this.commandClass = commandClass;
		this.name = name.toLowerCase();
		this.minArgs = minArgs;				
	}
	
	public static BaseCommands getByName(String name)
	{
		return BY_NAME.get(name);
	}
	
	public void addAlias(String alias)
	{
		aliases.add(alias.toLowerCase());
		BY_NAME.put(alias.toLowerCase(), this);
	}
	
	public boolean execute(CommandSender sender, String[] args, Location location)
	{
		return command.execute(sender, args, location);
	}
	
	public int getMinArgs()
	{
		return minArgs;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void initialise(LogiBlocksMain plugin)
	{
		try 
		{
			command = commandClass.getConstructor(LogiBlocksMain.class).newInstance(plugin);
		} 
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	static
	{
		for(BaseCommands command:BaseCommands.values())
		{
			BY_NAME.put(command.name, command);
		}
	}
}
