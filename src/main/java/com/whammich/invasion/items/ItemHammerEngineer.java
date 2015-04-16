package com.whammich.invasion.items;

import com.whammich.invasion.InvasionMod;
import com.whammich.invasion.Reference;
import net.minecraft.item.Item;

public class ItemHammerEngineer extends Item {

    public ItemHammerEngineer() {
        super();

        setUnlocalizedName(Reference.PREFIX + ".hammer.engineer");
        setTextureName(Reference.PREFIX + ":engyHammer");
        setCreativeTab(InvasionMod.tabInvasion);
        setMaxStackSize(1);
    }
}
