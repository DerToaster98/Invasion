package invasion.entity.projectile;


import invasion.init.ModEntityTypes;
import invasion.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;


public class BoltEntity extends Entity implements IEntityAdditionalSpawnData {
    private final long timeCreated;
    private final double[][] vertices = new double[3][0];
    private int age;
    private short ticksToRender;
    private long lastVertexUpdate;
    private float yaw;
    private float pitch;
    private double distance;
    private float widthVariance = 6.0f;
    private float vecX;
    private float vecY;
    private float vecZ;


    public BoltEntity(World world) {
        super(ModEntityTypes.BOLT, world);
        timeCreated = (this.lastVertexUpdate = System.currentTimeMillis());
        ignoreFrustumCheck = true;
    }

    public BoltEntity(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    public BoltEntity(World world, double x, double y, double z, double x2, double y2, double z2, short ticksToRender) {
        this(world, x, y, z);
        vecX = ((float) (x2 - x));
        vecY = ((float) (y2 - y));
        vecZ = ((float) (z2 - z));
        this.ticksToRender = ticksToRender;
        setHeading(this.vecX, this.vecY, this.vecZ);
        doVertexUpdate();
    }

    @Override
    protected void registerData() {
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeShort(ticksToRender);
        buffer.writeFloat(vecX);
        buffer.writeFloat(vecY);
        buffer.writeFloat(vecZ);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        ticksToRender = buffer.readShort();
        setHeading(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        doVertexUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        age += 1;
        if ((age == 1)) {
            playSound(ModSounds.ZAP.get(), 1f, 1f);
        }
        if (this.age > this.ticksToRender)
            remove();
    }

    public double[][] getVertices() {
        long time = System.currentTimeMillis();
        if (time - this.timeCreated > this.ticksToRender * 50) {
            return null;
        }
        if (time - this.lastVertexUpdate >= 75L) {
            this.doVertexUpdate();
            while (this.lastVertexUpdate + 50L <= time) {
                this.lastVertexUpdate += 50L;
            }
        }
        return this.vertices;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    private void setHeading(float x, float y, float z) {
        float xzSq = x * x + z * z;
        yaw = ((float) (Math.atan2(x, z) * 180.0D / Math.PI) + 90.0F);
        pitch = ((float) (Math.atan2(MathHelper.sqrt(xzSq), y) * 180.0D / Math.PI));
        distance = Math.sqrt(xzSq + y * y);
    }

    private void doVertexUpdate() {
        world.getProfiler().startSection("Bolt vertex generation");
        widthVariance = (10.0F / (float) Math.log10(distance + 1.0D));
        int numberOfVertexes = 60;
        if (numberOfVertexes != vertices[0].length) {
            vertices[0] = new double[numberOfVertexes];
            vertices[1] = new double[numberOfVertexes];
            vertices[2] = new double[numberOfVertexes];
        }

        for (int vertex = 0; vertex < numberOfVertexes; vertex++) {
            vertices[1][vertex] = (vertex * this.distance / (numberOfVertexes - 1));
        }

        createSegment(0, numberOfVertexes - 1);
        world.getProfiler().endSection();
    }

    private void createSegment(int begin, int end) {
        int points = end + 1 - begin;
        if (points <= 4) {
            createVertex(begin, begin + 1, end);
            if (points != 3) {
                createVertex(begin, begin + 2, end);
            }
            return;
        }
        int midPoint = begin + points / 2;
        createVertex(begin, midPoint, end);
        createSegment(begin, midPoint);
        createSegment(midPoint, end);
    }

    private void createVertex(int begin, int mid, int end) {
        double difference = vertices[0][end] - vertices[0][begin];
        double yDiffToMid = vertices[1][mid] - vertices[1][begin];
        double yRatio = yDiffToMid
                / (vertices[1][end] - vertices[1][begin]);
        vertices[0][mid] = (vertices[0][begin] + difference * yRatio + (world.rand
                .nextFloat() - 0.5D) * yDiffToMid * widthVariance);
        difference = vertices[2][end] - vertices[2][begin];
        vertices[2][mid] = (vertices[2][begin] + difference * yRatio + (world.rand
                .nextFloat() - 0.5D) * yDiffToMid * widthVariance);
    }


}