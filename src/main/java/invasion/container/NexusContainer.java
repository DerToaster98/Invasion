package invasion.container;

import invasion.init.ModBlocks;
import invasion.init.ModContainerTypes;
import invasion.nexus.NexusMode;
import invasion.tileentity.NexusTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;


public class NexusContainer extends Container {
    public final NexusTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;
    private final IIntArray syncData;

    public static final int INDEX_HP=0, INDEX_ACTIVATION_TYPE =1,INDEX_GENERATION_PROGRESS=2, INDEX_ACTIVATION_PROGRESS=3, INDEX_COOKING_PROGRESS=4, INDEX_LEVEL=5, INDEX_RADIUS=6, INDEX_KILLS=7;
    public static final int SYNC_DATA_SIZE=8;

    public NexusContainer(final int windowId, final PlayerInventory playerInventory, final NexusTileEntity tileEntity, final IIntArray syncData) {
        super(ModContainerTypes.NEXUS.get(), windowId);
        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
        this.syncData = syncData;
        assertIntArraySize(syncData,SYNC_DATA_SIZE);

        //Input
        this.addSlot(new Slot(tileEntity, 0, 32, 33));
        //Output
        this.addSlot(new OutputSlot(tileEntity, 1, 102, 33));
        //Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(playerInventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }
        //Player Hotbar
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }


        this.trackIntArray(this.syncData);

    }

    public NexusContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new IntArray(SYNC_DATA_SIZE));
    }

    private static NexusTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "player inventory cannot be null");
        Objects.requireNonNull(data, "packet data cannot be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof NexusTileEntity) {
            return (NexusTileEntity) tileAtPos;
        }
        throw new IllegalStateException("tile entity at given position is not of type 'nexus'");
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(canInteractWithCallable, player, ModBlocks.NEXUS.get());
    }

    //TODO maybe transferItem?

    private static class OutputSlot extends Slot {
        public OutputSlot(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        @Override
        public boolean isItemValid(ItemStack itemstack) {
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getHp() {
        return syncData.get(INDEX_HP);
    }

    @OnlyIn(Dist.CLIENT)
    public NexusMode getMode() {
        return NexusMode.values()[syncData.get(INDEX_ACTIVATION_TYPE)];
    }

    @OnlyIn(Dist.CLIENT)
    public int getGenerationProgressScaled(int width) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getActivationProgressScaled(int width) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookingProgressScaled(int width) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getKills() {
        return syncData.get(INDEX_KILLS);
    }

    @OnlyIn(Dist.CLIENT)
    public int getRadius() {
        return syncData.get(INDEX_RADIUS);
    }

    @OnlyIn(Dist.CLIENT)
    public int getLevel() {
        return syncData.get(INDEX_LEVEL);
    }
}
