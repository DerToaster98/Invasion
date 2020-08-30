package invasion.nexus;

import invasion.util.IPolarAngle;
import net.minecraft.util.math.BlockPos;


public class SpawnPoint implements IPolarAngle, Comparable<IPolarAngle> {
    private final BlockPos pos;
    private final float spawnAngle;
    private final SpawnType spawnType;

    public SpawnPoint(int x, int y, int z, float angle, SpawnType type) {
        pos = new BlockPos(x, y, z);
        spawnAngle = angle;
        spawnType = type;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public float getAngle() {
        return spawnAngle;
    }

    public SpawnType getType() {
        return spawnType;
    }

    @Override
    public int compareTo(IPolarAngle polarAngle) {
        return Float.compare(spawnAngle, polarAngle.getAngle());
    }

    @Override
    public String toString() {
        return "Spawn#" + spawnType + "#" + pos.toString() + "#" + spawnAngle;
    }
}