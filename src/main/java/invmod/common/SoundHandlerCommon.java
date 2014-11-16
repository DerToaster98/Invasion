package invmod.common;

import java.util.HashMap;
import java.util.Map;

public class SoundHandlerCommon
{
    protected Map<Byte, String> sfxMapToString;
    protected Map<String, Byte> sfxMapToByte;
    protected boolean soundsInstalled;
    protected boolean soundEnabled;

    public SoundHandlerCommon()
    {
        this.sfxMapToString = new HashMap();
        this.sfxMapToByte = new HashMap();
        this.soundsInstalled = false;
        this.soundEnabled = true;
        addNetworkSoundMapping("random.explode", (byte)0);
        addNetworkSoundMapping("invmod:scrape", (byte)1);
        addNetworkSoundMapping("invmod:chime", (byte)2);
        addNetworkSoundMapping("invmod:rumble", (byte)3);
        addNetworkSoundMapping("invmod:zap", (byte)4);
        addNetworkSoundMapping("invmod:fireball", (byte)5);
        addNetworkSoundMapping("invmod:bigzombie", (byte)6);
        addNetworkSoundMapping("invmod:egghatch", (byte)7);
        addNetworkSoundMapping("invmod:v_squawk", (byte)8);
        addNetworkSoundMapping("invmod:v_hiss", (byte)9);
        addNetworkSoundMapping("invmod:v_screech", (byte)10);
        addNetworkSoundMapping("invmod:v_longscreech", (byte)11);
        addNetworkSoundMapping("invmod:v_death", (byte)12);
    }

    public void addNetworkSoundMapping(String soundName, byte id)
    {
        this.sfxMapToByte.put(soundName, Byte.valueOf(id));
        this.sfxMapToString.put(Byte.valueOf(id), soundName);
    }

    public void playGlobalSFX(String s)
    {
        if (this.sfxMapToByte.containsKey(s))
        {
            mod_Invasion.sendInvasionPacketToAll(new byte[] { 0, ((Byte)this.sfxMapToByte.get(s)).byteValue() });
        }
    }

    public void playSingleSFX(String s)
    {
    }

    public void playSingleSFX(byte id)
    {
    }

    public void setSoundEnabled(boolean enabled)
    {
        this.soundEnabled = enabled;
    }

    public boolean soundsInstalled()
    {
        return this.soundsInstalled;
    }
}