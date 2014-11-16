package invmod.client;

import cpw.mods.fml.common.network.Player;
import invmod.common.PacketHandlerCommon;
import invmod.common.mod_Invasion;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHandlerClient extends PacketHandlerCommon
{
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        try
        {
            byte type = dataStream.readByte();

            if (type == 0)
            {
                byte id = dataStream.readByte();
                mod_Invasion.playSingleSFX(id);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}