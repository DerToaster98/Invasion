package invasion.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
    public static final ItemGroup INVASION = new ItemGroup("invasion") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.NEXUS.get());
        }
    };
}
