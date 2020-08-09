package invasion.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TrapBlock extends Block implements IBlockColor {

    public static final EnumProperty<Charge> CHARGE = EnumProperty.create("charge", Charge.class);

    public TrapBlock() {
        super(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.LANTERN));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

    }

    @Override
    public int getColor(BlockState blockState, @Nullable ILightReader lightReader, @Nullable BlockPos pos, int tintIndex) {
        return blockState.get(CHARGE).colour;
    }

    public enum Charge implements IStringSerializable {
        EMPTY("empty",0x000000),
        RIFT("rift",0x990099),
        FIRE("fire",0x990000),
        POISON("poison",0x00ff00);

        public final int colour;
        private final String name;

        Charge(String name, int colour) {
            this.name = name;
            this.colour = colour;
        }

        public String toString() {
            return this.name;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.name;
        }
    }
}