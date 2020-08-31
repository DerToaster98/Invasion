package invasion.tileentity;

import invasion.block.NexusBlock;
import invasion.container.NexusContainer;
import invasion.init.ModBlocks;
import invasion.init.ModItems;
import invasion.init.ModTileEntityTypes;
import invasion.nexus.Nexus;
import invasion.nexus.NexusMode;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NexusTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public final static int MAX_COOK_TIME = 1200, MAX_GENERATION_TIME = 3000, MAX_ACTIVATION_TIME = 400;

    private final NexusItemHandler items = new NexusItemHandler(2);
    private final IIntArray syncData;

    private NexusMode mode = NexusMode.OFF; // it seems like: { 0: inactive, 1: wave invasion ,2:continuous invasion, 3: strong wave invasion 4:? }?

    //the following fields are stored in NBT
    private int fluxGeneration;
    private int cookTime;
    private int activationTimer;


    public NexusTileEntity(TileEntityType<?> type) {
        super(type);

        syncData = new IIntArray() {
            @Override
            public int get(int i) {
                if(world == null) return 0;
                switch (i) {
                    case NexusContainer.INDEX_HP:
                        return Nexus.get(world).getHp();
                    case NexusContainer.INDEX_MODE:
                        return NexusTileEntity.this.getMode().ordinal();
                    case NexusContainer.INDEX_GENERATION_PROGRESS:
                        return NexusTileEntity.this.fluxGeneration;
                    case NexusContainer.INDEX_ACTIVATION_PROGRESS:
                        return NexusTileEntity.this.activationTimer;
                    case NexusContainer.INDEX_COOKING_PROGRESS:
                        return NexusTileEntity.this.cookTime;
                    case NexusContainer.INDEX_LEVEL:
                        return Nexus.get(world).getLevel();
                    case NexusContainer.INDEX_RADIUS:
                        return Nexus.get(world).getSpawnRadius();
                    case NexusContainer.INDEX_KILLS:
                        return Nexus.get(world).getKills();
                    case NexusContainer.INDEX_ACTIVATION_MODE:
                        // 0 for wave, 1 for stable invasion
                        return NexusTileEntity.this.getMode() == NexusMode.CONTINUOUS_INVASION || NexusTileEntity.this.items.getStackInSlot(0).getItem() == ModItems.STABLE_CATALYST.get() ? 0x0000ff : 0xff0000;
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int i, int i1) {
                throw new IllegalStateException("Cannot modify nexus data from client side");
            }

            @Override
            public int size() {
                return NexusContainer.SYNC_DATA_SIZE;
            }
        };
    }

    public NexusTileEntity() {
        this(ModTileEntityTypes.NEXUS.get());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, PlayerInventory playerInv, PlayerEntity player) {
        return new NexusContainer(windowId, playerInv, this, syncData);
    }

    @Override
    public void tick() {

        boolean dirty = false;

        if (world != null && !world.isRemote) {

            if (mode == NexusMode.OFF) {
                ItemStack input = items.getStackInSlot(0);
                if (!input.isEmpty() && input.getItem().isIn(ItemTags.COALS)) {
                    dirty = true;
                    activationTimer++;
                    if (activationTimer >= MAX_ACTIVATION_TIME) {
                        activationTimer = 0;

                        if (input.getItem() == ModItems.CATALYST.get()) {
                            Nexus.get(world).startInvasion(1, this);
                        } else if (input.getItem() == ModItems.STRONG_CATALYST.get()) {
                            Nexus.get(world).startInvasion(10, this);
                        } else if (input.getItem() == ModItems.STABLE_CATALYST.get()) {
                            Nexus.get(world).debugStartContinuous(this);
                        }
                        setActive(true);
                        world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
                        items.decrStackSize(0, 1);
                    }
                }
                if(activationTimer > 0 && input.isEmpty()) {
                    activationTimer = 0;
                    dirty = true;
                }
            } else {
                //generateFlux(1);
                dirty = true;
            }





        /*
        if (contents.get(0).isEmpty()) {
            cookTime = 0;
            activationTimer = 0;
        } else {
            if (isInInputSlot(Items.BRICK)) {
                if (cookTime < MAX_COOK_TIME) {
                    if (mode == NexusMode.MODE_0)
                        cookTime += 1;
                    else {
                        cookTime += 9;

                    }
                } else {
                    //TODO rift trap generation
                }
            } else if (isInInputSlot(ModItems.RIFT_FLUX.get()) && contents.get(0).getDamage() == 0 /*TODO find out what this means*/
             /*
            if ((cookTime < 1200) && (nexusLevel >= 10)) {
                cookTime += 5;
            }
            else if(cookTime <= MAX_COOK_TIME) {
                if (nexusItemStacks[1] == null) {
                    nexusItemStacks[1] = new ItemStack(BlocksAndItems.itemStrongCatalyst, 1);
                    if ((nexusItemStacks[0].getCount() - 1) <= 0) nexusItemStacks[0] = null;
                    cookTime = 0;
                */
            //    }
            //}


        }


        if (dirty) {
            markDirty();
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(this.pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt) {
        this.read(nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> items));
    }

    public final IItemHandler getInventory() {
        return items;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        super.read(nbt);
        fluxGeneration = nbt.getInt("flux");
        cookTime = nbt.getInt("cook");
        activationTimer = nbt.getInt("activation");
        NonNullList<ItemStack> inv = NonNullList.withSize(items.getSlots(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, inv);
        items.setNonNullList(inv);
    }

    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("flux", fluxGeneration);
        nbt.putInt("cook", cookTime);
        nbt.putInt("activation", activationTimer);
        ItemStackHelper.saveAllItems(nbt, items.toNonNullList());
        return nbt;
    }

    public boolean isInInputSlot(Item item) {
        ItemStack inputStack = items.getStackInSlot(0);
        return (!inputStack.isEmpty()) && inputStack.getItem() == item;
    }

    public void generateFlux(int increment) {
        fluxGeneration += increment;
        if (fluxGeneration >= MAX_GENERATION_TIME) {
            /*if (items.getStackInSlot(1).getItem() == ModItems.RIFT_FLUX.get()) {
                items.getStackInSlot(1).grow(1);
            } else {
                items.setStackInSlot(1, new ItemStack(ModItems.RIFT_FLUX.get()));

            }
             */
            items.insertItem(1, new ItemStack(ModItems.RIFT_FLUX.get()), false);
            fluxGeneration -= MAX_GENERATION_TIME;
            markDirty();
        }
    }

    public void reset() {
        //TODO
        mode = NexusMode.OFF;
        items.clear();
        world.setBlockState(pos, ModBlocks.NEXUS.get().getDefaultState());

    }

    public NexusMode getMode() {
        return mode;
    }

    public void setMode(NexusMode mode) {
        this.mode = mode;
        this.setActive(mode != NexusMode.OFF);
    }

    public void setActive(boolean flag) {
        world.setBlockState(this.pos, this.getBlockState().with(NexusBlock.ACTIVATED, flag));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.nexus");
    }

    public static class NexusItemHandler extends ItemStackHandler {

        public NexusItemHandler(int size, ItemStack... stacks) {
            super(size);

            for (int index = 0; index < stacks.length; index++) {
                this.stacks.set(index, stacks[index]);
            }
        }

        public void clear() {
            for (int index = 0; index < this.getSlots(); index++) {
                this.stacks.set(index, ItemStack.EMPTY);
                this.onContentsChanged(index);
            }
        }

        public boolean isEmpty() {
            for (ItemStack stack : this.stacks) {
                if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                    return true;
                }
            }
            return false;
        }

        public ItemStack decrStackSize(int index, int count) {
            ItemStack stack = getStackInSlot(index);
            stack.shrink(count);
            this.onContentsChanged(index);
            return stack;
        }

        public void removeStackFromSlot(int index) {
            this.stacks.set(index, ItemStack.EMPTY);
            this.onContentsChanged(index);
        }

        public NonNullList<ItemStack> toNonNullList() {
            NonNullList<ItemStack> items = NonNullList.create();
            items.addAll(this.stacks);
            return items;
        }

        public void setNonNullList(NonNullList<ItemStack> items) {
            if (items.size() == 0)
                return;
            if (items.size() != this.getSlots())
                throw new IndexOutOfBoundsException("NonNullList must be same size as ItemStackHandler!");
            for (int index = 0; index < items.size(); index++) {
                this.stacks.set(index, items.get(index));
            }
        }

        @Override
        public String toString() {
            return this.stacks.toString();
        }
    }
}
