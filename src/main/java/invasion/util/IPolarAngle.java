package invasion.util;

/**
An interface for objects that have an assigned polar angle (read: rotation) around some point
 */
public interface IPolarAngle {
    /**
     * Get the angle of rotation around some point ranging from 0 to 2π
     * @return the angle
     */
    float getAngle();
}