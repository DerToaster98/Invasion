package invasion.util;


public class PolarAngle implements IPolarAngle {
    private float angle;

    public PolarAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public float getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }


}