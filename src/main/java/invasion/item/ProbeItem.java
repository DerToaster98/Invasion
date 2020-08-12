package invasion.item;

import invasion.entity.monster.InvadingEntity;
import invasion.init.ModBlocks;
import invasion.nexus.Nexus;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ProbeItem extends Item {

    public ProbeItem(Item.Properties properties) {
        super(properties.maxStackSize(1).maxDamage(1));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (world.isRemote || player == null) return ActionResultType.FAIL;
        Block block = world.getBlockState(context.getPos()).getBlock();


        //Change Nexus spawn range
        if (block == ModBlocks.NEXUS.get()) {
            Nexus nexus = Nexus.get(context.getWorld());
            int newRange = nexus.getSpawnRadius();

            // check if the player wants to increase or decrease the range
            newRange += player.isSneaking() ? -8 : 8;
            if (newRange < 32) newRange = 128;
            if (newRange > 128) newRange = 32;
            nexus.setSpawnRadius(newRange);
            player.sendMessage(new TranslationTextComponent("message.range_changed", nexus.getSpawnRadius()));
            return ActionResultType.SUCCESS;
        }

        //Display block strength; material probe only
        if (stack.getDamage() == 1) {
            float blockStrength = InvadingEntity.getBlockStrength(context.getPos(), block, world);
            player.sendMessage(new TranslationTextComponent("message.block_strength", (int) ((blockStrength + 0.005D) * 100.0D) / 100.0D));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public int getItemEnchantability() {
        return 14;
    }


}