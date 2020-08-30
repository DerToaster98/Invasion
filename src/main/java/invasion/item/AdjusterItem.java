package invasion.item;

import invasion.init.ModBlocks;
import invasion.nexus.Nexus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class AdjusterItem extends Item {
    public AdjusterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();

        if (world.getBlockState(pos).getBlock() != ModBlocks.NEXUS.get()) return ActionResultType.FAIL;

        if (!world.isRemote && player != null) {
            Nexus nexus = Nexus.get(context.getWorld());

            if (!player.abilities.allowEdit) return ActionResultType.FAIL;
            int range = nexus.getSpawnRadius();

            // check if the player wants to increase or decrease the range
            range += player.isSneaking() ? -8 : 8;
            if (range < 32) range = 128;
            if (range > 128) range = 32;
            nexus.setSpawnRadius(range);
            player.sendMessage(new TranslationTextComponent("message.nexus.range_changed", nexus.getSpawnRadius()));
        }
        return ActionResultType.SUCCESS;
    }
}
