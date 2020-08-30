package invasion.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ProbeItem extends Item {

    public ProbeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        if (!world.isRemote && player != null) {
            BlockPos pos = context.getPos();
            //TODO
            // float blockStrength = InvadingEntity.getBlockStrength(context.getPos(), world.getBlockState(pos).getBlock(), world);
            float blockStrength = 10f;
            player.sendStatusMessage(new TranslationTextComponent("message.block_strength", (int) ((blockStrength + 0.005D) * 100.0D) / 100.0D), true);
        }
        return ActionResultType.SUCCESS;

    }
}