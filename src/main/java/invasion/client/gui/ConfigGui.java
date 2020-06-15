/*
package invasion.client.gui;


import java.util.ArrayList;
import java.util.List;
import invasion.Reference;
import invasion.util.config.Config;
import invasion.util.config.Config.Section;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ConfigGui extends GuiConfig
{

	public ConfigGui(GuiScreen parentScreen)
	{
		super(parentScreen, getConfigElements(), Reference.MODID, false, false, I18n.format("gui.invasion.config.title"));
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (Section section : Config.sections)
		{
			list.add(new ConfigElement(Config.config
				.getCategory(section.getName())));
		}

		return list;
	}
}

 */
