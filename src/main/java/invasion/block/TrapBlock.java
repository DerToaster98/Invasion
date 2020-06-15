package invasion.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TrapBlock extends Block {

    public static final EnumProperty<Charge> CHARGE = EnumProperty.create("charge", Charge.class);

    public TrapBlock() {
        super(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.LANTERN));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

    }

    public enum Charge implements IStringSerializable {
        EMPTY("empty"),
        RIFT("rift"),
        FIRE("fire"),
        POISON("poison");

        private final String name;

        Charge(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override @Nonnull
        public String getName() {
            return this.name;
        }
    }
}