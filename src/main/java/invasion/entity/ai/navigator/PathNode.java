package invasion.entity.ai.navigator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;


public class PathNode {

    public final BlockPos pos;
    public final PathAction action;
    private final int hash;
    public boolean isFirst;
    int index;
    float totalPathDistance;
    double distanceToNext;
    double distanceToTarget;
    private PathNode previous;

    public PathNode(int i, int j, int k) {
        this(i, j, k, PathAction.NONE);
    }

    public PathNode(int i, int j, int k, PathAction pathAction) {
        this(new BlockPos(i, j, k), pathAction);
    }

    public PathNode(double i, double j, double k) {
        this(i, j, k, PathAction.NONE);
    }

    public PathNode(double i, double j, double k, PathAction pathAction) {
        this(new BlockPos(i, j, k), pathAction);
    }

    public PathNode(BlockPos pos, PathAction pathAction) {
        index = -1;
        isFirst = false;
        this.pos = pos;
        action = pathAction;
        hash = makeHash(this.pos, action);
    }

	/*public static int makeHash(int x, int y, int z, PathAction action) {
		return y & 0xFF | (x & 0xFF) << 8 | (z & 0xFF) << 16
				| (action.ordinal() & 0xFF) << 24;
	}*/

	/*public static int makeHash(double x, double y, double z, PathAction action){
		return (Double.hashCode(y) & 0xFF) | (Double.hashCode(x) & 0xFF) << 8 | (Double.hashCode(z) & 0xFF) << 16
				| (action.ordinal() & 0xFF) << 24;
	}*/

    //DarthXenon: I hope I'm doing this right.
    public static int makeHash(BlockPos vec, PathAction action) {
        long j = Integer.toUnsignedLong(action.ordinal());
        return 31 * vec.hashCode() + (int) (j ^ j >>> 32);
    }

    public static int makeHash(double x, double y, double z, PathAction action) {
        return makeHash(new BlockPos(x, y, z), action);
    }

    public float distanceTo(PathNode pathpoint) {
        double d0 = pathpoint.pos.getX() - pos.getX();
        double d1 = pathpoint.pos.getY() - pos.getY();
        double d2 = pathpoint.pos.getZ() - pos.getZ();
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public float distanceTo(double x, double y, double z) {
        double d0 = x - pos.getX();
        double d1 = y - pos.getY();
        double d2 = z - pos.getZ();
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof PathNode)) {
            PathNode node = (PathNode) obj;
            return (hash == node.hash) && equals(node.pos) && (node.action == action);
        }

        return false;
    }

    public boolean equals(double x, double y, double z) {
        return pos.getX() == x && pos.getY() == y && pos.getZ() == z;
    }

    public boolean equals(BlockPos pos1) {
        return pos.equals(pos1);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public boolean isAssigned() {
        return index >= 0;
    }

    @Override
    public String toString() {
        return pos.toString() + ", " + action.toString();
    }

    public PathNode getPrevious() {
        return previous;
    }

    public void setPrevious(PathNode previous) {
        this.previous = previous;
    }
}