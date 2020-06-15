package invasion.util;

public class Coords {

    public static final int[] offsetAdjX = {1, -1, 0, 0};
    public static final int[] offsetAdjZ = {0, 0, 1, -1};

    public static final int[] offsetAdj2X = {2, 2, -1, -1, 1, 0, 0, 1};
    public static final int[] offsetAdj2Z = {0, 1, 1, 0, 2, 2, -1, -1};

    public static final int[] offsetRing1X = {1, 0, -1, -1, -1, 0, 1, 1};
    public static final int[] offsetRing1Z = {1, 1, 1, 0, -1, -1, -1, 0};

    public static final double[] offsetAdjXD = {0.0001d, -0.0001, 0, 0};
    public static final double[] offsetAdjZD = {0, 0, 0.0001d, -0.0001d};

    public static final double[] offsetAdj2XD = {0.0002d, 0.0002d, -0.0001d, -0.0001d, 0.0001d, 0, 0, 0.0001d};
    public static final double[] offsetAdj2ZD = {0, 0.0001d, 0.0001d, 0, 0.0002d, 0.0002d, -0.0001d, -0.0001d};

    public static final double[] offsetRing1XD = {0.0001d, 0, -0.0001d, -0.0001d, -0.0001d, 0, 0.0001d, 0.0001d};
    public static final double[] offsetRing1ZD = {0.0001d, 0.0001d, 0.0001d, 0, -0.0001d, -0.0001d, -0.0001d, 0};

}