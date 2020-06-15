/*
package invasion;


import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;


public class SoundHandler
{

	public static SoundEvent bigzombie1;
	public static SoundEvent chime1;
	public static SoundEvent egghatch1;
	public static SoundEvent egghatch2;
	public static SoundEvent fireball1;
	public static SoundEvent rumble1;
	public static SoundEvent scrape1;
	public static SoundEvent scrape2;
	public static SoundEvent scrape3;
	public static SoundEvent v_death1;
	public static SoundEvent v_hiss1;
	public static SoundEvent v_longscreech1;
	public static SoundEvent v_screech1;
	public static SoundEvent v_screech2;
	public static SoundEvent v_screech3;
	public static SoundEvent v_squawk1;
	public static SoundEvent v_squawk2;
	public static SoundEvent v_squawk3;
	public static SoundEvent v_squawk4;
	public static SoundEvent zap1;
	public static SoundEvent zap2;
	public static SoundEvent zap3;

	private static int size = 0;

	public static void init()
	{
		bigzombie1 = register("bigzombie1");
		chime1 = register("chime1");
		egghatch1 = register("egghatch1");
		egghatch2 = register("egghatch2");
		fireball1 = register("fireball1");
		rumble1 = register("rumble1");
		scrape1 = register("scrape1");
		scrape2 = register("scrape2");
		scrape3 = register("scrape3");
		v_death1 = register("v_death1");
		v_hiss1 = register("v_hiss1");
		v_longscreech1 = register("v_longscreech1");
		v_screech1 = register("v_screech1");
		v_screech2 = register("v_screech2");
		v_screech3 = register("v_screech3");
		v_squawk1 = register("v_squawk1");
		v_squawk2 = register("v_squawk2");
		v_squawk3 = register("v_squawk3");
		v_squawk4 = register("v_squawk4");
		zap1 = register("zap1");
		zap2 = register("zap2");
		zap3 = register("zap3");
	}

	public static SoundEvent register(String soundLocation)
	{
		ResourceLocation resource = new ResourceLocation(Reference.MODID + ":" + soundLocation);
		SoundEvent snd = new SoundEvent(resource);
		SoundEvent.REGISTRY.register(size, resource, snd);
		size++;
		return snd;
	}

}
*/