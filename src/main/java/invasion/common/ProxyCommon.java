/*
package invasion.common;


import java.io.File;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class ProxyCommon
{

	public int addTextureOverride(String fileToOverride, String fileToAdd)
	{
		return 0;
	}

	public void broadcastToAll(String message)
	{
		//FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText(message));
		List<EntityPlayerMP> playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
		for (int i = 0; i < playerList.size(); i++)
		{
			playerList.get(i).sendMessage(new TextComponentString(message));
		}
	}

	public void printGuiMessage(String message)
	{
	}

	public void registerEntityRenderers()
	{
	}

	public void loadAnimations()
	{
	}

	public File getFile(String fileName)
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(fileName);
	}
}

 */