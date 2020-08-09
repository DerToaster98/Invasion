package invasion.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
    public static final ItemGroup INVASION = new InvasionItemGroup("invasion");

    public static class InvasionItemGroup extends ItemGroup {

        public InvasionItemGroup(String name) {
            super(name);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.NEXUS.get());
        }
    }
}
