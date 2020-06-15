package invasion.util;

public class Version {
    public int major;
    public int minor;
    public int build;


    public Version(int majorNum, int minorNum, int buildNum) {
        this.major = majorNum;
        this.minor = minorNum;
        this.build = buildNum;
    }

    public static Version get(String s) {
        String[] parts = s.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        for (String i : parts) {
            for (Character c : i.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return null;
                }
            }
        }

        int[] digits = new int[3];

        for (int i = 0; i < 3; i++) {
            digits[i] = Integer.parseInt(parts[i]);
        }

        return new Version(digits[0], digits[1], digits[2]);
    }

    public byte comparedState(Version version) {
        if (version.major > this.major) {
            return -1;
        } else if (version.major == this.major) {
            if (version.minor > this.minor) {
                return -1;
            } else if (version.minor == this.minor) {
                if (version.build > this.build) {
                    return -1;
                } else if (version.build == this.build) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.build;
    }
}
