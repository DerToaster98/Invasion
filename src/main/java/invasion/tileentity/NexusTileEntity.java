package invasion.tileentity;

import invasion.block.NexusBlock;
import invasion.container.NexusContainer;
import invasion.init.ModBlocks;
import invasion.init.ModItems;
import invasion.init.ModTileEntityTypes;
import invasion.nexus.Nexus;
import invasion.nexus.NexusMode;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
//Todo which superclass to use?
public class NexusTileEntity extends LockableTileEntity implements ITickableTileEntity {

    //TODO maybe use two separate fields
    public final static int MAX_COOK_TIME = 1200, MAX_GENERATION_TIME = 3000, MAX_ACTIVATION_TIME = 400;

    private final NonNullList<ItemStack> contents = NonNullList.withSize(2, ItemStack.EMPTY);
    private final IItemHandlerModifiable items = createHandler();
    private final LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
    private final IIntArray syncData;
    private int numPlayersUsing;


    private NexusMode mode = NexusMode.MODE_0; // it seems like: { 0: inactive, 1: wave invasion ,2:continuous invasion, 3: strong wave invasion 4:? }?

    //the following fields are stored in NBT
    private int fluxGeneration;
    private int cookTime;

    private int activationTimer;


    public NexusTileEntity(TileEntityType<?> type) {
        super(type);
        assert world != null;

        syncData = new IIntArray() {
            @Override
            public int get(int i) {
                switch (i) {
                    case NexusContainer.INDEX_HP:
                        return Nexus.get(world).getHp();
                    case NexusContainer.INDEX_ACTIVATION_TYPE:
                        //TODO
                        return NexusTileEntity.this.mode.ordinal();
                    case NexusContainer.INDEX_GENERATION_PROGRESS:
                        return NexusTileEntity.this.fluxGeneration;
                    case NexusContainer.INDEX_ACTIVATION_PROGRESS:
                        return 0; //TODO;
                    case NexusContainer.INDEX_COOKING_PROGRESS:
                    case NexusContainer.INDEX_LEVEL:
                        return Nexus.get(world).getLevel();
                    case NexusContainer.INDEX_RADIUS:
                        return Nexus.get(world).getSpawnRadius();
                    case NexusContainer.INDEX_KILLS:
                        // return NexusTileEntity.this.invasion.getKills();
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

    public static int getNumPlayersUsing(IBlockReader reader, BlockPos pos) {
        BlockState blockstate = reader.getBlockState(pos);
        if (blockstate.hasTileEntity()) {
            TileEntity tileEntity = reader.getTileEntity(pos);
            if (tileEntity instanceof NexusTileEntity) {
                return ((NexusTileEntity) tileEntity).numPlayersUsing;
            }
        }
        return 0;
    }

    @Override
    public void tick() {

        if (world == null || world.isRemote) return;

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
            } else if (isInInputSlot(ModItems.RIFT_FLUX.get()) && contents.get(0).getDamage() == 0 /*TODO find out what this means*/) { /*
            if ((cookTime < 1200) && (nexusLevel >= 10)) {
                cookTime += 5;
            }
            else if(cookTime <= MAX_COOK_TIME) {
                if (nexusItemStacks[1] == null) {
                    nexusItemStacks[1] = new ItemStack(BlocksAndItems.itemStrongCatalyst, 1);
                    if ((nexusItemStacks[0].getCount() - 1) <= 0) nexusItemStacks[0] = null;
                    cookTime = 0;
                */
            }
        }
        if (activationTimer >= MAX_ACTIVATION_TIME) {
            activationTimer = 0;

            if (isInInputSlot(ModItems.CATALYST.get())) {
                Nexus.get(world).startInvasion(1, this);
            } else if (isInInputSlot(ModItems.STRONG_CATALYST.get())) {
                Nexus.get(world).startInvasion(10, this);
            } else if (isInInputSlot(ModItems.STABLE_CATALYST.get())) {
                Nexus.get(world).debugStartContinuous(this);
            }

            contents.get(0).shrink(1);
        }


    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new NexusContainer(id, playerInventory, this, syncData);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        super.read(nbt);
        fluxGeneration = nbt.getInt("fluxGeneration");
        cookTime = nbt.getInt("cookTime");
        activationTimer = nbt.getInt("activationTimer");
        ItemStackHelper.loadAllItems(nbt, contents);
    }

    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("fluxGeneration", fluxGeneration);
        nbt.putInt("cookTime", cookTime);
        nbt.putInt("activationTimer", activationTimer);
        ItemStackHelper.saveAllItems(nbt, contents);
        return nbt;
    }

    @Override
    public boolean isEmpty() {
        //TODO test this function
        return contents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i >= 0 && i < getSizeInventory() ? contents.get(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int i1, int i2) {
        return ItemStackHelper.getAndSplit(contents, i1, i2);
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return ItemStackHelper.getAndRemove(contents, i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        if (i >= 0 && i < getSizeInventory()) {
            contents.set(i, itemStack);
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (world.getTileEntity(pos) != this) {
            return false;
        }
        return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return i == 0 && true;//input only possible in input slot
        // stack.getItem(). //TODO is Nexus catalyst
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        return (Nexus.get(world).getNexusTE() == null || Nexus.get(world).getNexusTE() == this);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.nexus");
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (numPlayersUsing < 0) {
                numPlayersUsing = 0;
            }
            numPlayersUsing++;
            onOpenOrClose();
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            numPlayersUsing--;
            onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        //TODO
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        //TODO
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }

    @Override
    public void remove() {
        super.remove();
        itemHandler.invalidate();
    }

    @Override
    public void clear() {
        contents.clear();
    }

    public boolean isInInputSlot(Item item) {
        ItemStack inputStack = contents.get(0);
        return (!inputStack.isEmpty()) && inputStack.getItem() == item;
    }

    public void generateFlux(int increment) {
        fluxGeneration += increment;
        if (fluxGeneration >= MAX_GENERATION_TIME) {
            if (this.contents.get(1).isEmpty()) {
                contents.set(1, new ItemStack(ModItems.RIFT_FLUX.get()));
            } else if (contents.get(1).getItem() == ModItems.RIFT_FLUX.get()) {
                contents.get(1).grow(1);
            }
            fluxGeneration -= MAX_GENERATION_TIME;
        }
    }

    public void reset() {
        //TODO
        mode = NexusMode.MODE_0;
        clear();
        world.setBlockState(pos, ModBlocks.NEXUS.get().getDefaultState());

    }

    public NexusMode getMode() {
        return mode;
    }

    public void setMode(NexusMode mode) {
        this.mode = mode;
        this.setActive(mode != NexusMode.MODE_0);
    }

    //TODO
    public void setActive(boolean flag) {
        world.setBlockState(this.pos, this.getBlockState().with(NexusBlock.ACTIVATED, flag));
    }
 //TODO: remove
    //public boolean isActivating() {
   //     return (activationTimer > 0) && (activationTimer < 400);
   // }
}
